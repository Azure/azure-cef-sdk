//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "CEFHttpUtils.h"
#import "CEFServiceConfig.h"
#import "CEFConstants.h"
#import <CommonCrypto/CommonHMAC.h>
#import <CommonCrypto/CommonDigest.h>
#import "CEFErrorMessages.h"

@implementation CEFHttpUtils

static const NSInteger _timeTOExpiredInMins = 20;
static const NSString* _schema = @"SharedAccessSignature";
static const NSString* _signKey = @"sig";
static const NSString* _keyNameKey = @"skn";
static const NSString* _expiryKey = @"se";


+ (NSMutableURLRequest *) getRequestWithURL: (NSURL *)url
                                     Method: (NSString *)method
                                     Params: (NSDictionary *)dictParams{
    
    NSAssert(CEFConfigManager.CEFAccountName && CEFConfigManager.CEFSASKey && CEFConfigManager.CEFSASKeyName,CEFEM_CEFAccount_NotSetted);

    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = method;
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    NSString * accountName = CEFConfigManager.CEFAccountName;
    [request setValue:accountName forHTTPHeaderField:@"Account"];
    [request setValue:[self PrepareSharedAccessToken] forHTTPHeaderField:@"Authorization"];
    
    if(dictParams){
        NSData *data = [NSJSONSerialization dataWithJSONObject:dictParams options:0 error:nil];
        request.HTTPBody = data;
    }
    return request;
}

+ (NSString *)PrepareSharedAccessToken{
    NSTimeInterval interval = [[NSDate date] timeIntervalSince1970];
    int totalSeconds = interval+ _timeTOExpiredInMins*60;
    NSString* expiresOn = [NSString stringWithFormat:@"%d",totalSeconds];
    
    NSString* signContent=[NSString stringWithFormat:@"%@=%@&%@=%@",_expiryKey,[self urlEncode:expiresOn],_keyNameKey,[self urlEncode: CEFConfigManager.CEFSASKeyName]];
    NSString* signature = [self signString:signContent withKey:CEFConfigManager.CEFSASKey];
    
    NSString* token =[NSString stringWithFormat:@"%@ %@=%@&%@",_schema,_signKey,signature,signContent];
    return token;
}

+ (NSString *)signString:(NSString*) str withKey: (NSString*) key{
    const char *cData = [str cStringUsingEncoding:NSUTF8StringEncoding];
    const char *cKey = [key cStringUsingEncoding:NSASCIIStringEncoding];

    unsigned char cHMAC[CC_SHA256_DIGEST_LENGTH];


    CCHmac(kCCHmacAlgSHA256, cKey, strlen(cKey), cData, strlen(cData), cHMAC);
    
    NSData *HMAC = [[NSData alloc] initWithBytes:cHMAC length:CC_SHA256_DIGEST_LENGTH];
    
    
    NSString* base64Signature = [HMAC base64EncodedStringWithOptions:0];
    
    NSString* signature = [self urlEncode:base64Signature];
    
    return signature;
}



+ (NSString*) urlEncode: (NSString*)urlString{
    NSMutableString *output = [NSMutableString string];
    const unsigned char *source = (const unsigned char *)[urlString UTF8String];
    unsigned long int sourceLen = strlen((const char *)source);
    for (int i = 0; i < sourceLen; ++i) {
        const unsigned char thisChar = source[i];
        if (thisChar == ' '){
            [output appendString:@"+"];
        } else if (
                   thisChar == '!' || thisChar == '(' || thisChar == ')' || thisChar == '*' ||
                   thisChar == '-' || thisChar == '.' || thisChar == '_' ||
                   (thisChar >= 'a' && thisChar <= 'z') ||
                   (thisChar >= 'A' && thisChar <= 'Z') ||
                   (thisChar >= '0' && thisChar <= '9')) {
            [output appendFormat:@"%c", thisChar];
        } else {
            [output appendFormat:@"%%%02x", thisChar];
        }
    }
    return output;

}

+ (NSError*) handleServerError: (NSHTTPURLResponse*)response errorMessage:(NSString * _Nullable) errorMessage{
    if(errorMessage){
        return [NSError errorWithDomain:@"CEFService" code:response.statusCode userInfo:@{NSLocalizedDescriptionKey:errorMessage}];
    }else{
        return [NSError errorWithDomain:@"CEFService" code:response.statusCode userInfo:nil];
    }
}

@end
