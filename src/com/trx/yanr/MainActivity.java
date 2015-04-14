package com.trx.yanr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class MainActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    
    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;



    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static Boolean isQuit = false;
    Timer timer = new Timer ();
    
    // test:
    private String serverName = "news.eternal-september.org";
    private int port = 119;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.layout_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager ()
                .findFragmentById (R.id.navigation_drawer);
        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp (R.id.navigation_drawer,
                (DrawerLayout) findViewById (R.id.drawer_layout));
        
        
        if (savedInstanceState == null) {
            getFragmentManager ().beginTransaction ()
                    .replace (R.id.container, new SubscribedGroupFragment (serverName, port)).commit ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();
        if (id == R.id.action_groupsubscribe) {
            Intent subscribeIntent = new Intent ();
            subscribeIntent.setClass (this, AllGroupListActivity.class);
            // TODO: need dynamic change
            subscribeIntent.putExtra ("ServerName", serverName);
            subscribeIntent.putExtra ("Port", port);
            this.startActivity (subscribeIntent);
        }
        else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution ()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult (this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace ();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the user with
             * the error.
             */
            Toast.makeText (this,
                    String.valueOf (connectionResult.getErrorCode ()),
                    Toast.LENGTH_SHORT).show ();
        }        
    }

    @Override
    public void onConnected (Bundle arg0) {

        Toast.makeText (this, getText (R.string.connected), Toast.LENGTH_SHORT)
                .show ();
        // mLocationClient.requestLocationUpdates(mLocationRequest, this);
        // mCurrentLocation = mLocationClient.getLastLocation();        
    }

    @Override
    public void onDisconnected () {

        Toast.makeText (this, getText (R.string.disconnected),
                Toast.LENGTH_SHORT).show ();        
    }

    @Override
    public void onNavigationDrawerItemSelected (int position) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
     // return super.onKeyDown (keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            if (isQuit == false) {  
                isQuit = true;  
                Toast.makeText(getBaseContext(), getResources ().getString (R.string.pressagain), Toast.LENGTH_SHORT).show();  
                TimerTask task = null;  
                task = new TimerTask() {  
                    @Override  
                    public void run() {  
                        isQuit = false;  
                    }  
                };  
                timer.schedule(task, 2000);  
            } else {  
                finish();  
                System.exit(0);  
            }  
        }  
        return true;  
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    @SuppressWarnings ("unused")
    private void copyDataBase (String dbname) throws IOException {
        // Open your local db as the input stream
        InputStream myInput = this.getAssets ()
                .open (dbname);
        // Path to the just created empty db
        String outFileName = getString (R.string.db_path) + dbname;
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream (outFileName);
        // transfer bytes from the inputfile to the outputfile
        byte [] buffer = new byte [1024];
        int length;
        while ( (length = myInput.read (buffer)) > 0) {
            myOutput.write (buffer, 0, length);
        }
        // Close the streams
        myOutput.flush ();
        myOutput.close ();
        myInput.close ();
    }

    // ///////////////////////////////////////////////////////////////////
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SubscribedGroupFragment extends Fragment {

        private ListView lv;
        private SubscribeGroupCursorListAdapter subdGrpAdptr;
        private SubscribedGroupsDbOperator sbscrbdGrpDbOper;
        private String svrName;
        private int port;
        Cursor cursor;
        
        public SubscribedGroupFragment (String serverName, int port) {
            this.svrName = serverName;
            this.port = port;
            cursor = null;
        }
        
        

        @Override
        public void onCreate (Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            sbscrbdGrpDbOper = new SubscribedGroupsDbOperator (getActivity ());
        }



        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate (R.layout.frag_grouplist,
                    container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState) {
            super.onActivityCreated (savedInstanceState);
            Log.i ("--->", "onActivityCreated");

            lv = (ListView) getActivity ().findViewById (R.id.listview);
            
            try {
                sbscrbdGrpDbOper.open ();
                cursor = sbscrbdGrpDbOper.getGroupsByServer (svrName);
                
                subdGrpAdptr = new SubscribeGroupCursorListAdapter (getActivity (),
                        cursor, SubscribeGroupCursorListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
                
                SwingBottomInAnimationAdapter swingadapter = new SwingBottomInAnimationAdapter (subdGrpAdptr);
                swingadapter.setInitialDelayMillis (300); /* 刚开始进入的延长时间 */
                swingadapter.setAbsListView (lv);
                lv.setAdapter (swingadapter);

            } catch (Exception e) {
                
            }
            
            GroupItemClickListener grpSelectListener = new GroupItemClickListener ();
            lv.setOnItemClickListener (grpSelectListener);
            

            
        }

        @Override
        public void onResume () {
            sbscrbdGrpDbOper.open ();
            cursor = sbscrbdGrpDbOper.getGroupsByServer (svrName);
            Cursor newCursor = cursor;
            subdGrpAdptr.changeCursor (newCursor); // automatically closes old Cursor
            subdGrpAdptr.mCursor = newCursor;
            subdGrpAdptr.notifyDataSetChanged ();
            super.onResume ();
        }
        
        class GroupItemClickListener implements OnItemClickListener {

            @Override
            public void onItemClick (AdapterView <?> parent, View view,
                    int position, long id) {
                if (cursor != null) {
                    cursor.moveToPosition (position);
                    String grpName = cursor.getString (cursor.getColumnIndex (DBHelper.S_SG_GRPNAME));
                    Log.i ("--->", grpName);
                    
                    Intent selGrpIntent = new Intent ();
                    selGrpIntent.setClass (getActivity (), SingleGroupViewActivity.class);
                    selGrpIntent.putExtra ("ServerName", svrName);
                    selGrpIntent.putExtra ("Port", port);
                    selGrpIntent.putExtra ("GroupName", grpName);
                    getActivity ().startActivity (selGrpIntent);
                    
                }
            }
        }
    }
}
