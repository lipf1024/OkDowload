package com.example.one.okdowload.MySql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.one.okdowload.FileBean;

import java.util.ArrayList;

/**
 * Created by ONE on 2018/2/6.
 */

public class SqlTool {
    private MySQLite tool;
    private static SqlTool sqlTool;

    private SqlTool(Context context){
        tool=new MySQLite(context.getApplicationContext());
    }

    public synchronized static SqlTool getSqlTool(Context context){
        if(sqlTool==null){
            sqlTool=new SqlTool(context);
        }
        return  sqlTool;
    }

    public ArrayList<FileBean> getList(){
        ArrayList<FileBean> list=new ArrayList<>();
        SQLiteDatabase db=tool.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT *FROM downloadlist",null);
        while (cursor.moveToNext()){
            FileBean bean=new FileBean();
            bean.setId(cursor.getInt(0));
            bean.setName(cursor.getString(1));
            bean.setUrl(cursor.getString(2));
            bean.setStatus(cursor.getLong(3));
            bean.setSize(cursor.getLong(4));
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;

    }
    public FileBean getFilebean(int Key){
        FileBean bean=null;
        SQLiteDatabase db=tool.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT *FROM downloadlist WHERE id=?",new String[]{Key+""});
        if (cursor.moveToNext()){
            bean=new FileBean();
            bean.setId(cursor.getInt(0));
            bean.setName(cursor.getString(1));
            bean.setUrl(cursor.getString(2));
            bean.setStatus(cursor.getLong(3));
            bean.setSize(cursor.getLong(4));
        }
        cursor.close();
        db.close();
        return  bean;
    }
    public void UpdataDownload(FileBean bean){
        SQLiteDatabase db=tool.getWritableDatabase();
        db.execSQL("UPDATE downloadlist SET name=?,url=?,status=?,size=? WHERE id=?",
                new String[]{bean.getName(),bean.getUrl(),bean.getStatus()+"",bean.getSize()+"",bean.getId()+""});

    }

    public int AddDownload(FileBean bean){
        Log.d("AddDownload","success");
        SQLiteDatabase db=tool.getWritableDatabase();
        db.execSQL("INSERT INTO downloadlist(name,url,status,size) VALUES(?,?,?,?)",
                new String[]{bean.getName(),bean.getUrl(),bean.getStatus()+"",bean.getSize()+""});
        Cursor cursor=db.rawQuery("SELECT max(id) FROM downloadlist ",null);
        int id=-1;
        if(cursor.moveToNext())
             id=cursor.getInt(0);
        cursor.close();
        db.close();
        return id;

    }
    public void DeleteDownload(int id){
        SQLiteDatabase db=tool.getWritableDatabase();
        db.execSQL("DELETE FROM downloadlist WHERE id=?",new String[]{id+""});
        db.close();
    }


    public void DeleteAll(){
        SQLiteDatabase db=tool.getWritableDatabase();
        db.execSQL("DELETE FROM downloadlist ",null);
        db.close();
    }
}
