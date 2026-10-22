package org.owasp.wrongsecrets.challenges.docker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.github.jbellis.jvector.graph.GraphIndex;
import io.github.jbellis.jvector.graph.GraphIndexBuilder;
import io.github.jbellis.jvector.graph.GraphSearcher;
import io.github.jbellis.jvector.graph.ListRandomAccessVectorValues;
import io.github.jbellis.jvector.graph.RandomAccessVectorValues;
import io.github.jbellis.jvector.graph.similarity.BuildScoreProvider;
import io.github.jbellis.jvector.graph.similarity.SearchScoreProvider;
import io.github.jbellis.jvector.util.Bits;
import io.github.jbellis.jvector.vector.VectorSimilarityFunction;
import io.github.jbellis.jvector.vector.VectorizationProvider;
import io.github.jbellis.jvector.vector.types.VectorFloat;
import io.github.jbellis.jvector.vector.types.VectorTypeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Challenge about a retrieval augmented generation (RAG) vector store that indexed development
 * documents containing a synthetic secret. The chunks are indexed with JVector, a real in-memory
 * vector search library, loaded from a pre-generated vector dataset, and exposed through an
 * unauthenticated search endpoint, exactly like a misconfigured vector database that is reachable
 * without authentication.
 */
@Slf4j
@Component
public class Challenge76 extends FixedAnswerChallenge {

  static final String DEVELOPMENT_SECRET = "vector-store-dev-secret-e5c91a7b";
  private static final String DEFAULT_DATASET_LOCATION =
      "challenges/challenge-76/vector-store.json";

  /**
   * JVector's COSINE similarity is mapped to {@code (1 + cosine) / 2}, so 0.5f is the score of two
   * vectors without any term in common. Only chunks that share at least one term with the query
   * score above it and are therefore considered relevant.
   */
  private static final float MIN_RELEVANCE_SCORE = 0.5f;

  private static final int GRAPH_MAX_DEGREE = 16;
  private static final int GRAPH_CONSTRUCTION_DEPTH = 100;
  private static final float GRAPH_NEIGHBOR_OVERFLOW = 1.2f;
  private static final float GRAPH_ALPHA = 1.2f;

  private final Supplier<VectorStore> vectorStore;

  public Challenge76(@Value("classpath:" + DEFAULT_DATASET_LOCATION) Resource datasetResource) {
    this.vectorStore = Suppliers.memoize(() -> new VectorStore(datasetResource));
  }

  Challenge76() {
    this(new ClassPathResource(DEFAULT_DATASET_LOCATION));
  }

  /** A single text chunk as it is stored in the vector store. */
  public record IndexedChunk(String id, String text, Map<String, String> metadata) {
    public IndexedChunk(String id, String text) {
      this(id, text, Map.of());
    }
  }

  private record DatasetRecord(
      String id, String text, Map<String, String> metadata, float[] vector) {}

  @Override
  public String getAnswer() {
    return DEVELOPMENT_SECRET;
  }

  /**
   * Similarity search against the in-memory vector store: the query is embedded and an approximate
   * nearest neighbour search runs over the indexed chunks. Every chunk the index scores above the
   * relevance threshold is returned, including the chunk that leaks the development secret.
   *
   * @param query the search query, or null/blank to return every indexed chunk
   * @return the relevant indexed chunks, most similar first
   */
  public List<IndexedChunk> search(String query) {
    if (query == null || query.isBlank()) {
      return vectorStore.get().allChunks();
    }
    return vectorStore.get().similaritySearch(query);
  }

  /**
   * An in-memory RAG vector store backed by JVector: records and pre-generated vector values are
   * loaded from the JSON dataset and indexed in an in-memory HNSW graph. Nothing leaves the
   * application: the index is built once and queried locally.
   */
  private static final class VectorStore {

    private final List<IndexedChunk> chunks;
    private final Map<String, Integer> vocabulary = new LinkedHashMap<>();
    private final RandomAccessVectorValues vectors;
    private final GraphIndex index;

    private VectorStore(Resource datasetResource) {
      List<DatasetRecord> records = loadDataset(datasetResource);
      this.chunks = new ArrayList<>(records.size());
      for (DatasetRecord record : records) {
        chunks.add(new IndexedChunk(record.id(), record.text(), record.metadata()));
        for (var token : tokenize(record.text())) {
          vocabulary.putIfAbsent(token, vocabulary.size());
        }
      }

      int dimension = records.getFirst().vector().length;
      List<VectorFloat<?>> embeddings = new ArrayList<>(records.size());
      for (DatasetRecord record : records) {
        embeddings.add(vts().createFloatVector(record.vector()));
      }
      this.vectors = new ListRandomAccessVectorValues(embeddings, dimension);
      var buildScoreProvider =
          BuildScoreProvider.randomAccessScoreProvider(vectors, VectorSimilarityFunction.COSINE);
      try (var builder =
          new GraphIndexBuilder(
              buildScoreProvider,
              dimension,
              GRAPH_MAX_DEGREE,
              GRAPH_CONSTRUCTION_DEPTH,
              GRAPH_NEIGHBOR_OVERFLOW,
              GRAPH_ALPHA)) {
        this.index = builder.build(vectors);
      } catch (IOException e) {
        throw new UncheckedIOException("Could not build the vector index for challenge 76", e);
      }
      log.info(
          "Indexed {} chunks of challenge 76 into an in-memory vector store with {} dimensions",
          chunks.size(),
          dimension);
    }

    private static List<DatasetRecord> loadDataset(Resource resource) {
      try (InputStream is = resource.getInputStream()) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(is, new TypeReference<List<DatasetRecord>>() {});
      } catch (IOException e) {
        throw new UncheckedIOException("Could not load the vector dataset for challenge 76", e);
      }
    }

    private List<IndexedChunk> allChunks() {
      return chunks;
    }

    private List<IndexedChunk> similaritySearch(String query) {
      var queryVector = vts().createFloatVector(embeddingFor(query));
      if (isEmpty(queryVector)) {
        return List.of();
      }
      try (var searcher = new GraphSearcher(index)) {
        var searchScoreProvider =
            SearchScoreProvider.exact(queryVector, VectorSimilarityFunction.COSINE, vectors);
        var result = searcher.search(searchScoreProvider, chunks.size(), Bits.ALL);
        List<IndexedChunk> matches = new ArrayList<>();
        for (var nodeScore : result.getNodes()) {
          if (nodeScore.score > MIN_RELEVANCE_SCORE) {
            matches.add(chunks.get(nodeScore.node));
          }
        }
        return matches;
      } catch (IOException e) {
        throw new UncheckedIOException("Could not search the vector index of challenge 76", e);
      }
    }

    private float[] embeddingFor(String text) {
      float[] embedding = new float[vocabulary.size()];
      for (var token : tokenize(text)) {
        var dimension = vocabulary.get(token);
        if (dimension != null) {
          embedding[dimension] += 1f;
        }
      }
      return embedding;
    }

    private static boolean isEmpty(VectorFloat<?> vector) {
      for (int i = 0; i < vector.length(); i++) {
        if (vector.get(i) != 0f) {
          return false;
        }
      }
      return true;
    }

    private static List<String> tokenize(String text) {
      List<String> tokens = new ArrayList<>();
      for (var token : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
        if (!token.isEmpty()) {
          tokens.add(token);
        }
      }
      return tokens;
    }

    private static VectorTypeSupport vts() {
      return VectorizationProvider.getInstance().getVectorTypeSupport();
    }
  }
}
