//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

package com.microsoft.cef.prodsdk.bean;

import java.util.Map;

public class CEFSocialLoginProfile {
    private CEFSocialLoginChannel channelName;
    private Map channelProperties;

    public CEFSocialLoginChannel getChannelName() {
        return channelName;
    }

    public void setChannelName(CEFSocialLoginChannel channelName) {
        this.channelName = channelName;
    }

    public Map getChannelProperties() {
        return channelProperties;
    }

    public void setChannelProperties(Map channelProperties) {
        this.channelProperties = channelProperties;
    }

    @Override
    public String toString() {
        return "{" +
                "channelName:'" + channelName + '\'' +
                ", channelProperties:'" + channelProperties + '\'' +
                '}';
    }
}
