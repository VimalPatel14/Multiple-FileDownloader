package com.vimal.filedownloader.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tedpark.tedpermission.rx2.TedRx2Permission;
import com.vimal.filedownloader.R;
import com.vimal.filedownloader.adapters.DownloadListAdapter;
import com.vimal.filedownloader.downloader.Downloader;
import com.vimal.filedownloader.downloader.SampleUtils;
import com.vimal.filedownloader.downloader.enums.DownloadStatus;
import com.vimal.filedownloader.downloader.model.DownloadItem;
import com.vimal.filedownloader.downloader.utils.Utils;
import com.vimal.filedownloader.models.FileItem;
import com.vimal.filedownloader.models.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    EditText edtUrl;
    LinearLayout layoutDownload;
    RecyclerView rcvFileList;
    ArrayList<FileModel> mListFile;
    DownloadListAdapter mDownloadListAdapter;
    private List<FileItem> items;
    TextView tvClear;
    public static String ROOT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mContext = this;
        mListFile = new ArrayList<>();
        items = new ArrayList<>();
        findViewById();
        setOnClickListener();
        bindMethod();
    }


    public void findViewById() {
        edtUrl = findViewById(R.id.edtUrl);
        layoutDownload = findViewById(R.id.layoutDownload);
        rcvFileList = findViewById(R.id.rcvFileList);
        tvClear = findViewById(R.id.tvClear);
    }

    public void setOnClickListener() {
        layoutDownload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                if (edtUrl.getText().toString().equals(""))
                    Toast.makeText(mContext, "Please enter url.!", Toast.LENGTH_SHORT).show();
                else if (!URLUtil.isValidUrl(edtUrl.getText().toString()))
                    Toast.makeText(mContext, "Please enter valid url.!", Toast.LENGTH_SHORT).show();
                else {

                    TedRx2Permission.with(mContext)
                            .setRationaleTitle("Can we read your storage?")
                            .setRationaleMessage("We need your permission to access your storage and pick image")
                            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request()
                            .subscribe(permissionResult -> {
                                        if (permissionResult.isGranted()) {
                                            File mRoot = new File(ROOT);
                                            if (!mRoot.exists())
                                                mRoot.mkdirs();

                                            System.out.println("URL : " + edtUrl.getText().toString());
                                            DownloadItem item = new DownloadItem();
                                            item.setDownloadStatus(DownloadStatus.PENDING);
                                            item.setUri(edtUrl.getText().toString());
                                            item.setToken(System.currentTimeMillis() + "");
                                            FileItem fileItem = new FileItem(item);
                                            fileItem.setLocalFilePath(SampleUtils.DOWNLOAD_DIRECTORY);
                                            SampleUtils.setFileSize(getApplicationContext(), fileItem);
                                            items.add(0, fileItem);
                                            mDownloadListAdapter.notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(mContext, "You need to allow all permissions", Toast.LENGTH_SHORT).show();
                                        }
                                    }, throwable -> {
                                    }
                            );
                }
            }
        });

        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Clear")
                        .setMessage("Do you really want to clear list ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getDownloadStatus() != DownloadStatus.RUNNING)
                                        items.remove(i);
                                }

                                mDownloadListAdapter.notifyDataSetChanged();
                                tvClear.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }


    public void bindMethod() {
        System.setProperty("java.protocol.handler.pkgs", "org.andresoviedo.app.util.url");
        rcvFileList.setLayoutManager(new GridLayoutManager(mContext, 1));

        ROOT = Environment.getExternalStorageDirectory()
                + "/" + getResources().getString(R.string.app_name);


        mDownloadListAdapter = new DownloadListAdapter((Activity) mContext, rcvFileList, items, new DownloadListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                System.out.println("VIMAL : name :" + items.get(pos).getTitle());
                System.out.println("VIMAL : path :" + items.get(pos).getLocalUri());
                System.out.println("VIMAL : size :" + items.get(pos).getFileSize());

                Intent ip = new Intent(mContext, DetailsActivity.class);
                ip.putExtra("selectedName", items.get(pos).getTitle());
                ip.putExtra("selctedPath", items.get(pos).getLocalUri());
                ip.putExtra("selectedSize", items.get(pos).getFileSize());
                startActivity(ip);
            }

            @Override
            public void onComplete() {
                refreshAfter();
            }
        });

        if (items.size() > 0)
            tvClear.setVisibility(View.VISIBLE);

        rcvFileList.setAdapter(mDownloadListAdapter);

        TedRx2Permission.with(mContext)
                .setRationaleTitle("Can we read your storage?")
                .setRationaleMessage("We need your permission to access your storage and pick image")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request()
                .subscribe(permissionResult -> {
                            if (permissionResult.isGranted()) {
                                File mRoot = new File(ROOT);
                                if (!mRoot.exists())
                                    mRoot.mkdirs();

                            } else {
                                Toast.makeText(mContext, "Please accept all the permissions.", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                        }
                );

        refreshAfter();

    }

    void refreshAfter() {
        new LoadMyData().execute();
    }

    class LoadMyData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            items.clear();
            super.onPreExecute();
            for (int j = 0; j < Downloader.getDownloadsList(mContext).size(); j++) {
//            if (Downloader.getDownloadsList(mContext).get(j).getDownloadStatus() == DownloadStatus.PENDING) {
                FileItem fl = new FileItem(Downloader.getDownloadsList(mContext).get(j));
                SampleUtils.setFileSize(mContext, fl);
                System.out.println("VIMAL : *FOR LOOP** : " + fl.getFileSize());
                items.add(fl);
//            }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < items.size(); i++) {
                FileItem fl = new FileItem(items.get(i));
                items.get(i).setTitle(Utils.getFileName(fl.getUri()));
                System.out.println("VIMAL : NAME BG" + Utils.getFileName(fl.getUri()));
                SampleUtils.setFileSize(mContext, fl);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDownloadListAdapter.notifyDataSetChanged();
        }
    }
}