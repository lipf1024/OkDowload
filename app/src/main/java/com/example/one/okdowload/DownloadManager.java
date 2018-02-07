package com.example.one.okdowload;


import android.util.Log;

import com.example.one.okdowload.MySql.SqlTool;



import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by ONE on 2018/2/4.
 */

public class DownloadManager extends Thread{

     private static final String MARK="STOP_START";

     private OkHttpClient client;
     private FileBean bean;
     private ProgressListener listener;
     private boolean isOk=false;
    public DownloadManager(FileBean bean){
         client=new OkHttpClient();
         this.bean=bean;
    }

    @Override
    public  void run(){
            File progress = new File(getVar.getPath() , bean.getName() + ".my");//获取文件记录的进度
        try {
            Log.d("Progress",bean.getName()+"  "+bean.getSize());
            long StartPoint = 0;

            Log.d("Progress",progress.getAbsolutePath());
            if (progress.exists()) {
                RandomAccessFile accessFile = new RandomAccessFile(progress, "rwd");
                StartPoint=accessFile.readLong();
                accessFile.close();
            }else{
                RandomAccessFile accessFile = new RandomAccessFile(progress, "rwd");
                accessFile.writeLong(0);
                accessFile.close();
            }
            Log.d("Progress",StartPoint+"");
            Request request = new Request.Builder()
                    .url(bean.getUrl())
                    .header("Range", "bytes="+StartPoint+"-"+bean.getSize())
                    .build();
            Response response = client.newCall(request).execute();
            writeFiles(response,StartPoint);

            Log.d("FileBean", bean.getName() + "  " + bean.getSize());
        }catch (IOException e){
            progress.delete();
             /*
            将此任务从队列中移除
            */
            Factory.getDownloadList().Remove(bean.getId());
        }

    }

     private void writeFiles(Response response,long StartPoint) throws IOException {

         InputStream inputStream=response.body().byteStream();
         File file=new File(getVar.getPath(),bean.getName());
         RandomAccessFile randomAccessFile=new RandomAccessFile(file, "rwd");//下载文件
         randomAccessFile.setLength(bean.getSize());//预设下载文件长度



         File progress = new File(getVar.getPath() , bean.getName() + ".my");
         RandomAccessFile accessFile = new RandomAccessFile(progress, "rwd");//记录进度的文件



         long m=StartPoint;//实时进度
         byte[] bit=new byte[1024*2];
         int length =-1;




         randomAccessFile.seek(StartPoint);
         long start=System.currentTimeMillis();
         while((length=inputStream.read(bit))!=-1) {
             while (isOk) {
                 Stop();//暂停该进程 只有在该进程中执行才有效 必须循环判断
             }
             //  isOk=false;
             randomAccessFile.write(bit, 0, length);
             m=m+length;
             accessFile.seek(0);
             accessFile.writeLong(m);
             if(listener!=null)//判断listener是否传进
                 listener.Progress(m); //回调该接口更新进度

             Log.d("Progress",m+"");
         }
         /*

         下载完成后要执行的动作
          */
         isOk=true;
         accessFile.close();
         randomAccessFile.close();
         progress.delete();
         /*
         在此时将数据库中的status更新为1
          */
         bean.setStatus(1);
         SqlTool.getSqlTool(null).UpdataDownload(bean);

         /*
         将此任务从队列中移除
          */
         Factory.getDownloadList().Remove(bean.getId());
     }

     /*
     获取该下载任务的基本信息
      */
     public synchronized FileBean getFileBean(){

        return  bean;
     }

     /*
     暂停该任务
      */

     private  boolean Stop(){
         synchronized (MARK) {
             try {
                 MARK.wait();
             } catch (InterruptedException e) {
                 return false;
             }
             return true;
         }
     }
     public void onStop(){
         isOk=true;
     }
    /*
    开始该任务
     */
     private  boolean Start(){
         synchronized (MARK) {
             MARK.notifyAll();
             isOk = false;
             return true;
         }
     }
     public void onStart(){
         isOk=false;
         Start();
     }
     /*
     设置进度监听
      */

    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }
    /*
    移除监听
     */
    public void removeListener() {
        this.listener = null;
    }
    /*
    获取状态
     */
    public boolean getStatus(){
        return isOk;
    }
}
