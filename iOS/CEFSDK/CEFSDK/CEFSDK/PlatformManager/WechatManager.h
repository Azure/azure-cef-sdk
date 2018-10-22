//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "WXApi.h"
#import "CEFResponseSocialLogin.h"

typedef void(^GetChannellLoginCompletion)(CEFResponseSocialLogin *result);

@interface WechatManager : NSObject<WXApiDelegate>

@property (nonatomic,strong) NSString * appId;
@property (nonatomic,strong) NSString * appSecret;

@property (nonatomic,strong) CEFResponseSocialLogin *response;
@property (nonatomic,strong) GetChannellLoginCompletion completion;



-(void)registPlatformSDK:(NSString *)appId appSecret:(NSString *)appSecret;
-(void)sendReq;

@end
