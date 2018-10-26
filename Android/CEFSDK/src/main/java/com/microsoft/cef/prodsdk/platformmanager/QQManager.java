//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.platformmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.listener.CEFSocialLoginListener;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode;
import com.microsoft.cef.prodsdk.utils.CEFUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class QQManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile QQManager instance;
    private static Activity mContext;
    private static Tencent mTencent;
    private static CEFSocialLoginListener mLoginListener;
    private IUiListener Listener = new CEFUiListener();

    private QQManager() {

    }

    public static QQManager getInstance() {
        if (null == instance) {
            synchronized (QQManager.class) {
                if (null == instance) {
                    instance = new QQManager();
                }
            }
        }
        return instance;
    }

    public void initQQ(Activity context, String appkey) {
        QQManager.mContext = context;
        if (null == mTencent) {
            mTencent = Tencent.createInstance(appkey,mContext);
        }
    }

    public void evokeQQ(CEFSocialLoginListener loginListener) {
        if (null != mTencent) {
            mLoginListener = loginListener;
            mTencent.login(mContext, "all", Listener);
        } else {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultUnknown);
            auth.setResultDescription("login fail");
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_QQ);
            mLoginListener.onComplete(auth);
        }
    }

    public void setActivityResultData(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, Listener);
    }

    private class CEFUiListener implements IUiListener {
        public void onComplete(Object response) {
                JSONObject obj ;
                try {
                    obj = (JSONObject) response;
                    String openID = obj.getString("openid");
                    String accessToken = obj.getString("access_token");
                    String expires_in = obj.getString("expires_in");
                    long l = System.currentTimeMillis() + Long.parseLong(expires_in) * 1000;
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultSuccess);
                    auth.setResultDescription("QQ login success");
                    auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_QQ);
                    auth.setAccessToken(accessToken);
                    auth.setID(openID);
                    auth.setExpirationDate((int) l);
                    mLoginListener.onComplete(auth);
                } catch (JSONException e) {
                    Log.e(CEFUtils.CEF_LOG, e.toString());
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
                    auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_QQ);
                    mLoginListener.onComplete(auth);
                }
        }

        @Override
        public void onError(UiError uiError) {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
            auth.setResultDescription("errorCode：" + uiError.errorCode + ";errorMessage:" + uiError.errorMessage +
                    ";errorDetail:" + uiError.errorDetail);
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_QQ);
            mLoginListener.onComplete(auth);
        }

        @Override
        public void onCancel() {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultUserCancel);
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_QQ);
            mLoginListener.onComplete(auth);
        }
    }
}
