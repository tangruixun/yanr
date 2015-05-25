package com.trx.yanr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ServerSettingsFragment extends PreferenceFragment {
    
    private ServerDbOperator svrDbOptr;
    private Context context;
    private Cursor c = null;
    private ServerSettingAdapter adapter;
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        context = getActivity ();
        svrDbOptr = new ServerDbOperator (context);
        svrDbOptr.open ();
        c = svrDbOptr.getAllServers ();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu (true);
        //View view = super.onCreateView (inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_pref_servers, container, false);
        ListView listView = (ListView) view.findViewById (R.id.serverlistview);
        
        adapter = new ServerSettingAdapter (context, c, 
                ServerSettingAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter (adapter);
        listView.setOnItemLongClickListener (new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick (AdapterView <?> parent, View view,
                    int position, long id) {
                c.moveToPosition (position);
                svrDbOptr.deleteRecord (c);
                refresh ();
                return false;
            }
        });
        
        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu (menu, inflater);
        inflater.inflate (R.menu.settings_serverlistmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
        case R.id.add_server:
            showAddServerDlg ();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected (item);
    }

    private void refresh () {
        c = svrDbOptr.getAllServers ();
        Cursor newC = c;
        adapter.changeCursor (newC); // automatically closes old Cursor
        adapter.mCursor = newC;
        adapter.notifyDataSetChanged ();        
    }

    private void showAddServerDlg () {
        NewServerDialog newSvrDlg = new NewServerDialog (getActivity());
        newSvrDlg.setOnDismissListener (new OnDismissListener() {
            
            @Override
            public void onDismiss (DialogInterface dialog) {
                refresh ();
            }
        });
        newSvrDlg.show ();
    }

    @Override
    public void onDestroy () {
        svrDbOptr.close ();
        if (c != null) {
            c = null;
        }
        super.onDestroy ();
    }

    @Override
    public void onResume () {
        super.onResume ();
        refresh ();
    }

}
