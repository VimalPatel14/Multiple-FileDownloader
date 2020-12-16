package com.vimal.filedownloader.adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.vimal.filedownloader.R;
import com.vimal.filedownloader.downloader.Downloader;
import com.vimal.filedownloader.downloader.SampleUtils;
import com.vimal.filedownloader.downloader.enums.DownloadReason;
import com.vimal.filedownloader.downloader.enums.DownloadStatus;
import com.vimal.filedownloader.downloader.enums.Errors;
import com.vimal.filedownloader.downloader.enums.Storage;
import com.vimal.filedownloader.downloader.interfaces.ActionListener;
import com.vimal.filedownloader.downloader.interfaces.DownloadListener;
import com.vimal.filedownloader.downloader.model.DownloadItem;
import com.vimal.filedownloader.downloader.utils.Utils;
import com.vimal.filedownloader.models.FileItem;

import java.util.List;

import static com.vimal.filedownloader.downloader.SampleUtils.DOWNLOAD_DIRECTORY;
import static com.vimal.filedownloader.downloader.SampleUtils.NOTIFICATION_VISIBILITY;


public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {

    public interface OnItemClickListener<T> {
        void onItemClick(int pos);

        void onComplete();
    }

    private List<FileItem> items;
    private Activity activity;
    private OnItemClickListener mListener;
    private RecyclerView mRecyclerviewDownloads;

    public DownloadListAdapter(Activity activity, RecyclerView rvDownloads, List<FileItem> items, OnItemClickListener m) {
        this.items = items;
        this.activity = activity;
        mListener = m;
        mRecyclerviewDownloads = rvDownloads;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_file_list, parent, false);
        final ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FileItem item = items.get(position);
        holder.tvName.setText(Utils.getFileName(item.getUri()));

        if (item.getDownloadStatus() == DownloadStatus.PENDING) {
            holder.itemView.setTag(item);
            showProgress(holder, item, position);
            clickOnActionButton(holder, item);
        } else if (item.getDownloadStatus() == DownloadStatus.RUNNING) {
            showProgress(holder, item, position);
            holder.pauseButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.downloadProgressBar.setProgressColor(activity.getResources().getColor(R.color.themeBlue));
            holder.pauseButton.setImageResource(R.drawable.ic_pause);
            holder.pauseButton.setColorFilter(ContextCompat.getColor(activity, R.color.themeBlue), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        initItem(holder, item);

        System.out.println("VVVV : " + item.getDownloadStatus());

        if (item.getDownloadStatus() == DownloadStatus.CANCELED ||
                item.getDownloadStatus() == DownloadStatus.SUCCESSFUL ||
                item.getDownloadStatus() == DownloadStatus.FAILED) {
            holder.netLayout.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
        } else {
            holder.netLayout.setVisibility(View.VISIBLE);
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getDownloadStatus() == DownloadStatus.RUNNING || item.getDownloadStatus() == DownloadStatus.PAUSED) {
                    Toast.makeText(activity, "task not downloaded yet", Toast.LENGTH_SHORT).show();
                } else
                    mListener.onItemClick(position);
            }
        });


        holder.pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getDownloadStatus() == DownloadStatus.RUNNING) {
                    holder.pauseButton.setImageResource(R.drawable.ic_play);
                    holder.downloadProgressBar.setProgressColor(activity.getResources().getColor(R.color.themePause));
                    holder.pauseButton.setColorFilter(ContextCompat.getColor(activity, R.color.themePause), android.graphics.PorterDuff.Mode.SRC_IN);
                    getDownloader(holder, item).pause(activity, item.getToken());
                    item.setDownloadStatus(DownloadStatus.PAUSED);
                    System.out.println("LOOP : paused click** : " + item.getDownloadStatus());
                    notifyItemChanged(position);
                } else {
                    holder.downloadProgressBar.setProgressColor(activity.getResources().getColor(R.color.themeBlue));
                    holder.pauseButton.setImageResource(R.drawable.ic_pause);
                    holder.pauseButton.setColorFilter(ContextCompat.getColor(activity, R.color.themeBlue), android.graphics.PorterDuff.Mode.SRC_IN);
                    getDownloader(holder, item).resume(activity, item.getToken());
                    item.setDownloadStatus(DownloadStatus.RUNNING);
                    System.out.println("LOOP : paused click** : " + item.getDownloadStatus());
                    notifyItemChanged(position);
                }
            }
        });

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(activity)
                        .setTitle("Cancel")
                        .setMessage("Do you really want to cancel download?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                getDownloader(holder, item).cancel(item.getToken());
                                item.setDownloadStatus(DownloadStatus.CANCELED);
                                holder.cancelButton.setVisibility(View.GONE);
                                notifyItemChanged(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        if (item.getDownloadStatus() == DownloadStatus.PAUSED) {
            holder.downloadProgressBar.setProgressColor(activity.getResources().getColor(R.color.themePause));
            holder.pauseButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.pauseButton.setImageResource(R.drawable.ic_play);
            holder.pauseButton.setColorFilter(ContextCompat.getColor(activity, R.color.themePause), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.tv_percent.setText(item.getPercent() + " %");
            holder.tvSpeed.setText("0/0");
            holder.tvSize.setText(Utils.readableFileSize(item.getDownloadedBytes())
                    + "/" + Utils.readableFileSize(item.getFileSize()) + " - Paused");
        }
    }


    private void initItem(ViewHolder holder, FileItem item) {
        switch (item.getDownloadStatus()) {
            case NONE:
            case CANCELED:
            case FAILED:

                break;
            case PAUSED:

                break;
            case PENDING:

                break;
            case SUCCESSFUL:

                break;
            case RUNNING:

                break;
            default:

        }
        holder.downloadProgressBar.setProgress(item.getPercent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private DownloadListener listener;
        private RoundCornerProgressBar downloadProgressBar;
        TextView tv_percent, tvSpeed, tvSize, tvName;
        LinearLayout mainCard, netLayout;
        ImageView navigationThumb, pauseButton, cancelButton;

        private ViewHolder(View itemView) {
            super(itemView);
            downloadProgressBar = (RoundCornerProgressBar) itemView.findViewById(R.id.progressbar);
            tv_percent = itemView.findViewById(R.id.tv_percent);
            tvSpeed = itemView.findViewById(R.id.tvSpeed);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvName = itemView.findViewById(R.id.tvName);
            mainCard = itemView.findViewById(R.id.mainCard);
            netLayout = itemView.findViewById(R.id.netLayout);
            navigationThumb = itemView.findViewById(R.id.navigationThumb);
            pauseButton = itemView.findViewById(R.id.pauseButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);

        }

    }

    private void showProgress(ViewHolder holder, FileItem item, int position) {
        initListener(holder, item, position);

        Downloader.getInstance(activity)
                .setUrl(item.getUri())
                .setListener(holder.listener)
                .setToken(item.getToken())
                .setDestinationDir(Storage.DIRECTORY_DOWNLOADS
                        , Utils.getFileName(item.getUri()))
                .setNotificationTitle(Utils.getFileName(item.getUri()))
                .showProgress();
    }

    private void initListener(final ViewHolder holder, final FileItem item, final int position) {
        System.out.println("VIMI : " + item.getDownloadStatus());
        holder.listener = new DownloadListener() {

            @Override
            public void onComplete(int totalBytes, DownloadItem downloadInfo) {
                item.setDownloadStatus(DownloadStatus.SUCCESSFUL);
                item.setPercent(100);
                if (isCurrentListViewItemVisible(position)) {
                    holder.downloadProgressBar.setProgress(100);
                    holder.tv_percent.setText(item.getPercent() + "%");
                }
                holder.netLayout.setVisibility(View.GONE);
                holder.pauseButton.setVisibility(View.GONE);
                mListener.onComplete();
            }

            @Override
            public void onPause(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                if (isCurrentListViewItemVisible(position)) {
                    holder.downloadProgressBar.setProgress(percent);
                    holder.tv_percent.setText(item.getPercent() + "%");
                    holder.tvSize.setText(Utils.readableFileSize(downloadedBytes)
                            + "/" + Utils.readableFileSize(totalBytes) + " - Paused");
                }
                System.out.println("LOOP : paused called");
                item.setDownloadStatus(DownloadStatus.PAUSED);
            }

            @Override
            public void onPending(int percent, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                if (isCurrentListViewItemVisible(position)) {
                    if (item.getDownloadStatus() != DownloadStatus.PENDING) {
                        holder.downloadProgressBar.setProgress(percent);
                        holder.tv_percent.setText(item.getPercent() + "%");
                        holder.tvSize.setText(Utils.readableFileSize(downloadedBytes)
                                + "/" + Utils.readableFileSize(totalBytes) + " - Pending");
                    }
                }
                item.setDownloadStatus(DownloadStatus.PENDING);
            }

            @Override
            public void onFail(int percent, DownloadReason reason, int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                item.setDownloadStatus(DownloadStatus.FAILED);
                item.setPercent(0);
                if (isCurrentListViewItemVisible(position)) {
                    holder.downloadProgressBar.setProgress(item.getPercent());
                    holder.tv_percent.setText(item.getPercent() + "%");
                    holder.tvSize.setText(Utils.readableFileSize(downloadedBytes)
                            + "/" + Utils.readableFileSize(totalBytes) + " - Failed");
                }
                holder.netLayout.setVisibility(View.GONE);
                holder.pauseButton.setVisibility(View.GONE);
            }

            @Override
            public void onCancel(int totalBytes, int downloadedBytes, DownloadItem downloadInfo) {
                item.setDownloadStatus(DownloadStatus.CANCELED);
                item.setPercent(0);
                if (isCurrentListViewItemVisible(position)) {
                    holder.downloadProgressBar.setProgress(item.getPercent());
                    holder.tv_percent.setText(item.getPercent() + "%");
                    holder.tvSize.setText(Utils.readableFileSize(downloadedBytes)
                            + "/" + Utils.readableFileSize(totalBytes) + " - Canceled");
                }
                holder.netLayout.setVisibility(View.GONE);
                holder.pauseButton.setVisibility(View.GONE);
            }

            @Override
            public void onRunning(int percent, int totalBytes, int downloadedBytes, float downloadSpeed, DownloadItem downloadInfo) {
                item.setDownloadStatus(DownloadStatus.RUNNING);
                item.setPercent(percent);
                if (isCurrentListViewItemVisible(position)) {
                    holder.downloadProgressBar.setProgress(item.getPercent());
                    holder.tv_percent.setText(item.getPercent() + "%");
                    if (totalBytes < 0 || downloadedBytes < 0)
                        holder.tvSize.setText("loading...");
                    else
                        holder.tvSize.setText(Utils.readableFileSize(downloadedBytes)
                                + "/" + Utils.readableFileSize(totalBytes));
                    holder.tvSpeed.setText(Math.round(downloadSpeed) + " KB/sec");
                }
                holder.pauseButton.setVisibility(View.VISIBLE);
                holder.cancelButton.setVisibility(View.VISIBLE);
//                Log.i("Title: " + downloadInfo.getTitle());
            }

        };
    }

    private void clickOnActionButton(ViewHolder holder, FileItem item) {
        if (!SampleUtils.isStoragePermissionGranted(activity))
            return;
        final Downloader downloader = getDownloader(holder, item);
        if (downloader.getStatus(item.getToken()) == DownloadStatus.RUNNING
                || downloader.getStatus(item.getToken()) == DownloadStatus.PAUSED
                || downloader.getStatus(item.getToken()) == DownloadStatus.PENDING)
            downloader.cancel(item.getToken());
        else if (downloader.getStatus(item.getToken()) == DownloadStatus.SUCCESSFUL) {
            Utils.openFile(activity, downloader.getDownloadedFilePath(item.getToken()));
        } else
            downloader.start();
    }

    private Downloader getDownloader(ViewHolder holder, FileItem item) {
        Downloader request = Downloader.getInstance(activity)
                .setListener(holder.listener)
                .setUrl(item.getUri())
                .setToken(item.getToken())
                .setKeptAllDownload(false)//if true: canceled download token keep in db
                .setAllowedOverRoaming(true)
                .setVisibleInDownloadsUi(true)
                .setDescription(Utils.readableFileSize(item.getFileSize()))
                .setScanningByMediaScanner(true)
                .setNotificationVisibility(NOTIFICATION_VISIBILITY)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                //.setCustomDestinationDir(DOWNLOAD_DIRECTORY, Utils.getFileName(item.getUri()))//TargetApi 28 and lower
                .setDestinationDir(DOWNLOAD_DIRECTORY, Utils.getFileName(item.getUri()))
                .setNotificationTitle(SampleUtils.getFileShortName(Utils.getFileName(item.getUri())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            request.setAllowedOverMetered(true); //Api 16 and higher
        }
        return request;
    }

    private ActionListener getDeleteListener(final ViewHolder holder
            , final RoundCornerProgressBar numberProgressBar
            , final FileItem item
            , final int position) {
        return new ActionListener() {
            @Override
            public void onSuccess() {
                item.setPercent(0);
                numberProgressBar.setProgress(item.getPercent());
                Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                holder.downloadProgressBar.setProgress(item.getPercent());
                holder.tv_percent.setText(item.getPercent() + "%");
            }

            @Override
            public void onFailure(Errors error) {
                Toast.makeText(activity, "" + error, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerviewDownloads.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }

    private DownloadListAdapter.ViewHolder getViewHolder(int position) {
        return (DownloadListAdapter.ViewHolder) mRecyclerviewDownloads.findViewHolderForLayoutPosition(position);
    }
}
