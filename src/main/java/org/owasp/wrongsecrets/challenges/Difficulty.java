package org.owasp.wrongsecrets.challenges;

/** Representation of the difficulty levels. */
public class Difficulty {

  public static final int EASY = 1;
  public static final int NORMAL = 2;
  public static final int HARD = 3;
  public static final int EXPERT = 4;
  public static final int MASTER = 5;

  private static final int[] allLevels = new int[] {EASY, NORMAL, HARD, EXPERT, MASTER};

  public static int totalOfDifficultyLevels() {
    return allLevels.length;
  }
}
