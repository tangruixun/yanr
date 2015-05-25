package com.trx.yanr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DynamicServerList___ extends ListView {
    
    private ServerDbOperator svrDbOptr;
    private Context context;
    private Cursor c = null;
    
    public DynamicServerList___(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAdapter(adapter());
    }

    public DynamicServerList___(Context context) {
        super(context);
        this.context = context;
    }

    private ListAdapter adapter() {
        return new ArrayAdapter <String> (getContext(), android.R.layout.simple_list_item_1, entries());
    }

    private String[] entries() {
        //action to provide entry data in char sequence array for list
        List <String> entryTextArray = new ArrayList <String> ();
        svrDbOptr = new ServerDbOperator (context);
        svrDbOptr.open ();
        c = svrDbOptr.getAllServers ();
        if (c != null) {
            if (c.getCount () > 0) {
                c.moveToFirst ();
                do {
                    String addr = c.getString (c.getColumnIndex (DBHelper.S_SRV_ADDR));
                    String port = c.getString (c.getColumnIndex (DBHelper.S_SRV_PORT));
                    String entryText = addr + ":" + port;
                    entryTextArray.add (entryText);
                } while (c.moveToNext ());
            }
        }
        return entryTextArray.toArray (new String [entryTextArray.size()]);
    }
}
