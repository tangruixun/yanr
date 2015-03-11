package com.trx.yanr;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;

public class SingleGroupViewActivity extends Activity {

    private final static int MSG_RETRIEVE_ARTICLES_COMPLETE = 1;
    
    private Handler mUI_handler;
    private Handler mGetArticles_handler;
    private HandlerThread mGetArticles_thread;
    private GetArticlesRunnable myGetArticlesRunnable;
    private Context context;
    
    private Bundle bundle;
    private String svrName;
    private int port;
    private String grpName;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.layout_single_group_view);
        
        bundle = getIntent ().getExtras ();
        svrName = bundle.getString ("ServerName");
        port = bundle.getInt ("Port");
        grpName = bundle.getString ("GroupName");
        
        // TODO: display article title in this Group;
        mUI_handler = new Handler () {

            @Override
            public void handleMessage (Message msg) {
                super.handleMessage (msg);
                if (msg.what == MSG_RETRIEVE_ARTICLES_COMPLETE) {
                    
                }
            }
        };
        


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
        mGetArticles_thread = new HandlerThread ("GetGroupArticles");
        mGetArticles_thread.start ();
        mGetArticles_handler = new Handler (mGetArticles_handler.getLooper ());
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
                newsOpHelper.retrieveArticleNumbers (svrName, port, grpName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
}
