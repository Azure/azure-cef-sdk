//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "WeiboManager.h"
#import "CEFErrorMessages.h"
#import "CEFHttpUtils.h"

@implementation WeiboManager

-(void)registPlatformSDK:(NSString *)appKey redirectURL:(NSString *)redirectURL {
    self.redirectURL = redirectURL;
    self.appKey = appKey;
    [WeiboSDK registerApp:appKey];
}

-(void)sendReq {
    self.response = [[CEFResponseSocialLogin alloc] init];
    self.response.channel = CEFSocialLoginChannel_WeiBo;
    
    if(NSStringIsNullOrEmpty(self.appKey) || NSStringIsNullOrEmpty(self.redirectURL)){
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.response.resultDescription = CEFEM_CEFSocialLogin_WeiboNotConfig;
        self.completion(self.response);
        return;
    }
    
    [self weiboLogin:self.appKey redirectURL:self.redirectURL];

}


-(void)weiboLogin:(NSString *)appkey redirectURL:(NSString *)redirectURL {
    
    WBAuthorizeRequest *request = [WBAuthorizeRequest request];
    request.redirectURI = redirectURL;
    request.scope = @"all";
    request.userInfo = @{
                         @"SSO_FROM": @"CEFSDK"
                         };
    [WeiboSDK sendRequest:request];
}

#pragma mark - Weibo Methods


- (void)didReceiveWeiboResponse:(WBBaseResponse *)response
{
    if ([response isKindOfClass:WBAuthorizeResponse.class])
    {
        WBAuthorizeResponse* resp = (WBAuthorizeResponse *)response;
        
        if(resp.statusCode == WeiboSDKResponseStatusCodeSuccess)
        {
            self.response.resultCode = CEFSocialLoginResultSuccess;
            self.response.ID = resp.userID;
            self.response.accessToken = resp.accessToken;
            self.response.refreshToken = resp.refreshToken;
            self.response.expirationDate = resp.expirationDate;
        }else if(resp.statusCode == WeiboSDKResponseStatusCodeUserCancel){
            self.response.resultCode = CEFSocialLoginResultUserCancel;
        }else{
            self.response.resultCode = CEFSocialLoginResultTokenFailure;
        }
        
        self.completion(self.response);

    }else {
        self.response.resultCode = CEFSocialLoginResultUnknownError;
        self.completion(self.response);
    }
}

- (void)didReceiveWeiboRequest:(WBBaseRequest *)request {
    
}


@end
















