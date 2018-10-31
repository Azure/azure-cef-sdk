//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class CEFUtils {
    public final static String CEF_LOG = "CEF_LOG";
    private final static int TIME_TO_EXPIRED_IN_MINS = 20;
    private final static String SCHEMA = "SharedAccessSignature";
    private final static String SIGN_KEY = "sig";
    private final static String KEY_NAME_KEY = "skn";
    private final static String EXPIRY_KEY = "se";
    private static String accountName = "";
    private static String sasKey = "";
    private static String sasKeyName="";
    private static String serverEndpoint="";

    public static void setServerEndpoint(String serverEndpoint) {
        CEFUtils.serverEndpoint = serverEndpoint;
    }

    static String getAccountName() {
        return accountName;
    }

    public static void setAccountName(String accountName) {
        CEFUtils.accountName = accountName;
    }

    static String getSasKey(Context context) {
        if (!TextUtils.isEmpty(sasKey)) {
            return sasKey;
        }
        if (!TextUtils.isEmpty(getMetaDataValue(context, "cef_saskey"))) {
            return getMetaDataValue(context, "cef_saskey");
        }
        return "";
    }

    public static void setSasKey(String sasKey) {
        CEFUtils.sasKey = sasKey;
    }

    static String getSasKeyName(Context context) {
        if (!TextUtils.isEmpty(sasKeyName)) {
            return sasKeyName;
        }
        if (!TextUtils.isEmpty(getMetaDataValue(context, "cef_saskeyname"))) {
            return getMetaDataValue(context, "cef_saskeyname");
        }
        return "";
    }

    public static void setSasKeyName(String sasKeyName) {
        CEFUtils.sasKeyName = sasKeyName;
    }

    public static void illegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }

    static String getLocalAccount(Context context) {
        if (!TextUtils.isEmpty(accountName)) {
            return accountName;
        }
        if (!TextUtils.isEmpty(getMetaDataValue(context, "cef_accountname"))) {
            return getMetaDataValue(context, "cef_accountname");
        }
        return "";
    }

    public static String getURL() {
        if (TextUtils.isEmpty(serverEndpoint)) {
            return CEFConstants.CEFSERVICE_DEFAULT_ENDPOINT;
        }
        return serverEndpoint;
    }

    static String prepareSharedAccessToken(Context context) {
        long totalSeconds = System.currentTimeMillis() / 1000 + TIME_TO_EXPIRED_IN_MINS * 60;
        String expiresOn = String.valueOf(totalSeconds);
        StringBuilder signContent = new StringBuilder();
        signContent
                .append(EXPIRY_KEY)
                .append("=")
                .append(urlEncode(expiresOn))
                .append("&")
                .append(KEY_NAME_KEY)
                .append("=")
                .append(urlEncode(getSasKeyName(context)));
        String signature = signString(getSasKey(context), String.valueOf(signContent));
        StringBuilder builder = new StringBuilder();
        builder
                .append(SCHEMA)
                .append(" ")
                .append(SIGN_KEY)
                .append("=")
                .append(signature)
                .append("&")
                .append(signContent);
        return String.valueOf(builder);
    }

    private static String signString(String secret, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()), 0).trim();
            return urlEncode(hash);
        } catch (Exception e) {
            Log.e(CEF_LOG, e.toString());
        }
        return "";
    }

    private static String urlEncode(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            Log.e(CEF_LOG, localException.toString());
        }
        return "";
    }

    private static String getMetaDataValue(Context context, String key) {
        Bundle metaValue = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().
                    getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != appInfo) {
                metaValue = appInfo.metaData;
            }
            if (null != metaValue) {
                return metaValue.getString(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(CEF_LOG, CEFErrorMessages.CEFEM_CEFACCOUNT_NOTSETTED);
        }
        return "";
    }
}
