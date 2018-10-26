//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.utils;

public class CEFConstants {

    //--------------CEFServiceManager-----------------------
    private final static String API_VERSION = "2018-10-01";
    final static String CEFSERVICE_DEFAULT_ENDPOINT = "https://cef.chinacloudapi.cn";

    //--------------CEFServiceOTP------------------------
    public final static String CEFSERVICE_OTP_START = "/services/otp/start?api-version="+ API_VERSION;
    public final static String CEFSERVICE_OTP_CHECK = "/services/otp/check?api-version="+ API_VERSION;

    //--------------CEFServiceSocialLogin----------------
    public final static String CEFSERVICE_SOCIAL_LOGIN_USERINFO = "/services/sociallogin/userinfo?api-version="+API_VERSION;
}
