package com.example.wavemaker;

public class MessageData {
    int progress;
    int position;
    public MessageData(int progress, int position) {
        this.progress = progress;
        this.position = position;
    }
    public int getPosition() { return position; }
    public int getProgress() {
        return progress;
    }
}
