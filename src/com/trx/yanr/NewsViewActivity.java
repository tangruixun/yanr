package com.trx.yanr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class NewsViewActivity extends Activity {

    private Bundle bundle;
    private String svrName;
    private int port;
    private String grpName;
    private int articleNo;
    private Context context;

    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        
        context = this;
        
        bundle = getIntent ().getExtras ();
        svrName = bundle.getString ("ServerName");
        port = bundle.getInt ("Port");
        grpName = bundle.getString ("GroupName");
        articleNo = bundle.getInt ("ArticleNo");
        
        
    }

}
