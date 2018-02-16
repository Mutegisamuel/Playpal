package com.example.humungus.playpal;

/**
 * Created by humungus on 2/15/18.
 */

public class model {
    private String title;
    private String displayName;
    private long duration;

    public model(String title, String displayName, long duration) {
        this.title = title;
        this.displayName = displayName;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getDuration() {
        return duration;
    }
}
