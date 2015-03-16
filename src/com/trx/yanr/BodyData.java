package com.trx.yanr;

public class BodyData {
    private String groupName;
    private String serverName;
    private int articleNumber;
    private String body;
    
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
    public String getBody () {
        return body;
    }
    public void setBody (String body) {
        this.body = body;
    }
}
