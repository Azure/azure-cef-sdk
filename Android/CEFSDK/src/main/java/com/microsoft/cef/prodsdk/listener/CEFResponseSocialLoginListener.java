
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.listener;

import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginProfile;

public interface CEFResponseSocialLoginListener {
    /**
     * @param result  A CEFResponseSocialLogin object which contains resultCode, ID, accessToken, refreshToken etc.
     * @param socialLoginProfile A CEFSocialLoginProfile object which contains channel and channelProperties.
     *                          channelProperties contains user info from this social channel.
     */
    void onComplete(CEFResponseSocialLogin result, CEFSocialLoginProfile socialLoginProfile);
}
