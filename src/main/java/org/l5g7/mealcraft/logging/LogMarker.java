package org.l5g7.mealcraft.logging;

public enum LogMarker {
    CRITICAL("CRITICAL", "⚰️"),
    BIGBOB("BIG_BOB", "🥜"),
    WARN("WARN", "⚠️"),
    INFO("INFO", "ℹ️");

    private final String markerName;
    private final String emoji;

    LogMarker(String markerName, String emoji) {
        this.markerName = markerName;
        this.emoji = emoji;
    }

    public String getMarkerName() {
        return markerName;
    }

    public String getEmoji() {
        return emoji;
    }

    public static String getEmojiForMarkers(String markers) {
        for (LogMarker logMarker : values()) {
            if (markers.contains(logMarker.markerName)) {
                return logMarker.emoji;
            }
        }
        return " ";
    }
}