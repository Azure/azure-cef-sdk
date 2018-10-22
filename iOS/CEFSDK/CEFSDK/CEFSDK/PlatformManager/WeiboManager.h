//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "WeiboSDK.h"
#import "CEFResponseSocialLogin.h"

typedef void(^GetChannellLoginCompletion)(CEFResponseSocialLogin *result);

@interface WeiboManager : NSObject<WeiboSDKDelegate>

@property (nonatomic,strong) CEFResponseSocialLogin *response;
@property (nonatomic,strong) GetChannellLoginCompletion completion;

@property (nonatomic,strong) NSString * redirectURL;
@property (nonatomic,strong) NSString * appKey;


-(void)registPlatformSDK:(NSString *)appKey redirectURL:(NSString *)redirectURL;
-(void)sendReq;

@end
