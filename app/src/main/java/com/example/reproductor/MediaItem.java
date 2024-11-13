package com.example.reproductor;
public class MediaItem {
    private String type; // "video" o "audio"
    private int resourceId; // ID del archivo en raw

    public MediaItem(String type, int resourceId) {
        this.type = type;
        this.resourceId = resourceId;
    }

    public String getType() {
        return type;
    }

    public int getResourceId() {
        return resourceId;
    }
}
