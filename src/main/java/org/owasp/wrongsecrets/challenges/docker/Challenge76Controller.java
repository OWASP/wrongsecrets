package org.owasp.wrongsecrets.challenges.docker;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for Challenge 76 exposing the simulated RAG vector store search. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class Challenge76Controller {

  private final Challenge76 challenge;

  /**
   * Unauthenticated search endpoint of the simulated vector store. It returns the indexed text
   * chunks that match the query, leaking the development secret that was indexed along with the
   * other documents.
   */
  @GetMapping("/rag/search")
  public List<Challenge76.IndexedChunk> search(
      @RequestParam(value = "q", required = false) String query) {
    log.info("Searching the simulated vector store for Challenge 76...");
    return challenge.search(query);
  }
}
