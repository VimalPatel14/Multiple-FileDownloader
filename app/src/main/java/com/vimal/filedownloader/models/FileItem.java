package com.vimal.filedownloader.models;


import com.vimal.filedownloader.downloader.interfaces.DownloadListener;
import com.vimal.filedownloader.downloader.model.DownloadItem;

import java.io.Serializable;


public class FileItem extends DownloadItem implements Serializable {

    private String reasonMessage;
    private DownloadListener listener;
    private int fileSize = -1;

    public FileItem() {
        super();
    }

    public FileItem(DownloadItem downloadItem) {
        super(downloadItem);
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public void setReasonMessage(String reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

    public DownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
