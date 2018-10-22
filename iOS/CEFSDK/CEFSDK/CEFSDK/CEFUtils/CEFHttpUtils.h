//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>

#define NSStringIsNullOrEmpty(str) ({ NSString *_str=(str); ((_str==nil) || [_str isEqualToString:@""]);})


@interface CEFHttpUtils: NSObject
    
+ (NSMutableURLRequest *) getRequestWithURL: (NSURL *)url
                                     Method: (NSString *)method
                                     Params: (NSDictionary *)dictParams;

+ (NSString *) PrepareSharedAccessToken;

+ (NSString *) signString:(NSString*) str withKey: (NSString*) key;

+ (NSString*) urlEncode: (NSString*)urlString;

+ (NSError*) handleServerError: (NSURLResponse*)response errorMessage:(NSString * _Nullable) errorMessage;

@end

