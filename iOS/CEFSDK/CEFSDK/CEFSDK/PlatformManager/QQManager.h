//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/TencentOAuth.h>
#import "CEFResponseSocialLogin.h"


typedef void(^GetChannellLoginCompletion)(CEFResponseSocialLogin *result);

@interface QQManager : NSObject<TencentSessionDelegate,QQApiInterfaceDelegate>

@property(strong,nonatomic) TencentOAuth *tencentOAuth;

@property (nonatomic,strong) CEFResponseSocialLogin *response;
@property (nonatomic,strong) GetChannellLoginCompletion completion;

@property (nonatomic,strong) NSString * appId;

-(void)registPlatformSDK:(NSString *)appId;
-(void)sendReq;


@end
