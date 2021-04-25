package com.davidangbalu.kanjirecognizer.model;

public class Recognition {
    public final int id;
    public final long timestamp;
    public final String title;
    public final float percentage;

    public Recognition(int id, long timestamp, String title, float percentage) {
        this.id = id;
        this.timestamp = timestamp;
        this.title = title;
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - (%.1f)", id, title, percentage);
    }
}