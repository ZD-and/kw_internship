package jp.kinwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.linecorp.linesdk.LoginDelegate;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.co.yahoo.yconnect.YConnectImplicit;
import jp.co.yahoo.yconnect.core.oauth2.AuthorizationException;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ClassDdl.User;
import jp.kinwork.Common.ClassDdl.UserToken;
import jp.kinwork.Common.CommonAsyncTask;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;
import jp.kinwork.Common.YConnectAsyncTask;

public class NewSignInActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "NewSignInActivity";
    final static String other_Login = "/usersMobile/otherLogin";
    String mLoginFlag ="";
    String mDeviceId;
    String mAesKey;

    private static final int RC_SIGN_IN = 9001;
    private PreferenceUtils mPreferenceUtils;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    TwitterLoginButton mTwitterLoginButton;
//    com.linecorp.linesdk.widget.LoginButton mLineLoginButton;
//    com.facebook.login.widget.LoginButton mFacebookLoginButton;
    LineApiClient lineApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sign_in);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mPreferenceUtils = new PreferenceUtils(this);
        mDeviceId = mPreferenceUtils.getdeviceId();
        mAesKey = mPreferenceUtils.getAesKey();
        Log.d(TAG, "onStart mDeviceId: " + mDeviceId);
        Log.d(TAG, "onStart mAesKey: " + mAesKey);
        Log.d(TAG, "onStart mPreferenceUtils.getLoginFlag(): " + mPreferenceUtils.getLoginFlag());
        TextView tbBack = findViewById(R.id.tv_back);
        tbBack.setText("戻る");
        tbBack.setOnClickListener(this);
        findViewById(R.id.button_logout).setOnClickListener(this);
        findViewById(R.id.google_login_button).setOnClickListener(this);
//        findViewById(R.id.btn_yahoo_login).setOnClickListener(this);
//        findViewById(R.id.btn_facebook_login).setOnClickListener(this);
//        findViewById(R.id.btn_line_login).setOnClickListener(this);
//        findViewById(R.id.btn_twitter_login).setOnClickListener(this);
        mTwitterLoginButton = findViewById(R.id.twitter_login_button);
//        mLineLoginButton = findViewById(R.id.line_login_btn);

//        mFacebookLoginButton = findViewById(R.id.facebook_login_button);
//
        mTwitterLoginButton.setOnClickListener(this);
//        mLineLoginButton.setOnClickListener(this);
//        mFacebookLoginButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        initLine();
        getYahooUserInfo();
        initFaceBook();
        initTwitter();
        initGoogle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.tv_back:
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(NewSignInActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
//            case R.id.google_login_button:
//                Log.d(TAG, "onClick google signIn: ");
//                mLoginFlag = "1";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                googleSignIn();
//                break;
//            case R.id.btn_facebook_login:
//                mLoginFlag = "2";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList( "email"));
//                break;
//            case R.id.btn_yahoo_login:
//                mLoginFlag = "3";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                intent.setClass(NewSignInActivity.this,
//                        NewSignInActivity.class);
//                startActivity(intent);
//                loginYahooStart();
//                break;
//            case R.id.twitter_login_button:
//                mLoginFlag = "4";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                break;
//            case R.id.btn_twitter_login:
//                mLoginFlag = "4";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                twitterAuthClient.authorize(this,twitterCallback);
//                break;
//            case R.id.btn_line_login:
//                mLoginFlag = "5";
//                mPreferenceUtils.setLoginFlag(mLoginFlag);
//                try{
//                    intent = LineLoginApi.getLoginIntent(
//                            view.getContext(),
//                            getString(R.string.line_client_id),
//                            new LineAuthenticationParams.Builder()
//                                    .scopes(Arrays.asList(Scope.PROFILE,Scope.OPENID_CONNECT,Scope.OC_EMAIL))
//                                    .build());
//                    startActivityForResult(intent, 1);
//                }
//                catch(Exception e) {
//                    Log.e("ERROR", e.toString());
//                }
//                break;
            case R.id.button_logout:
                mPreferenceUtils.setLoginFlag("0");
                LoginManager.getInstance().logOut();//facebookのログアウト
                TwitterCore.getInstance().getSessionManager().clearActiveSession();//twitterのログアウト
                mAuth.signOut();//googleのログアウト
                new Thread(new Runnable() {
                    public void run() {
                        if(lineApiClient.getProfile().isSuccess()){
                            lineApiClient.logout();
                        }
                    }
                }).start();
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode:" + requestCode);
        Log.d(TAG, "onActivityResult resultCode:" + resultCode);
        Log.d(TAG, "onActivityResult mLoginFlg:" + mLoginFlag);
        String falg =mPreferenceUtils.getLoginFlag();
        Log.d(TAG, "onActivityResult mPreferenceUtils.getLoginFlag():" + mPreferenceUtils.getLoginFlag());
        Log.d(TAG, "onActivityResult falg:" + falg);
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
                faceBookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
    CallbackManager faceBookCallbackManager;
    private void initFaceBook(){
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        faceBookCallbackManager = CallbackManager.Factory.create();
//        mFacebookLoginButton.setPermissions(Arrays.asList( "email"));

        LoginManager.getInstance().registerCallback(faceBookCallbackManager, new FacebookCallback<LoginResult>() {
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
        mTwitterLoginButton.setCallback(twitterCallback);
        Log.d(TAG, "loginTwitter getActiveSession: " + TwitterCore.getInstance().getSessionManager().getActiveSession());

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
                            Log.d(TAG, "signInWithCredential:success");
                            Log.d(TAG, "handleSession mLoginFlg:" + mLoginFlag);
                            Log.d(TAG, "handleSession userId:" + userId);
                            Log.d(TAG, "signInWithCredential:user.getEmail()" + user.getEmail());
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
        SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = getSharedPreferences(YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);
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
        param.put(getString(R.string.file),other_Login);
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

    //通信结果提示
    private void alertdialog(String title, String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }

    //解密，并且保存得到的数据
    private void decryptchange(String datas){
        Log.d(TAG, datas);
        Gson mGson = new Gson();
        try {
            JSONObject obj = new JSONObject(datas);
            User user = mGson.fromJson(obj.getString(getString(R.string.User)),User.class);
            UserToken userToken = mGson.fromJson(obj.getString(getString(R.string.UserToken)),UserToken.class);
            String userId = userToken.getUser_id();
            String token = userToken.getToken();
            String email = user.getEmail();
            Log.d(TAG, "decryptchange Userid: " + userId);
            Log.d(TAG, "decryptchange token: " + token);
            Log.d(TAG, "decryptchange email: " + email);
            Log.d(TAG, "decryptchange User_type: " + user.getUser_type());
            if(user.getUser_type().equals("1")){
                alertdialog(getString(R.string.error),getString(R.string.alertdialog9));
            } else {

            }
//            mMyApplication.setuser_id(Userid);
//            mMyApplication.setToken(Token);
//            mPreferenceUtils.setUserFlg("1");
//            mPreferenceUtils.setuserId(Userid);
//            mPreferenceUtils.settoken(Token);
//            mPreferenceUtils.setemail(email);
//            if(UserDate.getUser_type().equals("1")){
//                alertdialog(getString(R.string.error),getString(R.string.alertdialog9));
//            } else {
//
//                InitialData();
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
