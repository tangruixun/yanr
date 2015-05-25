package com.trx.yanr;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ServerSettingAdapter extends CursorAdapter {

    public Cursor mCursor;
    private Context mContext;
    private LayoutInflater adapterInflater;
    private TagView tag;
    
    public ServerSettingAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
        mContext = context;
        mCursor = c;
        adapterInflater = LayoutInflater.from (context);
    }
    
    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        tag = new TagView ();
        View view = adapterInflater.inflate (R.layout.server_item, parent, false);
        tag.serverTextView = (TextView) view.findViewById (R.id.servertext);
        view.setTag (tag);
        return view;
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        tag = (TagView) view.getTag ();
        String serverText = "";
        try {
            String serverAddr = mCursor.getString (mCursor.getColumnIndex (DBHelper.S_SRV_ADDR));
            String serverPort = mCursor.getString (mCursor.getColumnIndex (DBHelper.S_SRV_PORT));
            
            serverText = serverAddr + ":" + serverPort;
        } catch (Exception e) {
            e.printStackTrace ();
        }
        
        tag.serverTextView.setText (serverText);
    }
    
    public final class TagView {
        TextView serverTextView;
    }
}
