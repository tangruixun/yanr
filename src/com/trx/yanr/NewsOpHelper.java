package com.trx.yanr;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

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
    
    public String retrieveArticleNumbers (String servername, int port, String groupname) throws IOException, Exception {
        NNTPClientClass nntpclient = new NNTPClientClass();
        String allArticlesNumber = "";
        try {
            Socket nntpSocket = nntpclient.connect (servername, port);
            if (nntpSocket != null && nntpclient.in != null && nntpclient.out != null) {
                Log.i("--->", "connect successful");
                int i = nntpclient.open();
                if (i != 0) {
                    Log.i("open () error!!! Error: news server not found. --->", "" + i);
                }
                allArticlesNumber = nntpclient.listNewsgroup (groupname);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return allArticlesNumber;
    }

//    public String retrieveHeader (String servername, int port, String groupname, int articleNo) throws IOException, Exception {
//        NNTPClientClass nntpclient = new NNTPClientClass();
//        String headerText = "";
//        try {
//            Socket nntpSocket = nntpclient.connect (servername, port);
//            if (nntpSocket != null && nntpclient.in != null && nntpclient.out != null) {
//                Log.i("--->", "connect successful");
//                int i = nntpclient.open();
//                if (i != 0) {
//                    Log.i("open () error!!! Error: news server not found. --->", "" + i);
//                }
//                nntpclient.displayNewsgroupHeadsWithArticleNo (groupname, articleNo);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//        return headerText;
//    }
    
    public List <SparseArray<NNTPMessageHeader>> retrieveAllHeaders (String servername, int port, String groupname) throws IOException, Exception {
        NNTPClientClass nntpclient = new NNTPClientClass();
        List <SparseArray<NNTPMessageHeader>> headerList= new ArrayList <SparseArray<NNTPMessageHeader>> ();
        try {
            Socket nntpSocket = nntpclient.connect (servername, port);
            if (nntpSocket != null && nntpclient.in != null && nntpclient.out != null) {
                Log.i("--->", "connect successful");
                int i = nntpclient.open();
                if (i != 0) {
                    Log.i("open () error!!! Error: news server not found. --->", "" + i);
                }
                headerList = nntpclient.displayNewsgroupHeadsWithRawReturn (groupname);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return headerList;
    }
}
