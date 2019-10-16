
package com.bluecloud.utils;


import com.bluecloud.model.Config;


import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;


public class CEFUtils {
    public final static String CEF_LOG = "CEF_LOG";
    private final static int TIME_TO_EXPIRED_IN_MINS = 20;
    private final static String SCHEMA = "SharedAccessSignature";
    private final static String SIGN_KEY = "sig";
    private final static String KEY_NAME_KEY = "skn";
    private final static String EXPIRY_KEY = "se";
    // private static String sasKey = "";
    // private static String sasKeyName="";
    // private static String serverEndpoint="";

    // public static void setServerEndpoint(String serverEndpoint) {
    //     CEFUtils.serverEndpoint = serverEndpoint;
    // }

    static String getAccountName() {
        return Config.cef_accountname;
    }

    static String getSasKey() {

        return Config.cef_saskey;
    }


    static String getSasKeyName() {

        return Config.cef_saskeyname;
    }

    public static void illegalArgument(String msg) {
        throw new IllegalArgumentException(msg);
    }

    static String getLocalAccount() {
        return Config.cef_accountname;
    }

    public static String getURL() {
        return Config.cef_accountname;
    }

    public static String prepareSharedAccessToken() {
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
                .append(urlEncode(getSasKeyName()));
        String signature = signString(getSasKey(), String.valueOf(signContent));
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

            String hash = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes())).trim();
            return urlEncode(hash);
        } catch (Exception e) {
            // Log.e(CEF_LOG, e.toString());
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
          //  Log.e(CEF_LOG, localException.toString());
        }
        return "";
    }

}
