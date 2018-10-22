//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "CEFResponseSocialLogin.h"

@interface CEFSocialLoginProfile: NSObject
@property (nonatomic, assign) CEFSocialLoginChannel channel;
@property (nonatomic, strong) NSDictionary *channelProperties;
@end

