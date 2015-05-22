package com.trx.yanr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DynamicListPreference extends ListPreference {
    
    private ServerDbOperator svrDbOptr;
    private Context context;
    private Cursor c = null;
    
    public DynamicListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DynamicListPreference(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View onCreateDialogView() {
        svrDbOptr = new ServerDbOperator (context);
        svrDbOptr.open ();
        c = svrDbOptr.getAllServers ();
        
        
        ListView view = new ListView(getContext());
        view.setAdapter(adapter());
        setEntries(entries());
        setEntryValues(entryValues());
        //setValueIndex(initializeIndex());
        return view;
    }

    private ListAdapter adapter() {
        return new ArrayAdapter(getContext(), android.R.layout.simple_list_item_single_choice);
    }

    private CharSequence[] entries() {
        //action to provide entry data in char sequence array for list
        List <String> entryTextArray = new ArrayList <String> ();
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
        return entryTextArray.toArray (new CharSequence[entryTextArray.size()]);
    }

    private CharSequence[] entryValues() {
        //action to provide value data for list
        List <String> entryTextArray = new ArrayList <String> ();
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
        return entryTextArray.toArray (new CharSequence[entryTextArray.size()]);
    }
    
    private int initializeIndex () {
        return 0;
    }
}
