package jp.kinwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import jp.kinwork.Common.MyApplication;

public class WebActivity extends AppCompatActivity {

    private WebView wvurl;
    private MyApplication myApplication;
    private String url;
    private WebView mWebview;
    private WebSettings mWebSettings;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private Intent Intent;
    private ProgressDialog dialog ;
    private String Act;

    private ImageView Ivsearch;
    private TextView tvsearch;
    private ImageView ivmylist;
    private TextView tvmylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent = getIntent();
        //Act = Intent.getStringExtra("Act");
        Initialization();
        dialog = new ProgressDialog(this) ;
        //dialog.setTitle("提示") ;
        dialog.setMessage(getString(R.string.kensakuchu)) ;
//        dialog.show();
        mWebview = (WebView) findViewById(R.id.webView1);
        mWebSettings = mWebview.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        mWebSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        mWebSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        mWebSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        mWebview.loadUrl(url);

        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
//        dialog.dismiss();
    }

    //初期化
    private void Initialization(){
        tvback                    = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle               = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);

        tvbacktitle.setText(getString(R.string.detailedinformation));
        myApplication = (MyApplication) getApplication();
        Act = myApplication.getAct();
        url = myApplication.getURL();
        Log.d("Act", Act);
        if(Act.equals(getString(R.string.Search))){
            tvback.setText(getString(R.string.SearchResults));
            tvbackdummy.setText(getString(R.string.SearchResults));
            Ivsearch = (ImageView) findViewById(R.id.iv_search);
            Ivsearch.setImageResource(R.mipmap.blue_search);
            tvsearch = (TextView) findViewById(R.id.tv_search);
            tvsearch.setTextColor(Color.parseColor("#5EACE2"));
            if(myApplication.getSURL(0).equals("1")){
                url = myApplication.getSURL(1);
            }
        } else {
            tvback.setText(getString(R.string.mylist));
            tvbackdummy.setText(getString(R.string.mylist));
            ivmylist = (ImageView) findViewById(R.id.iv_mylist);
            tvmylist = (TextView) findViewById(R.id.tv_mylist);
            ivmylist.setImageResource(R.mipmap.blue_mylist);
            tvmylist.setTextColor(Color.parseColor("#5EACE2"));
            if(myApplication.getMURL(0).equals("1")){
                url = myApplication.getMURL(1);
            }
        }
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            if(Act.equals(getString(R.string.Search))){
                myApplication.setSURL("0",0);
            } else {
                myApplication.setMURL("0",0);
            }
            mWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }

    //返回检索画面
    public void Click_back(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(Act.equals("Search")){
            myApplication.setSURL("0",0);
            intent.setClass(WebActivity.this, SearchResultsActivity.class);
        } else {
            myApplication.setMURL("0",0);
            intent.setClass(WebActivity.this, MylistActivity.class);
        }
        startActivity(intent);
    }

    //获取搜索结果菜单栏按钮
    public void ll_Click(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_search:
                myApplication.setAct(getString(R.string.Search));
                myApplication.setMURL("1",0);
                myApplication.setMURL(url,1);
                if(! Act.equals(getString(R.string.Search))){
                    if(myApplication.getSURL(0).equals("0")){
                        if(myApplication.getSApply(0).equals("0")){
                            if(myApplication.getSearchResults(0).equals("0")){
                                intent.setClass(WebActivity.this, SearchActivity.class);
                                intent.putExtra(getString(R.string.act),"");
                            } else {
                                intent.setClass(WebActivity.this, SearchResultsActivity.class);
                            }
                        } else {
                            intent.setClass(WebActivity.this, ApplyActivity.class);
                        }
                    } else {
                        intent.setClass(WebActivity.this, WebActivity.class);
                        Initialization();
                    }
                }
                break;
            //Myリスト画面に移動
            case R.id.ll_contact:
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(WebActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(WebActivity.this, ContactDialogActivity.class);
                }
                break;
            case R.id.ll_mylist:
                myApplication.setAct(getString(R.string.Apply));
                myApplication.setSURL("1",0);
                myApplication.setSURL(url,1);
                if(myApplication.getMURL(0).equals("0")){
                    if(Act.equals(getString(R.string.Search))){
                        if(myApplication.getMApply(0).equals("0")){
                            intent.setClass(WebActivity.this, MylistActivity.class);
                        } else {
                            intent.setClass(WebActivity.this, ApplyActivity.class);
                        }
                    }
                } else {
                    intent.setClass(WebActivity.this, WebActivity.class);
                    Initialization();
                }
                break;
            //個人設定画面に移動
            case R.id.ll_personalsettings:
                if(Act.equals(getString(R.string.Search))){
                    myApplication.setSURL("1",0);
                    myApplication.setSURL(url,1);
                } else {
                    myApplication.setMURL("1",0);
                    myApplication.setMURL(url,1);
                }
                if(myApplication.getpersonalset(0).equals("0")){
                    intent.setClass(WebActivity.this, PersonalSetActivity.class);
                } else if(myApplication.getpersonalset(0).equals("1")){
                    intent.setClass(WebActivity.this, BasicinfoeditActivity.class);
                } else if(myApplication.getpersonalset(0).equals("2")){
                    intent.setClass(WebActivity.this, ChangepwActivity.class);
                } else if(myApplication.getpersonalset(0).equals("3")){
                    intent.setClass(WebActivity.this, ResumeActivity.class);
                }
                break;
        }
        startActivity(intent);

    }
}
