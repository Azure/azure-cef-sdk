//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------


#import "AppDelegate.h"
#import <CEFSDK/CEFSDK.h>
#import "Constants.h"

@interface AppDelegate ()


@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    // -- CEFSDK Begin --
    CEFCredential* cred = [[CEFCredential alloc] init];
    // 设置微信AppId和AppSecret
    [cred setWeChatCredential:Wechat_AppId AppSecret:Wechat_AppSecret];
    // 设置微博的AppKey和RedirectURL
    cred.weiboAppKey = Weibo_AppKey;
    cred.weiboRedirectURL = Weibo_RedirectURL;
    // 设置QQ的AppId
    [cred setQqAppId:QQ_AppId];
    // 初始化 CEFSocialLoginManager 信息
    [CEFSocialLoginManager setupChannel:cred];
    // -- CEFSDK End --
    
    return YES;
}

// -- CEFSDK Begin --
// 必须在AppDelegate.m里面实现这两个函数，否则会导致从微信等第三方App跳转回本App时无法启动等问题
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options {
    return  [CEFSocialLoginManager handleOpenURL:url options:options];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(nullable NSString *)sourceApplication annotation:(id)annotation {
    return  [CEFSocialLoginManager handleOpenURL:url];
}
// -- CEFSDK End --


@end
