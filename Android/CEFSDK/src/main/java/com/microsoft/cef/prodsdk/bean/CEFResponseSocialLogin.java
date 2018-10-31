
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.bean;

public class CEFResponseSocialLogin {
    private CEFSocialLoginResultCode resultCode;
    private String resultDescription = "";
    private CEFSocialLoginChannel channel;
    private String accessToken = "";
    private String ID = "";
    private String refreshToken = "";
    private int expirationDate = 0;

    public CEFSocialLoginResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(CEFSocialLoginResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public CEFSocialLoginChannel getChannel() {
        return channel;
    }

    public void setChannel(CEFSocialLoginChannel channel) {
        this.channel = channel;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(int expirationDate) {
        this.expirationDate = expirationDate;
    }
}
