package com.example.one.okdowload;

import android.util.Log;


import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;


/**
 * Created by ONE on 2018/2/4.
 */

public class GetFileParameter implements Callable<FileBean> {
    private String path;
    private String name;
    private long size=0;
    private FileBean bean;
    public GetFileParameter(String url){
         this.path=url;
    }

    @Override
    public FileBean call() throws Exception {

        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
          //  connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setConnectTimeout(5000);
            connection.connect();
            int code = connection.getResponseCode();
            if(code == 200){
                size = connection.getContentLength();
                if(size==-1){
                    size=Long.parseLong(connection.getHeaderField("Content-Length"));
                }
                if(name==null){
                    // name=getFileName(connection);

                    name=getName(getFileName(connection));
                }
                Log.d("DDDM","请求失败"+name);

            }

        }catch (Exception e){


        }finally {
            bean=new FileBean(name,path,0,size);
        }
        return bean;
    }



    public  String getFileName( HttpURLConnection conn) {
        String filename = "";
        boolean isok = false;

        if (conn == null) {
            return null;
        }
        Map<String, List<String>> hf=conn.getHeaderFields();
        if (hf != null) {
            Set<String> key = hf.keySet();
            if (key != null) {
                for (String skey : key) {
                    List<String> values = hf.get(skey);
                    for (String value : values) {
                        String result;

                        try {
                            result = new String(value.getBytes("gb2312"), "GBK");
                            if (result.contains("filename")) {
                                int location = result.indexOf("filename");
                                result = result.substring(location + "filename".length());
                                if (result.contains("\"")){
                                    filename = result.substring(result.indexOf("=") + 2, result.lastIndexOf("\""));
                                }else{
                                    filename = result.substring(result.indexOf("=") + 2);
                                }
                                isok = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }// ISO-8859-1 UTF-8 gb2312
                    }
                    if (isok) {
                        break;
                    }
                }
            }
        }
        // 从路径中获取
        if (filename == null || "".equals(filename)) {
            String url=conn.getURL().getFile();
            filename = url.substring(url.lastIndexOf("/") + 1);
        }

        return filename;
    }







    private  String getName(String name){
        String name1=name;
        File file1 = new File(getVar.getPath(),name);
        File file = new File(getVar.getPath(),name+".my");//用于记载文件进度
        if (file.exists()||file1.exists()) {
            int i=0;
            do{
                name="("+String.valueOf(i)+")"+name1;
                file1 = new File(getVar.getPath(),name);
                file = new File(getVar.getPath(),name+".my");
                i++;
            }while(file.exists()||file1.exists());
            return name;

        } else {
            return name;
        }
    }
}
