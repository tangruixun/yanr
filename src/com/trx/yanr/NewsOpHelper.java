package com.trx.yanr;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class NewsOpHelper {
        
    NewsGroups retrieveAllGroups (String servername, int port) throws IOException, Exception {
        NNTPClientClass nntpclient = new NNTPClientClass();
        List<String> messageHeaderList = new ArrayList<String>();
        NewsGroups newsgroups = new NewsGroups ();
        
        try {
            Socket nntpSocket = nntpclient.connect (servername, port);
            if (nntpSocket != null && nntpclient.in != null && nntpclient.out != null) {
                Log.i("--->", "connect successful");
                int i = nntpclient.open();
                if (i != 0) {
                    Log.i("open () error!!! Error: news server not found. --->", "" + i);
                }
                // add newsgroups to array, logout all newsgroups
                newsgroups = nntpclient.listNewsgroups();
                return newsgroups;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return newsgroups;
    }
    
    void retrieveArticleNumbers (String servername, int port, String groupname) throws IOException, Exception {
        NNTPClientClass nntpclient = new NNTPClientClass();
        try {
            if (!nntpSocket.isConnected ()) {
                nntpSocket = nntpclient.connect (servername, port);
            }
            nntpclient.displayNewsgroup (groupname);
            
            
            
            
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
