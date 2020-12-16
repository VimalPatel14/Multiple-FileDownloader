package com.vimal.filedownloader.downloader.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vimal.filedownloader.downloader.Downloader;
import com.vimal.filedownloader.downloader.interfaces.DownloadNotificationListener;
import com.vimal.filedownloader.downloader.model.DownloadItem;

public class NotificationBroadcastReceiver extends BroadcastReceiver implements DownloadNotificationListener {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            String id = Downloader.getToken(context, downloadId);
            DownloadItem downloadItem = Downloader.getDownloadItem(context, id);
            if (downloadItem != null && downloadItem.getPercent() == 100) {
                this.onCompleted(context, intent, downloadId);
            } else {
                this.onFailed(context, intent, downloadId);
            }
        } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
            long[] downloadIdList = intent
                    .getLongArrayExtra(DownloadManager
                            .EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            this.onClicked(context, intent, downloadIdList);
        }
    }

    @Override
    public void onCompleted(Context context, Intent intent, long downloadId) {
    }

    @Override
    public void onFailed(Context context, Intent intent, long downloadId) {
    }

    @Override
    public void onClicked(Context context, Intent intent, long[] downloadIdList) {
    }
}