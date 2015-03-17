package com.trx.yanr;

import javax.mail.MessagingException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class NewsViewActivity extends Activity {

    private Bundle bundle;
    private String svrName;
    private int port;
    private String grpName;
    private int articleNo;
    private Context context;

    private TextView subjectView, fromView, newsgroupView, dateView;
    private TextView bodyView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.layout_newsview);

        context = this;

        String strSubject, strFrom, strNewsgroup, strDate, strBody;
        NNTPMessageHeader header = new NNTPMessageHeader ();
        HeaderDbOperator headerDbOptr = new HeaderDbOperator (context);
        BodyDbOperator bodyDbOptr = new BodyDbOperator (context);
        headerDbOptr.open ();
        bodyDbOptr.open ();

        bundle = getIntent ().getExtras ();
        svrName = bundle.getString ("ServerName");
        port = bundle.getInt ("Port");
        grpName = bundle.getString ("GroupName");
        articleNo = bundle.getInt ("ArticleNo");

        subjectView = (TextView) findViewById (R.id.subjecttext);
        fromView = (TextView) findViewById (R.id.fromtext);
        newsgroupView = (TextView) findViewById (R.id.newsgrouptext);
        dateView = (TextView) findViewById (R.id.datetext);
        bodyView = (TextView) findViewById (R.id.bodytext);
        bodyView.setMovementMethod (new ScrollingMovementMethod ());

        try {
            // header
            Cursor cHeader = headerDbOptr.getRecordByArticleId (grpName, svrName,
                    articleNo);
            String headerText = cHeader.getString (cHeader.getColumnIndex (DBHelper.S_H_HEADERTEXT));
            header = NNTPMessageHeader.cvrtTxt (headerText);
            strSubject = header.getSubject ();
            strFrom = header.getFrom ();
            strNewsgroup = grpName;
            strDate = header.getDate ();
        
            // body
            Cursor cBody = bodyDbOptr.getRecordByArticleId (grpName, svrName, articleNo);
            strBody = cBody.getString (cBody.getColumnIndex (DBHelper.S_B_BODYTEXT));
            
            subjectView.setText (strSubject); 
            fromView.setText (strFrom); 
            newsgroupView.setText (strNewsgroup); 
            dateView.setText (strDate);
            bodyView.setText (strBody);
        } catch (MessagingException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu (menu);
        getMenuInflater ().inflate (R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        return super.onOptionsItemSelected (item);
    }

}
