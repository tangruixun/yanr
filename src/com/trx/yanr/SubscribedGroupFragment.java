package com.trx.yanr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class SubscribedGroupFragment extends Fragment {
    private ListView lv;
    private SubscribeGroupCursorListAdapter subdGrpAdptr;
    private AllGroupsDbOperator sbscrbdGrpDbOper;
    private String serverName;
    private int port;
    private Context context;
    Cursor cursor = null;
    Bundle bundle;

    public static final SubscribedGroupFragment newInstance (String serverName,
            int port) {
        SubscribedGroupFragment f = new SubscribedGroupFragment ();
        Bundle bdl = new Bundle (2);
        bdl.putString ("ServerName", serverName);
        bdl.putInt ("Port", port);
        f.setArguments (bdl);
        return f;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach (activity);
        context = activity;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (bundle == null && savedInstanceState != null) {
            bundle = savedInstanceState;
            serverName = bundle.getString ("ServerName");
            port = bundle.getInt ("Port");
        } else {
            serverName = getArguments ().getString ("ServerName");
            port = getArguments ().getInt ("Port");
        }
        sbscrbdGrpDbOper = new AllGroupsDbOperator (getActivity ());
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.frag_grouplist, container,
                false);
        setHasOptionsMenu (true);
        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        Log.i ("--->", "onActivityCreated");

        lv = (ListView) getActivity ().findViewById (R.id.listview);

        try {
            sbscrbdGrpDbOper.open ();
            cursor = sbscrbdGrpDbOper.getSubscribeGroupsCursorByServer (serverName);

            subdGrpAdptr = new SubscribeGroupCursorListAdapter (
                    getActivity (),
                    cursor,
                    SubscribeGroupCursorListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

            SwingBottomInAnimationAdapter swingadapter = new SwingBottomInAnimationAdapter (
                    subdGrpAdptr);
            swingadapter.setInitialDelayMillis (300); /* 刚开始进入的延长时间 */
            swingadapter.setAbsListView (lv);
            lv.setAdapter (swingadapter);

        } catch (Exception e) {
            e.printStackTrace ();
        }

        GroupItemClickListener grpSelectListener = new GroupItemClickListener ();
        lv.setOnItemClickListener (grpSelectListener);

    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
        outState.putString ("ServerName", serverName);
        outState.putInt ("Port", port);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu (menu, inflater);
        inflater.inflate (R.menu.main, menu);

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId ();
        switch (id) {
        case R.id.action_groupsubscribe:
            Intent subscribeIntent = new Intent ();
            subscribeIntent.setClass (context, AllGroupListActivity.class);
            // TODO: need dynamic change
            subscribeIntent.putExtra ("ServerName", serverName);
            subscribeIntent.putExtra ("Port", port);
            this.startActivity (subscribeIntent);
            break;

        case R.id.action_refresh:
            refreshGroups ();
            break;

        case R.id.action_settings:
            // return true;
            Intent settingIntent = new Intent ();
            settingIntent.setClass (context, SettingPrefActivity.class);
            this.startActivity (settingIntent);
            break;

        case R.id.action_rebuilddb:
            AlertDialog.Builder builder = new AlertDialog.Builder (context);
            builder.setTitle (getString (R.string.warning));
            builder.setMessage (getString (R.string.rebuild_alert));
            builder.setCancelable (true);
            builder.setIcon (android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton (android.R.string.yes,
                    new DialogInterface.OnClickListener () {
                        public void onClick (DialogInterface dialog, int id) {

                            DBHelper dbHelper = new DBHelper (context);
                            dbHelper.onRebuild ();
                            refreshGroups ();

                            dialog.cancel ();
                        }
                    });
            builder.setNegativeButton (android.R.string.no,
                    new DialogInterface.OnClickListener () {
                        public void onClick (DialogInterface dialog, int id) {
                            dialog.cancel ();
                        }
                    });

            AlertDialog alertbox = builder.create ();
            alertbox.show ();
            break;

        default:

            break;
        }
        return super.onOptionsItemSelected (item);
    }

    private void refreshGroups () {
        cursor = sbscrbdGrpDbOper.getGroupCursorByServer (serverName);
        Cursor newCursor = cursor;
        subdGrpAdptr.changeCursor (newCursor); // automatically closes old
                                               // Cursor
        subdGrpAdptr.mCursor = newCursor;
        subdGrpAdptr.notifyDataSetChanged ();
        
    }

    @Override
    public void onResume () {
        sbscrbdGrpDbOper.open ();
        refreshGroups ();
        super.onResume ();
    }

    class GroupItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick (AdapterView <?> parent, View view,
                int position, long id) {
            if (cursor != null) {
                cursor.moveToPosition (position);
                String grpName = cursor.getString (cursor
                        .getColumnIndex (DBHelper.S_SG_GRPNAME));
                Log.i ("--->", grpName);

                Intent selGrpIntent = new Intent ();
                selGrpIntent.setClass (getActivity (),
                        SingleGroupViewActivity.class);
                selGrpIntent.putExtra ("ServerName", serverName);
                selGrpIntent.putExtra ("Port", port);
                selGrpIntent.putExtra ("GroupName", grpName);
                getActivity ().startActivity (selGrpIntent);

            }
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    @SuppressWarnings ("unused")
    private void copyDataBase (String dbname) throws IOException {
        // Open your local db as the input stream
        InputStream myInput = context.getAssets ().open (dbname);
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
}
