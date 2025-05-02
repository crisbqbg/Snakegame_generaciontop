package com.example.snake;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final List<ScoreEntry> highScores = new ArrayList<>();

    public static void saveScore(String playerName, int score, boolean isExtremeMode) {
        highScores.add(new ScoreEntry(playerName, score, isExtremeMode));
        highScores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
    }

    public static List<ScoreEntry> getHighScores() {
        return highScores;
    }

    public static class ScoreEntry {
        private final String playerName;
        private final int score;
        private final boolean isExtremeMode;

        public ScoreEntry(String playerName, int score, boolean isExtremeMode) {
            this.playerName = playerName;
            this.score = score;
            this.isExtremeMode = isExtremeMode;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public boolean isExtremeMode() {
            return isExtremeMode;
        }
    }
}