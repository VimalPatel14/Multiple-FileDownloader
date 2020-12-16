package com.vimal.filedownloader.downloader.enums;


public enum Errors {

    CAN_NOT_DELETE_FILE(2000),
    FILE_NOT_FOUND(2001),
    FILE_PATH_NOT_FOUND(2002);

    int value;

    Errors(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
