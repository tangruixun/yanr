package com.trx.yanr;

import java.util.List;

import android.preference.PreferenceActivity;

public class SettingPrefActivity extends PreferenceActivity {

//    @Override
//    protected void onCreate (Bundle savedInstanceState) {
//        super.onCreate (savedInstanceState);
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
//    }

    @Override
    public void onBuildHeaders (List <Header> target) {
        loadHeadersFromResource (R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment (String fragmentName) {
        return ServerSettingsFragment.class.getName ().equals (fragmentName);
        // return super.isValidFragment (fragmentName);
    }
}
