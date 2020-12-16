package com.vimal.filedownloader.models;

import com.vimal.filedownloader.utils.BasicUtils;

public class FileModel {
    String fileName,fileUrl,fileSize;
    BasicUtils.fileType fileType;

    public FileModel(String fileName, String fileUrl, String fileSize, BasicUtils.fileType fileType) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public BasicUtils.fileType getFileType() {
        return fileType;
    }

    public void setFileType(BasicUtils.fileType fileType) {
        this.fileType = fileType;
    }
}
