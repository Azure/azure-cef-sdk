//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------


typedef NS_ENUM(NSInteger, CEFOTPChannel) {
    CEFOTPChannel_SMS    = 21,
};

typedef NS_ENUM(NSInteger, CEFOTPResultCode) {
    CEFOTPResultSuccess = 0,      // Success
    CEFOTPResultWrongCode = -1,   // OTP Code is Wrong
    CEFOTPResultExpired = -2,     // OTP code is expired
    CEFOTPResultUnknown = -3,     // Unknown
};


