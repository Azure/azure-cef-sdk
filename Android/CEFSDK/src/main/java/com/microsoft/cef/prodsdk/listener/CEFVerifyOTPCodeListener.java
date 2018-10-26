//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.listener;

import com.microsoft.cef.prodsdk.bean.CEFOTPResultCode;

public interface CEFVerifyOTPCodeListener {
   /**
    *
    * @param code A CEFOTPResultCode object which indicates verify result.
    * @param msg response msg.
    */
   void onComplete(CEFOTPResultCode code, String msg);
}
