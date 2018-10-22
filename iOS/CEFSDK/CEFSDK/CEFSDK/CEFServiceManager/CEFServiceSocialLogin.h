//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "CEFResponseSocialLogin.h"
#import "CEFSocialLoginProfile.h"
#import "CEFCredential.h"

#define CEFSocialLoginManager [CEFServiceSocialLogin defaultManager]


typedef void(^LoginWithChannelCompletion)(CEFResponseSocialLogin *loginResult,CEFSocialLoginProfile* socialProfile);


@interface CEFServiceSocialLogin : NSObject
/**
 Get a singleton instance
 
 @return A CEFServiceSocialLogin intance.
 */
+ (instancetype)defaultManager;


/**
 Asks the delegate to open a resource identified by URL.

 @param url Url schemas.
 @param options Url options.
 @return YES if the delegate successfully handled the request; NO if the attempt to handle the URL failed.
 */
- (BOOL) handleOpenURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options;

/**
 Asks the delegate to open a resource identified by URL.
 
 @param url Url schemas.
 @return YES if the delegate successfully handled the request; NO if the attempt to handle the URL failed.
 */
- (BOOL) handleOpenURL:(NSURL *)url;

/**
 Setup payment channels by CEFCredential.
 
 @param credential A CEFCredetial instance contains AppKey,AppScerect for different channels. Will only setup channels has valid keys in credential.
 */
- (void) setupChannel:(CEFCredential *)credential;


/**
 Get user information with social channel.
 
 @param channel one of CEFAuthChannel.
 @param completion The completion handler to call when the load request is complete.This completion handler takes the following parameters:
 loginResult:A CEFResponseSocialLogin object which contains resultCode, ID, accessToken, refreshToken etc.
 socialProfile: A CEFSocialLoginProfile object which contains channel and channelProperties. channelPrpperties contains user info from this social channel.
 */
- (void) loginWithChannel:(CEFSocialLoginChannel) channel completion:(LoginWithChannelCompletion)completion;


@end
