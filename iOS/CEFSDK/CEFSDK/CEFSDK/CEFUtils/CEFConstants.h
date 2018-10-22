//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#ifndef CEFServiceConstants_h
#define CEFServiceConstants_h


#define CEF_Social_Channel_Name_WeChat @"wechat"
#define CEF_Social_Channel_Name_WeiBo @"weibo"
#define CEF_Social_Channel_Name_QQ @"qq"


//--------------CEFServiceManager-----------------------
#define CEFService_DefaultEndpoint @"https://cef.chinacloudapi.cn"
#define CEFService_APIVersion @"api-version=2018-10-01"

//--------------CEFServiceSocialLogin----------------
#define CEFService_SocialLogin_Userinfo @"%@/services/sociallogin/userinfo?%@"

//--------------CEFServiceOTP------------------------
#define CEFService_OTP_Start @"%@/services/otp/start?%@"
#define CEFService_OTP_Check @"%@/services/otp/check?%@"



#endif /* CEFServiceConstants_h */
