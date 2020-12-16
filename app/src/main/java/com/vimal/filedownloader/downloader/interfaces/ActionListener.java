package com.vimal.filedownloader.downloader.interfaces;


import com.vimal.filedownloader.downloader.enums.Errors;

public interface ActionListener {

    void onSuccess();

    void onFailure(Errors error);
}
