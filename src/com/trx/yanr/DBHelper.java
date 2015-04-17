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
    public final static String S_SG_LATESTARTICLENO = "s_LatestArticleNumber";
    public final static String S_SG_POSTABLE = "s_Postable";
    public final static String S_SG_GRPDES = "s_GrpDescription";
    public final static String S_SG_MEMO = "s_Memo";
    
    public final static String HEADER_TABLE = "Headers";
    public final static String S_H_ID = "_id";
    public final static String S_H_GRPNAME = "s_GroupName";
    public final static String S_H_SVRNAME = "s_ServerName";
    public final static String S_H_ARTICLENO = "s_ArticleNumber";
    public final static String S_H_HEADERTEXT = "s_HeaderText";
    
    public final static String BODY_TABLE = "Bodys";
    public final static String S_B_ID = "_id";
    public final static String S_B_GRPNAME = "s_GroupName";
    public final static String S_B_SVRNAME = "s_ServerName";
    public final static String S_B_ARTICLENO = "s_ArticleNumber";
    public final static String S_B_BODYTEXT = "s_BodyText";
    
    private final static String createBodyTableSQL = "CREATE TABLE "
            + BODY_TABLE
            + " ("
            + S_B_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + S_B_GRPNAME + " NVARCHAR(256), "
            + S_B_SVRNAME + " NVARCHAR(256), "
            + S_B_ARTICLENO + " INT, "
            + S_B_BODYTEXT + " NVARCHAR(2147483647)"
            + ")";
            
            
    private final static String createHeaderTableSQL = "CREATE TABLE "
            + HEADER_TABLE
            + " ("
            + S_H_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + S_H_GRPNAME + " NVARCHAR(256), "
            + S_H_SVRNAME + " NVARCHAR(256), "
            + S_H_ARTICLENO + " INT, "
            + S_H_HEADERTEXT + " NVARCHAR(10000)"
            + ")";
    
    private final static String createSubscribedGroupTableSQL = "CREATE TABLE "
            + SUBSCRIBED_GROUPS_TABLE
            + " ("
            + S_SG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + S_SG_GRPNAME + " NVARCHAR(256), "
            + S_SG_SVRNAME + " NVARCHAR(256), "
            + S_SG_READNO + " INT, "
            + S_SG_ARTICLENO + " INT, "
            + S_SG_LATESTARTICLENO + " INT, "
            + S_SG_POSTABLE + " INT, "
            + S_SG_GRPDES + " NVARCHAR(512), "
            + S_SG_MEMO + " NVARCHAR(512)"
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
        db.execSQL (createHeaderTableSQL);
        db.execSQL (createBodyTableSQL);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        // not required until second version :)
        Log.i (DBHelper.class.getName (), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL ("DROP TABLE IF EXISTS " + BODY_TABLE);
        db.execSQL ("DROP TABLE IF EXISTS " + HEADER_TABLE);
        db.execSQL ("DROP TABLE IF EXISTS " + SUBSCRIBED_GROUPS_TABLE);
        onCreate (db);
    }

    public void onRebuild () {
        SQLiteDatabase db = getWritableDatabase ();
        db.execSQL ("DROP TABLE IF EXISTS " + SUBSCRIBED_GROUPS_TABLE);
        db.execSQL ("DROP TABLE IF EXISTS " + HEADER_TABLE);
        db.execSQL ("DROP TABLE IF EXISTS " + BODY_TABLE);

        
        onCreate (db);
    }

}
