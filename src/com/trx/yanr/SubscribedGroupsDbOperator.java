package com.trx.yanr;

import java.util.ArrayList;

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
            DBHelper.S_SG_ARTICLENO, DBHelper.S_SG_LATESTARTICLENO, DBHelper.S_SG_POSTABLE, 
            DBHelper.S_SG_GRPDES, DBHelper.S_SG_MEMO };
    
    public SubscribedGroupsDbOperator (Context context) {
        super ();
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
        int nLatestArticleNo = grpData.getLatestArticleNumber ();
        Boolean bPostable = grpData.getPostable ();
        String strGrpDes = grpData.getGroupDes ();
        String strGrpMemo = grpData.getMemo ();
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, bPostable, strGrpDes, strGrpMemo);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName) {
        return createRecord (strGrpName, strSrvName, -1, -1, -1);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, true);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo, Boolean bPostable) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, bPostable, "");
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo, Boolean bPostable, String strGrpDes) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, bPostable, strGrpDes, "");
    }

    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo, Boolean bPostable, String strGrpDes, String strGrpMemo) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_SG_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_SG_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_SG_READNO, nReadNo);
        contentValues.put (DBHelper.S_SG_ARTICLENO, nArticleNo);
        contentValues.put (DBHelper.S_SG_LATESTARTICLENO, nLatestArticleNo);        
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
    
    private void deleteRecord (String grpName, String serverName) {
        try {
            Cursor c = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, 
                    DBHelper.S_SG_SVRNAME + " = '" + serverName + "' AND " + DBHelper.S_SG_GRPNAME + " = '" + grpName + "'", 
                    null, null, null, null);
            c.moveToFirst ();
            deleteRecord (c);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    private void deleteRecord (Cursor c) {
        try {
            if (c!=null) {
                if (c.getCount () > 0) {
                    Long _id = c.getLong (c.getColumnIndex (DBHelper.S_SG_ID));
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

    // grpList: all new subscribed groups
    public int subscribeGroup (ArrayList <String> grpList, String serverName) {
        ArrayList <String> newAddedGrpList = grpList;
        ArrayList <String> grpNeedUnsubscribed = new ArrayList <String> ();
        Cursor cursor = null;
        try {            
            cursor = getGroupsByServer (serverName); 
            cursor.moveToFirst ();
            if (cursor.getCount () > 0) {
                do {
                    String subedGrpName = cursor.getString (cursor.getColumnIndex (DBHelper.S_SG_GRPNAME)); // old subscribed group
                    if (newAddedGrpList.contains (subedGrpName)) {
                        // newAddedGroup = all new subscribed groups - remove groups already subscribed
                        newAddedGrpList.remove (subedGrpName); 
                    } else {
                        // add old groups not in the new subscribed group list to the list that groups needed to be removed.
                        grpNeedUnsubscribed.add (subedGrpName); 
                    }
                } while (cursor.moveToNext ());
                
                for (String grpName : grpNeedUnsubscribed) {
                    deleteRecord (grpName, serverName); // unsubscribe groups in db
                }
                for (String grpName : newAddedGrpList) {
                    createRecord (grpName, serverName); // subscribe new groups in db
                }
                return 0;
            } else if (cursor.getCount () == 0) {
                for (String grpName : newAddedGrpList) {
                    createRecord (grpName, serverName); // subscribe new groups in db
                }
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 2;
    }
}
