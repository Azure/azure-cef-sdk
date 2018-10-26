//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.bean;



public enum CEFOTPChannel {
    CEFOTPChannel_SMS(21);
    private int otpChannelValue = 0;
    CEFOTPChannel(int otpChannelValue) {
        this.otpChannelValue = otpChannelValue;
    }
}
