//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFResponseSocialLogin.h"


@implementation CEFResponseSocialLogin
- (NSString *)description
{
    return [NSString stringWithFormat:@"resultCode=%ld; resultDescription=%@; channel=%ld; accessToken=%@; Id=%@; refreshToken=%@; expirationDate=%@",(long)self.resultCode,self.resultDescription,(long)self.channel,self.accessToken,self.ID,self.refreshToken,self.expirationDate];
}

@end
