//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFCredential.h"
#import "CEFConstants.h"

@implementation CEFCredential


-(void)setWeChatCredential:(NSString *)appId AppSecret:(NSString *)secret{
    self.wechatAppId = appId;
    self.wechatAppSecret = secret;
}

-(void)setQQCredential:(NSString *)appId{
    self.qqAppId = appId;
}

-(void)setWeiboCredential:(NSString *)appKey redirectURL:(NSString *)redirectURL{
    self.weiboAppKey = appKey;
    self.weiboRedirectURL = redirectURL;
}

@end
