package org.owasp.wrongsecrets.challenges;

public enum Difficulty {

    EASY, NORMAL, HARD, EXPERT, MASTER;

    public int toInt() {
        return ordinal() + 1;
    }

    public static int totalOfDifficultyLevels() {
        return values().length;
    }
}
