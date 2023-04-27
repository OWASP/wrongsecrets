package org.owasp.wrongsecrets.challenges;

/**
 * Representation of the difficulty levels.
 */
public class Difficulty {

    public static int EASY = 1;
    public static int NORMAL = 2;
    public static int HARD = 3;
    public static int EXPERT = 4;
    public static int MASTER = 5;

    private static final int[] allLevels = new int[] { EASY, NORMAL, HARD, EXPERT, MASTER};

    public static int totalOfDifficultyLevels() {
        return allLevels.length;
    }
}
