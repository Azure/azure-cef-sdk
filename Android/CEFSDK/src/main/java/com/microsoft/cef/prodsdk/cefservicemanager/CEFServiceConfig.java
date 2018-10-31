//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.cefservicemanager;

import android.text.TextUtils;

import com.microsoft.cef.prodsdk.utils.CEFErrorMessages;
import com.microsoft.cef.prodsdk.utils.CEFUtils;

public class CEFServiceConfig {
    private static CEFServiceConfig instance;

    private CEFServiceConfig() {

    }

    public static CEFServiceConfig getInstance() {
        if (null == instance) {
            synchronized (CEFServiceConfig.class) {
                if (null == instance) {
                    instance = new CEFServiceConfig();
                }
            }
        }
        return instance;
    }

    /**
     * @param accountName CEFAccountName.
     * @param sasKey      CEFSASKey.
     * @param sasKeyName  CEFSASKeyName.
     */
    public void setCEFAccountWithName(String accountName, String sasKey, String sasKeyName) {
        setCEFAccountWithName(accountName, sasKey, sasKeyName, CEFUtils.getURL());
    }

    /**
     * @param accountName    CEFAccountName.
     * @param sasKey         CEFSASKey.
     * @param sasKeyName     CEFSASKeyName.
     * @param serverEndpoint CEFServer Endpoint.
     */
    public void setCEFAccountWithName(String accountName, String sasKey, String sasKeyName, String serverEndpoint) {
        if (TextUtils.isEmpty(accountName) ||
                TextUtils.isEmpty(sasKey) ||
                TextUtils.isEmpty(sasKeyName) ||
                TextUtils.isEmpty(serverEndpoint)) {
            CEFUtils.illegalArgument(CEFErrorMessages.CEFEM_CEFACCOUNT_NOTSETTED);
        }
        CEFUtils.setAccountName(accountName);
        CEFUtils.setSasKey(sasKey);
        CEFUtils.setSasKeyName(sasKeyName);
        CEFUtils.setServerEndpoint(serverEndpoint);
    }
}
