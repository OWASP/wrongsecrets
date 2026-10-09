package org.owasp.wrongsecrets.challenges.docker;

import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/**
 * Challenge about a retrieval augmented generation (RAG) vector store that indexed development
 * documents containing a synthetic secret. The simulated vector store exposes its indexed text
 * chunks through an unauthenticated search endpoint, exactly like a misconfigured vector database
 * that is reachable without authentication.
 */
@Slf4j
@Component
public class Challenge76 extends FixedAnswerChallenge {

  static final String DEVELOPMENT_SECRET = "vector-store-dev-secret-e5c91a7b";

  private static final List<IndexedChunk> INDEXED_CHUNKS =
      List.of(
          new IndexedChunk(
              "chunk-0",
              "Onboarding guide: the staging environment is reset every night at 02:00 UTC."),
          new IndexedChunk(
              "chunk-1",
              "Runbook: restart the ingestion worker with `kubectl rollout restart"
                  + " deployment/ingestion`."),
          new IndexedChunk(
              "chunk-2",
              "Draft API note: the development vector store uses the credential "
                  + DEVELOPMENT_SECRET
                  + " for the nightly embedding job. Rotate before production."),
          new IndexedChunk(
              "chunk-3", "Meeting notes: evaluation set v3 improved recall from 0.71 to 0.78."));

  /** A single text chunk as it is stored in the simulated vector store. */
  public record IndexedChunk(String id, String text) {}

  @Override
  public String getAnswer() {
    return DEVELOPMENT_SECRET;
  }

  /**
   * Simulates a similarity search against the in-app vector store: every indexed chunk whose text
   * contains the query (case-insensitive) is returned, including the chunk that leaks the
   * development secret.
   */
  public List<IndexedChunk> search(String query) {
    if (query == null || query.isBlank()) {
      return INDEXED_CHUNKS;
    }
    var normalizedQuery = query.toLowerCase(Locale.ROOT);
    return INDEXED_CHUNKS.stream()
        .filter(chunk -> chunk.text().toLowerCase(Locale.ROOT).contains(normalizedQuery))
        .toList();
  }
}
