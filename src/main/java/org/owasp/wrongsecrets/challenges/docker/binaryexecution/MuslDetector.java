package org.owasp.wrongsecrets.challenges.docker.binaryexecution;

/** Musl detector interface which can have multiple implementations. */
public interface MuslDetector {

  /**
   * boolean indicating whether we are running on a MUSL based/enabled environment.
   *
   * @return true if binaries should use Musl instead of LibC
   */
  boolean isMusl();
}
