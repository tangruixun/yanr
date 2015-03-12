package com.trx.yanr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ArticleNoDbOperator {

    private SQLiteDatabase database;
    private DBHelper articleNoDbHelper;
    public String [] allColumns = { DBHelper.S_AN_ID, DBHelper.S_AN_GRPNAME, 
            DBHelper.S_AN_SVRNAME, DBHelper.S_AN_ARTICLENO };
    
    public ArticleNoDbOperator (Context context) {
        super ();
        articleNoDbHelper = new DBHelper (context);
    }
    
    public void open () throws SQLException {
        database = articleNoDbHelper.getWritableDatabase ();
    }
    
    public void close () throws SQLException {
        articleNoDbHelper.close ();
    }
    
    public Cursor createRecord (ArticleNoData articleNoData) {
        String strGrpName = articleNoData.getGroupName ();
        String strSrvName = articleNoData.getServerName ();
        int nArticleNo = articleNoData.getArticleNumber ();
        return createRecord (strGrpName, strSrvName, nArticleNo);
    }

    public Cursor createRecord (String strGrpName, String strSrvName,
            int nArticleNo) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_AN_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_AN_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_AN_ARTICLENO, nArticleNo);
        
        long insertId = database.insert (DBHelper.ARTICLE_NO_TABLE, null, contentValues);
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.ARTICLE_NO_TABLE, allColumns, DBHelper.S_AN_ID + " = " + insertId, 
                    null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public Cursor getArticleNoByGroup (String groupName, String serverName) {
        Cursor cursor = null;
        try {
            String [] selectArg = {groupName, serverName};
            cursor = database.query (DBHelper.ARTICLE_NO_TABLE, allColumns, 
                    DBHelper.S_AN_GRPNAME + " = ? AND " + DBHelper.S_AN_SVRNAME + " = ?", 
                    selectArg, null, null, DBHelper.S_AN_ARTICLENO + " DESC");
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public void deleteRecord (String groupName, int articleNo) {
        Cursor c = null;
        try {
            c = database.query (DBHelper.ARTICLE_NO_TABLE, allColumns, 
                    DBHelper.S_AN_GRPNAME + " = '" + groupName + "' AND " + DBHelper.S_AN_ARTICLENO + " = " + articleNo,
                    null, null, null, null);
            c.moveToFirst ();
            deleteRecord (c);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public void deleteRecord (Cursor c) {
        try {
            if (c!=null) {
                if (c.getCount () > 0) {
                    Long _id = c.getLong (c.getColumnIndex (DBHelper.S_AN_ID));
                    deleteRecord (_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public void deleteRecord (Long id) {
        try {
            // long id = record.getId();
            Log.i ("Record deleted with id: ---> ", String.valueOf (id));
            int m = database.delete (DBHelper.ARTICLE_NO_TABLE, DBHelper.S_AN_ID
                    + " = " + id, null);
            Log.i ("--->", m + " line(s) deleted");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public int clearAllRecords () {
        int deleteRow = 0;
        try {
            deleteRow = database.delete (DBHelper.ARTICLE_NO_TABLE, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteRow;
    }



    public boolean isNumberExisted (String groupName, String serverName, 
            int articleNo) {
        Cursor c = null;
        try {
            c = database.query (DBHelper.ARTICLE_NO_TABLE, allColumns, 
                    DBHelper.S_AN_SVRNAME + " = '" + serverName + "' AND " + DBHelper.S_AN_GRPNAME + " = '" + groupName + "' AND " + DBHelper.S_AN_ARTICLENO + " = " + articleNo,
                    null, null, null, null);
            c.moveToFirst ();
            int result = c.getCount ();
            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}


