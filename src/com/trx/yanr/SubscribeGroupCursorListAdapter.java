package com.trx.yanr;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SubscribeGroupCursorListAdapter extends CursorAdapter {
    
    public Cursor mCursor;
    private Context mContext;
    private LayoutInflater adapterInflater;
    private TagView tag;

    public SubscribeGroupCursorListAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
        mContext = context;
        mCursor = c;
        adapterInflater = LayoutInflater.from (context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        tag = new TagView ();
        View view = adapterInflater.inflate (R.layout.subscribedgrp_item, parent, false);
        
        tag.grpNameView = (TextView) view.findViewById (R.id.GroupName);
        tag.unreadAndTotalView = (TextView) view.findViewById (R.id.unreadandtotal);
        
        view.setTag (tag);
        return view;
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        tag = (TagView) view.getTag ();
        String grpName = mCursor.getString (mCursor.getColumnIndex (DBHelper.S_SG_GRPNAME));
        tag.grpNameView.setText (grpName);
    }
    
    public final class TagView {
        TextView grpNameView;
        TextView unreadAndTotalView;
    }

}
