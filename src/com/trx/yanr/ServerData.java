package com.trx.yanr;

public class ServerData {
    String address;
    String port;
    String serverusername;
    String serverpassword;
    boolean loginrequired;
    String mynickname;
    String myemail;
    String myorganization;
    String myface;
    String myxface;
    boolean sslrequired;
    int timeout;
    
    public String getAddress () {
        return address;
    }
    public void setAddress (String address) {
        this.address = address;
    }
    public String getPort () {
        return port;
    }
    public void setPort (String port) {
        this.port = port;
    }
    public String getServerusername () {
        return serverusername;
    }
    public void setServerusername (String serverusername) {
        this.serverusername = serverusername;
    }
    public String getServerpassword () {
        return serverpassword;
    }
    public void setServerpassword (String serverpassword) {
        this.serverpassword = serverpassword;
    }
    public boolean isLoginrequired () {
        return loginrequired;
    }
    public void setLoginrequired (boolean loginrequired) {
        this.loginrequired = loginrequired;
    }
    public String getMynickname () {
        return mynickname;
    }
    public void setMynickname (String mynickname) {
        this.mynickname = mynickname;
    }
    public String getMyemail () {
        return myemail;
    }
    public void setMyemail (String myemail) {
        this.myemail = myemail;
    }
    public String getMyorganization () {
        return myorganization;
    }
    public void setMyorganization (String myorganization) {
        this.myorganization = myorganization;
    }
    public String getMyface () {
        return myface;
    }
    public void setMyface (String myface) {
        this.myface = myface;
    }
    public String getMyxface () {
        return myxface;
    }
    public void setMyxface (String myxface) {
        this.myxface = myxface;
    }
    public boolean isSslrequired () {
        return sslrequired;
    }
    public void setSslrequired (boolean sslrequired) {
        this.sslrequired = sslrequired;
    }
    public int getTimeout () {
        return timeout;
    }
    public void setTimeout (int timeout) {
        this.timeout = timeout;
    }
}
