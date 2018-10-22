//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------


#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, CEFSocialLoginChannel) {
    CEFSocialLoginChannel_WeChat     = 1,
    CEFSocialLoginChannel_QQ         = 2,
    CEFSocialLoginChannel_WeiBo      = 3,
};

typedef NS_ENUM(NSInteger, CEFSocialLoginResultCode) {
    CEFSocialLoginResultSuccess = 0,         // Success
    CEFSocialLoginResultTokenFailure = -1,   // Failed
    CEFSocialLoginResultUserCancel = -2,     // User Cancel
    CEFSocialLoginResultHttpError = -3,      // Http Error
    CEFSocialLoginResultUnknownError = -4,   // Unkown Error
    
};

@interface CEFResponseSocialLogin : NSObject
@property (nonatomic, assign) CEFSocialLoginResultCode resultCode;
@property (nonatomic, strong) NSString* resultDescription;
@property (nonatomic, assign) CEFSocialLoginChannel channel;
@property (nonatomic, strong) NSString* accessToken;
@property (nonatomic, strong) NSString* ID;
@property (nonatomic, strong) NSString* refreshToken;
@property (nonatomic, strong) NSDate* expirationDate;
@end


