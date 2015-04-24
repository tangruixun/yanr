package com.trx.yanr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public final class NewServerDialog extends Dialog implements OnClickListener {
    
    Context context;
    EditText svrAddrV, svrPortV, svrUsranemV, svrPwdV, nickNameV, emailV, orgV, faceV, xfaceV;
    CheckBox loginReqV, sslReqV;

    public NewServerDialog (Context _context) {
        super (_context);
        context = _context;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.newserver_dlg);
        
        svrAddrV = (EditText) findViewById (R.id.serveraddr);
        svrPortV = (EditText) findViewById (R.id.serverport);
        svrUsranemV = (EditText) findViewById (R.id.serverusername);
        svrPwdV = (EditText) findViewById (R.id.serverpwd);
        nickNameV = (EditText) findViewById (R.id.mynick);
        emailV = (EditText) findViewById (R.id.myemail);
        orgV = (EditText) findViewById (R.id.myorg);
        faceV = (EditText) findViewById (R.id.myface);
        xfaceV = (EditText) findViewById (R.id.myxface);
        loginReqV = (CheckBox) findViewById (R.id.logincheckbox);
        sslReqV = (CheckBox) findViewById (R.id.sslcheckbox);

        
    }

    @Override
    public void onClick (View v) {
        
    }

}
