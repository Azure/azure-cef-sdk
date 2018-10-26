
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CEFHttpsApi {
    private static volatile CEFHttpsApi instance;
    private static OkHttpClient okHttpClient = null;

    private CEFHttpsApi() {
    }

    public static CEFHttpsApi getInstance() {
        if (null == instance) {
            synchronized (CEFHttpsApi.class) {
                if (null == instance) {
                    instance = new CEFHttpsApi();
                    okHttpClient = new OkHttpClient.Builder()
                            .build();
                }
            }
        }
        return instance;
    }

    public void getRequestWithURL(Context context, Type type, JsonObject json, String url, final CEFCallback mCEFCallback) {
        try {
            if (!isNetworkConnected(context)){
                Log.e(CEFUtils.CEF_LOG, "network is unConnected.");
                return;
            }
        } catch (Exception e) {
            Log.e(CEFUtils.CEF_LOG, "please add uses-permission(android.permission.ACCESS_NETWORK_STATE) in your Manifest.");
        }

        if (isNotCorrectConfig(context)) return;
        if (TextUtils.isEmpty(url)) {
            Log.e(CEFUtils.CEF_LOG, "url can not be null");
            return;
        }
        switch (type) {
            case GET:
                getRequestGet(context, url, mCEFCallback);
                break;
            case POST:
                if (null != json) {
                    getRequestPost(context, json, url, mCEFCallback);
                } else {
                    Log.e(CEFUtils.CEF_LOG, "while Type is POST, json can not be null");
                }
                break;
        }
    }

    private void getRequestPost(Context context, JsonObject json, String url, final CEFCallback mCEFCallback) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),
                String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Account", CEFUtils.getLocalAccount(context))
                .addHeader("Authorization", CEFUtils.prepareSharedAccessToken(context))
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMsg = "{" +
                        "errorMessage:'" + e.toString() + '\'' +
                        '}';
                mCEFCallback.onFail(errorMsg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseLabel = response.body().string();
                if (response.isSuccessful()) {
                    mCEFCallback.onSuccess(responseLabel);
                } else {
                    mCEFCallback.onFail(responseLabel);
                }
            }
        });
    }

    private boolean isNotCorrectConfig(Context context) {
        if (TextUtils.isEmpty(CEFUtils.getLocalAccount(context)) ||
                TextUtils.isEmpty(CEFUtils.getSasKeyName(context)) ||
                TextUtils.isEmpty(CEFUtils.getSasKey(context))) {
            Log.e(CEFUtils.CEF_LOG, CEFErrorMessages.CEFEM_CEFACCOUNT_NOTSETTED);
            return true;
        }
        return false;
    }

    private void getRequestGet(Context context, String url, final CEFCallback mCEFCallback) {
        Request request = new Request.Builder()
                .get()
                .addHeader("Account", CEFUtils.getAccountName())
                .addHeader("Authorization", CEFUtils.prepareSharedAccessToken(context))
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String errorMsg = "{" +
                        "errorMessage:'" + e.toString() + '\'' +
                        '}';
                mCEFCallback.onFail(errorMsg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseLabel = response.body().string();
                if (response.isSuccessful()) {
                    mCEFCallback.onSuccess(responseLabel);
                } else {
                    mCEFCallback.onFail(responseLabel);
                }
            }
        });
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public enum Type {
        POST, GET
    }
}
