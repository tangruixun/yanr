package com.trx.yanr;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AllGroupCListAdapter extends CursorAdapter {
    
    public Cursor mCursor;
    private LayoutInflater adapterInflater;
    private TagView tag;
    private SparseBooleanArray selectionArray = new SparseBooleanArray ();

    public AllGroupCListAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
        mCursor = c;
        adapterInflater = LayoutInflater.from (context);
        for (int i = 0; i < this.getCount(); i++) {
            selectionArray.put (i, false); // initializes all items value with false
        }
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        tag = new TagView ();
        View view = adapterInflater.inflate (R.layout.allgrouplist_item, parent, false);
        tag.groupNameView = (TextView) view.findViewById (R.id.GroupName);
        tag.subedView = (CheckBox) view.findViewById (R.id.checkbox);
        view.setTag (tag);
        return view;
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        tag = (TagView) view.getTag ();
        String groupNameText = "";
        int bSubed = 0;
        try {
            groupNameText = mCursor.getString (mCursor.getColumnIndex (DBHelper.S_SG_GRPNAME));
            bSubed = mCursor.getInt (mCursor.getColumnIndex (DBHelper.S_SG_SUBED));
        } catch (Exception e) {
            e.printStackTrace ();
        }
        
        tag.groupNameView.setText (groupNameText);
        tag.subedView.setChecked (selectionArray.get (cursor.getPosition ()));
    }
    
    public boolean isChecked (int position) {
        if (selectionArray.get (position) == false) {
            return false;
        } else {
            return true;
        }
    }
    
    public void setSelectionArray (int position, boolean checked) {
        selectionArray.put(position, checked);
    }
    
    public SparseBooleanArray getSelectionArray () {
        return selectionArray;
    }
    
    @Override
    public int getCount () {
        return mCursor.getCount ();
    }

    public final class TagView {
        TextView groupNameView;
        CheckBox subedView;
    }
}
