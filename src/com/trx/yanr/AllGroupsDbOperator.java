package com.trx.yanr;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AllGroupsDbOperator {
    
    private SQLiteDatabase database;
    private DBHelper subscribedGroupDbHelper;
    public String [] allColumns = {
            DBHelper.S_SG_ID, DBHelper.S_SG_GRPNAME, 
            DBHelper.S_SG_SVRNAME, DBHelper.S_SG_READNO, 
            DBHelper.S_SG_ARTICLENO, DBHelper.S_SG_LATESTARTICLENO, 
            DBHelper.S_SG_POSTABLE, DBHelper.S_SG_GRPDES, 
            DBHelper.S_SG_SUBED, DBHelper.S_SG_MEMO };
    
    public AllGroupsDbOperator (Context context) {
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
        Boolean bSubed = grpData.getSubscribed ();
        String strGrpMemo = grpData.getMemo ();
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, bPostable, strGrpDes, bSubed, strGrpMemo);
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
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, nLatestArticleNo, bPostable, "", false);
    }
    
    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo, Boolean bPostable, String strGrpDes, 
            Boolean bSubed) {
        return createRecord (strGrpName, strSrvName, nReadNo, nArticleNo, 
                nLatestArticleNo, bPostable, strGrpDes, bSubed, "");
    }

    public Cursor createRecord (String strGrpName, String strSrvName, int nReadNo,
            int nArticleNo, int nLatestArticleNo, Boolean bPostable, String strGrpDes, 
            Boolean bSubed, String strGrpMemo) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_SG_GRPNAME, strGrpName);
        contentValues.put (DBHelper.S_SG_SVRNAME, strSrvName);
        contentValues.put (DBHelper.S_SG_READNO, nReadNo);
        contentValues.put (DBHelper.S_SG_ARTICLENO, nArticleNo);
        contentValues.put (DBHelper.S_SG_LATESTARTICLENO, nLatestArticleNo);        
        contentValues.put (DBHelper.S_SG_POSTABLE, bPostable);
        contentValues.put (DBHelper.S_SG_GRPDES, strGrpDes);
        contentValues.put (DBHelper.S_SG_SUBED, bSubed);
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
    
    public Cursor getGroupCursorByServer (String serverAddr) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, 
                    DBHelper.S_SG_SVRNAME + " = '" + serverAddr + "'", null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public Cursor getCursorByGroupAndServer (String group, String serverAddr) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns,
                    DBHelper.S_SG_GRPNAME + " = '" + group
                    + "' AND " + DBHelper.S_SG_SVRNAME + " = '" + serverAddr + "'",
                    null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    public Cursor getSubscribeGroupsCursorByServer (String serverAddr) {
        Cursor cursor = null;
        try {
            cursor = database.query (DBHelper.SUBSCRIBED_GROUPS_TABLE, allColumns, 
                    DBHelper.S_SG_SVRNAME + " = '" + serverAddr + "' AND "
                    + DBHelper.S_SG_SUBED + " = 1 ", null, null, null, null);
            cursor.moveToFirst ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return cursor;
    }
    
    private int Unsubscribe (String group, String serverAddr) {
        Cursor cursor = null;
        int i = 0;
        
        try {
            cursor = getCursorByGroupAndServer (group, serverAddr);
            
            if (cursor != null) {
                cursor.moveToFirst ();
                int strId = cursor.getInt (cursor.getColumnIndex (DBHelper.S_SG_ID));
                ContentValues conValues = new ContentValues ();
                conValues.put (DBHelper.S_SG_SUBED, 0);
                i = database.update (DBHelper.SUBSCRIBED_GROUPS_TABLE, 
                        conValues, DBHelper.S_SG_ID + "=" + strId, null);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        
        return i;
    }

    private int Subscribe (String group, String serverAddr) {
        Cursor cursor = null;
        int i = 0;
        
        try {
            cursor = getCursorByGroupAndServer (group, serverAddr);
            
            if (cursor != null) {
                cursor.moveToFirst ();
                int strId = cursor.getInt (cursor.getColumnIndex (DBHelper.S_SG_ID));
                ContentValues conValues = new ContentValues ();
                conValues.put (DBHelper.S_SG_SUBED, 1);
                i = database.update (DBHelper.SUBSCRIBED_GROUPS_TABLE, 
                        conValues, DBHelper.S_SG_ID + "=" + strId, null);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return i;
    }
    
    public Boolean isGroupNameInDb (String group, String serverAddr) {
        Cursor c = null;
        Boolean result = false;
        try {
            c = getCursorByGroupAndServer (group, serverAddr);
            if (c != null) {
                if (c.moveToFirst () && c.getCount () > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return result;
    }
    
    public void deleteRecord (String group, String serverAddr) {
        try {
            Cursor c = getCursorByGroupAndServer (group, serverAddr);
            c.moveToFirst ();
            deleteRecord (c);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public void deleteAllRecordsByServerName (String serverAddr) {
        try {
            Cursor c = getGroupCursorByServer (serverAddr);
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

    public void addOnlyNewGroup (String group, String serverAddr) {
        Cursor c = null;
        try {
            c = getCursorByGroupAndServer (group, serverAddr);
            if (c != null) {
                if (c.getCount () == 0) { // no this group record in db
                    createRecord (group, serverAddr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close ();
        }
    }
    
    // grpList: all new subscribed groups
    public int subscribeGroup (ArrayList <String> grpList, String serverAddr) {
        ArrayList <String> newAddedGrpList = grpList;
        ArrayList <String> grpNeedUnsubscribed = new ArrayList <String> ();
        Cursor cursor = null;
        try {            
            cursor = getSubscribeGroupsCursorByServer (serverAddr); // group already subscribed
            cursor.moveToFirst ();
            if (cursor.getCount () > 0) {
                do {
                    String subedGrpName = cursor.getString (cursor.getColumnIndex (DBHelper.S_SG_GRPNAME)); // old subscribed group
                    if (newAddedGrpList.contains (subedGrpName)) {
                        // newAddedGroup = all new subscribed groups - remove groups already subscribed
                        newAddedGrpList.remove (subedGrpName); 
                    } else {
                        // add old groups not in the new subscribed group list to the list that groups needed to be unsubscribed.
                        grpNeedUnsubscribed.add (subedGrpName); 
                    }
                } while (cursor.moveToNext ());
                
                for (String grpName : grpNeedUnsubscribed) {
                    Unsubscribe (grpName, serverAddr); // unsubscribe groups in db
                }
                for (String grpName : newAddedGrpList) {
                    Subscribe (grpName, serverAddr); // subscribe new groups in db
                }
                return 0;
            } else if (cursor.getCount () == 0) {
                for (String grpName : newAddedGrpList) {
                    createRecord (grpName, serverAddr); // subscribe new groups in db
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
