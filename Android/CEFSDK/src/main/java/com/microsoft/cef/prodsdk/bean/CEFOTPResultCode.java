//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.bean;



public enum CEFOTPResultCode {
    CEFOTPResultSuccess(0), // Success
    CEFOTPResultWrongCode(-1),// OTP Code is Wrong
    CEFOTPResultExpired(-2), // OTP code expired
    CEFOTPResultUnknown(-3);// Unknown

    private int otpResultCodeValue = 0;

    CEFOTPResultCode(int otpResultCodeValue) {
        this.otpResultCodeValue = otpResultCodeValue;
    }

}
