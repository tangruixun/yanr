package com.trx.yanr;

public class HeaderData {
    // listGroup groupname
    
    private String groupName;
    private String serverName;
    private int articleNumber;
    private NNTPMessageHeader header;
    
    public String getGroupName () {
        return groupName;
    }
    public void setGroupName (String groupName) {
        this.groupName = groupName;
    }
    public String getServerName () {
        return serverName;
    }
    public void setServerName (String serverName) {
        this.serverName = serverName;
    }
    public int getArticleNumber () {
        return articleNumber;
    }
    public void setArticleNumber (int articleNumber) {
        this.articleNumber = articleNumber;
    }
    public NNTPMessageHeader getHeader () {
        return header;
    }
    public void setHeader (NNTPMessageHeader header) {
        this.header = header;
    }
    
    
}
