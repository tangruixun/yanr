package com.trx.yanr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public final class NewServerDialog extends Dialog implements OnClickListener {
    
    Context context;
    EditText svrAddrV, svrPortV, svrUsranemV, svrPwdV, nickNameV, emailV, orgV, faceV, xfaceV;
    CheckBox loginReqV, sslReqV;
    Button savBtnV;

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
        savBtnV = (Button) findViewById (R.id.savebutton);
        
        loginReqV.setOnCheckedChangeListener (new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                int c = (isChecked == true) ? 1 : 0;
                switch (c) {
                case 1:
                    svrUsranemV.setEnabled (true);
                    svrPwdV.setEnabled (true);
                    break;

                default:
                    svrUsranemV.setEnabled (false);
                    svrPwdV.setEnabled (false);
                    break;
                }
            }
        });
        
        savBtnV.setOnClickListener (this);

    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
        case R.id.savebutton:
            ServerData serverData = new ServerData ();
            ServerDbOperator svrDbOptr = new ServerDbOperator (context);
            svrDbOptr.open ();
            
            String strAddr = svrAddrV.getText ().toString ().trim ();
            String strPort = svrPortV.getText ().toString ().trim ();
            String strSvrUsrName = svrUsranemV.getText ().toString ();
            String strSvrPwd = svrPwdV.getText ().toString ();
            String strNick = nickNameV.getText ().toString ().trim ();
            String strEmail = emailV.getText ().toString ().trim ();
            String strOrg = orgV.getText ().toString ().trim ();
            String strFace = faceV.getText ().toString ().trim ();
            String strXface = xfaceV.getText ().toString ().trim ();
            Boolean bLogReq = loginReqV.isChecked ();
            Boolean bSslReq = sslReqV.isChecked ();
            
            serverData.setAddress (strAddr);
            serverData.setPort (strPort);
            serverData.setServerusername (strSvrUsrName);
            serverData.setServerpassword (strSvrPwd);
            serverData.setLoginrequired (bLogReq);
            serverData.setMynickname (strNick);
            serverData.setMyemail (strEmail);
            serverData.setMyorganization (strOrg);
            serverData.setMyface (strFace);
            serverData.setMyxface (strXface);
            serverData.setSslrequired (bSslReq);
            
            if (!strAddr.equals ("") && !strPort.equals ("")) {
                svrDbOptr.createRecord (serverData);
            }
            dismiss ();
            break;

        default:
            break;
        }
    }

}
