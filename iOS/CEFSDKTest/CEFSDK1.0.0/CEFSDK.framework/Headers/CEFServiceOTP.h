//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "CEFResponseOTP.h"

#define CEFOTPManager [CEFServiceOTP defaultManager]

typedef void(^GenerateOTPCodeCompletion)(NSDictionary* result,NSError* _Nullable error);
typedef void(^VerifyOTPCodeCompletion)(CEFOTPResultCode result,NSError* _Nullable error);

@interface CEFServiceOTP : NSObject
/**
 Get a singleton instance
 
 @return A CEFServiceOTP intance.
 */
+ (instancetype)defaultManager;


/**
 Generate OTP code.
 
 @param phoneNumber The phone number to send code for.
 @param templateName OTP template name.
 @param expireTime Expire time.
 @param codeLength Length of OTP code.
 @param channel One of CEFOTPChannel.
 @param completion The completion handler to call when the load request is complete.This completion handler takes the following parameters:
 result:A NSDictionary object which contains info about this OTP code.
 error:An error object that indicates why the request failed, or nil if the request was successful.
 */
- (void) generateOTPCode:(NSString*)phoneNumber
            templateName:(NSString*)templateName
              expireTime:(int)expireTime
              codeLength:(int)codeLength
                 channel:(CEFOTPChannel)channel
              completion:(GenerateOTPCodeCompletion)completion;


/**
 Verify OTP code.
 
 @param phoneNumber The phone number to verify.
 @param code OTP code.
 @param channel One of CEFOTPChannel.
 @param completion The completion handler to call when the load request is complete.This completion handler takes the following parameters:
 result:A CEFOTPResultCode object which indicates verify result.
 error:An error object that indicates why the request failed, or nil if the request was successful.
 */
- (void) verifyOTPCode:(NSString*)phoneNumber
                  code:(NSString*)code
               channel:(CEFOTPChannel)channel
            completion:(VerifyOTPCodeCompletion)completion;


@end
