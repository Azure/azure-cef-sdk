//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "QQManager.h"
#import "CEFErrorMessages.h"
#import "CEFHttpUtils.h"

@implementation QQManager


-(void)registPlatformSDK:(NSString *)appId {
    self.appId = appId;
    self.tencentOAuth = [[TencentOAuth alloc] initWithAppId:appId andDelegate:self];
}

-(void)sendReq{
    self.response = [[CEFResponseSocialLogin alloc] init];
    self.response.channel = CEFSocialLoginChannel_QQ;
    
    if(NSStringIsNullOrEmpty(self.appId)){
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.response.resultDescription = CEFEM_CEFSocialLogin_QQNotConfig;
        self.completion(self.response);
        return;
    }
    
    
    [self QQLogin];
}


-(void)QQLogin{
    NSArray *permissions = [NSArray arrayWithObjects:kOPEN_PERMISSION_GET_INFO, kOPEN_PERMISSION_GET_USER_INFO, kOPEN_PERMISSION_GET_SIMPLE_USER_INFO, nil];
    [self.tencentOAuth authorize:permissions inSafari:NO];
}

#pragma mark - QQ Methods

- (void)tencentDidLogin{
    
    if (_tencentOAuth.accessToken) {
        self.response.resultCode = CEFSocialLoginResultSuccess;
        self.response.accessToken = self.tencentOAuth.accessToken;
        self.response.ID = self.tencentOAuth.openId;
        self.response.expirationDate = self.tencentOAuth.expirationDate;

        self.completion(self.response);
        
    }else{
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.completion(self.response);

    }
    
}

- (void)tencentDidNotLogin:(BOOL)cancelled{
    if (cancelled) {
        self.response.resultCode = CEFSocialLoginResultUserCancel;
        self.completion(self.response);
        
    }else{
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.completion(self.response);
    }
}

- (void)tencentDidNotNetWork{
    self.response.resultCode = CEFSocialLoginResultTokenFailure;
    self.response.resultDescription = CEFEM_CEFSocialLogin_QQDidNotNetWork;
    self.completion(self.response);
}

- (BOOL)tencentNeedPerformIncrAuth:(TencentOAuth *)tencentOAuth withPermissions:(NSArray *)permissions{
    
    [tencentOAuth incrAuthWithPermissions:permissions];
    return NO;
}


- (void)tencentDidUpdate:(TencentOAuth *)tencentOAuth{
    if (tencentOAuth.accessToken)
    {
        self.response.resultCode = CEFSocialLoginResultSuccess;
        self.response.accessToken = tencentOAuth.accessToken;
        self.response.ID = tencentOAuth.openId;
        self.response.expirationDate = tencentOAuth.expirationDate;
        self.completion(self.response);
    }
    else
    {
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.response.resultDescription = CEFEM_CEFSocialLogin_QQUpdateFailed;
        self.completion(self.response);
        
    }
    
}

- (void)tencentFailedUpdate:(UpdateFailType)reason{
    self.response.resultCode = CEFSocialLoginResultTokenFailure;
    self.response.resultDescription = CEFEM_CEFSocialLogin_QQUpdateFailed;
    self.completion(self.response);
}


- (void)isOnlineResponse:(NSDictionary *)response {
    
}

- (void)onReq:(QQBaseReq *)req {
    
}

- (void)onResp:(QQBaseResp *)resp {
    
}

@end
