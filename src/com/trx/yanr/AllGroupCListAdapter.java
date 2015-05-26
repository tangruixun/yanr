package com.trx.yanr;

import com.trx.yanr.ServerSettingAdapter.TagView;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AllGroupCListAdapter extends CursorAdapter {
    
    public Cursor mCursor;
    private Context mContext;
    private LayoutInflater adapterInflater;
    private TagView tag;
    
    public AllGroupCListAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
        mContext = context;
        mCursor = c;
        adapterInflater = LayoutInflater.from (context);
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
        if (bSubed == 0) {
            tag.subedView.setChecked (false);
        } else {
            tag.subedView.setChecked (true);
        }
    }
    
    public final class TagView {
        TextView groupNameView;
        CheckBox subedView;
    }
}
