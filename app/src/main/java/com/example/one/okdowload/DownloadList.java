package com.example.one.okdowload;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ONE on 2018/2/4.
 */

public class DownloadList{

  //  private static ArrayList<DownloadManager> list=new ArrayList();
    private static HashMap<Integer,DownloadManager> list=new HashMap<>();


    public void add(FileBean bean){
        DownloadManager manager=new DownloadManager(bean);//创建下载任务
        Factory.getCachedThreadPool().execute(manager);
        list.put(bean.getId(),manager);//记录下载任务
    }
    /*
    获取下载任务
     */
    public DownloadManager get(Integer Key){

        return list.get(Key);
    }
    /*
    移除下载任务
     */
    public void Remove(int Key){
       list.remove(Key);
    }
    public HashMap<Integer, DownloadManager> getList(){
        return list;
    }
}
