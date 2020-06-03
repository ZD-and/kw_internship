package jp.kinwork;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    final static String PARAM_KeywordHint = "/SearchesMobile/getKeywordHint";
    final static String PARAM_AddressHint = "/SearchesMobile/getAddressHint";
    private String deviceId;
    private String AesKey;
    private ImageView ivkinwork;
    private EditText etkeyword;
    private EditText etworklocation;
    private ImageView ivclearkeyword;
    private ImageView ivclearworklocation;
    private String UserLoginFlg;
    private ImageView Ivsearch;
    private ImageView Ivclearkeyword;
    private ImageView Ivclearworklocation;
    private TextView tvsearch;
    private TextView tvtitle;
    private TextView tvkeyword;
    private TextView tvworklocation;
    private MyApplication myApplication;
    private jp.kinwork.Common.PreferenceUtils PreferenceUtils;
    private Intent Intent;
    private TableLayout tllayoutsearch;
    private TableLayout tlkeyword;
    private TableLayout tlworklocation;
    private boolean blkeyword = false;
    private boolean blworklocation = false;

    private Button bu_search;

    private ArrayAdapter<String> adapter;

    private ListView lvgethint;

    private int keywordTop = 0;
    private int worklocationTop = 0;
    private String etname;

    public String TAG = "SearchActivity";

    private LinearLayout ll_search;
    private LinearLayout ll_contact;
    private LinearLayout ll_mylist;
    private LinearLayout ll_personalsettings;
    
//    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG+TAG+"onCreate", "onCreate-SearchActivity" );
        Intent  = getIntent();
        load();
        Initialization();
        initData();
    }

    //点击输入框以外键盘隐藏-star
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {

                return true;
            }
        }
        return false;
    }
    //点击输入框以外键盘隐藏-end

    //位置取得权限是否取得的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG+"onRequestrequestCode", requestCode+"");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationStart();
        }

    }
    //初期化
    public void Initialization(){
        ivkinwork = (ImageView) findViewById(R.id.iv_kinwork);
        Ivclearkeyword=findViewById(R.id.iv_clear_keyword);
        Ivclearworklocation=findViewById(R.id.iv_clear_worklocation);
        Ivclearkeyword.setOnClickListener(this);
        Ivclearworklocation.setOnClickListener(this);

        lvgethint = (ListView)findViewById(R.id.lv_gethint);
        tvkeyword = (TextView) findViewById(R.id.tv_keyword);
        tllayoutsearch = (TableLayout) findViewById(R.id.tllayout_search);
        tlkeyword = (TableLayout) findViewById(R.id.tl_keyword);
        etkeyword = (EditText)findViewById(R.id.et_keyword);
        bu_search=findViewById(R.id.bu_search);
        bu_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Search();
            }
        });

        keywordTop= dp2px(SearchActivity.this, 10);
        Log.d(TAG+"keywordTop", keywordTop + "");
        tvkeyword.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG+"tvkeywordgetHeight", tvkeyword.getHeight() + "");
                Log.d(TAG+"getMeasuredHeight", tvkeyword.getMeasuredHeight() + "");
                Log.d(TAG+"keywordTop", keywordTop + "");
                keywordTop = keywordTop + tvkeyword.getMeasuredHeight();
                Log.d(TAG+"tvkeyword", keywordTop + "");
            }
        });
        tlkeyword.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG+"tlkeywordgetHeight", tlkeyword.getHeight() + "");
                Log.d(TAG+"getMeasuredHeight", tlkeyword.getMeasuredHeight() + "");
                Log.d(TAG+"keywordTop", keywordTop + "");
                keywordTop = keywordTop + tlkeyword.getMeasuredHeight();
                Log.d(TAG+"tlkeyword", keywordTop + "");
            }
        });

        tvworklocation = (TextView) findViewById(R.id.tv_worklocation);
        tlworklocation = (TableLayout) findViewById(R.id.tl_worklocation);
        etworklocation = (EditText)findViewById(R.id.et_worklocation);

        worklocationTop= dp2px(SearchActivity.this, 50);
        worklocationTop = worklocationTop + keywordTop;
        Log.d(TAG+"worklocationTop", worklocationTop + "");
        tvworklocation.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG+"tlworklocationgetHeight", tvworklocation.getHeight() + "");
                Log.d(TAG+"getMeasuredHeight", tvworklocation.getMeasuredHeight() + "");
                Log.d(TAG+"worklocationTop", worklocationTop + "");
                worklocationTop = worklocationTop + tvworklocation.getMeasuredHeight();
                Log.d(TAG+"tvworklocation", worklocationTop + "");
            }
        });
        tlworklocation.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG+"tlworklocationgetHeight", tlworklocation.getHeight() + "");
                Log.d(TAG+"getMeasuredHeight", tlworklocation.getMeasuredHeight() + "");
                Log.d(TAG+"worklocationTop", worklocationTop + "");
                worklocationTop = worklocationTop + tlworklocation.getMeasuredHeight();
                worklocationTop = worklocationTop + tlworklocation.getMeasuredHeight();
                Log.d(TAG+"tlworklocation", worklocationTop + "");
            }
        });

        ivclearkeyword = (ImageView) findViewById(R.id.iv_clear_keyword);
        ivclearworklocation = (ImageView) findViewById(R.id.iv_clear_worklocation);
        Ivsearch = (ImageView) findViewById(R.id.iv_search);
        Ivsearch.setImageResource(R.mipmap.blue_search);
        tvsearch = (TextView) findViewById(R.id.tv_search);
        tvsearch.setTextColor(Color.parseColor("#5EACE2"));
        tvtitle      = (TextView) findViewById(R.id.tv_title_name);
        tvtitle.setText(getString(R.string.shigotosagasu));
        myApplication = (MyApplication) getApplication();
        PreferenceUtils = new PreferenceUtils(SearchActivity.this);
        UserLoginFlg = PreferenceUtils.getUserFlg();
        etkeyword.setText(myApplication.getkeyword());
        etworklocation.setText(myApplication.getaddress());

        if(etkeyword.getText().length() > 0){
            ivclearkeyword.setVisibility(View.VISIBLE);
        }
        if(Intent.getStringExtra(getString(R.string.act)).equals(getString(R.string.Boot))){
            if(myApplication.getaddress_components().length() >0){
                SetAddress(myApplication.getaddress_components());
            } else {
                AccessPermission();
            }
        }
        if(etworklocation.getText().length() > 0){
            ivclearworklocation.setImageResource(R.drawable.ic_cancel);
            ivclearworklocation.setTag(getString(R.string.clear));
        }
        ll_search=findViewById(R.id.ll_search);
        ll_contact=findViewById(R.id.ll_contact);
        ll_mylist=findViewById(R.id.ll_mylist);
        ll_personalsettings=findViewById(R.id.ll_personalsettings);
        ll_search.setOnClickListener(buttomMenu);
        ll_contact.setOnClickListener(buttomMenu);
        ll_mylist.setOnClickListener(buttomMenu);
        ll_personalsettings.setOnClickListener(buttomMenu);

    }
    //菜单栏按钮触发事件
    private View.OnClickListener buttomMenu =new View.OnClickListener() {
        public void onClick(View View) {
            String ViewID = "";
            myApplication.setkeyword(etkeyword.getText().toString());
            myApplication.setaddress(etworklocation.getText().toString());
            Log.d(TAG + "ViewID", View.getId() + "");
            switch (View.getId()) {
                //連絡画面に移動
                case R.id.ll_contact:
                    ViewID = "ll_contact";
                    break;
                //Myリスト画面に移動
                case R.id.ll_mylist:
                    ViewID = "ll_mylist";
                    break;
                //個人設定画面に移動
                case R.id.ll_personalsettings:
                    ViewID = "ll_personalsettings";
                    break;
            }
            if (!ViewID.equals("")) {
                PreferenceUtils.setsaveid(ViewID);
                if (UserLoginFlg.equals("0")) {
                    Click_intent(getString(R.string.UserLogin));
                } else if (UserLoginFlg.equals("1")) {
                    Click_intent(ViewID);
                }
            }
        }
    };

    //画面移动
    private void Click_intent(String name){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (name){
            //  ログイン画面に移動
            case "UserLogin":
                intent.setClass(SearchActivity.this, MainKinWork.class);
                intent.putExtra(getString(R.string.Activity),"");
                break;
            //連絡画面に移動
            case "ll_contact":
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(SearchActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(SearchActivity.this, ContactDialogActivity.class);
                }
                break;
            //Myリスト画面に移動
            case "ll_mylist":
                myApplication.setAct(getString(R.string.Apply));
                if(myApplication.getMURL(0).equals("0")){
                    if(myApplication.getMApply(0).equals("0")){
                        intent.setClass(SearchActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(SearchActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(SearchActivity.this, WebActivity.class);
                }
                break;
            //個人設定画面に移動
            case "ll_personalsettings":
                if(myApplication.getpersonalset(0).equals("0")){
                    intent.setClass(SearchActivity.this, PersonalSetActivity.class);
                } else if(myApplication.getpersonalset(0).equals("1")){
                    intent.setClass(SearchActivity.this, BasicinfoeditActivity.class);
                } else if(myApplication.getpersonalset(0).equals("2")){
                    intent.setClass(SearchActivity.this, ChangepwActivity.class);
                } else if(myApplication.getpersonalset(0).equals("3")){
                    intent.setClass(SearchActivity.this, ResumeActivity.class);
                }
                break;
        }
        startActivity(intent);
    }
    //检索结果画面移动
    public void Click_Search(){
        myApplication.setkeyword(etkeyword.getText().toString());
        myApplication.setaddress(etworklocation.getText().toString());
        myApplication.setemploymentStatus("");
        myApplication.setyearlyIncome("");
        myApplication.setpage("1");

        Intent intent_results = new Intent();
        intent_results.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent_results.setClass(SearchActivity.this, SearchResultsActivity.class);
        intent_results.putExtra("Act","search");
        startActivity(intent_results);
    }
    //ログインフラグ取得
    public  void load(){
        SharedPreferences Resultflg = getSharedPreferences(getString(R.string.Initial), Context.MODE_PRIVATE);
        String Result = Resultflg.getString(getString(R.string.Result),"A");
        deviceId = Resultflg.getString(getString(R.string.deviceid),"A");
        AesKey = Resultflg.getString(getString(R.string.Information_Name_aesKey),"A");
        if(Result.equals("0") || Result.equals("A") ){
            alertdialog(getString(R.string.error),getString(R.string.tsushinshippai));
        }
    }
    //通信结果提示
    private void alertdialog(String title,String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }
    //设置当前位置
    private void SetAddress(String date){
        Log.d(TAG+"date", date);
        try {
            JSONObject obj = new JSONObject(date);
            JSONArray results = obj.getJSONArray(getString(R.string.address_components));
            Log.d(TAG+"results", results.toString());
            int iIndex = -1;
            String add_1 = "";
            String add_2 = "";
            String add_3 = "";
            for (int i = 0;i < results.length();i++){
                if(results.getJSONObject(i).getString(getString(R.string.long_name)).equals(getString(R.string.nihon))){
                    iIndex = i;
                    break;
                }
            }
            if(iIndex > -1){
                add_1 = results.getJSONObject(iIndex - 1).getString(getString(R.string.long_name));
                add_2 = results.getJSONObject(iIndex - 2).getString(getString(R.string.long_name));
                add_3 = results.getJSONObject(iIndex - 3).getString(getString(R.string.long_name));
            } else {
                ivclearworklocation.setVisibility(View.GONE);
            }
            etworklocation.setText(add_1 + " " + add_2 + " " + add_3);
            myApplication.setaddress(etworklocation.getText().toString());
            if(etworklocation.getText().length() > 0){
                blworklocation = true;
                ivclearworklocation.setImageResource(R.drawable.ic_cancel);
                ivclearworklocation.setTag(getString(R.string.clear));
            }
            Log.d(TAG+"etworklocation", etworklocation.getText().toString());
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //输入框监视，有数据的时候显示清空按钮
    public void initData(){

        etkeyword.setOnTouchListener(new View.OnTouchListener() {
            int flg = 0;

            @Override
            public boolean onTouch(View view,MotionEvent event){
                Log.d(TAG+"onTouchflg1: ", flg+"");
                flg = flg +1;
                if(flg == 1){
                    Log.d(TAG+"onTouchflg2: ", flg+"");
                    ivkinwork.setVisibility(View.GONE);
                    lvgethint.setVisibility(View.GONE);
                    etkeyword.setCursorVisible(true);
                    flg = 0;
                }
                return false;
            }
        });

        etworklocation.setOnTouchListener(new View.OnTouchListener() {
            int flg = 0;
            @Override
            public boolean onTouch(View view,MotionEvent event){

                Log.d(TAG+"onTouchflg1: ", flg+"");
                flg = flg +1;
                if(flg == 1){
                    Log.d(TAG+"onTouchflg2: ", flg+"");
                    ivkinwork.setVisibility(View.GONE);
                    lvgethint.setVisibility(View.GONE);
                    etworklocation.setCursorVisible(true);
                    flg = 0;
                }
                Log.d(TAG+"onTouchflg2: ", flg+"");
                return false;
            }
        });

        tllayoutsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivkinwork.setVisibility(View.VISIBLE);
                etkeyword.setCursorVisible(false);
                etworklocation.setCursorVisible(false);
                lvgethint.setVisibility(View.GONE);
            }
        });

        etkeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivclearkeyword.setVisibility(View.VISIBLE);
                    String Text = etkeyword.getText().toString();
                    if( blkeyword == false){
                        getSearchResults("0",Text);
                    }
                } else {
                    lvgethint.setVisibility(View.GONE);
                    ivclearkeyword.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                blkeyword = false;
            }
        });

        etworklocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    ivclearworklocation.setImageResource(R.drawable.ic_location_on);
                    ivclearworklocation.setTag(getString(R.string.location));
                    lvgethint.setVisibility(View.GONE);
                } else {
                    ivclearworklocation.setImageResource(R.drawable.ic_cancel);
                    ivclearworklocation.setTag(getString(R.string.clear));
                    String Text = etworklocation.getText().toString();
                    if(blworklocation == false){
                        getSearchResults("1",Text);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                blworklocation = false;
            }
        });

        lvgethint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前选择的值
                String data=(String) adapter.getItem(position);
                Log.d(TAG+"etname", etname);

                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.yyyyMMddHHmmssSSS));
                Date curDate =  new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                Log.d(TAG+"***lvgethintDate1***", strDate);

               curDate =  new Date(System.currentTimeMillis());
               strDate = formatter.format(curDate);
                Log.d(TAG+"***lvgethintDate2***", strDate);
                if(etname.equals("keyword")){
                    blkeyword = true;
                    etkeyword.setText(data);
                    etkeyword.setSelection(etkeyword.getText().length());

                } else {
                    blworklocation = true;
                    etworklocation.setText(data);
                    etworklocation.setSelection(etworklocation.getText().length());
                }
                lvgethint.setVisibility(View.GONE);
            }
        });
    }
    //输入框的清空处理
    public void onClick(View View){
        switch (View.getId()){
            case R.id.iv_clear_keyword:
                etkeyword.setText("");
                break;
            case R.id.iv_clear_worklocation:
                //当前图片为清空的时候，清空输入框
                if (ivclearworklocation.getTag().equals("clear")){
                    etworklocation.setText("");
                    ivclearworklocation.setTag(getString(R.string.location));
                    ivclearworklocation.setImageResource(R.drawable.ic_location_on);
                }else{
                    //当前图片不为清空的时候，重新获取当前所在地
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                                1000);
                        return;
                    } else {
                        blworklocation = true;
                        locationStart();
                    }
                }
                break;
        }
    }
    //当前位置经纬度取得
    private void locationStart(){
        Log.d(TAG+"debug","locationStart()");
        // LocationManager インスタンス生成
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d(TAG+"debug", "checkSelfPermission false");
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        criteria.setAltitudeRequired(false);//不要求海拔信息
        criteria.setBearingRequired(false);//不要求方位信息
        criteria.setCostAllowed(false);//是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
        Location Location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,true));
        getaddress_components(Location);
    }
    //当前地址通信取得
    private void getaddress_components(Location location){
        if(location != null){
            // 緯度の表示
            String str1 = getString(R.string.Latitude)+location.getLatitude();
            Log.d(TAG+"str1", str1);
            // 経度の表示
            String str2 = getString(R.string.Longtude)+location.getLongitude();
            Log.d(TAG+"str2", str2);
            Map<String,String> param = new HashMap<String, String>();
            param.put(getString(R.string.url),"https://maps.google.com/maps/api/geocode/json?latlng=");
            param.put(getString(R.string.itude),location.getLatitude() + ","+location.getLongitude());
            param.put(getString(R.string.key),"&sensor=false&language=ja&key=AIzaSyBzSkvprYMmBmLWaon_uBWJEiJ9DH21B6g");
//            new GithubQueryTask2().execute(param);
        }

    }
    //地址通信
    public class GithubQueryTask2 extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String url = map.get(getString(R.string.url));
            String itude = map.get(getString(R.string.itude));
            String key = map.get(getString(R.string.key));
            Log.d(TAG+"***url***", url + itude + key);
            URL searchUrl = null;
            try {
                searchUrl = new URL(url + itude + key);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl,"","");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d(TAG+"***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    JSONArray job = obj.getJSONArray("results");
                    myApplication.setaddress_components(job.get(0).toString());
                    Log.d(TAG+"***job***", job.get(0).toString());
                    SetAddress(job.get(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //获取搜索结果
    public void getSearchResults(String flg,String hintdata){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        if(flg.equals("0")){
            Pdata.setkeyword(hintdata);
            param.put(getString(R.string.file),PARAM_KeywordHint);
            param.put(getString(R.string.name),getString(R.string.keyword));
            etname = getString(R.string.keyword);
        } else {
            Pdata.setaddress(hintdata);
            param.put(getString(R.string.file),PARAM_AddressHint);
            param.put(getString(R.string.name),getString(R.string.worklocation));
            etname = getString(R.string.worklocation);
        }
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
    //转换为Json格式并且AES加密
    public String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d(TAG+"sdPdata", sdPdata);
        AES mAes = new AES();
        byte[] mBytes = null;
        try {
            mBytes = sdPdata.getBytes("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String enString = mAes.encrypt(mBytes,AesKey);
        String encrypt = enString.replace("\n", "").replace("+","%2B");
        return encrypt;

    }
    ////联想关键字访问服务器，并取得访问结果
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        String name = "";
        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            name = map.get(getString(R.string.name));
            URL searchUrl = buildUrl(file);
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl,data,deviceId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d(TAG+"***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String meg = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        Log.d(TAG+"***returnData***", obj.getString("returnData"));
                        JSONArray returnData = obj.getJSONArray(getString(R.string.returnData));
                        ArrayList<String> stringArrayList = new ArrayList<String>();
                        String[] Stringdata = {};
                        int length = returnData.length();
                        if(!obj.getString(getString(R.string.returnData)).equals("[]")){
                            for (int i=0; i< length; i++) {
                                Log.d(TAG+"***i***", i+"");
                                if( i < 6){
                                    stringArrayList.add(returnData.getString(i)); //add to arraylist
                                } else {
                                    break;
                                }
                            }
                        }
                        Stringdata = stringArrayList.toArray(new String[stringArrayList.size()]);
                        Log.d(TAG+"***name***", name);
                        getItem(Stringdata,name);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    //联想关键字设定悬浮窗
    public void getItem(String []Stringdata,String name){
        int top= 0;
        Log.d(TAG+"***name***", name);
        if(name.equals(getString(R.string.keyword))){
            top= keywordTop;
        } else {
            top= worklocationTop;
        }
        Log.d(TAG+"***top***", top+"");
        int left_right= dp2px(SearchActivity.this, 30);
        FrameLayout.LayoutParams flparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flparams.setMargins(left_right,top,left_right,0);
        lvgethint.setLayoutParams(flparams);
        adapter = new ArrayAdapter<>(this, R.layout.list_item, Stringdata);
        lvgethint.setAdapter(adapter);
        lvgethint.setVisibility(View.VISIBLE);
//        initData();
    }
    //位置取得权限是否取得
    private void AccessPermission() {
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                locationStart();
            }
        }else {
            Log.d(TAG+"AskForPermission4-2", "AskForPermission4");
            locationStart();
        }
    }
    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
}
