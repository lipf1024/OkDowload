package com.example.one.okdowload.MySql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ONE on 2018/2/6.
 */

public class MySQLite extends SQLiteOpenHelper {
    private static final int VERSION=1;
    public MySQLite(Context context) {
        super(context,"UserData", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
                 sqLiteDatabase.execSQL("create table downloadlist (id integer primary key               " +
                         " autoincrement,name varchar(100),url varchar(100),status integer,size integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
