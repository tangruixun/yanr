package com.trx.yanr;

import javax.mail.MessagingException;

import com.trx.yanr.SubscribeGroupCursorListAdapter.TagView;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SingleGroupViewAdapter extends CursorAdapter {

    public Cursor mCursor;
    private Context mContext;
    private LayoutInflater adapterInflater;
    private TagView tag;
    
    public SingleGroupViewAdapter (Context context, Cursor c, int flags) {
        super (context, c, flags);
        mContext = context;
        mCursor = c;
        adapterInflater = LayoutInflater.from (context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        tag = new TagView ();
        View view = adapterInflater.inflate (R.layout.postsubject_item, parent, false);
        
        tag.subjectView = (TextView) view.findViewById (R.id.PostSubject);
        tag.authorView = (TextView) view.findViewById (R.id.PostFrom);
        tag.dateView = (TextView) view.findViewById (R.id.PostDateTime);
        
        view.setTag (tag);        
        return view;
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        tag = (TagView) view.getTag ();
        String subject = "";
        String author = "";
        String dateTime = "";
        try {
            String headerText = mCursor.getString (mCursor.getColumnIndex (DBHelper.S_H_HEADERTEXT));
            NNTPMessageHeader nntpHeader = NNTPMessageHeader.cvrtTxt (headerText);
            subject = nntpHeader.getSubject ();
            author = nntpHeader.getFrom ();
            dateTime = nntpHeader.getDate ();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tag.subjectView.setText (subject);
        tag.authorView.setText (author);
        tag.dateView.setText (dateTime);

    }
    
    public final class TagView {
        TextView subjectView;
        TextView authorView;
        TextView dateView;
    }

}
