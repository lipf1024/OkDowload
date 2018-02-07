package com.example.one.okdowload;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ONE on 2018/2/4.
 */

public class Factory {
    private static DownloadList downloadList=new DownloadList();
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    public static DownloadList getDownloadList(){
        return downloadList;
    }
    public static ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }
}
