//---------------------------------------------------------------
//  Copyright (C) Microsoft Corporation. All rights reserved.
//---------------------------------------------------------------

#import "ViewController.h"
#import "Constants.h"
#import <CEFSDK/CEFSDK.h>


@interface ViewController ()
@property (weak, nonatomic) IBOutlet UITextField *verifyCodeTextField;
@property (weak, nonatomic) IBOutlet UITextField *phoneNumberTextField;
@property (weak, nonatomic) IBOutlet UITextView *logOutput;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

// -- CEFSDK Begin --
- (IBAction)WechatLogin:(id)sender {
    // 打开微信完成登录授权
    [CEFSocialLoginManager loginWithChannel:CEFSocialLoginChannel_WeChat completion:^(CEFResponseSocialLogin *loginResult, CEFSocialLoginProfile *socialProfile) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[WechatLogin] loginWithChannel loginResult:%@\n",loginResult];
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[WechatLogin] loginWithChannel socialProfile:%@\n",socialProfile];
             [self scrollTextView];
        });
    }];
   
}
- (IBAction)QQLogin:(id)sender {
    // 打开QQ完成登录授权
    [CEFSocialLoginManager loginWithChannel:CEFSocialLoginChannel_QQ completion:^(CEFResponseSocialLogin *loginResult, CEFSocialLoginProfile *socialProfile) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[QQLogin] loginWithChannel loginResult:%@\n",loginResult];
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[QQLogin] loginWithChannel socialProfile:%@\n",socialProfile];
             [self scrollTextView];

        });
    }];
}
- (IBAction)WeiboLogin:(id)sender {
    // 打开手机微博完成登录授权
    [CEFSocialLoginManager loginWithChannel:CEFSocialLoginChannel_WeiBo completion:^(CEFResponseSocialLogin *loginResult, CEFSocialLoginProfile *socialProfile) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[WeiboLogin] loginWithChannel loginResult:%@\n",loginResult];
            self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[WeiboLogin] loginWithChannel socialProfile:%@\n",socialProfile];
             [self scrollTextView];

        });
    }];
}
- (IBAction)generateOTPCode:(id)sender {
    //获取用户输入的电话号码
    NSString* phone = self.phoneNumberTextField.text;
    if(phone==nil || [phone length] == 0 ){
        UIAlertController * alert = [UIAlertController alertControllerWithTitle:@"请输入电话号码" message:@"" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault
                             handler:^(UIAlertAction * action)
                             {
                                 [self dismissViewControllerAnimated:YES completion:nil];
                                 
                             }];
        [alert addAction:ok];
        [self presentViewController:alert animated:YES completion:nil];
        
    }else{
        // 调用CEFOTPManager获取短信验证码
        [CEFOTPManager generateOTPCode:phone  templateName:OTPTemplateName expireTime:300 codeLength:6 channel:CEFOTPChannel_SMS completion:^(NSDictionary *result, NSError * _Nullable error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if(error){
                    self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[generateOTPCode] generateOTPCode error:%@\n",error];
                }else{
                    self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[generateOTPCode] generateOTPCode result:%@\n",result];
                }
                 [self scrollTextView];
            });
        }];
    }
}
- (IBAction)verifyOTPCode:(id)sender {
    // 获取用户输入的电话号码
    NSString* phone = self.phoneNumberTextField.text;
    // 获取用户输入的验证码
    NSString* verifyCode = self.verifyCodeTextField.text;
    if(phone==nil || [phone length] == 0 || verifyCode == nil || [verifyCode length]==0 ){
        UIAlertController * alert = [UIAlertController alertControllerWithTitle:@"请输入电话号码和验证码" message:@"" preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction* ok = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault
                                                   handler:^(UIAlertAction * action)
                             {
                                 [self dismissViewControllerAnimated:YES completion:nil];
                                 
                             }];
        [alert addAction:ok];
        [self presentViewController:alert animated:YES completion:nil];
        
    }else{
        // 调用CEFOTPManager校验用户提供的短信验证码
        [CEFOTPManager verifyOTPCode:phone code:verifyCode channel:CEFOTPChannel_SMS completion:^(CEFOTPResultCode result, NSError * _Nullable error) {
            
            dispatch_async(dispatch_get_main_queue(), ^{
                if(error){
                    self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[verifyOTPCode] error:%@\n",error];
                }else{
                    self.logOutput.text = [self.logOutput.text stringByAppendingFormat:@"[verifyOTPCode] result:%ld\n",(long)result];
                }
                [self scrollTextView];
            });
        }];
    }

}

- (void) scrollTextView{
    [self.logOutput becomeFirstResponder];
    NSRange lastLine = NSMakeRange(self.logOutput.text.length - 1, 1);
    [self.logOutput scrollRangeToVisible:lastLine];
}

// -- CEFSDK End --

@end
