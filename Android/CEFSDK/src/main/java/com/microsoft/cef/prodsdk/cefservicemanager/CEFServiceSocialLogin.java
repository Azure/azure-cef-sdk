//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.cefservicemanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.cef.prodsdk.bean.CEFCredential;
import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginProfile;
import com.microsoft.cef.prodsdk.listener.CEFResponseSocialLoginListener;
import com.microsoft.cef.prodsdk.listener.CEFSocialLoginListener;
import com.microsoft.cef.prodsdk.platformmanager.QQManager;
import com.microsoft.cef.prodsdk.platformmanager.WeChatManager;
import com.microsoft.cef.prodsdk.platformmanager.WeiboManager;
import com.microsoft.cef.prodsdk.utils.CEFCallback;
import com.microsoft.cef.prodsdk.utils.CEFConstants;
import com.microsoft.cef.prodsdk.utils.CEFHttpsApi;
import com.microsoft.cef.prodsdk.utils.CEFErrorMessages;
import com.microsoft.cef.prodsdk.utils.CEFUtils;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel.*;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode.CEFSocialLoginResultHttpError;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode.CEFSocialLoginResultSuccess;
import static com.microsoft.cef.prodsdk.utils.CEFErrorMessages.CEFEM_CEFSOCIALLOGIN_QQNOTCONFIG;
import static com.microsoft.cef.prodsdk.utils.CEFErrorMessages.CEFEM_CEFSOCIALLOGIN_WECHATNOTSUPPORTAPI;
import static com.microsoft.cef.prodsdk.utils.CEFErrorMessages.CEFEM_CEFSOCIALLOGIN_WEIBONOTCONFIG;
import static com.microsoft.cef.prodsdk.utils.CEFErrorMessages.CEFEM_WECHAT_NOTCONFIG;
import static com.microsoft.cef.prodsdk.utils.CEFErrorMessages.CEFEM_CEFSOCIALLOGIN_WECHATNOTINSTALLED;
import static com.microsoft.cef.prodsdk.utils.CEFUtils.CEF_LOG;

public class CEFServiceSocialLogin {
    @SuppressLint("StaticFieldLeak")
    private static volatile CEFServiceSocialLogin instance;
    private CEFResponseSocialLoginListener responseSocialLoginListener;
    private CEFCredential mCredential;
    private static Activity mActivity;
    private CEFSocialLoginListener localLoginListener;

    private CEFServiceSocialLogin() {
        localLoginListener = new CEFSocialLoginListener() {
            @Override
            public void onComplete(CEFResponseSocialLogin result) {
                if (result.getResultCode().equals(CEFSocialLoginResultSuccess)) {
                    getUserInfo(result);
                } else {
                    responseSocialLoginListener.onComplete(result, null);
                }
            }
        };
    }

    public static CEFServiceSocialLogin getInstance(Activity activity) {
        mActivity = activity;
        if (null == instance) {
            synchronized (CEFServiceSocialLogin.class) {
                if (null == instance) {
                    instance = new CEFServiceSocialLogin();
                }
            }
        }
        return instance;
    }

    /**
     * Setup channels by CEFCredential.
     *
     * @param credential A CEFCredential instance contains AppKey,AppScerect for different channels.
     *                   Will only setup channels has valid keys in credential.
     */
    public void setupChannel(CEFCredential credential) {
        mCredential = credential;
        if (TextUtils.isEmpty(mCredential.getWeChatAppId()) ||
                TextUtils.isEmpty(mCredential.getWeChatAppSecret())) {
            Log.e(CEF_LOG, CEFEM_WECHAT_NOTCONFIG);
        } else {
            WeChatManager.getInstance().initWeChat(mActivity, mCredential);
        }

        if (TextUtils.isEmpty(mCredential.getWeiboAppkey()) ||
                TextUtils.isEmpty(mCredential.getWeiboSecret()) ||
                TextUtils.isEmpty(mCredential.getWeiboRedirectURL())) {
            Log.e(CEF_LOG, CEFEM_CEFSOCIALLOGIN_WEIBONOTCONFIG);
        } else {
            WeiboManager.getInstance().initWeibo(mActivity, mCredential);
        }

        if (TextUtils.isEmpty(mCredential.getQQAppId())) {
            Log.e(CEF_LOG, CEFErrorMessages.CEFEM_CEFSOCIALLOGIN_QQNOTCONFIG);
        } else {
            QQManager.getInstance().initQQ(mActivity, mCredential.getQQAppId());
        }
    }

    /**
     * @param channel             CEFSocialLoginChannel
     * @param socialLoginListener CEFResponseSocialLoginListener
     * @see CEFSocialLoginChannel#CEFSocialLoginChannel_WeChat
     * @see CEFSocialLoginChannel#CEFSocialLoginChannel_QQ
     * @see CEFSocialLoginChannel#CEFSocialLoginChannel_Weibo
     */
    public void loginWithChannel(CEFSocialLoginChannel channel, CEFResponseSocialLoginListener socialLoginListener) {
        responseSocialLoginListener = socialLoginListener;
        if (null == mCredential){
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultTokenFailure);
            auth.setResultDescription("CEFCredential is null,please init it.");
            auth.setChannel(channel);
            responseSocialLoginListener.onComplete(auth, null);
            return;
        }
        switch (channel) {
            case CEFSocialLoginChannel_WeChat:
                if (!WeChatManager.getInstance().isWeChatInstall()) {
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultTokenFailure);
                    auth.setResultDescription(CEFEM_CEFSOCIALLOGIN_WECHATNOTINSTALLED);
                    auth.setChannel(CEFSocialLoginChannel_WeChat);
                    responseSocialLoginListener.onComplete(auth, null);
                    return;
                }
                if (!WeChatManager.getInstance().isWeChatSupportAPI()) {
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultTokenFailure);
                    auth.setResultDescription(CEFEM_CEFSOCIALLOGIN_WECHATNOTSUPPORTAPI);
                    auth.setChannel(CEFSocialLoginChannel_WeChat);
                    responseSocialLoginListener.onComplete(auth, null);
                    return;
                }
                if (TextUtils.isEmpty(mCredential.getWeChatAppId()) ||
                        TextUtils.isEmpty(mCredential.getWeChatAppSecret())) {
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultTokenFailure);
                    auth.setResultDescription(CEFEM_WECHAT_NOTCONFIG);
                    auth.setChannel(CEFSocialLoginChannel_WeChat);
                    responseSocialLoginListener.onComplete(auth, null);
                    return;
                }
                WeChatManager.getInstance().evokeWeChat(localLoginListener);
                break;
            case CEFSocialLoginChannel_Weibo:
                if (TextUtils.isEmpty(mCredential.getWeiboAppkey()) ||
                        TextUtils.isEmpty(mCredential.getWeiboSecret()) ||
                        TextUtils.isEmpty(mCredential.getWeiboRedirectURL())) {
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultTokenFailure);
                    auth.setResultDescription(CEFEM_CEFSOCIALLOGIN_WEIBONOTCONFIG);
                    auth.setChannel(CEFSocialLoginChannel_Weibo);
                    responseSocialLoginListener.onComplete(auth, null);
                    return;
                }
                WeiboManager.getInstance().evokeWeibo(localLoginListener);
                break;
            case CEFSocialLoginChannel_QQ:
                if (TextUtils.isEmpty(mCredential.getQQAppId())) {
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultTokenFailure);
                    auth.setResultDescription(CEFEM_CEFSOCIALLOGIN_QQNOTCONFIG);
                    auth.setChannel(CEFSocialLoginChannel_QQ);
                    responseSocialLoginListener.onComplete(auth, null);
                    return;
                }
                QQManager.getInstance().evokeQQ(localLoginListener);
                break;
            default:
                CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                auth.setResultCode(CEFSocialLoginResultTokenFailure);
                auth.setResultDescription(CEFErrorMessages.CEFEM_CHANNEL_ERROR);
                auth.setChannel(channel);
                responseSocialLoginListener.onComplete(auth, null);
        }
    }

    /**
     * weChat register
     *
     * @param mIWXAPIEventHandler WX interface IWXAPIEventHandler
     */
    public void onCreate(IWXAPIEventHandler mIWXAPIEventHandler) {
        WeChatManager.getInstance().onCreate(mActivity, mIWXAPIEventHandler);
    }

    /**
     * weChat used in function onNewIntent(Intent intent)
     *
     * @param intent              Intent
     * @param mIWXAPIEventHandler WX interface IWXAPIEventHandler
     */
    public void handleIntent(Intent intent, IWXAPIEventHandler mIWXAPIEventHandler) {
        WeChatManager.getInstance().handleIntent(intent, mIWXAPIEventHandler);
    }

    /**
     * weChat used in function onResp(BaseResp baseResp)
     *
     * @param resp BaseResp
     */
    public void handleOnResp(BaseResp resp) {
        WeChatManager.getInstance().handleOnResp(resp);
    }

    /**
     * used in onActivityResult(int requestCode, int resultCode, Intent data)
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        intent
     */
    public void setActivityResultData(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            //Android_SDK_V3.3.3
            QQManager.getInstance().setActivityResultData(requestCode, resultCode, data);
        } else {
            WeiboManager.getInstance().setActivityResultData(requestCode, resultCode, data);
        }
    }

    private void getUserInfo(final CEFResponseSocialLogin auth) {
        String url = CEFUtils.getURL() + CEFConstants.CEFSERVICE_SOCIAL_LOGIN_USERINFO;
        String channelName = "";
        switch (auth.getChannel()) {
            case CEFSocialLoginChannel_QQ:
                channelName = "qq";
                break;
            case CEFSocialLoginChannel_Weibo:
                channelName = "weibo";
                break;
            case CEFSocialLoginChannel_WeChat:
                channelName = "wechat";
                break;
        }
        JsonObject json = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("accesstoken", auth.getAccessToken());
        properties.addProperty("id", auth.getID());
        properties.addProperty("platform", "android");
        json.add("channelProperties", properties);
        json.addProperty("channelName", channelName);
        CEFHttpsApi.getInstance().getRequestWithURL(CEFServiceSocialLogin.mActivity, CEFHttpsApi.Type.POST, json, url, new CEFCallback() {
            @Override
            public void onSuccess(String response) {
                CEFSocialLoginProfile cefUserAuthProfile = new CEFSocialLoginProfile();
                try {
                    JSONObject json = new JSONObject(response);
                    String description = json.getString("description");
                    JSONObject jsonDes = new JSONObject(description);
                    String channelNameStr = jsonDes.getString("channelName");
                    String channelPropertiesStr = jsonDes.getString("channelProperties");
                    switch (channelNameStr) {
                        case "qq":
                            cefUserAuthProfile.setChannelName(CEFSocialLoginChannel_QQ);
                            break;
                        case "weibo":
                            cefUserAuthProfile.setChannelName(CEFSocialLoginChannel_Weibo);
                            break;
                        case "wechat":
                            cefUserAuthProfile.setChannelName(CEFSocialLoginChannel_WeChat);
                    }
                    Gson gson = new Gson();
                    Map mapChannelPro = new HashMap<String, Object>();
                    mapChannelPro = gson.fromJson(channelPropertiesStr, mapChannelPro.getClass());
                    cefUserAuthProfile.setChannelProperties(mapChannelPro);
                    responseSocialLoginListener.onComplete(auth, cefUserAuthProfile);
                } catch (Exception e) {
                    Log.e(CEF_LOG, e.toString());
                    auth.setResultCode(CEFSocialLoginResultHttpError);
                    auth.setChannel(auth.getChannel());
                    responseSocialLoginListener.onComplete(auth, null);
                }
            }

            @Override
            public void onFail(String fail) {
                auth.setResultCode(CEFSocialLoginResultHttpError);
                auth.setResultDescription(fail);
                auth.setChannel(auth.getChannel());
                responseSocialLoginListener.onComplete(auth, null);
            }
        });
    }
}
