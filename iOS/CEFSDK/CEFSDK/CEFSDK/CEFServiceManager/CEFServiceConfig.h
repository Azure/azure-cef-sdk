//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import <Foundation/Foundation.h>
#import "CEFCredential.h"


#define CEFConfigManager [CEFServiceConfig defaultManager]

@interface CEFServiceConfig : NSObject


@property(assign,nonatomic)NSString *CEFAccountName;
@property(assign,nonatomic)NSString *CEFSASKey;
@property(assign,nonatomic)NSString *CEFSASKeyName;

@property(assign,nonatomic)NSString *CEFServerEndpoint;

/**
 Get a singleton instance
 
 @return A CEFServiceConfig intance.
 */
+ (instancetype)defaultManager;



/**
 Set CEF Acccount.

 @param accoutName CEFAccountName.
 @param sasKey CEFSASKey.
 @param sasKeyName CEFSASKeyName.
 @param serverEndpoint CEFServer Endpoint.
 */
- (void)setCEFAccount:(NSString *)accoutName CEFSASKey:(NSString *)sasKey CEFSASKeyName:(NSString *)sasKeyName CEFServerEndpoint:(NSString *)serverEndpoint;


/**
 Set CEF Acccount.

 @param accoutName CEFAccountName.
 @param sasKey CEFSASKey.
 @param sasKeyName CEFSASKeyName.
 */
- (void)setCEFAccount:(NSString *)accoutName CEFSASKey:(NSString *)sasKey CEFSASKeyName:(NSString *)sasKeyName;



@end


