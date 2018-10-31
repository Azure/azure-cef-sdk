
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.bean;

public enum CEFSocialLoginResultCode {
    CEFSocialLoginResultSuccess(0), // Success
    CEFSocialLoginResultTokenFailure(-1),// Failed
    CEFSocialLoginResultUserCancel(-2), // User Cancel
    CEFSocialLoginResultHttpError(-3), // net error
    CEFSocialLoginResultUnknown(-4);// Unknown

    private int loginResultCodeValue = 0;

    CEFSocialLoginResultCode(int loginResultCodeValue) {
        this.loginResultCodeValue = loginResultCodeValue;
    }
}
