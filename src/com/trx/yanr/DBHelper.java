package com.trx.yanr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    
    public final static String DATABASE_NAME = "YANR.db"; // db name
    private final static int DATABASE_VERSION = 1; // db version
    
    public final static String SUBSCRIBED_GROUPS_TABLE = "SubscribedGroups"; // table name
    public final static String S_SG_ID = "_id";
    public final static String S_SG_GRPNAME = "s_GroupName";
    public final static String S_SG_SVRNAME = "s_ServerName";
    public final static String S_SG_READNO = "s_ReadNumber";
    public final static String S_SG_ARTICLENO = "s_ArticleNumber";
    public final static String S_SG_POSTABLE = "s_Postable";
    public final static String S_SG_GRPDES = "s_GrpDescription";
    public final static String S_SG_MEMO = "s_Memo";
    
    private final static String createSubscribedGroupTableSQL = "create table "
            + SUBSCRIBED_GROUPS_TABLE
            + " ("
            + S_SG_ID + " integer primary key autoincrement, "
            + S_SG_GRPNAME + " nvarchar(256), "
            + S_SG_SVRNAME + " nvarchar(256), "
            + S_SG_READNO + " int,"
            + S_SG_ARTICLENO + " int,"
            + S_SG_POSTABLE + " int,"
            + S_SG_GRPDES + " nvarchar(512), "
            + S_SG_MEMO + " nvarchar(512) "
            + ")";

    public DBHelper (Context context, String name, CursorFactory factory,
            int version) {
        super (context, name, factory, version);
    }
    
    public DBHelper (Context context, int version) {
        this (context, DATABASE_NAME, null, version);
    }

    public DBHelper (Context context) {
        this (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.i ("--->", "create database");
        db.execSQL (createSubscribedGroupTableSQL);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
     // not required until second version :)
        Log.i (DBHelper.class.getName (), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL ("DROP TABLE IF EXISTS " + SUBSCRIBED_GROUPS_TABLE);
        onCreate (db);
    }

}
