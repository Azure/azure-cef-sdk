package com.eestorm.eestest.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.microsoft.cef.prodsdk.cefservicemanager.CEFServiceSocialLogin;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private CEFServiceSocialLogin serviceSocialLogin;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceSocialLogin = CEFServiceSocialLogin.getInstance(this);
        serviceSocialLogin.onCreate(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        serviceSocialLogin.handleIntent(intent,this);
        finish();
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        serviceSocialLogin.handleOnResp(baseResp);
        WXEntryActivity.this.finish();
    }
}