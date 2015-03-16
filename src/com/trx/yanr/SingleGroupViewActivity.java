package com.trx.yanr;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class SingleGroupViewActivity extends Activity {

    private final static int MSG_RETRIEVE_ARTICLES_COMPLETE = 1;
    
    private Handler mUI_handler;
    private Handler mGetArticles_handler;
    private HandlerThread mGetArticles_thread;
    private GetArticlesRunnable myGetArticlesRunnable;
    private Context context;
    
    private ProgressDialog pDialog;
    
    private Bundle bundle;
    private String svrName;
    private int port;
    private String grpName;
    private SingleGroupViewAdapter listItemAdapter;
    private HeaderDbOperator headDbOptr;
    private Cursor cursor;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.layout_single_group_view);
        
        context = this;
        pDialog = new ProgressDialog (context);
        
        bundle = getIntent ().getExtras ();
        svrName = bundle.getString ("ServerName");
        port = bundle.getInt ("Port");
        grpName = bundle.getString ("GroupName");
        
        // TODO: display article title in this Group;
        mUI_handler = new Handler (new Handler.Callback() {

            @Override
            public boolean handleMessage (Message msg) {
                if (msg.what == MSG_RETRIEVE_ARTICLES_COMPLETE) {
                    dismissProDialog ();
                }
                return true;
            }
        });
        
        headDbOptr = new HeaderDbOperator (context);
        headDbOptr.open ();
        cursor = headDbOptr.getRecordByGroup (grpName, svrName);
        if (cursor != null) {
            listItemAdapter = new SingleGroupViewAdapter (context, cursor,
                    SingleGroupViewAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            SwingBottomInAnimationAdapter swingadapter = new SwingBottomInAnimationAdapter (
                    listItemAdapter);
            ListView lv = (ListView) findViewById (R.id.listview);
            swingadapter.setInitialDelayMillis (300); /* 刚开始进入的延长时间 */
            swingadapter.setAbsListView (lv);
            lv.setAdapter (swingadapter);
            lv.setOnItemClickListener (new OnItemClickListener() {

                @Override
                public void onItemClick (AdapterView <?> parent, View view,
                        int position, long id) {
                    if (cursor != null) {
                        cursor.moveToPosition (position);
                        int articleNo = cursor.getInt (cursor.getColumnIndex (DBHelper.S_H_ARTICLENO));
                        Intent newsIntent = new Intent ();
                        newsIntent.setClass (context, NewsViewActivity.class);
                        newsIntent.putExtra ("ArticleNo", articleNo);
                        startActivity (newsIntent);
                    }                    
                }
            }); 
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.single_group_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        if (id == R.id.action_retrieve_articles) {
            getGroupArticleNumbers ();

            return true;
        }
        return super.onOptionsItemSelected (item);
    }
    
    private void getGroupArticleNumbers () {
        showProDialog(0, 100);
        
        mGetArticles_thread = new HandlerThread ("GetGroupArticles");
        mGetArticles_thread.start ();
        mGetArticles_handler = new Handler (mGetArticles_thread.getLooper ());
        myGetArticlesRunnable = new GetArticlesRunnable (mUI_handler);
        mGetArticles_handler.post (myGetArticlesRunnable);
    }

    public class GetArticlesRunnable implements Runnable {
        private Handler handler;

        public GetArticlesRunnable (Handler mUI_handler) {
            super ();            
            this.handler = mUI_handler; // UI handler
        }        

        @Override
        public void run () {
            Message msg;
            NewsOpHelper newsOpHelper = new NewsOpHelper ();
            try {
                // String allArticleNumbers = newsOpHelper.retrieveArticleNumbers (svrName, port, grpName);
                List <SparseArray<NNTPMessageHeader>> headerList = newsOpHelper.retrieveAllHeaders (svrName, port, grpName);
                HeaderDbOperator headerDbOptr = new HeaderDbOperator (context);
                //HeaderDbOperator headerDbOptr = new HeaderDbOperator (context);
                headerDbOptr.open ();
                for (SparseArray <NNTPMessageHeader> idHeaderMap : headerList) {
                    
                    int articleNo;
                    NNTPMessageHeader headers;
                    articleNo = idHeaderMap.keyAt (0);
                    headers = idHeaderMap.get (articleNo);

                    if (!headerDbOptr.isNumberExisted (grpName, svrName, articleNo)) {
                        headerDbOptr.createRecord (grpName, svrName, articleNo, headers);
                    }
                }

//                String [] everyLinesArray = allArticleNumbers.split ("\\r?\\n");
//                ArticleNoDbOperator artlNoDbOptr = new ArticleNoDbOperator (context);
//                artlNoDbOptr.open ();
//                for (String strArticleNo : everyLinesArray) {
//                    int articleNo = Integer.valueOf (strArticleNo);
//                    if (!artlNoDbOptr.isNumberExisted (grpName, svrName, articleNo)) {
//                        artlNoDbOptr.createRecord (grpName, svrName, articleNo);
//
//                    }
//                }

                
                msg = this.handler.obtainMessage ();
                msg.what = MSG_RETRIEVE_ARTICLES_COMPLETE;
                this.handler.sendMessage (msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    
    // style
    // process
    private void showProDialog (int style, int process) {
        // Showing progress dialog
        pDialog.setTitle(getString (R.string.pleasewait));
        pDialog.setMessage (getString (R.string.pleasewaitlong));

        if (style == 0) {
            pDialog.setIndeterminate(true);
        } else {
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
        }
        pDialog.setCancelable (true);
        if (process == 0) {
            process = 7;
        }
        pDialog.setProgress(process);
        pDialog.show ();
    }

    private void dismissProDialog () {
        if (pDialog.isShowing ())
            pDialog.dismiss ();
    }
}
