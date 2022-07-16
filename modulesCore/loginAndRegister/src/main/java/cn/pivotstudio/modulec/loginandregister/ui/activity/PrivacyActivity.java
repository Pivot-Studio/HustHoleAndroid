package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.libbase.base.ui.activity.BaseActivity;

import cn.pivotstudio.modulec.loginandregister.R;

/**
 * @classname:PrivacyActivity
 * @description:隐私条款界面
 * @date:2022/4/27 14:32
 * @version:1.0
 * @author:
 */
public class PrivacyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_privacy);
        initView();
    }

    /**
     * View相关初始化
     */
    private void initView(){
        WebView wv = findViewById(R.id.privacy_wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        wv.loadUrl("https://husthole.pivotstudio.cn/privacy");
    }
}
