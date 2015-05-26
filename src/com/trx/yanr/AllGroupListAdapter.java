//package com.trx.yanr;
//
//import android.content.Context;
//import android.util.SparseBooleanArray;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//public class AllGroupListAdapter extends BaseAdapter {
//
//    private TagView tag;
//    private NewsGroups _newsgroups;
//    private LayoutInflater mAdapterInflater;
//    
//    private SparseBooleanArray selectionArray;
//
//    public AllGroupListAdapter (Context context, NewsGroups newsgroups) {
//        super ();
//        mAdapterInflater = LayoutInflater.from (context);
//        _newsgroups = newsgroups;
//        
//        selectionArray = new SparseBooleanArray ();
//        for(int i=0; i<_newsgroups.size(); i++) {
//            selectionArray.put(i, false);
//        }
//    }
//    
//    public SparseBooleanArray getSelectionArray() {
//        return selectionArray;
//    }
//    
//    public void setSelectionArray (int position, Boolean checked) {
//        selectionArray.put (position, checked);
//    }
//    
//    public Boolean isChecked (int position) {
//        if (selectionArray.get (position) == false) {
//            return false;
//        } else {
//            return true;
//        }        
//    }
//
//    @Override
//    public int getCount () {
//        return _newsgroups.size ();
//    }
//
//    @Override
//    public Object getItem (int position) {
//        return _newsgroups.elementAt (position);
//    }
//
//    @Override
//    public long getItemId (int position) {
//        return position;
//    }
//
//    @Override
//    public View getView (int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = mAdapterInflater.inflate (R.layout.allgrouplist_item, parent, false);
//            tag = new TagView ();
//            tag.groupNameV = (TextView) convertView.findViewById (R.id.GroupName);
//            tag.checkBox = (CheckBox) convertView.findViewById (R.id.checkbox);
//            convertView.setTag (tag);
//        } else {
//            tag = (TagView) convertView.getTag ();
//        }
//        
//        tag.groupNameV.setText (_newsgroups.elementAt (position));
//        tag.checkBox.setChecked (selectionArray.get(position));
//        return convertView;
//    }
//    
//    public class TagView {
//        TextView groupNameV;
//        CheckBox checkBox;
//    }
//}
