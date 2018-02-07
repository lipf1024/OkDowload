package com.example.one.okdowload;

/**
 * Created by ONE on 2018/2/4.
 */

public class FileBean {
    private int id;
    private String name;
    private String url;
    private long size;
    private long status;
    public FileBean(){

    }
    public FileBean(String name,String url,long size){
        this.name=name;
        this.url=url;
        this.size=size;
    }
    public FileBean(String name,String url,long status,long size){
        this.name=name;
        this.url=url;
        this.status=status;
        this.size=size;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStatus() {
        return status;
    }
    public void setStatus(long status) {
        this.status = status;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
