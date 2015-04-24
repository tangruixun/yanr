package com.trx.yanr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ServerDbOperator {

    private SQLiteDatabase database;
    private DBHelper serverDbHelper;

    public String [] allColumns = { DBHelper.S_SRV_ID, DBHelper.S_SRV_ADDR,
            DBHelper.S_SRV_PORT, DBHelper.S_SRV_SRVUSRNAME,
            DBHelper.S_SRV_SRVPWD, DBHelper.S_SRV_LOGINREQ,
            DBHelper.S_SRV_MYNICK, DBHelper.S_SRV_MYEMAIL,
            DBHelper.S_SRV_MYORG, DBHelper.S_SRV_MYFACE,
            DBHelper.S_SRV_MYXFACE, DBHelper.S_SRV_SSLREQ,
            DBHelper.S_SRV_TIMEOUT };
    
    public ServerDbOperator (Context context) {
        super ();
        serverDbHelper =  new DBHelper (context);
    }
    
    public void open () throws SQLException {
        database = serverDbHelper.getWritableDatabase ();
    }
    
    public void close () throws SQLException {
        serverDbHelper.close ();
    }

    // TODO: need add more, such as create Record... etc.
    
    public Cursor createRecord (ServerData serverData) {
        String strSrvAddr = serverData.getAddress ();
        String strSrvPort = serverData.getPort ();
        String strSrvUsrName = serverData.getServerusername ();
        String strSrvPwd = serverData.getServerpassword ();
        boolean bLoginReq = serverData.isLoginrequired ();
        String strMyNick = serverData.getMynickname ();
        String strMyEmail = serverData.getMyemail ();
        String strMyOrg = serverData.getMyorganization ();
        String strMyFace = serverData.getMyface ();
        String strMyXFace = serverData.getMyxface ();
        boolean bSslReq = serverData.isSslrequired ();
        int nTimeOut = serverData.getTimeout ();
        
        return createRecord (strSrvAddr, strSrvPort, strSrvUsrName, strSrvPwd, bLoginReq, 
                strMyNick, strMyEmail, strMyOrg, strMyFace, strMyXFace, 
                bSslReq, nTimeOut);
    }

    private Cursor createRecord (String strSrvAddr, String strSrvPort,
            String strSrvUsrName, String strSrvPwd, boolean bLoginReq,
            String strMyNick, String strMyEmail, String strMyOrg,
            String strMyFace, String strMyXFace, boolean bSslReq, int nTimeOut) {
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DBHelper.S_SRV_ADDR, strSrvAddr);
        contentValues.put (DBHelper.S_SRV_PORT, strSrvPort);
        contentValues.put (DBHelper.S_SRV_SRVUSRNAME, strSrvUsrName);
        contentValues.put (DBHelper.S_SRV_SRVPWD, strSrvPwd);
        contentValues.put (DBHelper.S_SRV_LOGINREQ, bLoginReq);
        contentValues.put (DBHelper.S_SRV_MYNICK, strMyNick);
        contentValues.put (DBHelper.S_SRV_MYEMAIL, strMyEmail);
        contentValues.put (DBHelper.S_SRV_MYORG, strMyOrg);
        contentValues.put (DBHelper.S_SRV_MYFACE, strMyFace);
        contentValues.put (DBHelper.S_SRV_MYXFACE, strMyXFace);
        contentValues.put (DBHelper.S_SRV_SSLREQ, bSslReq);
        contentValues.put (DBHelper.S_SRV_TIMEOUT, nTimeOut);
        
        Cursor cursor = null;

        try {
            long insertId = database.insert (DBHelper.SERVER_TABLE, null, contentValues);
            cursor = database.query (DBHelper.SERVER_TABLE, allColumns, DBHelper.S_SRV_ID + " = " + insertId, 
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
                    Long _id = c.getLong (c.getColumnIndex (DBHelper.S_SRV_ID));
                    deleteRecord (_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void deleteRecord (Long _id) {
        try {
            // long id = record.getId();
            Log.i ("Record deleted with id: ---> ", String.valueOf (_id));
            int m = database.delete (DBHelper.SERVER_TABLE, DBHelper.S_SRV_ID
                    + " = " + _id, null);
            Log.i ("--->", m + " line(s) deleted");
        } catch (Exception e) {
            e.printStackTrace ();
        }        
    }
    
    public int clearAllRecords () {
        int deleteRow = 0;
        try {
            deleteRow = database.delete (DBHelper.SERVER_TABLE, "1", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteRow;
    }
}
