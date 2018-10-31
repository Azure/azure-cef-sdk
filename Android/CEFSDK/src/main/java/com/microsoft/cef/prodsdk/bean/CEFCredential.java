
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.bean;

public class CEFCredential {

    //weChat
    private String weChatAppId = "";
    private String weChatAppSecret = "";

    //Weibo
    private String weiboAppkey= "";
    private String weiboSecret= "";
    private String weiboRedirectURL= "";

    //qq
    private String qqAppId= "";

    public void setWeChatAppWithKey(String appId,String scope){
        setWeChatAppId(appId);
        setWeChatAppSecret(scope);
    }

    public void setQQAppWithKey(String appKey){
        setQQAppId(appKey);
    }

    public void setWeiboAppWithKey(String appKey,String redirectUrl,String scope){
        setWeiboRedirectURL(redirectUrl);
        setWeiboAppkey(appKey);
        setWeiboSecret(scope);
    }

    public String getWeChatAppId() {
        return weChatAppId;
    }

    private void setWeChatAppId(String weChatAppId) {
        this.weChatAppId = weChatAppId;
    }

    public String getWeChatAppSecret() {
        return weChatAppSecret;
    }

    private void setWeChatAppSecret(String weChatAppSecret) {
        this.weChatAppSecret = weChatAppSecret;
    }

    public String getWeiboAppkey() {
        return weiboAppkey;
    }

    private void setWeiboAppkey(String weiboAppkey) {
        this.weiboAppkey = weiboAppkey;
    }

    public String getWeiboSecret() {
        return weiboSecret;
    }

    private void setWeiboSecret(String weiboSecret) {
        this.weiboSecret = weiboSecret;
    }

    public String getWeiboRedirectURL() {
        return weiboRedirectURL;
    }

    private void setWeiboRedirectURL(String weiboRedirectURL) {
        this.weiboRedirectURL = weiboRedirectURL;
    }

    public String getQQAppId() {
        return qqAppId;
    }

    private void setQQAppId(String qqAppId) {
        this.qqAppId = qqAppId;
    }

    @Override
    public String toString() {
        return "{" +
                "wechatAppkey='" + weChatAppId + '\'' +
                ", wechatSecret='" + weChatAppSecret + '\'' +
                ", weiboAppkey='" + weiboAppkey + '\'' +
                ", weiboSecret='" + weiboSecret + '\'' +
                ", weiboRedirectURL='" + weiboRedirectURL + '\'' +
                ", qqAppId='" + qqAppId + '\'' +
                '}';
    }
}
