package com.trx.yanr;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class SingleGroupViewActivity extends Activity {

    private final static int MSG_RETRIEVE_HEADERS_COMPLETE = 1;
    private final static int MSG_RETRIEVE_BODYS_COMPLETE = 2;
    private final static int MSG_RETRIEVE_HEADERS_INCOMPLETE = 3;
    public final static int MSG_RETRIEVE_HEADERS_PROGRESS = 5;
    
    
    private Handler mUI_handler;
    private Handler mGetHeader_handler, mGetBody_handler;
    private HandlerThread mGetHeader_thread, mGetBody_thread;
    private GetHeaderRunnable myGetHeaderRunnable;
    private GetBodyRunnable myGetBodyRunnable;
    private Context context;
    
    private ProgressDialog pDialog;
    
    private Bundle bundle;
    private String svrName;
    private int port;
    private String grpName;
    private SingleGroupViewAdapter listItemAdapter;
    private HeaderDbOperator headDbOptr;
    private BodyDbOperator bodyDbOptr;
    private Cursor cursor;

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        if (bundle != null) {
            outState = bundle;
        }
        super.onSaveInstanceState (outState);
    }
    
    @Override
    protected void onPause () {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences (this);
        Editor prefEditor = mySharedPreferences.edit ();
        prefEditor.putString ("TempServerName", svrName);
        prefEditor.putInt ("TempPort", port);
        prefEditor.putString ("TempGroupName", grpName);
        prefEditor.apply ();
        super.onPause ();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.layout_single_group_view);
        
        context = this;
        pDialog = new ProgressDialog (context);

        if (bundle == null) {
            if (savedInstanceState != null) {
                bundle = savedInstanceState; // get saved bundle in onSaveInstanceState ()
            } else {
                bundle = getIntent ().getExtras ();
            }
        }

        if (bundle != null) {
            svrName = bundle.getString ("ServerName");
            port = bundle.getInt ("Port");
            grpName = bundle.getString ("GroupName");
        } else {
            SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences (this);
            svrName = mySharedPreferences.getString ("TempServerName", "");
            port = mySharedPreferences.getInt ("TempPort", 119);
            grpName = mySharedPreferences.getString ("TempGroupName", "");
        }
        
        ActionBar actionBar = getActionBar ();
        actionBar.setTitle (grpName);
        actionBar.setDisplayShowTitleEnabled (true);
        actionBar.setDisplayHomeAsUpEnabled (true);
        
        mGetHeader_thread = new HandlerThread ("GetGroupHeaders");
        mGetHeader_thread.start ();
        mGetHeader_handler = new Handler (mGetHeader_thread.getLooper ());
        
        mGetBody_thread = new HandlerThread ("GetGroupBodys");
        mGetBody_thread.start ();
        mGetBody_handler = new Handler (mGetBody_thread.getLooper ());
        
        headDbOptr = new HeaderDbOperator (context);
        bodyDbOptr = new BodyDbOperator (context);
        headDbOptr.open ();
        bodyDbOptr.open ();
        cursor = headDbOptr.getAllRecordByGroup (grpName, svrName);

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
                        if (!bodyDbOptr.isNumberExisted (grpName, svrName, articleNo)) {
                            // record not exist in Body Table
                            showProDialog (0, 100, 100);
                            myGetBodyRunnable = new GetBodyRunnable (articleNo);
                            mGetBody_handler.post (myGetBodyRunnable);
                            
                        } else { // go to NewsViewActivity directly
                            dismissProDialog ();
                            Intent newsIntent = new Intent ();
                            newsIntent.setClass (context, NewsViewActivity.class);
                            newsIntent.putExtra ("ArticleNo", articleNo);
                            newsIntent.putExtra ("ServerName", svrName);
                            newsIntent.putExtra ("Port", port);
                            newsIntent.putExtra ("GroupName", grpName);
                            startActivity (newsIntent);
                        }
                    }
                }
            });
        }
        
        // display article title in this Group;
        mUI_handler = new Handler (new Handler.Callback() {

            @Override
            public boolean handleMessage (Message msg) {
                dismissProDialog ();

                if (msg.what == MSG_RETRIEVE_HEADERS_COMPLETE) {

                    cursor = headDbOptr.getAllRecordByGroup (grpName, svrName);
                    Cursor newCursor = cursor;
                    listItemAdapter.changeCursor (newCursor); // automatically closes old Cursor
                    listItemAdapter.mCursor = newCursor;
                    listItemAdapter.notifyDataSetChanged ();
                } else if (msg.what == MSG_RETRIEVE_BODYS_COMPLETE) {
                    int articleNo = msg.arg1;
                    Intent newsIntent = new Intent ();
                    newsIntent.setClass (context, NewsViewActivity.class);
                    newsIntent.putExtra ("ArticleNo", articleNo);
                    newsIntent.putExtra ("ServerName", svrName);
                    newsIntent.putExtra ("Port", port);
                    newsIntent.putExtra ("GroupName", grpName);
                    startActivity (newsIntent);
                } else if (msg.what == MSG_RETRIEVE_HEADERS_INCOMPLETE) {
                    // some error happened

                } else if (msg.what == MSG_RETRIEVE_HEADERS_PROGRESS) {
                    // progress bar animation
                    showProDialog (1, msg.arg1, msg.arg2);
                }
                return true;
            }
        });
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
            showProDialog(1, 100, 100);
            getGroupArticleNumbers ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }
    
    private void getGroupArticleNumbers () {
        myGetHeaderRunnable = new GetHeaderRunnable ();
        mGetHeader_handler.post (myGetHeaderRunnable);
    }

    public class GetHeaderRunnable implements Runnable {
        private Handler handler;
        private NntpOpHelper nntpOpHelper;
        Message msg;

        public GetHeaderRunnable () {
            super ();            
            this.handler = mUI_handler; // UI handler
            nntpOpHelper = new NntpOpHelper ();
        }        

        @Override
        public void run () {

            try {
                HeaderDbOperator headerDbOptr = new HeaderDbOperator (context);
                //HeaderDbOperator headerDbOptr = new HeaderDbOperator (context);
                headerDbOptr.open ();
                // get latest Article number in the database by group name
                int lastArticleNoinDb = headerDbOptr.getLatestArticleNoinDb (grpName, svrName);
                
                // String allArticleNumbers = newsOpHelper.retrieveArticleNumbers (svrName, port, grpName);
                // TODO: need fix to only retrieving new headers
                //List <SparseArray<NNTPMessageHeader>> headerList = newsOpHelper.retrieveAllHeaders (svrName, port, grpName);
                int getRusult = nntpOpHelper.retrieveNewHeaders (headerDbOptr, svrName, port, grpName, lastArticleNoinDb, handler);
                
                if (getRusult == 0) {
                    msg = this.handler.obtainMessage ();
                    msg.what = MSG_RETRIEVE_HEADERS_COMPLETE;
                    this.handler.sendMessage (msg);
                } else {
                    msg = this.handler.obtainMessage ();
                    msg.what = MSG_RETRIEVE_HEADERS_INCOMPLETE;
                    this.handler.sendMessage (msg);
                }


//                for (SparseArray <NNTPMessageHeader> idHeaderMap : newHeaderList) {
//                    
//                    int articleNo;
//                    NNTPMessageHeader headers;
//                    articleNo = idHeaderMap.keyAt (0);
//                    Log.i ("--->", articleNo + "");
//                    headers = idHeaderMap.get (articleNo);
//
//                    if (!headerDbOptr.isNumberExisted (grpName, svrName, articleNo)) {
//                        headerDbOptr.createRecord (grpName, svrName, articleNo, headers);
//                    }
//                }

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
                

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }

    public class GetBodyRunnable implements Runnable {
        private Handler handler;
        private int articleNo;
        private NntpOpHelper nntpOpHelper;

        public GetBodyRunnable (int articleNumber) {
            super ();
            this.handler = mUI_handler; // UI handler
            articleNo = articleNumber;
            nntpOpHelper = new NntpOpHelper ();
        }

        @Override
        public void run () {
            Message msg;
            try {
                String articleBody = nntpOpHelper.retrieveBodyText (svrName, port, grpName, articleNo);
                
                BodyDbOperator bodyDbOptr = new BodyDbOperator (context);
                bodyDbOptr.open ();
                bodyDbOptr.createRecord (grpName, svrName, articleNo, articleBody);
                
                
                
                msg = this.handler.obtainMessage ();
                msg.what = MSG_RETRIEVE_BODYS_COMPLETE;
                msg.arg1 = articleNo;
                this.handler.sendMessage (msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void onDestroy () {
        Log.i ("onDestroy --->", "onDestroy()");
        cursor.close ();
        if (mGetHeader_handler != null) {
            mGetHeader_handler.removeCallbacks (myGetHeaderRunnable);
        }
        
        if (mGetBody_handler != null) {
            mGetBody_handler.removeCallbacks (myGetBodyRunnable);
        }
        
        if (mGetBody_thread != null) {
            mGetBody_thread.quit ();
        }
        
        if (mGetHeader_thread != null) {
            mGetHeader_thread.quit ();
        }
        
        super.onDestroy ();
    }

    // style
    // process
    private void showProDialog (int style, int process, int max) {
        // Showing progress dialog
        pDialog.setTitle(getString (R.string.pleasewait));
        pDialog.setMessage (getString (R.string.pleasewaitlong));

        if (style == 0) {
            pDialog.setIndeterminate(true);
        } else {
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setIndeterminate(false);
            pDialog.setMax(max);
        }
        pDialog.setCancelable (false);
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
