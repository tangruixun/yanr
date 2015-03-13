package com.trx.yanr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HeaderDbOperator {

    private SQLiteDatabase database;
    private DBHelper articleNoDbHelper;
    public String [] allColumns = { DBHelper.S_H_ID, DBHelper.S_H_GRPNAME, 
            DBHelper.S_H_SVRNAME, DBHelper.S_H_ARTICLENO, DBHelper.S_H_HEADERTEXT };
    
    public HeaderDbOperator (Context context) {
        super ();
        articleNoDbHelper = new DBHelper (context);
    }
    
    public void open () throws SQLException {
        database = articleNoDbHelper.getWritableDatabase ();
    }
    
    public void close () throws SQLException {
        articleNoDbHelper.close ();
    }
    
    public Cursor createRecord (HeaderData headerData) {
        String strGrpName = headerData.getGroupName ();
        String strSrvName = headerData.getServerName ();
        int nArticleNo = headerData.getArticleNumber ();
        NNTPMessageHeader header = headerData.getHeader ();
        String strHeaderText = header.getAllHeaderString ();
        return createRecord (strGrpName, strSrvName, nArticleNo, strHeaderText);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nArticleNo,
            NNTPMessageHeader headers) {
        String tmp = headers.getHeader ("Injection-Info"); // test
        String strHeaderText = headers.getAllHeaderString ();
        return createRecord (strGrpName, strSrvName, nArticleNo, strHeaderText);
    }

    public Cursor createRecord (String strGrpName, String strSrvName,
            int nArticleNo, String strHeaderText) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_H_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_H_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_H_ARTICLENO, nArticleNo);
        contentValues.put (DBHelper.S_H_HEADERTEXT, strHeaderText);
        
        long insertId = database.insert (DBHelper.HEADER_TABLE, null, contentValues);
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.HEADER_TABLE, allColumns, DBHelper.S_H_ID + " = " + insertId, 
                    null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public Cursor getRecordByGroup (String groupName, String serverName) {
        Cursor cursor = null;
        try {
            String [] selectArg = {groupName, serverName};
            cursor = database.query (DBHelper.HEADER_TABLE, allColumns, 
                    DBHelper.S_H_GRPNAME + " = ? AND " + DBHelper.S_H_SVRNAME + " = ?", 
                    selectArg, null, null, DBHelper.S_H_ARTICLENO + " DESC");
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public void deleteRecord (String groupName, int articleNo) {
        Cursor c = null;
        try {
            c = database.query (DBHelper.HEADER_TABLE, allColumns, 
                    DBHelper.S_H_GRPNAME + " = '" + groupName + "' AND " + DBHelper.S_H_ARTICLENO + " = " + articleNo,
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
                    Long _id = c.getLong (c.getColumnIndex (DBHelper.S_H_ID));
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
            int m = database.delete (DBHelper.HEADER_TABLE, DBHelper.S_H_ID
                    + " = " + id, null);
            Log.i ("--->", m + " line(s) deleted");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public int clearAllRecords () {
        int deleteRow = 0;
        try {
            deleteRow = database.delete (DBHelper.HEADER_TABLE, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteRow;
    }



    public boolean isNumberExisted (String groupName, String serverName, 
            int articleNo) {
        Cursor c = null;
        try {
            c = database.query (DBHelper.HEADER_TABLE, allColumns, 
                    DBHelper.S_H_SVRNAME + " = '" + serverName + "' AND " + DBHelper.S_H_GRPNAME + " = '" + groupName + "' AND " + DBHelper.S_H_ARTICLENO + " = " + articleNo,
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


