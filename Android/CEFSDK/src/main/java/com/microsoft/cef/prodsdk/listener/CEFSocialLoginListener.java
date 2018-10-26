
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.listener;

import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;

public interface CEFSocialLoginListener {
    void onComplete(CEFResponseSocialLogin result);
}
