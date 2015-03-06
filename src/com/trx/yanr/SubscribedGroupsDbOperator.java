package com.trx.yanr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SubscribedGroupsDbOperator {
    
    private SQLiteDatabase database;
    private DBHelper subscribedGroupDbHelper;
    public String [] allColumns = {
            DBHelper.S_SG_ID, DBHelper.S_SG_GRPNAME, 
            DBHelper.S_SG_SVRNAME, DBHelper.S_SG_READNO, 
            DBHelper.S_SG_ARTICLENO, DBHelper.S_SG_POSTABLE, 
            DBHelper.S_SG_GRPDES, DBHelper.S_SG_MEMO };
    
    public SubscribedGroupsDbOperator (Context context) {
        subscribedGroupDbHelper = new DBHelper (context);
    }
    
    public void open () throws SQLException {
        database = subscribedGroupDbHelper.getWritableDatabase ();
    }
    
    public void close () throws SQLException {
        subscribedGroupDbHelper.close ();
    }
    
    public Cursor createRecord (GroupData grpData) {
        String strGrpName = grpData.getGroupName ();
        String strSrvName = grpData.getServerName ();
        int nReadNo = grpData.getReadNumber ();
        int nArticleNo = grpData.getArticleNumbers ();
        Boolean bPostable = grpData.getPostable ();
        String strGrpDes = grpData.getGroupDes ();
        String strGrpMemo = grpData.getMemo ();
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, bPostable, strGrpDes, strGrpMemo);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName) {
        return createRecord (strGrpName, strSrvName, -1, -1);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, true);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, Boolean bPostable) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, bPostable, "");
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, Boolean bPostable, String strGrpDes) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, bPostable, strGrpDes, "");
    }

    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, Boolean bPostable, String strGrpDes, String strGrpMemo) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_SG_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_SG_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_SG_READNO, nReadNo);
        contentValues.put (DBHelper.S_SG_ARTICLENO, nArticleNo);
        contentValues.put (DBHelper.S_SG_POSTABLE, bPostable);
        contentValues.put (DBHelper.S_SG_MEMO, strGrpMemo);
        
        long insertId = database.insert (DBHelper.SUBSCRIBED_GROUPS_TABLE, null, contentValues);
        
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, DBHelper.S_SG_ID + " = " + insertId, 
                    null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public Cursor getGroupsByServer (String serverName) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, 
                    DBHelper.S_SG_SVRNAME + " = '" + serverName + "'", null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public void deleteRecord (Long id) {
        try {
            // long id = record.getId();
            Log.i ("Record deleted with id: ---> ", String.valueOf (id));
            int m = database.delete (DBHelper.SUBSCRIBED_GROUPS_TABLE, DBHelper.S_SG_ID
                    + " = " + id, null);
            Log.i ("--->", m + " line(s) deleted");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public int clearAllRecords () {
        int deleteRow = 0;
        try {
            deleteRow = database.delete (DBHelper.SUBSCRIBED_GROUPS_TABLE, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteRow;
    }

    public Cursor subscribeGroup (String grpName, String serverName) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, 
                    DBHelper.S_SG_SVRNAME + " = '" + serverName + "' AND " + DBHelper.S_SG_GRPNAME + " = '" + grpName + "'",
                    null, null, null, null);
            cursor.moveToFirst ();
            if (cursor.getCount () > 0) {
                do {
                    long _id = cursor.getLong (cursor.getColumnIndex (DBHelper.S_SG_ID));
                    deleteRecord (_id);
                } while (cursor.moveToNext ());                
            }
            cursor = createRecord (grpName, serverName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }
}
