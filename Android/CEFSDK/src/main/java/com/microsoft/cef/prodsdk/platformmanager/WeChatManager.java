//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.platformmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.microsoft.cef.prodsdk.bean.CEFCredential;
import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.listener.CEFSocialLoginListener;
import com.microsoft.cef.prodsdk.utils.CEFCallback;
import com.microsoft.cef.prodsdk.utils.CEFHttpsApi;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginResultCode;
import com.microsoft.cef.prodsdk.utils.CEFUtils;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class WeChatManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile WeChatManager instance;
    private static IWXAPI msgApi;
    private static Activity mContext;
    private static CEFSocialLoginListener mLoginListener;
    private CEFCredential credential;

    private WeChatManager() {

    }

    public static WeChatManager getInstance() {
        if (null == instance) {
            synchronized (WeChatManager.class) {
                if (null == instance) {
                    instance = new WeChatManager();
                }
            }
        }
        return instance;
    }

    public void initWeChat(final Activity activity, CEFCredential credential) {
        WeChatManager.mContext = activity;
        this.credential = credential;
        if (null == msgApi) {
            msgApi = WXAPIFactory.createWXAPI(mContext, credential.getWeChatAppId(), true);
        }
        msgApi.registerApp(credential.getWeChatAppId());
    }

    public void evokeWeChat(CEFSocialLoginListener loginListener) {
        mLoginListener = loginListener;
        SendAuth.Req req = new SendAuth.Req();
        if (msgApi != null) {
            req.scope = "snsapi_userinfo";

            //Custom information
            req.state = "CEF_authorization_state";

            //Send a request to WeChat
            msgApi.sendReq(req);
        } else {
            CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
            auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
            auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
            loginListener.onComplete(auth);
        }
    }

    public void onCreate(Activity activity, IWXAPIEventHandler mIWXAPIEventHandler) {
        initWeChat(activity, credential);
        try {
            boolean result = msgApi.handleIntent(activity.getIntent(), mIWXAPIEventHandler);
            if (!result) {
                activity.finish();
            }
        } catch (Exception e) {
            Log.e(CEFUtils.CEF_LOG, e.toString());
        }
    }

    public void handleIntent(Intent intent, IWXAPIEventHandler mIWXAPIEventHandler) {
        msgApi.handleIntent(intent, mIWXAPIEventHandler);
    }

    public void handleOnResp(final BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                final String code = ((SendAuth.Resp) resp).code;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loginSuccessByCode(code, resp.errStr);
                    }
                }).start();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultUserCancel);
                auth.setResultDescription(resp.errStr);
                auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
                mLoginListener.onComplete(auth);
                break;

            default:
                CEFResponseSocialLogin unKnown = new CEFResponseSocialLogin();
                unKnown.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
                unKnown.setResultDescription(resp.errStr);
                unKnown.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
                mLoginListener.onComplete(unKnown);
                break;
        }
    }

    public boolean isWeChatInstall() {
        return msgApi.isWXAppInstalled();
    }

    public boolean isWeChatSupportAPI() {
        return msgApi.isWXAppSupportAPI();
    }


    private void loginSuccessByCode(String code, final String errStr) {
        String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + credential.getWeChatAppId() +
                "&secret=" + credential.getWeChatAppSecret() + "&code=" + code + "&grant_type=authorization_code";
        CEFHttpsApi.getInstance().getRequestWithURL(WeChatManager.mContext, CEFHttpsApi.Type.GET, null, TOKEN_URL, new CEFCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String access_token = jsonObject.getString("access_token");
                    String openid = jsonObject.getString("openid");
                    String expires_in = jsonObject.getString("expires_in");
                    String refresh_token = jsonObject.getString("refresh_token");

                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultSuccess);
                    auth.setResultDescription(errStr);
                    auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
                    auth.setAccessToken(access_token);
                    auth.setID(openid);
                    auth.setExpirationDate(Integer.parseInt(expires_in));
                    auth.setRefreshToken(refresh_token);
                    mLoginListener.onComplete(auth);
                } catch (JSONException e) {
                    Log.e(CEFUtils.CEF_LOG, e.toString());
                    CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                    auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
                    auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
                    mLoginListener.onComplete(auth);
                }
            }

            @Override
            public void onFail(String fail) {
                CEFResponseSocialLogin auth = new CEFResponseSocialLogin();
                auth.setResultCode(CEFSocialLoginResultCode.CEFSocialLoginResultTokenFailure);
                auth.setResultDescription(fail);
                auth.setChannel(CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat);
                mLoginListener.onComplete(auth);
            }
        });
    }
}
