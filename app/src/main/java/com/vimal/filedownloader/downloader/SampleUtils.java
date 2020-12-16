package com.vimal.filedownloader.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.vimal.filedownloader.downloader.enums.Storage;
import com.vimal.filedownloader.downloader.model.DownloadItem;
import com.vimal.filedownloader.downloader.utils.Utils;
import com.vimal.filedownloader.models.FileItem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SampleUtils {

    public static final String STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
    public static String DOWNLOAD_DIRECTORY = Storage.DIRECTORY_DOWNLOADS;//STORAGE_DIRECTORY + "/dmp";
    public static int NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
    static String[] downloadDirectoryArray = new String[]{
            Storage.DIRECTORY_ALARMS
            , Storage.DIRECTORY_DCIM
            , Storage.DIRECTORY_DOWNLOADS
            , Storage.DIRECTORY_MOVIES
            , Storage.DIRECTORY_MUSIC
            , Storage.DIRECTORY_NOTIFICATIONS
            , Storage.DIRECTORY_PICTURES
            , Storage.DIRECTORY_PODCASTS
            , Storage.DIRECTORY_RINGTONES
    };

    public static FileItem getDownloadItem(int number) {
        FileItem item = new FileItem();

        if (number == 1) {
            String link = "https://file-examples-com.github.io/uploads/2017/10/file_example_JPG_500kB.jpg";
            item.setToken("id1252");
            item.setUri(link);
        } else if (number == 2) {
            String link = "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4";
            item.setToken("id1259");
            item.setUri(link);
        } else {
            String link = "https://file-examples-com.github.io/wp-content/uploads/2017/11/file_example_MP3_2MG.mp3";
            item.setToken("id1282");
            item.setUri(link);
        }

        return item;
    }

    public static void getFileList(List<DownloadItem> list, int number) {
        for (int i = 0; i < number; i++) {
            DownloadItem item = new DownloadItem();
            item.setToken("" + i);
            String link = "";
            switch (i % 12) {
                case 0:
                    link = "https://s3-us-west-1.amazonaws.com/powr/defaults/image-slider2.jpg";
                    break;
                case 1:
                    link = "http://dolly.roslin.ed.ac.uk/wp-content/uploads/2016/01/DollySideView.jpg";
                    break;
                case 2:
                    link = "https://i.pinimg.com/originals/ce/69/4f/ce694f560636dffcf42ecf40d4f2f962.gif";
                    break;
                case 3:
                    link = "https://dl2.soft98.ir/soft/m/Mozilla.Firefox.76.0.1.EN.x64.zip";
                    break;
                case 4:
                    link = "https://wallpapercave.com/wp/wp5211914.jpg";
                    break;
                case 5:
                    link = "http://wallpaperpulse.com/img/2242184.jpg";
                    break;
                case 6:
                    link = "https://file-examples-com.github.io/wp-content/uploads/2017/04/file_example_MP4_1280_10MG.mp4";
                    break;
                case 7:
                    link = "https://file-examples-com.github.io/wp-content/uploads/2017/11/file_example_MP3_2MG.mp3";
                    break;
                case 8:
                    link = "http://yesofcorsa.com/wp-content/uploads/2016/12/4k-Love-Wallpaper-HQ-1024x576.jpg";
                    break;
                case 9:
                    link = "http://yesofcorsa.com/wp-content/uploads/2017/01/4K-Rain-Wallpaper-Download-1024x640.jpeg";
                    break;
                case 10:
                    link = "https://file-examples.com/wp-content/uploads/2017/10/file-example_PDF_500_kB.pdf";
                    break;
                case 11:
                    link = "https://file-examples.com/wp-content/uploads/2017/10/file_example_JPG_100kB.jpg";
                    break;

                default:
                    link = "https://file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip";
            }
            item.setUri(link);
            list.add(item);
        }
    }

    public static String getFileShortName(String name) {
        if (name.length() > 10) {
            name = name.substring(0, 5) + ".." + name.substring(name.length() - 4, name.length());
        }
        return name;
    }

    /*public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }*/

    public static void setFileSize(final Context context, final FileItem item) {
        new AsyncTask<String, Integer, Integer>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Integer doInBackground(String... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(item.getUri());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(3000);
                    connection.getInputStream();
                    return connection.getContentLength();
                } catch (IOException e) {
                    return -1;
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            @Override
            protected void onPostExecute(Integer i) {
                item.setFileSize(i);
                System.out.println("VIMAL : SIZE** : " + i);
                super.onPostExecute(i);
            }
        }.execute();
    }

    public static String getFileType(String url) {
        if (url == null || !url.contains("."))
            return null;
        return url.substring(url.lastIndexOf('.'), url.length());
    }


    public static boolean isStoragePermissionGranted(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity
                , WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }


    public static void showInfoDialog(final Activity activity, final DownloadItem downloadItem) {
        if (downloadItem == null) {
            Toast.makeText(activity, "Download details not available", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog dialog;
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        dialog = builder.setTitle("Details")
                .setMessage(downloadItem+"")
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openFile(activity, downloadItem.getLocalUri());

                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }


    public static void cancelDownload(Context context, FileItem item) {
        Downloader downloader = Downloader.getInstance(context)
                .setUrl(item.getUri())
                .setListener(item.getListener());

        downloader.cancel(item.getToken());
    }

    public static boolean isSamsung() {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null) return manufacturer.contains("samsung");
        return false;
    }


    static AdapterView.OnItemSelectedListener getNotificationOnItemSelectListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;

                        break;
                    case 1:
                        NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION;
                        break;

                    case 2:
                        NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE;
                        break;

                    case 3:
                        NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_HIDDEN;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                NOTIFICATION_VISIBILITY = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
            }
        };
    }

    static AdapterView.OnItemSelectedListener getDownloadOnItemSelectListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DOWNLOAD_DIRECTORY = downloadDirectoryArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                DOWNLOAD_DIRECTORY = Storage.DIRECTORY_DOWNLOADS;
            }
        };
    }
}
