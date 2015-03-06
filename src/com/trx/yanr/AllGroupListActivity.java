package com.trx.yanr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

public class AllGroupListActivity extends Activity {
    
    private final static int MSG_ALL_GROUPS_RETRIEVED = 1; 

    private Handler mUI_handler;
    private Handler mGetAllGroups_handler;
    private HandlerThread mGetAllGroups_thread;
    private GetAllGroupsRunnable myGetAllGroupsRunnable;
    private NewsGroups allnewsgroups;
    private SparseBooleanArray selectionArray;
    private Context context; 
    
    private Bundle bundle;
    private String servername;
    private int port;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        
        context = this;
        bundle = getIntent ().getExtras ();
        servername = bundle.getString ("ServerName");
        port = bundle.getInt ("Port");
        
        allnewsgroups = new NewsGroups ();
        selectionArray = new SparseBooleanArray ();
        
        setContentView (R.layout.layout_subscribe);
        mUI_handler = new Handler () {

            @Override
            public void handleMessage (Message msg) {
                super.handleMessage (msg);
                if (msg.what == MSG_ALL_GROUPS_RETRIEVED) {
                    if (allnewsgroups != null) {
                        ListView lv = (ListView) findViewById (R.id.listview);
                        final AllGroupListAdapter grpAdptr = new AllGroupListAdapter (context, allnewsgroups);
                        lv.setAdapter (grpAdptr);
                        
                        lv.setOnItemClickListener (new OnItemClickListener() {

                            @Override
                            public void onItemClick (AdapterView <?> parent,
                                    View view, int position, long id) {
                                CheckBox ckbView = (CheckBox) view.findViewById (R.id.checkbox); // get checkbox view
                                if (grpAdptr.isChecked (position)) {
                                    grpAdptr.setSelectionArray (position, false);
                                    ckbView.setChecked (false);
                                } else {
                                    grpAdptr.setSelectionArray (position, true);
                                    ckbView.setChecked (true);
                                }
                                
                                selectionArray = grpAdptr.getSelectionArray();
                                
                                int i = 1+1;
                            }
                        });
                    }
//                    cursor = tasksDbOperator.getAllRecords ();
//                    Cursor newCursor = cursor;
//                    listItemAdapter.changeCursor (newCursor); // automatically closes old
//                                                                // Cursor
//                    listItemAdapter.mCursor = newCursor;
//                    listItemAdapter.notifyDataSetChanged ();
//                    dismissProDialog ();
                }
            }            
        };
        
        mGetAllGroups_thread = new HandlerThread ("GetAllGoups");
        mGetAllGroups_thread.start ();
        mGetAllGroups_handler = new Handler (mGetAllGroups_thread.getLooper ());
        myGetAllGroupsRunnable = new GetAllGroupsRunnable (mUI_handler);
        mGetAllGroups_handler.post (myGetAllGroupsRunnable);
    }

    @Override
    protected void onDestroy () {
        Log.i ("onDestroy --->", "onDestroy()");
        if (mGetAllGroups_handler != null) {
            mGetAllGroups_handler.removeCallbacks (myGetAllGroupsRunnable);
        }
        super.onDestroy ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.subscribe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        if (id == R.id.action_subscribe) {
            SubscribedGroupsDbOperator sgDbOpr = new SubscribedGroupsDbOperator (context);
            sgDbOpr.open ();
            String grpName;
            for (int i=0; i<allnewsgroups.size (); i++) {
                if (selectionArray.get (i) == true) {
                    grpName = allnewsgroups.elementAt (i);
                    sgDbOpr.subscribeGroup (grpName, servername);
                }
            }
            finish ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }
    
    public class GetAllGroupsRunnable implements Runnable {
        
        private Handler handler;

        public GetAllGroupsRunnable (Handler mUI_handler) {
            super ();
            this.handler = mUI_handler; // UI handler
        }

        @Override
        public void run () {
            Message msg;
            NewsOpHelper newsOpHelper = new NewsOpHelper ();
            try {
                allnewsgroups = newsOpHelper.retrieveAllGroups (servername, port);
                
                for (String group : allnewsgroups.groups) {
                    Log.i ("--->", group);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            
            msg = this.handler.obtainMessage ();
            msg.what = MSG_ALL_GROUPS_RETRIEVED;
            //msg.arg1 = 20;
            this.handler.sendMessage (msg);
        }
    }
}
