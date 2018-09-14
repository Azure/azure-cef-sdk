//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>



@interface CEFCredential : NSObject

#pragma mark - wechat
@property(assign,nonatomic)NSString *wechatAppId;
@property(assign,nonatomic)NSString *wechatAppSecret;

#pragma mark - weibo
@property(assign,nonatomic)NSString *weiboAppKey;
@property(assign,nonatomic)NSString *weiboRedirectURL;

#pragma mark - QQ
@property(assign,nonatomic)NSString *qqAppId;



-(void)setWeChatCredential:(NSString *)appId AppSecret:(NSString *)secret;
-(void)setQQCredential:(NSString *)appId;
-(void)setWeiboCredential:(NSString *)appKey redirectURL:(NSString *)redirectURL;


@end
