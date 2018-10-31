//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.cefservicemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.cef.prodsdk.bean.CEFOTPChannel;
import com.microsoft.cef.prodsdk.listener.CEFGenerateOTPCodeListener;
import com.microsoft.cef.prodsdk.listener.CEFVerifyOTPCodeListener;
import com.microsoft.cef.prodsdk.utils.CEFConstants;
import com.microsoft.cef.prodsdk.utils.CEFHttpsApi;
import com.microsoft.cef.prodsdk.utils.CEFCallback;
import com.microsoft.cef.prodsdk.utils.CEFUtils;
import com.microsoft.cef.prodsdk.bean.CEFOTPResultCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CEFServiceOTP {
    @SuppressLint("StaticFieldLeak")
    private static volatile CEFServiceOTP instance;
    private static Context mContext;
    private Gson gson;

    private CEFServiceOTP() {
        gson = new Gson();
    }

    public static CEFServiceOTP getInstance(Context context) {
        CEFServiceOTP.mContext = context;
        if (null == instance) {
            synchronized (CEFServiceOTP.class) {
                if (null == instance) {
                    instance = new CEFServiceOTP();
                }
            }
        }
        return instance;
    }

    /**
     * Send verifications to end users.
     * @param phoneNumber The phone number to send code for.
     * @param templateName  OTP template name.
     * @param expireTime Expiration time of the verifying code
     * @param codeLength The length of the verifying code
     * @param channel channel
     * @see CEFOTPChannel#CEFOTPChannel_SMS
     * @param codeCompletion  listener
     */
    public void generateOTPCode(String phoneNumber,
                                String templateName,
                                int expireTime,
                                int codeLength,
                                CEFOTPChannel channel,
                                final CEFGenerateOTPCodeListener codeCompletion) {
        String url = CEFUtils.getURL() + CEFConstants.CEFSERVICE_OTP_START;
        String channelName = "";
        switch (channel) {
            case CEFOTPChannel_SMS:
                channelName = "sms";
                break;
        }

        JsonObject json = new JsonObject();
        json.addProperty("phoneNumber", phoneNumber);
        json.addProperty("templateName", templateName);
        json.addProperty("expireTime", expireTime);
        json.addProperty("codeLength", codeLength);
        json.addProperty("channel", channelName);

        CEFHttpsApi.getInstance().getRequestWithURL(mContext,
                CEFHttpsApi.Type.POST,
                json,
                url,
                new CEFCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e(CEFUtils.CEF_LOG, "response:"+response);
                try {
                    Map mapResponse = new HashMap<String, Object>();
                    mapResponse = gson.fromJson(response, mapResponse.getClass());
                    codeCompletion.onComplete(mapResponse);
                }catch (Exception e){
                    Log.e(CEFUtils.CEF_LOG, e.toString());
                }
            }

            @Override
            public void onFail(String fail) {
                Log.e(CEFUtils.CEF_LOG, "fail:"+fail);
                try {
                    Map mapFail = new HashMap<String, Object>();
                    mapFail = gson.fromJson(fail, mapFail.getClass());
                    codeCompletion.onComplete(mapFail);
                }catch (Exception e){
                    Log.e(CEFUtils.CEF_LOG, "onFail:"+e.toString());
                }
            }
        });
    }

    /**
     * check Verify code
     * @param phoneNumber The phone number to verify.
     * @param code verifying code
     * @param channel One of CEFOTPChannel.
     * @see CEFOTPChannel#CEFOTPChannel_SMS
     * @param codeCompletion listener
     */
    public void verifyOTPCode(String phoneNumber,
                              String code ,
                              CEFOTPChannel channel,
                              final CEFVerifyOTPCodeListener codeCompletion) {
        String url = CEFUtils.getURL() + CEFConstants.CEFSERVICE_OTP_CHECK;
        String channelName = "";
        switch (channel) {
            case CEFOTPChannel_SMS:
                channelName = "sms";
                break;
        }
        JsonObject json = new JsonObject();
        json.addProperty("phoneNumber", phoneNumber);
        json.addProperty("channel", channelName);
        json.addProperty("code", code);
        CEFHttpsApi.getInstance().getRequestWithURL(mContext,
                CEFHttpsApi.Type.POST,
                json,
                url,
                new CEFCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String state = json.getString("state");
                    switch (state) {
                        case "SUCCESS":
                            codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultSuccess, response);
                            break;
                        case "WRONG_CODE":
                            codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultWrongCode, response);
                            break;
                        case "CODE_EXPIRED":
                            codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultExpired, response);
                            break;
                        default:
                            codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultUnknown, response);
                            break;
                    }
                } catch (JSONException e) {
                    Log.e(CEFUtils.CEF_LOG, e.toString());
                    codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultUnknown, e.toString());
                }
            }

            @Override
            public void onFail(String fail) {
                codeCompletion.onComplete(CEFOTPResultCode.CEFOTPResultUnknown, fail);
            }
        });
    }
}
