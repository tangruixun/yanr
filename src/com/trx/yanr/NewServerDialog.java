package com.trx.yanr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public final class NewServerDialog extends Dialog implements OnClickListener {
    
    Context context;

    public NewServerDialog (Context _context) {
        super (_context);
        context = _context;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.newserver_dlg);

        
    }

    @Override
    public void onClick (View v) {
        
    }

}
