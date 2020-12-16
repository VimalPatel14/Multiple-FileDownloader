package com.vimal.filedownloader.downloader.enums;


public enum DownloadStatus {
    PENDING(1),
    RUNNING(2),
    PAUSED(4),
    SUCCESSFUL(8),
    FAILED(16),
    CANCELED(20),
    NONE(0);

    int value;

    DownloadStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
