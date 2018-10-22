//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "WechatManager.h"
#import "CEFErrorMessages.h"
#import "CEFHttpUtils.h"

@implementation WechatManager

-(void)registPlatformSDK:(NSString *)appId appSecret:(NSString *)appSecret{
    self.appId = appId;
    self.appSecret = appSecret;
    
    [WXApi registerApp:self.appId];

}

-(void)sendReq{
    self.response = [[CEFResponseSocialLogin alloc] init];
    self.response.channel = CEFSocialLoginChannel_WeChat;
    
    if(NSStringIsNullOrEmpty(self.appId) || NSStringIsNullOrEmpty(self.appSecret)){
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.response.resultDescription = CEFEM_CEFSocialLogin_WechatNotConfig;
        self.completion(self.response);
        return;
    }
    
    if ([WXApi isWXAppInstalled]) {
        [self wechatLogin:self.appId];
    }else{
        self.response.resultCode = CEFSocialLoginResultTokenFailure;
        self.response.resultDescription = CEFEM_CEFSocialLogin_WechatNotInstalled;
        self.completion(self.response);
    }
}





-(void)wechatLogin:(NSString *)appId{
    
    SendAuthReq *req = [[SendAuthReq alloc]init];
    req.scope = @"snsapi_userinfo";
    req.openID = appId;
    req.state = @"CEF_authorization_state";
    
    [WXApi sendReq:req];
}

- (void)loginSuccessByCode:(NSString *)code { 
    
    NSString *url =[NSString stringWithFormat:
                    @"https://api.weixin.qq.com/sns/oauth2/access_token?appid=%@&secret=%@&code=%@&grant_type=authorization_code",
                    self.appId,self.appSecret,code];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSURL *zoneUrl = [NSURL URLWithString:url];
        NSString *zoneStr = [NSString stringWithContentsOfURL:zoneUrl encoding:NSUTF8StringEncoding error:nil];
        NSData *data = [zoneStr dataUsingEncoding:NSUTF8StringEncoding];
        dispatch_async(dispatch_get_main_queue(), ^{
            
            if (data)
            {
                NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data
                                                                    options:NSJSONReadingMutableContainers error:nil];
                                
                if(dic[@"access_token"])
                {
                    NSDate* now = [NSDate date];
                    NSDate* expired = [now dateByAddingTimeInterval:[dic[@"expires_in"] intValue]];
                    self.response.resultCode = CEFSocialLoginResultSuccess;
                    self.response.accessToken = dic[@"access_token"];
                    self.response.ID = dic[@"openid"];
                    self.response.refreshToken = dic[@"refresh_token"];
                    self.response.expirationDate = expired;
                    
                    return self.completion(self.response);
                }
                else{
                    self.response.resultCode = CEFSocialLoginResultTokenFailure;
                    self.response.resultDescription = dic[@"errmsg"];
                    return self.completion(self.response);
                }
            }
            self.response.resultCode = CEFSocialLoginResultUnknownError;
            return self.completion(self.response);
        });
    });

}

#pragma mark - WeChat Methods

-(void) onResp:(BaseResp*)resp{
    
    if ([resp isKindOfClass:[SendAuthResp class]]) {
        self.response.resultDescription = resp.errStr;
        
        if (resp.errCode == WXSuccess) {
            SendAuthResp *authResp = (SendAuthResp *)resp;
            [self loginSuccessByCode:authResp.code];
        }else if(resp.errCode == WXErrCodeUserCancel){
            self.response.resultCode = CEFSocialLoginResultUserCancel;
            self.completion(self.response);
        }else{
            self.response.resultCode = CEFSocialLoginResultTokenFailure;
            self.completion(self.response);
        }
    }else{
        self.response.resultCode = CEFSocialLoginResultUnknownError;
        self.completion(self.response);
    }
    
}
@end
