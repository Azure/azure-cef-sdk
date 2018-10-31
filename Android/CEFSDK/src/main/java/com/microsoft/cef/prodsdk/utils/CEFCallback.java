//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.utils;

public interface CEFCallback {
    void onSuccess(String response);
    void onFail(String fail);
}
