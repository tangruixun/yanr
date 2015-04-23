package com.trx.yanr;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ServerDbOperator {

    private SQLiteDatabase database;
    private DBHelper serverDbHelper;

    public String [] allColumns = { DBHelper.S_SRV_ID, DBHelper.S_SRV_ADDR,
            DBHelper.S_SRV_PORT, DBHelper.S_SRV_SRVUSRNAME,
            DBHelper.S_SRV_SRVPWD, DBHelper.S_SRV_LOGINREQ,
            DBHelper.S_SRV_MYNICK, DBHelper.S_SRV_MYEMAIL,
            DBHelper.S_SRV_MYORG, DBHelper.S_SRV_MYFACE,
            DBHelper.S_SRV_MYXFACE, DBHelper.S_SRV_SSLREQ,
            DBHelper.S_SRV_TimeOut };
    
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
}
