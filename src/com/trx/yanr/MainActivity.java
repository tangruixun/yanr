package com.trx.yanr;

import java.util.Timer;
import java.util.TimerTask;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;

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
                    .replace (R.id.container, SubscribedGroupFragment.newInstance (serverName, port)).commit ();
        }
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

    @Override
    protected void onPause () {
        super.onPause ();
        // TODO: remember selected server and port  

    }

    @Override
    protected void onResume () {
        super.onResume ();
        // TODO: restore selected server and port  

    }
    
    
}
