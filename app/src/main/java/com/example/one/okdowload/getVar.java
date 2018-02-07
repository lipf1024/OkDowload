package com.example.one.okdowload;

/**
 * Created by ONE on 2018/2/6.
 */

public class getVar {
    private static String path="/sdcard/HDownload/";

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
       getVar.path = path;
    }
}
