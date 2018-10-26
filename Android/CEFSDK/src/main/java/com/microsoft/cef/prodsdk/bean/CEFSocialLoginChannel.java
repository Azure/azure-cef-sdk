
//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------
package com.microsoft.cef.prodsdk.bean;

public enum CEFSocialLoginChannel {
    CEFSocialLoginChannel_WeChat(1),
    CEFSocialLoginChannel_QQ(2),
    CEFSocialLoginChannel_Weibo(3);

    private int channelValue = 0;

    CEFSocialLoginChannel(int channelValue) {
        this.channelValue = channelValue;
    }

}
