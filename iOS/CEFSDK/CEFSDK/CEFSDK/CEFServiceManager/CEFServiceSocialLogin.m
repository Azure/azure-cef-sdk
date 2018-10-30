//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFServiceSocialLogin.h"
#import "CEFConstants.h"
#import "CEFHttpUtils.h"
#import "CEFServiceConfig.h"
#import "WechatManager.h"
#import "WeiboManager.h"
#import "QQManager.h"

#import "WeiboSDK.h"
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/TencentOAuth.h>

typedef void(^GetChannellLoginCompletion)(CEFResponseSocialLogin *result);

@interface CEFServiceSocialLogin ()

@property(strong,nonatomic)CEFCredential* credential;
@property(strong,nonatomic)WechatManager* wechatmanager;
@property(strong,nonatomic)WeiboManager* weiboManager;
@property(strong,nonatomic)QQManager* qqManager;

@end

@implementation CEFServiceSocialLogin

+ (instancetype)defaultManager {
    static CEFServiceSocialLogin * _instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[self alloc] init];
    });
    return _instance;
}

-(BOOL)handleOpenURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options{
    
    if ([options[UIApplicationOpenURLOptionsSourceApplicationKey] isEqualToString:@"com.sina.weibo"]) {
        return [WeiboSDK handleOpenURL:url delegate:self.weiboManager];
        
    }else if ([options[UIApplicationOpenURLOptionsSourceApplicationKey] isEqualToString:@"com.tencent.xin"]){
        return [WXApi handleOpenURL:url delegate:self.wechatmanager];
        
    }else if ([options[UIApplicationOpenURLOptionsSourceApplicationKey] isEqualToString:@"com.tencent.mqq"]){
        [QQApiInterface handleOpenURL:url delegate:self.qqManager];
        return [TencentOAuth HandleOpenURL:url];
    }
    
    return NO;
}

//  Only weibo HTML page will back here.
-(BOOL)handleOpenURL:(NSURL *)url {
        if ([url.absoluteString hasPrefix:@"wb"]) {
        return [WeiboSDK handleOpenURL:url delegate:self.weiboManager];
    }
    return NO;
}


- (void) setupChannel:(CEFCredential *)credential{
    self.credential = credential;
    self.wechatmanager = [[WechatManager alloc] init];
    self.qqManager = [[QQManager alloc] init];
    self.weiboManager = [[WeiboManager alloc] init];

    if((!NSStringIsNullOrEmpty(credential.wechatAppId)) && (!NSStringIsNullOrEmpty(credential.wechatAppSecret)))
    {
        [self.wechatmanager registPlatformSDK:credential.wechatAppId appSecret:credential.wechatAppSecret];
    }
    if((!NSStringIsNullOrEmpty(credential.qqAppId)))
    {
        [self.qqManager registPlatformSDK:credential.qqAppId];
    }
    if((!NSStringIsNullOrEmpty(credential.weiboAppKey)) && (!NSStringIsNullOrEmpty(credential.weiboRedirectURL)))
    {
        [self.weiboManager registPlatformSDK:credential.weiboAppKey redirectURL:self.credential.weiboRedirectURL];
    }
}

- (void) loginWithChannel:(CEFSocialLoginChannel) channel completion:(LoginWithChannelCompletion)completion {
    [self getTokenWithChannel:channel completion:^(CEFResponseSocialLogin *result) {
        
        if((!result) || result.resultCode != CEFSocialLoginResultSuccess){
            return completion(result,nil);
        }
        
        // get userinfo from CEFService
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:CEFService_SocialLogin_Userinfo,CEFConfigManager.CEFServerEndpoint,CEFService_APIVersion]];
        NSString* channelName = @"";
        switch (channel) {
            case CEFSocialLoginChannel_WeiBo:
                channelName = CEF_Social_Channel_Name_WeiBo;
                break;
            case CEFSocialLoginChannel_WeChat:
                channelName = CEF_Social_Channel_Name_WeChat;
                break;
            case CEFSocialLoginChannel_QQ:
                channelName = CEF_Social_Channel_Name_QQ;
                break;
            default:
                break;
        }
        NSDictionary *dictProperties = @{
                                         @"accessToken":result.accessToken?:@"",
                                         @"id":result.ID?:@"",
                                         @"platform":@"ios"
                                         };
        
        NSDictionary *dictPramas = @{
                                     @"channelName":channelName,
                                     @"channelProperties":dictProperties
                                     };
        NSMutableURLRequest *request = [CEFHttpUtils getRequestWithURL:url Method:@"POST" Params:dictPramas];
        
        NSURLSession *session = [NSURLSession sharedSession];
        NSURLSessionDataTask *sessionDataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
            
            if(error){
                result.resultCode = CEFSocialLoginResultHttpError;
                result.resultDescription = [error localizedDescription];
                return completion(result,nil);
            }
            
            
            if(response)
            {
                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data?:[NSData data] options:(NSJSONReadingMutableLeaves) error:nil];

                NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
                
                NSString* trackingId = [[httpResponse allHeaderFields]objectForKey:@"x-ms-request-id"];
                NSLog(@"$$CEFSDK$$ %s TrackingId: %@",__func__,trackingId);
                
                if(!(httpResponse.statusCode >=200 && httpResponse.statusCode <300))
                {
                    NSString* errorMessage =dict?[dict objectForKey:@"errorMessage"]:nil;
                    result.resultCode = CEFSocialLoginResultHttpError;
                    NSString* resultDescription = [NSString stringWithFormat:@"HttpStatusCode=%ld;ErrorMessage=%@",(long)httpResponse.statusCode,errorMessage];
                    result.resultDescription = resultDescription;
                    return completion(result,nil);
                }
                
                if([NSNull null] == [dict objectForKey:@"description"]){
                    return completion(result,nil)
                }
                CEFSocialLoginProfile* profile = [[CEFSocialLoginProfile alloc] init];
                NSString* channelName = [[dict objectForKey:@"description"] objectForKey:@"channelName"];
                if([channelName isEqualToString: CEF_Social_Channel_Name_WeiBo]){
                    profile.channel = CEFSocialLoginChannel_WeiBo;
                }else if([channelName isEqualToString:CEF_Social_Channel_Name_WeChat]){
                    profile.channel = CEFSocialLoginChannel_WeChat;
                }else if ([channelName isEqualToString:CEF_Social_Channel_Name_QQ]){
                    profile.channel = CEFSocialLoginChannel_QQ;
                }
                profile.channelProperties = [[dict objectForKey:@"description"] objectForKey:@"channelProperties"];
                
                return completion(result,profile);
                
            }else {
                result.resultCode = CEFSocialLoginResultUnknownError;
                return completion(result,nil);
            }
        }];
        [sessionDataTask resume];
    }];
    
}




#pragma mark - private functions

-(void) getTokenWithChannel:(CEFSocialLoginChannel) channel completion:(GetChannellLoginCompletion) completion{
    if (channel == CEFSocialLoginChannel_WeChat) {
        self.wechatmanager.completion = completion;
        [self.wechatmanager sendReq];
    } else if (channel == CEFSocialLoginChannel_WeiBo){
        self.weiboManager.completion = completion;
        [self.weiboManager sendReq];
    }else if(channel == CEFSocialLoginChannel_QQ){
        self.qqManager.completion = completion;
        [self.qqManager sendReq];
    }else{
        NSLog(@"$$CEFSDK$$ %s Channel is not right.",__func__);
    }
}

@end


