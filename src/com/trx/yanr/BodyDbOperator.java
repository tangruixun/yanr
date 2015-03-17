package com.trx.yanr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BodyDbOperator {

    private SQLiteDatabase database;
    private DBHelper bodyDbHelper;
    public String [] allColumns = { DBHelper.S_B_GRPNAME, DBHelper.S_B_SVRNAME, 
            DBHelper.S_B_ARTICLENO, DBHelper.S_B_BODYTEXT };
    
    public BodyDbOperator (Context context) {
        super ();
        bodyDbHelper = new DBHelper (context);
    }
    
    public void open () throws SQLException {
        database = bodyDbHelper.getWritableDatabase ();
    }
    
    public void close () throws SQLException {
        bodyDbHelper.close ();
    }
    
    public Cursor createRecord (BodyData bodyData) {
        String strGrpName = bodyData.getGroupName ();
        String strSrvName = bodyData.getServerName ();
        int nArticleNo = bodyData.getArticleNumber ();
        String strBody = bodyData.getBody ();
        return createRecord (strGrpName, strSrvName, nArticleNo, strBody);
    }

    public Cursor createRecord (String strGrpName, String strSrvName,
            int nArticleNo, String strBody) {
        
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_B_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_B_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_B_ARTICLENO, nArticleNo);
        contentValues.put (DBHelper.S_B_BODYTEXT, strBody);
        
        Cursor cursor = null;
        try {
            long insertId = database.insert (DBHelper.BODY_TABLE, null, contentValues);
            cursor = database.query (DBHelper.BODY_TABLE, allColumns, DBHelper.S_B_ID + " = " + insertId, 
                    null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;    
    }
    
    public void deleteRecord (Cursor c) {
        try {
            if (c!=null) {
                if (c.getCount () > 0) {
                    Long _id = c.getLong (c.getColumnIndex (DBHelper.S_B_ID));
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
            int m = database.delete (DBHelper.BODY_TABLE, DBHelper.S_B_ID
                    + " = " + id, null);
            Log.i ("--->", m + " line(s) deleted");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public int clearAllRecords () {
        int deleteRow = 0;
        try {
            deleteRow = database.delete (DBHelper.BODY_TABLE, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteRow;
    }
    
    public boolean isNumberExisted (String groupName, String serverName, int articleNo) {
        Cursor c = null;
        try {
            c = database.query (DBHelper.BODY_TABLE, allColumns, 
                    DBHelper.S_B_SVRNAME + " = '" + serverName + "' AND " + DBHelper.S_B_GRPNAME + " = '" + groupName + "' AND " + DBHelper.S_B_ARTICLENO + " = " + articleNo,
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
    
    public Cursor getRecordByArticleId (String groupName, String serverName, int articleNo) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.BODY_TABLE, allColumns, 
                    DBHelper.S_B_GRPNAME + " = '" + groupName 
                    + "' AND " + DBHelper.S_B_SVRNAME + " = '" + serverName 
                    + "' AND " + DBHelper.S_B_ARTICLENO + " = " + articleNo,
                    null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
}
