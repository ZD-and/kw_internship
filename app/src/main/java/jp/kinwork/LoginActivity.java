package jp.kinwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.co.yahoo.yconnect.YConnectImplicit;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;
import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ActivityCollector;
import jp.kinwork.Common.ClassDdl.Resume;
import jp.kinwork.Common.ClassDdl.User;
import jp.kinwork.Common.ClassDdl.UserToken;
import jp.kinwork.Common.CommonAsyncTask;
import jp.kinwork.Common.CommonView.JumpTextWatcher;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;
import jp.kinwork.Common.YConnectAsyncTask;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    final static String PARAM_login = "/usersMobile/login";
    final static String PARAM_Forgetwe = "/usersMobile/sendPasswordRecoverMail";
    final static String PARAM_init = "/MypagesMobile/initMypageData";
    final static String PARAM_other_Login = "/usersMobile/otherLogin";
    final static String PARAM_setAndroidToken = "/usersMobile/setAndroidToken";

    private String mDeviceId;
    private String mAesKey;
    private String mEmail;
    private String mPassword;
    private String mKeyword;
    private String mAddress;
    private String mEmploymentStatus;
    private String mYearlyIncome;
    private String mPage;
    private String mFlg;
    private String mActivity = "";
    private String mJobInfo;
    private EditText mEdLoginEmail;
    private EditText mEdPassword;
    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;

    private String mDeviceToken;
    private ProgressDialog mDialog;
    String TAG = "LoginActivity";
    String mLoginFlag ="";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    // [END declare_auth]

    LineApiClient lineApiClient;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //初期化
        Initialization();
        //设备IDと対象Key取得
        load_id_key();
        if( ActivityCollector.activities.size() > 0){
            ActivityCollector.finishAll();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //null reference 処理
        if(im.isActive() && getCurrentFocus() != null)
        {
            if (getCurrentFocus().getApplicationWindowToken() != null)
            {
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    //初期化
    private void Initialization(){
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        tvBack.setText("戻る");
        mEdLoginEmail = (EditText) findViewById(R.id.ed_login_email);
        mEdPassword = (EditText) findViewById(R.id.ed_password);

//        点击输入框变蓝
        mEdLoginEmail.addTextChangedListener(new JumpTextWatcher(mEdLoginEmail, mEdPassword));
        mEdPassword.addTextChangedListener(new JumpTextWatcher(mEdPassword, mEdLoginEmail));

        findViewById(R.id.bu_MainLoginClick).setOnClickListener(this);
        findViewById(R.id.Click_Forgetpw).setOnClickListener(this);
        findViewById(R.id.MakeNewuser_Click).setOnClickListener(this);

//        findViewById(R.id.login_btn_google).setOnClickListener(this);
//        findViewById(R.id.login_btn_facebook).setOnClickListener(this);
//        findViewById(R.id.login_btn_yahoo).setOnClickListener(this);
//        findViewById(R.id.login_btn_twitter).setOnClickListener(this);
//        findViewById(R.id.login_btn_line).setOnClickListener(this);

        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(LoginActivity.this);
        mKeyword = mMyApplication.getkeyword();
        mAddress = mMyApplication.getaddress();
        mEmploymentStatus = mMyApplication.getemploymentStatus();
        mYearlyIncome = mMyApplication.getyearlyIncome();
        mPage = mMyApplication.getpage();
        mJobInfo = mMyApplication.getjobinfo();
        mActivity = getIntent().getStringExtra(getString(R.string.Activity));
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.login)) ;
        mAuth = FirebaseAuth.getInstance();
        getDeviceToken();
        initLine();
        getYahooUserInfo();
        initFaceBook();
        initTwitter();
        initGoogle();
        Log.d(TAG, "saveid:" +mPreferenceUtils.getsaveid());
    }

    //登录处理
    public void login(){
        mFlg = "0";
        mEmail = mEdLoginEmail.getText().toString();
        mPassword = mEdPassword.getText().toString();
        urllodad(mEmail, mPassword);
    }

    private void getDeviceToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        mDeviceToken = task.getResult().getToken();

                        Log.d(TAG, "androidデバイストークン："+mDeviceToken);
                    }
                });
    }

    //密码忘记的时候，再取得
    public void getPassword(){
        mFlg = "1";
        mEmail = mEdLoginEmail.getText().toString();
        if(mEmail.equals("")){
            alertdialog(getString(R.string.error),getString(R.string.mailinput));
            return;
        }
        //Json格式转换并且加密
        String data = JsonChnge(mAesKey, mEmail,"", mFlg);
        Map<String,String>param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_Forgetwe);
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //设备IDと対象Key取得
    private void load_id_key(){
        SharedPreferences object = getSharedPreferences("Initial", Context.MODE_PRIVATE);
        mDeviceId = object.getString(getString(R.string.deviceid),"A");
        mAesKey = object.getString(getString(R.string.Information_Name_aesKey),"A");
        mPreferenceUtils.setdeviceId(mDeviceId);
        mPreferenceUtils.setAesKey(mAesKey);
    }

    //内容取得、通信
    private void urllodad(String data_A, String data_B) {
        //Json格式转换并且加密
        String data = JsonChnge(mAesKey,data_A,data_B, mFlg);
        Map<String,String>param = new HashMap<String, String>();
        if (mFlg.equals("0")){
            param.put("file",PARAM_login);
        } else if (mFlg.equals("3")){
            param.put(getString(R.string.file), PARAM_setAndroidToken);
        } else {
            param.put(getString(R.string.file),PARAM_init);
        }
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //转换为Json格式并且AES加密
    private String JsonChnge(String AesKey,String Data_a,String Data_b,String Flg) {
        PostDate Pdata = new PostDate();
        if(Flg.equals("0")){
            Pdata.setEmail(Data_a);
            Pdata.setPassword(Data_b);
        }
        if(Flg.equals("2") || Flg.equals("3")){
            Pdata.setUserId(Data_a);
            Pdata.setToken(Data_b);
            if(Flg.equals("3")){
                Pdata.setAndroidToken(mDeviceToken);
            }
        }
        if(Flg.equals("1")){
            Pdata.setEmail(Data_a);
        }
        Gson mGson1 = new Gson();
        String sdPdata = mGson1.toJson(Pdata,PostDate.class);

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

    //访问服务器，并取得访问结果
    private class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        //在界面上显示进度条
        protected void onPreExecute() {
            mDialog.show();
        };

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            URL searchUrl = buildUrl(file);
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl,data, mDeviceId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d("***Results***", githubSearchResults);
                mDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = obj.getString(getString(R.string.message));
                    if(processResult) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        if(mFlg.equals("0")){
                            decryptchange(returnData);
                        } else if(mFlg.equals("2")){
                            setMyApplication(returnData);
                        } else if(mFlg.equals("3")){
                            mPreferenceUtils.setSendAndroidTokenProcessResult(processResult);
                            MoveScreen();
                        } else {
                            alertdialog("",getString(R.string.alertdialog8));
                        }
                    } else {
                        if(mFlg.equals("3")){
                            mPreferenceUtils.setSendAndroidTokenProcessResult(processResult);
                            MoveScreen();
                        } else {
                            alertdialog("エラー",message);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    //解密，并且保存得到的数据
    private void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data, mAesKey);
        Log.d("***+++datas+++***", datas);
        Gson mGson = new Gson();
        try {
            JSONObject obj = new JSONObject(datas);
            User UserDate = mGson.fromJson(obj.getString(getString(R.string.User)),User.class);
            UserToken UTDate = mGson.fromJson(obj.getString(getString(R.string.UserToken)),UserToken.class);
            String Userid = UTDate.getUser_id();
            String Token = UTDate.getToken();
            String email = UserDate.getEmail();
            if(UserDate.getUser_type().equals("1")){
                alertdialog(getString(R.string.error),getString(R.string.alertdialog9));
            } else {
                mMyApplication.setuser_id(Userid);
                mMyApplication.setToken(Token);
                mPreferenceUtils.setUserFlg("1");
                mPreferenceUtils.setuserId(Userid);
                mPreferenceUtils.settoken(Token);
                mPreferenceUtils.setemail(email);
                InitialData();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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

    //初めのデータ取得
    private void InitialData(){
        mFlg = "2";
        String userid = mMyApplication.getuser_id();
        String token = mMyApplication.getToken();
        urllodad(userid,token);
    }

    private void sendDeviceToken(){
        mFlg = "3";
        String userid = mMyApplication.getuser_id();
        String token = mMyApplication.getToken();
        urllodad(userid,token);
    }

    //設定の基本情報をセット
    private void setMyApplication(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data, mAesKey);
        Log.d("***+++datas+++***", datas);
        try {
            JSONObject obj = new JSONObject(datas);
            if(obj.has(getString(R.string.id))){
                mMyApplication.setMyApplicationFlg("1");
                mMyApplication.setid(obj.getString(getString(R.string.id)));
                mMyApplication.setuser_id(obj.getString(getString(R.string.user_id)));
                mMyApplication.setfirst_name(obj.getString(getString(R.string.first_name)));
                mMyApplication.setlast_name(obj.getString(getString(R.string.last_name)));
                mMyApplication.setfirst_name_kana(obj.getString(getString(R.string.first_name_kana)));
                mMyApplication.setlast_name_kana(obj.getString(getString(R.string.last_name_kana)));
                mMyApplication.setbirthday(obj.getString(getString(R.string.birthday)));
                mMyApplication.setsex_div(obj.getString(getString(R.string.sex_div)));
                mMyApplication.setcountry(obj.getString(getString(R.string.country)));
                mMyApplication.setpost_code(obj.getString(getString(R.string.post_code)));
                mMyApplication.setadd_1(obj.getString(getString(R.string.add_1)));
                mMyApplication.setadd_2(obj.getString(getString(R.string.add_2)));
                mMyApplication.setadd_3(obj.getString(getString(R.string.add_3)));
                mMyApplication.setadd_4(obj.getString(getString(R.string.add_4)));
                mMyApplication.setphone_number(obj.getString(getString(R.string.phone_number)));
                JSONArray Resumes = obj.getJSONArray(getString(R.string.Resumes));
                int ResumeNum = 0;
                for(int i = 0; i < Resumes.length(); i++){
                    Gson mGson = new Gson();
                    Resume Resumedata = mGson.fromJson(Resumes.getString(i),Resume.class);
                    if(i == 0 && ! Resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_1(Resumedata.getId());
                        mMyApplication.setresume_name(Resumedata.getresume_name(),"1");
                        ResumeNum += 1;
                    } else if(i == 1 && ! Resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_2(Resumedata.getId());
                        mMyApplication.setresume_name(Resumedata.getresume_name(),"2");
                        ResumeNum += 1;
                    } else if(i == 2 && ! Resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_3(Resumedata.getId());
                        mMyApplication.setresume_name(Resumedata.getresume_name(),"3");
                        ResumeNum += 1;
                    }
                }
                mPreferenceUtils.setresume_number(ResumeNum);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        MoveScreen();
        sendDeviceToken();
    }

    //画面移動
    private void MoveScreen(){
        String saveid = mPreferenceUtils.getsaveid();
        Log.d(TAG, "saveid:" +saveid);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (saveid){
            case "ll_contact":
                intent.setClass(LoginActivity.this, ContactActivity.class);
                break;
            case "ll_mylist":
                intent.setClass(LoginActivity.this, MylistActivity.class);
                break;
            case "ll_personalsettings":
                intent.setClass(LoginActivity.this, PersonalSetActivity.class);
                break;
            case "SearchResults":
                mMyApplication.setkeyword(mKeyword);
                mMyApplication.setaddress(mAddress);
                mMyApplication.setemploymentStatus(mEmploymentStatus);
                mMyApplication.setyearlyIncome(mYearlyIncome);
                mMyApplication.setpage(mPage);
                mMyApplication.setjobinfo(mJobInfo);
                intent.setClass(LoginActivity.this, SearchResultsActivity.class);
                break;
            case "Apply":
                mMyApplication.setjobinfo(mJobInfo);
                intent.setClass(LoginActivity.this, ApplyActivity.class);
                break;
        }
        startActivity(intent);
    }

    //返回检索結果画面
    public void onClick(View view){
        Intent intent = new Intent();
        String saveId = mPreferenceUtils.getsaveid();
        switch (view.getId()){
            case R.id.tv_back:
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if(saveId.equals(getString(R.string.ll_search))){
                    intent.setClass(LoginActivity.this, SearchActivity.class);
                }
                if(saveId.equals(getString(R.string.ll_searchresults))){
                    intent.setClass(LoginActivity.this, SearchResultsActivity.class);
                }

                if(mActivity == null || mActivity.equals("")){
                    intent.setClass(LoginActivity.this, SearchActivity.class);
                    intent.putExtra(getString(R.string.act),"");
                } else if(mActivity.equals(getString(R.string.Apply))){
                    intent.setClass(LoginActivity.this, ApplyActivity.class);
                } else if(mActivity.equals(getString(R.string.SearchResults))){
                    intent.setClass(LoginActivity.this, SearchResultsActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.bu_MainLoginClick:
                login();
                break;
            case R.id.Click_Forgetpw:
                getPassword();
                break;
            case R.id.MakeNewuser_Click:
                mMyApplication.settermsofserviceflg("0");
                mMyApplication.setprivacypolicyflg("0");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(LoginActivity.this, MakeUserActivity.class);
                startActivity(intent);
                break;
            case R.id.login_btn_google:
                Log.d(TAG, "onClick google signIn: ");
                mLoginFlag = "1";
                googleSignIn();
                break;
            case R.id.login_btn_facebook:
                mLoginFlag = "2";
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
                break;
            case R.id.login_btn_yahoo:
                mLoginFlag = "3";
                intent.setClass(LoginActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                loginYahooStart();
                break;
            case R.id.login_btn_twitter:
                mLoginFlag = "4";
                twitterAuthClient.authorize(this, twitterCallback);
                break;
            case R.id.login_btn_line:
                mLoginFlag = "5";
                try {
                    intent = LineLoginApi.getLoginIntent(
                            view.getContext(),
                            getString(R.string.line_client_id),
                            new LineAuthenticationParams.Builder()
                                    .scopes(Arrays.asList(Scope.PROFILE, Scope.OPENID_CONNECT, Scope.OC_EMAIL))
                                    .build());
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
                break;
        }
        mPreferenceUtils.setLoginFlag(mLoginFlag);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (mPreferenceUtils.getLoginFlag()){
            case "1":
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle account.getId():" + account.getId());
                    Log.d(TAG, "firebaseAuthWithGoogle account.getEmail():" + account.getEmail());
                    //flag:1 googlle关联登录
                    SignInToKinWork(account.getEmail(),account.getId(),"1");
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(TAG, "Google sign in failed", e);
                    Log.e(TAG, "e："+ e.getMessage());
                }
                break;
            case "2":
                mFaceBookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case "4":
                twitterAuthClient.onActivityResult(requestCode, resultCode, data);
                break;
            case "5":
                LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
                Log.d(TAG, "result.getResponseCode():" + result.getResponseCode());
                switch (result.getResponseCode()) {
                    case SUCCESS:
                        // Login successful
                        Log.d(TAG, "result:" + result.toString());
                        Log.d(TAG, "result.getLineProfile().getUserId():" + result.getLineProfile().getUserId());
                        Log.d(TAG, "result.getLineIdToken().getEmail():" + result.getLineIdToken().getEmail());

                        //flag:5 line关联登录
                        SignInToKinWork(result.getLineIdToken().getEmail(),result.getLineProfile().getUserId(),"5");
                        break;
                    case CANCEL:
                        // Login canceled by user
                        Log.w(TAG, "Login canceled by user result.isSuccess():"+result.isSuccess());

                        break;
                    default:
                        // Login canceled due to other error
                        Log.e(TAG, "ERROR　Login FAILED!");
                        Log.e(TAG, "ERROR:" + result.getErrorData().toString());
                        Log.e(TAG, "ERROR lineApiClient:" + lineApiClient);
                }
                break;
        }
    }


    //lineログイン
    private void initLine(){
        lineApiClient = new LineApiClientBuilder(getApplicationContext(), getString(R.string.line_client_id)).build();
    }

    private void initGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void googleSignIn() {
        Log.d(TAG, "googleSignIn: ");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START facebook]
    CallbackManager mFaceBookCallbackManager;
    private void initFaceBook(){
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        mFaceBookCallbackManager = CallbackManager.Factory.create();
//        mFacebookLoginButton.setPermissions(Arrays.asList( "email"));

        LoginManager.getInstance().registerCallback(mFaceBookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "loginFaceBook onSuccess: ");
                //flag:2 facebook关联登录
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                handleSession(credential,loginResult.getAccessToken().getUserId(),"2");
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "loginFaceBook onCancel: ");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "loginFaceBook onError: ");
                Log.d(TAG, "exception: " + exception.toString());
                // App code
            }
        });
    }
    // [END facebook]

    // [START twitter]
    TwitterAuthClient twitterAuthClient;
    private void initTwitter(){
        twitterAuthClient = new TwitterAuthClient();
    }

    Callback<TwitterSession> twitterCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            Log.d(TAG, "setCallback twitterLogin:success" + result);
            ;
            AuthCredential credential = TwitterAuthProvider.getCredential(
                    result.data.getAuthToken().token,
                    result.data.getAuthToken().secret);
            String userId = String.valueOf(result.data.getUserId());
            //flag:4 twitter关联登录
            handleSession(credential,userId,"4");
        }
        @Override
        public void failure(TwitterException exception) {
            Log.w(TAG, "twitterLogin:failure", exception);
        }
    };
    // [END twitter]

    //ログインのuserIdとEmailを取得
    private void handleSession(AuthCredential credential,String userId,String flag) {
        Log.d(TAG, "handleTwitterSession:" );
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            SignInToKinWork(user.getEmail(),userId,flag);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }
    // [END twitter]


    // [START yahoo]
    //yahooID連携処理後、リクエスト処理
    //1を指定した場合、同意キャンセル時にredirect_uri設定先へ遷移する
    public final static String BAIL = "1";
    //最大認証経過時間
    public final static String MAX_AGE = "3600";
    public final static String YCONNECT_PREFERENCE_NAME = "yconnect";
    private String androidAppScheme = "";
    //yahooID連携処理
    private void loginYahooStart(){
        SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, android.app.Activity.MODE_PRIVATE);
        // YConnectインスタンス取得
        YConnectImplicit yconnect = YConnectImplicit.getInstance();
        /********************************************************
         Request Authorization Endpoint for getting Access Token.
         ********************************************************/

        Log.i(TAG, "Request authorization.");

        // 各パラメーター初期化
        // Client ID
        String clientId = getString(R.string.yahoo_client_id);
        //スキーム
        String scheme = getString(R.string.yahoo_login_scheme);
        //ホスト
        String host = getString(R.string.yahoo_login_host);
        // リクエストとコールバック間の検証用のランダムな文字列を指定してください
        String state = "44GC44Ga44GrWeOCk+ODmuODreODmuODrShez4leKQ==";
        // リプレイアタック対策のランダムな文字列を指定してください
        String nonce = "KOOAjeODu8+J44O7KeOAjVlhaG9vISAo77yP44O7z4njg7sp77yPSkFQQU4=";
        String display = OIDCDisplay.TOUCH;
        String[] prompt = {OIDCPrompt.DEFAULT};
        String[] scope = {OIDCScope.OPENID, OIDCScope.PROFILE, OIDCScope.EMAIL, OIDCScope.ADDRESS};

        // state、nonceを保存
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("state", state);
        editor.putString("nonce", nonce);
        editor.apply();

        String androidAppScheme = "android-app://" + getApplicationContext().getPackageName() + "/" + scheme + "/" + host + "/";
        Log.d(TAG, "androidAppScheme: " + androidAppScheme);
        // 各パラメーターを設定
        yconnect.init(clientId, androidAppScheme, state, display, prompt, scope, nonce, BAIL, MAX_AGE);
        // Authorizationエンドポイントにリクエスト
        // (CustomTabs、ブラウザーを起動して同意画面を表示)
        yconnect.requestAuthorization(this);
        finish();

    }
    private void getYahooUserInfo(){
        Log.d(TAG, "loginYahoo: ");
        Log.d(TAG, "mLoginFlg: " + mLoginFlag);
        SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, android.app.Activity.MODE_PRIVATE);
        // YConnectインスタンス取得
        YConnectImplicit yconnect = YConnectImplicit.getInstance();
        Intent intent = getIntent();
        Log.d(TAG, "loginYahoo intent.getAction(): " + intent.getAction());
        Log.d(TAG, "loginYahoo Intent.ACTION_VIEW: " + Intent.ACTION_VIEW);
        Log.d(TAG, "loginYahoo mPreferenceUtils.getLoginFlag(): " + mPreferenceUtils.getLoginFlag());
        // Client ID
        String clientId = getString(R.string.yahoo_client_id);
        //スキーム
        String scheme = getString(R.string.yahoo_login_scheme);
        //ホスト
        String host = getString(R.string.yahoo_login_host);

        if (mPreferenceUtils.getLoginFlag().equals("3")) {
            /*************************************************
             Parse the Response Url and Save the Access Token.
             *************************************************/
            try {
                Log.i(TAG, "Get Response Url and parse it.");
                // stateの読み込み
                String state = sharedPreferences.getString("state", null);
                String nonce = sharedPreferences.getString("nonce", null);
                // response Url(Authorizationエンドポイントより受け取ったコールバックUrl)から各パラメータを抽出
                Uri uri = intent.getData();
                Log.d(TAG, "uri:" + uri);
                String customUriScheme = scheme + "://" + host + "/";
                yconnect.parseAuthorizationResponse(uri, customUriScheme, state);
                // Access Token、ID Tokenを取得
                String accessTokenString = yconnect.getAccessToken();
                String idTokenString = yconnect.getIdToken();
                Log.i(TAG, "accessTokenString:" + accessTokenString);
                Log.i(TAG, "idTokenString:" + idTokenString);
                // Access Tokenを保存
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("access_token", accessTokenString);
                editor.apply();

                // 別スレッド(AsynckTask)でID Tokenの検証、UserInfoエンドポイントにリクエスト
                // androidAppSchemeを設定する
                YConnectAsyncTask asyncTask = new YConnectAsyncTask();
                asyncTask.setParams(this, clientId,idTokenString,accessTokenString,nonce, androidAppScheme,BAIL,MAX_AGE);
                asyncTask.setListener(new YConnectAsyncTask.Listener() {
                    @Override
                    public void onSuccess(YConnectImplicit yconnect) {
                        if(yconnect != null){
                            Log.i(TAG, "yconnect.getIdTokenObject().getSub():" + yconnect.getIdTokenObject().getSub());
                            Log.i(TAG, "yconnect.getUserInfoObject().getEmail():" + yconnect.getUserInfoObject().getEmail());
                            SignInToKinWork(yconnect.getUserInfoObject().getEmail(),yconnect.getIdTokenObject().getSub(),"3");
                        }
                    }
                });
                asyncTask.execute("Verify ID Token and Request UserInfo.");
            } catch (AuthorizationException e) {
                Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
            }

        }
    }
    // [END yahoo]

    /*****************************************
     flagの設定値:
     1: googlle
     2: facebook
     3: yahoo
     4: twitter
     5: line
     ****************************************/
    private void SignInToKinWork(String email,String loginId,String flag){
//        dialog = new ProgressDialog(this) ;
        //dialog.setTitle("提示") ;
//        dialog.setMessage(getString(R.string.kensakuchu)) ;
//        dialog.show();
        Log.d(TAG, "SignInToKinWork: ");
        Log.d(TAG, "SignInToKinWork email: "+email);
        Log.d(TAG, "SignInToKinWork loginId: " + loginId);
        Log.d(TAG, "SignInToKinWork flag: " +flag);
        PostDate pd = new PostDate();
        pd.setEmail(email);
        pd.setId(loginId);
        pd.setFlag(flag);
        String processData = new AESprocess().getencrypt(new Gson().toJson(pd,PostDate.class),mAesKey);
        Map<String,String> param = new HashMap<String, String>();
        param.put(getString(R.string.file), PARAM_other_Login);
        param.put(getString(R.string.data),processData);
        //数据通信处理（访问服务器，并取得访问结果）
        CommonAsyncTask commonAsyncTask = new CommonAsyncTask();
        commonAsyncTask.setParams(mDeviceId);
        commonAsyncTask.setListener(new CommonAsyncTask.Listener() {
            @Override
            public void onSuccess(String results) {
                if(results != null && !results.equals("")){
                    Log.d(TAG, "onSuccess results: " + results);
                    try {
                        JSONObject obj = new JSONObject(results);
                        Boolean processResult = obj.getBoolean(getString(R.string.processResult));
                        String message = obj.getString(getString(R.string.message));
                        if(processResult == true) {
                            String returnData = AESprocess.getdecrypt(obj.getString(getString(R.string.returnData)),mAesKey);
                            decryptchange(returnData);
                        } else {
                            alertdialog("エラー",message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        commonAsyncTask.execute(param);

    }
}


