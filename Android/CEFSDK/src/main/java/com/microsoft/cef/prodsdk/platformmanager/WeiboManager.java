//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.platformmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import com.microsoft.cef.prodsdk.bean.CEFCredential;
import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.listener.CEFSocialLoginListener;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

public class WeiboManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile WeiboManager instance;
    private static CEFSocialLoginListener mLoginListener;

    @SuppressLint("StaticFieldLeak")
    private  static SsoHandler mSsoHandler;

    private WeiboManager() {

    }

    public static WeiboManager getInstance() {
        if (null == instance) {
            synchronized (WeiboManager.class) {
                if (null == instance) {
                    instance = new WeiboManager();
                }
            }
        }
        return instance;
    }

    public void initWeibo(Activity mContext, CEFCredential credential) {
        WbSdk.install(mContext, new AuthInfo(
                mContext,
                credential.getWeiboAppkey(),
                credential.getWeiboRedirectURL(),
                credential.getWeiboSecret()
        ));
        mSsoHandler = new SsoHandler(mContext);
    }

    public void evokeWeibo(CEFSocialLoginListener loginListener) {
        mLoginListener = loginListener;
        if (mSsoHandler != null) {
            mSsoHandler.authorize(new CEFSelfWbAuthListener());
        }
    }

    public void setActivityResultData(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private class CEFSelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultSuccess);
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_Weibo);
            auth.setAccessToken(token.getToken());
            auth.setID(token.getUid());
            auth.setExpirationDate((int) token.getExpiresTime());
            auth.setRefreshToken(token.getRefreshToken());
            mLoginListener.onComplete(auth);
        }

        @Override
        public void cancel() {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultUserCancel);
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_Weibo);
            mLoginListener.onComplete(auth);
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
            auth.setResultDescription("errorCode:"+errorMessage.getErrorCode()+";errorMessage:"+errorMessage.getErrorMessage());
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_Weibo);
            mLoginListener.onComplete(auth);
        }
    }
}
