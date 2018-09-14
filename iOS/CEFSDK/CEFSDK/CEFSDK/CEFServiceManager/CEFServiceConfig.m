//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFServiceConfig.h"
#import "CEFConstants.h"

@implementation CEFServiceConfig

+ (instancetype)defaultManager{
    static CEFServiceConfig * _instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[self alloc] init];
        NSDictionary *infoPlistDict = [[NSBundle mainBundle] infoDictionary];
        _instance.CEFAccountName = infoPlistDict[@"CEFAccount"];
        _instance.CEFSASKey = infoPlistDict[@"CEFSASKey"];
        _instance.CEFSASKeyName = infoPlistDict[@"CEFSASKeyName"];
        _instance.CEFServerEndpoint = infoPlistDict[@"CEFServerEndpoint"]?: CEFService_DefaultEndpoint;
    });
    return _instance;
}


-(void)setCEFAccount:(NSString *)accoutName CEFSASKey:(NSString *)sasKey CEFSASKeyName:(NSString *)sasKeyName CEFServerEndpoint:(NSString *)  serverEndpoint{
    self.CEFAccountName = accoutName;
    self.CEFSASKey = sasKey;
    self.CEFSASKeyName = sasKeyName;
    self.CEFServerEndpoint = serverEndpoint;
}

-(void)setCEFAccount:(NSString *)accoutName CEFSASKey:(NSString *)sasKey CEFSASKeyName:(NSString *)sasKeyName{
    self.CEFAccountName = accoutName;
    self.CEFSASKey = sasKey;
    self.CEFSASKeyName = sasKeyName;
}

@end


