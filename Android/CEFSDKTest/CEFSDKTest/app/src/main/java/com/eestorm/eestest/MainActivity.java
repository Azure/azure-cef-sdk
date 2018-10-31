package com.eestorm.eestest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.microsoft.cef.prodsdk.bean.CEFCredential;
import com.microsoft.cef.prodsdk.bean.CEFOTPChannel;
import com.microsoft.cef.prodsdk.bean.CEFOTPResultCode;
import com.microsoft.cef.prodsdk.bean.CEFResponseSocialLogin;
import com.microsoft.cef.prodsdk.bean.CEFSocialLoginProfile;
import com.microsoft.cef.prodsdk.cefservicemanager.CEFServiceConfig;
import com.microsoft.cef.prodsdk.cefservicemanager.CEFServiceOTP;
import com.microsoft.cef.prodsdk.cefservicemanager.CEFServiceSocialLogin;
import com.microsoft.cef.prodsdk.listener.CEFGenerateOTPCodeListener;
import com.microsoft.cef.prodsdk.listener.CEFResponseSocialLoginListener;
import com.microsoft.cef.prodsdk.listener.CEFVerifyOTPCodeListener;

import java.util.Map;

import static com.microsoft.cef.prodsdk.bean.CEFOTPChannel.CEFOTPChannel_SMS;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel.CEFSocialLoginChannel_QQ;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel.CEFSocialLoginChannel_WeChat;
import static com.microsoft.cef.prodsdk.bean.CEFSocialLoginChannel.CEFSocialLoginChannel_Weibo;


public class MainActivity extends AppCompatActivity {
    private String logLabel = "";
    private TextView label;
    private Handler handler = null;
    private EditText phone, code;
    private CEFServiceSocialLogin serviceSocialLogin;
    private CEFServiceOTP serviceOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        serviceSocialLogin = CEFServiceSocialLogin.getInstance(this);
        serviceOTP = CEFServiceOTP.getInstance(this);

        code = (EditText) findViewById(R.id.code);
        phone = (EditText) findViewById(R.id.phone);
        label = (TextView) findViewById(R.id.labelTxt);
        CEFServiceConfig.getInstance().setCEFAccountWithName(Constants.CEF_ACCOUNT_NAME,
                Constants.CEF_SASKEY, Constants.CEF_SASKEYNAME);

        //初始化Credential
        CEFCredential mCEFCredential = new CEFCredential();
        //微信
        mCEFCredential.setWeChatAppWithKey(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_SECRET);
        //qq
        mCEFCredential.setQQAppWithKey(Constants.QQ_APP_ID);
        //新浪
        mCEFCredential.setWeiboAppWithKey(Constants.WEIBO_APP_KEY, Constants.WEIBO_REDIRECT_URL, Constants.WEIBO_SECRET);

        //第三方登录
        serviceSocialLogin.setupChannel(mCEFCredential);
    }

    //qq 和 weibo的登录回调 必须调用，否则收不到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        serviceSocialLogin.setActivityResultData(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    //微信登录
    public void loginWithWeChat(View view) {
        serviceSocialLogin.loginWithChannel(CEFSocialLoginChannel_WeChat, new CEFResponseSocialLoginListener() {
            @Override
            public void onComplete(CEFResponseSocialLogin result, CEFSocialLoginProfile socialLoginProfile) {
                setLabel("[WechatLogin] loginWithChannel result：" + valueToString(result));
                setLabel("[WechatLogin] loginWithChannel socialLoginProfile：" + socialLoginProfile);
            }
        });
    }

    //QQ登录
    public void loginWithQQ(View view) {
        serviceSocialLogin.loginWithChannel( CEFSocialLoginChannel_QQ, new CEFResponseSocialLoginListener() {
            @Override
            public void onComplete(CEFResponseSocialLogin result, CEFSocialLoginProfile socialLoginProfile) {
                setLabel("[loginQQ] loginWithChannel result:" +valueToString(result));
                setLabel("[loginQQ] loginWithChannel socialProfile:" + socialLoginProfile);
            }

        });
    }

    //新浪登录
    public void loginWithWeibo(View view) {
        serviceSocialLogin.loginWithChannel(CEFSocialLoginChannel_Weibo, new CEFResponseSocialLoginListener() {
            @Override
            public void onComplete(CEFResponseSocialLogin result, CEFSocialLoginProfile socialLoginProfile) {
                setLabel("[WeiboLogin] loginWithChannel result:" + valueToString(result));
                setLabel("[WeiboLogin] loginWithChannel socialProfile:" + socialLoginProfile);
            }

        });
    }

    //生成验证码
    public void generateOTPCode(View view) {
        String phoneLabel = phone.getText().toString();
        if (TextUtils.isEmpty(phoneLabel)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        serviceOTP.generateOTPCode(phoneLabel, "OTPTemplate001", 300, 6,
                CEFOTPChannel_SMS, new CEFGenerateOTPCodeListener() {
            @Override
            public void onComplete(Map result) {
                setLabel("[generateOTPCode] result:" + result);
            }
        });
    }

    //验证验证码
    public void verifyOTPCode(View view) {
        String phoneLabel = phone.getText().toString();

        if (TextUtils.isEmpty(phoneLabel)) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        String codeLabel = code.getText().toString();
        if (TextUtils.isEmpty(codeLabel)) {
            Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        serviceOTP.verifyOTPCode(phoneLabel, codeLabel, CEFOTPChannel.CEFOTPChannel_SMS, new CEFVerifyOTPCodeListener() {
            @Override
            public void onComplete(CEFOTPResultCode code, String msg) {
                setLabel("[verifyOTPCode] code:" + code.ordinal() + "  ;msg:" + msg);
            }
        });
    }

    //日志更新界面
    private void setLabel(String labelText) {
        logLabel = "\n" +labelText +"\n"+ logLabel;
        new Thread() {
            public void run() {
                handler.post(runnableUi);
            }
        }.start();
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            label.setText(logLabel);
        }

    };

    private String valueToString(CEFResponseSocialLogin socialLogin) {
        return
                "resultCode=" + socialLogin.getResultCode().ordinal() +
                        ", resultDescription='" + socialLogin.getResultDescription() + '\'' +
                        ", channel=" + socialLogin.getChannel().ordinal() +
                        ", accessToken='" + socialLogin.getAccessToken() + '\'' +
                        ", ID='" + socialLogin.getID() + '\'' +
                        ", refreshToken='" + socialLogin.getRefreshToken() + '\'' +
                        ", expirationDate=" + socialLogin.getExpirationDate();
    }
}
