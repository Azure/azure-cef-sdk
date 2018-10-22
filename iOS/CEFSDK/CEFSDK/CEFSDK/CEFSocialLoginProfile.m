//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFSocialLoginProfile.h"


@implementation CEFSocialLoginProfile
- (NSString *)description
{
    return [NSString stringWithFormat:@"channel=%ld; channelProperties=%@",(long)self.channel,self.channelProperties];
}
@end

