package com.trx.yanr;

public class GroupData {
    // alt.activism 0000385432 0000327369 y
    private String groupName; // alt.activism
    private String serverName;
    private int readNumber; // read number
    private int articleNumbers; // 0000385432 - 0000327369
    private int latestArticleNumber;
    private Boolean postable; // y
    private String groupDes;
    private Boolean subscribed;
    private String memo;
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
    public int getReadNumber () {
        return readNumber;
    }
    public void setReadNumber (int readNumber) {
        this.readNumber = readNumber;
    }
    public int getArticleNumbers () {
        return articleNumbers;
    }
    public void setArticleNumbers (int articleNumbers) {
        this.articleNumbers = articleNumbers;
    }
    public int getLatestArticleNumber () {
        return latestArticleNumber;
    }
    public void setLatestArticleNumber (int latestArticleNumber) {
        this.latestArticleNumber = latestArticleNumber;
    }
    public Boolean getPostable () {
        return postable;
    }
    public void setPostable (Boolean postable) {
        this.postable = postable;
    }
    public String getGroupDes () {
        return groupDes;
    }
    public void setGroupDes (String groupDes) {
        this.groupDes = groupDes;
    }
    public Boolean getSubscribed () {
        return subscribed;
    }
    public void setSubscribed (Boolean subscribed) {
        this.subscribed = subscribed;
    }
    public String getMemo () {
        return memo;
    }
    public void setMemo (String memo) {
        this.memo = memo;
    }
}
