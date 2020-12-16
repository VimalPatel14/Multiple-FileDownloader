package com.vimal.filedownloader.downloader.interfaces;


import com.vimal.filedownloader.downloader.enums.DownloadReason;
import com.vimal.filedownloader.downloader.model.DownloadItem;


public interface DownloadListener {

    void onComplete(int totalBytes, DownloadItem downloadInfo);

    void onPause(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo);

    void onPending(int percent, int totalBytes, int downloadedBytes, DownloadItem downloadInfo);

    void onFail(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo);

    void onCancel(int totalBytes, int downloadedBytes, DownloadItem downloadInfo);

    void onRunning(int percent, int totalBytes, int downloadedBytes, float downloadSpeed, DownloadItem downloadInfo);

}
