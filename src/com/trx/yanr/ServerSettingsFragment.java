package com.trx.yanr;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ServerSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        addPreferencesFromResource (R.xml.fragmented_preferences);
        //ListView serverListV = (ListView) v.findViewById (R.id.);
    }



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        setHasOptionsMenu (true);
        return super.onCreateView (inflater, container, savedInstanceState);
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

    private void showAddServerDlg () {
        NewServerDialog newSvrDlg = new NewServerDialog (getActivity());
        newSvrDlg.show ();
    }
    
    
}
