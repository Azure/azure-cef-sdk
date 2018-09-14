//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFServiceOTP.h"
#import "CEFConstants.h"
#import "CEFHttpUtils.h"
#import "CEFServiceConfig.h"


@implementation CEFServiceOTP

+ (instancetype)defaultManager {
    static CEFServiceOTP * _instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[self alloc] init];
    });
    return _instance;
}


- (void) generateOTPCode:(NSString*)phoneNumber
            templateName:(NSString*)templateName
              expireTime:(int)expireTime
              codeLength:(int)codeLength
                 channel:(CEFOTPChannel)channel
              completion:(GenerateOTPCodeCompletion)completion{
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:CEFService_OTP_Start,CEFConfigManager.CEFServerEndpoint,CEFService_APIVersion]];
    
    NSString* channelName = @"";
    switch (channel) {
        case CEFOTPChannel_SMS:
            channelName = @"sms";
            break;
        default:
            break;
    }
    
    
    NSDictionary *dictPramas = @{
                                 @"phoneNumber":phoneNumber,
                                 @"templateName":templateName,
                                 @"expireTime":[NSNumber numberWithInt:expireTime],
                                 @"codeLength":[NSNumber numberWithInt:codeLength],
                                 @"channel":channelName
                                 };
    
    
    NSMutableURLRequest *request = [CEFHttpUtils getRequestWithURL:url Method:@"POST" Params:dictPramas];
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *sessionDataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        
        if(error){
            return completion(nil,error);
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
                
                
                return completion(nil,[CEFHttpUtils handleServerError:httpResponse errorMessage:errorMessage]);
            }
            

            completion(dict,error);
            
        }else {
            completion(nil,error);
        }
    }];
    [sessionDataTask resume];
    
}

- (void) verifyOTPCode:(NSString*)phoneNumber
                  code:(NSString*)code
               channel:(CEFOTPChannel)channel
            completion:(VerifyOTPCodeCompletion)completion{
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:CEFService_OTP_Check,CEFConfigManager.CEFServerEndpoint,CEFService_APIVersion]];
    
    NSString* channelName = @"";
    switch (channel) {
        case CEFOTPChannel_SMS:
            channelName = @"sms";
            break;
        default:
            break;
    }
    
    
    NSDictionary *dictPramas = @{
                                 @"phoneNumber":phoneNumber,
                                 @"channel":channelName,
                                 @"code":code
                                 };
    
    
    NSMutableURLRequest *request = [CEFHttpUtils getRequestWithURL:url Method:@"POST" Params:dictPramas];
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *sessionDataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        
        if(error){
            return completion(CEFOTPResultUnknown,error);
        }
        

        if(response)
        {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data?:[NSData data] options:(NSJSONReadingMutableLeaves) error:nil];

            NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
            
            NSString* trackingId = [[httpResponse allHeaderFields]objectForKey:@"x-ms-request-id"];
            NSLog(@"$$CEFSDK$$ %s TrackingId: %@",__func__,trackingId);
            
            if(!(httpResponse.statusCode >=200 && httpResponse.statusCode <300))
            {
                NSString* errorMessage = dict?[dict objectForKey:@"errorMessage"]:@"";
                
                return completion(CEFOTPResultUnknown,[CEFHttpUtils handleServerError:httpResponse errorMessage:errorMessage]);
            }
            

            NSString* state =[dict objectForKey:@"state"];
            if([state isEqualToString:@"SUCCESS"] )
            {
                return completion(CEFOTPResultSuccess,error);
            }else if([state isEqualToString:@"WRONG_CODE"] )
            {
                return completion(CEFOTPResultWrongCode,error);
            }else if([state isEqualToString:@"CODE_EXPIRED"])
            {
                return completion(CEFOTPResultExpired,error);
            }
            else{
                return completion(CEFOTPResultUnknown,error);
            }
            
        }else {
            return completion(CEFOTPResultUnknown,error);
        }
    }];
    [sessionDataTask resume];
    
}

#pragma mark - private functions


@end


