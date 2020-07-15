package jp.kinwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ActivityCollector;
import jp.kinwork.Common.ClassDdl.UserToken;
import jp.kinwork.Common.CommonView.JumpTextWatcher;
import jp.kinwork.Common.CountDown;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.NetworkUtils;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

public class MakeUserActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG = "MakeUserActivity";
    final static String PARAM_sendValidateEmail = "/usersMobile/sendValidateEmail";
    final static String PARAM_checkValidateCode = "/usersMobile/checkValidateCode";
    final static String PARAM_setPassword = "/usersMobile/setPassword";
    private String mDeviceId;
    private String mAesKey;
    private String mScreenflg = "";
    private String mPrivacypolicyflg = "";
    private String mTermsofserviceflg = "";
    private String mEmail = "";
    private String mToken = "";

    private TextView tvinputA;
    private EditText edinputA;
    private TextView tvinputB;
    private EditText edinputB;

    private TextView tvDateCode;
    private TextView tvCountdown;
    private ImageView ivtermsofservice,ivprivacypolicy;
    private Button mStartMakeUser;

    private PreferenceUtils mPreferenceUtils;
    private MyApplication mMyApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_user);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        ActivityCollector.addActivity(this);
        load();
        Initialization();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //null reference 処理
        if(im.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getApplicationWindowToken() != null) {
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    //关闭，返回登录画面
    public void Click_cancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(getString(R.string.buildermessage)).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                mPreferenceUtils.deluserinfo();
                mMyApplication.setscreenflg("");
                mMyApplication.settermsofserviceflg("");
                mMyApplication.setprivacypolicyflg("");
                mMyApplication.setinputA("");
                mMyApplication.setinputB("");
                Intent intentClose = new Intent();
                intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intentClose.setClass(MakeUserActivity.this, LoginActivity.class);
                startActivity(intentClose);
            }
        }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消按钮的点击事件
            }
        }).show();
    }


    public void Initialization(){
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        tvBack.setText("戻る");
        tvinputA = (TextView) findViewById(R.id.tv_input_A);
        edinputA = (EditText) findViewById(R.id.ed_input_A);
        tvinputB = (TextView) findViewById(R.id.tv_input_B);
        edinputB = (EditText) findViewById(R.id.ed_input_B);
        edinputA.addTextChangedListener(new JumpTextWatcher(edinputA,edinputB));
        edinputB.addTextChangedListener(new JumpTextWatcher(edinputB,edinputA));
        tvDateCode = (TextView) findViewById(R.id.tv_Date_Code);
        tvDateCode.setOnClickListener(this);
        tvCountdown = (TextView) findViewById(R.id.tv_countdown);
        findViewById(R.id.ll_termsofservice).setOnClickListener(this);
        findViewById(R.id.ll_privacypolicy).setOnClickListener(this);
        ivtermsofservice = findViewById(R.id.iv_termsofservice);
        ivprivacypolicy = findViewById(R.id.iv_privacypolicy);
        mStartMakeUser = (Button) findViewById(R.id.bu_start_make_user);
        mStartMakeUser.setOnClickListener(this);
        mPreferenceUtils = new PreferenceUtils(MakeUserActivity.this);
        mEmail = mPreferenceUtils.getEmail();
        mToken = mPreferenceUtils.gettoken();
        mMyApplication = (MyApplication) getApplication();
        mScreenflg = mMyApplication.getscreenflg();
        mTermsofserviceflg = mMyApplication.gettermsofserviceflg();
        mPrivacypolicyflg = mMyApplication.getprivacypolicyflg();
        if(mMyApplication.getinputA().length() > 0){
            edinputA.setText(mMyApplication.getinputA());
        }
        if(mMyApplication.getinputB().length() > 0){
            edinputB.setText(mMyApplication.getinputB());
        }
        if(mTermsofserviceflg.equals("1")){
            ivtermsofservice.setImageResource(R.drawable.ic_check_box);
        }
        if(mPrivacypolicyflg.equals("1")) {
            ivprivacypolicy.setImageResource(R.drawable.ic_check_box);
        }
        if(mScreenflg.equals("") || mScreenflg.equals(getString(R.string.sendValidateEmail))){
            sendValidateEmail();
        } else if(mScreenflg.equals(getString(R.string.checkValidateCode))){
            checkValidateCode();
        } else {
            setPassword();
        }
    }

    //设备IDと対象Key取得
    public void load(){
        SharedPreferences Initial_object = getSharedPreferences(getString(R.string.Initial), Context.MODE_PRIVATE);
        mDeviceId = Initial_object.getString(getString(R.string.deviceid),"A");
        mAesKey = Initial_object.getString(getString(R.string.Information_Name_aesKey),"A");

    }
    //注册账号画面设定
    public void sendValidateEmail(){
        tvinputA.setText(getString(R.string.mailaddressnihongo));
        tvinputB.setText(getString(R.string.mailaddressnihongo));
        tvDateCode.setVisibility(View.GONE);
        mStartMakeUser.setText(getString(R.string.bubutton));
        mScreenflg = getString(R.string.sendValidateEmail);
//        // 新建一个可以添加文本的对象
//        SpannableString ee = new SpannableString(getString(R.string.mailaddressnihongo));
//        // 设置文本字体大小
//        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
//        // 将字体大小附加到文本的属性
//        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // 设置hint属性
//        edinputA.setHint(new SpannedString(ee));//转码
        // 新建一个可以添加文本的对象
        SpannableString eec = new SpannableString(getString(R.string.Spannable));
        // 设置文本字体大小
        AbsoluteSizeSpan aeec = new AbsoluteSizeSpan(15, true);
        // 将字体大小附加到文本的属性
        eec.setSpan(aeec, 0, eec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint属性
        edinputB.setHint(new SpannedString(eec));//转码

        edinputA.setHint(getString(R.string.mailaddressnihongo));//转码
//        edinputB.setHint(getString(R.string.Spannable));//转码
    }
    //検証コード画面设定
    public void checkValidateCode(){
        tvinputA.setText(getString(R.string.tvinputA));
        tvinputB.setVisibility(View.GONE);
        edinputB.setVisibility(View.GONE);
        tvDateCode.setVisibility(View.VISIBLE);
        mStartMakeUser.setText(getString(R.string.bubutton2));
        mScreenflg = getString(R.string.checkValidateCode);
//        // 新建一个可以添加文本的对象
//        SpannableString ee = new SpannableString(getString(R.string.Spannableee));
//        // 设置文本字体大小
//        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
//        // 将字体大小附加到文本的属性
//        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // 设置hint属性
//        edinputA.setHint(new SpannedString(ee));//转码

        edinputA.setHint(getString(R.string.Spannableee));//转码
    }
    //输入密码画面设定
    public void setPassword(){
        tvinputA.setText(getString(R.string.password));
        tvinputB.setText(getString(R.string.password));
        tvinputB.setVisibility(View.VISIBLE);
        edinputB.setVisibility(View.VISIBLE);
        edinputA.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edinputB.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tvDateCode.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.GONE);
        mStartMakeUser.setText(getString(R.string.kakunin));
        mScreenflg = getString(R.string.setPassword);
//        // 新建一个可以添加文本的对象
//        SpannableString ee = new SpannableString(getString(R.string.password));
//        // 设置文本字体大小
//        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
//        // 将字体大小附加到文本的属性
//        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // 设置hint属性
//        edinputA.setHint(new SpannedString(ee));//转码
        // 新建一个可以添加文本的对象
        SpannableString eec = new SpannableString(getString(R.string.Spannable));
        // 设置文本字体大小
        AbsoluteSizeSpan aeec = new AbsoluteSizeSpan(15, true);
        // 将字体大小附加到文本的属性
        eec.setSpan(aeec, 0, eec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint属性
        edinputB.setHint(new SpannedString(eec));//转码


        edinputA.setHint(getString(R.string.password));//转码
//        edinputB.setHint(getString(R.string.Spannable));//转码

    }

    //按钮触发事件
    public void bt_Click(){
        String inputA = edinputA.getText().toString();
        String inputB = edinputB.getText().toString();
        mPreferenceUtils.setdeviceId(mDeviceId);
        mPreferenceUtils.setAesKey(mAesKey);
        if(mScreenflg.equals(getString(R.string.setPassword)) && (! mTermsofserviceflg.equals("1") || ! mPrivacypolicyflg.equals("1"))){
            alertdialog(getString(R.string.alertdialog10));
        } else {
            sendToServer(inputA,inputB);
        }
    }

    private void sendToServer(String inputA,String inputB){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        //Json格式转换并且加密
        switch (mScreenflg){
            case "sendValidateEmail":
                Pdata.setEmail(inputA);
                Pdata.setEmailConform(inputB);
                param.put("file",PARAM_sendValidateEmail);
                break;
            case "checkValidateCode":
                Pdata.setEmail(mEmail);
                Pdata.setValidateCode(inputA);
                param.put("file",PARAM_checkValidateCode);
                break;
            case "setPassword":
                Pdata.setEmail(mEmail);
                Pdata.setToken(mToken);
                Pdata.setPassword(inputA);
                Pdata.setPasswordConform(inputB);
                param.put("file",PARAM_setPassword);
                break;
        }
        String data = JsonChnge(mAesKey,Pdata);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name), mScreenflg);
        Log.d("***screenflg", mScreenflg);
        Log.d("***Email", mEmail);
        Log.d("***token", mToken);
        new GithubQueryTask().execute(param);
    }

    //契约按钮触发事件
    public void onClick(View View){
        String Agreement = "";
        switch (View.getId()){
            case R.id.tv_back:
                Click_cancel();
                break;
            case R.id.ll_privacypolicy:
                if(mPrivacypolicyflg.equals("1")){
                    ivprivacypolicy.setImageResource(R.drawable.ic_check_box_outline);
                    mPrivacypolicyflg = "0";
                    mMyApplication.setprivacypolicyflg("0");
                } else {
                    Agreement = getString(R.string.privacypolicy);
                }
                break;
            case R.id.ll_termsofservice:
                if(mTermsofserviceflg.equals("1")){
                    ivtermsofservice.setImageResource(R.drawable.ic_check_box_outline);
                    mTermsofserviceflg = "0";
                    mMyApplication.settermsofserviceflg("0");
                } else {
                    Agreement = getString(R.string.termsofservice);
                }
                break;
            case R.id.tv_Date_Code:
                Log.d(TAG, "onClick: tv_Date_Code" );
                tvDateCode.setTextColor(Color.parseColor("#80323232"));
                tvDateCode.setEnabled(false);
                tvCountdown.setVisibility(android.view.View.VISIBLE);
                mScreenflg = getString(R.string.sendValidateEmail);
                sendToServer(mEmail,mEmail);
                CountDown countDown = new CountDown(60000,1000);
                countDown.setOnFinishListener(new CountDown.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        tvDateCode.setEnabled(true);
                        tvDateCode.setTextColor(Color.parseColor("#0196FF"));
                        tvCountdown.setVisibility(android.view.View.INVISIBLE);
                    }
                });
                countDown.setOnTickListener(new CountDown.OnTickListener() {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d(TAG, "onTick: " + millisUntilFinished);
                        int millisUntilFinished2 = (int) millisUntilFinished/1000;
                        tvCountdown.setText("("+ millisUntilFinished2 + "s)");
                    }
                });
                countDown.start();
                break;
            case R.id.bu_start_make_user:
                bt_Click();
                break;

        }
        Log.d("Agreement", Agreement);
        if(!Agreement.equals("")){
            mMyApplication.setAgreement(Agreement);
            mMyApplication.setscreenflg(mScreenflg);
            if(edinputA.getText().length() > 0){
                mMyApplication.setinputA(edinputA.getText().toString());
            }
            if(edinputB.getText().length() > 0){
                mMyApplication.setinputB(edinputB.getText().toString());
            }
            MoveIntent(getString(R.string.Agreement));
        }
    }

    public void MoveIntent(String Activity){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(Activity.equals(getString(R.string.setPassword))){
            intent.setClass(MakeUserActivity.this, PersonalSetActivity.class);
        } else {
            mMyApplication.setActivity("MakeUserActivity");
            intent.setClass(MakeUserActivity.this, AgreementActivity.class);
        }
        startActivity(intent);
    }

    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d("sdPdata", sdPdata);
        AESprocess AESprocess = new AESprocess();
        String encrypt = AESprocess.getencrypt(sdPdata,AesKey);
        return encrypt;
    }
    //访问服务器，并取得访问结果
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            URL searchUrl = NetworkUtils.buildUrl(file);
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl,data, mDeviceId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d(TAG, "Results:" + githubSearchResults);
                Log.d(TAG, "mScreenflg:" + mScreenflg);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        decryptchange(returnData);
                    } else {
                        if(mScreenflg.equals(getString(R.string.checkValidateCode))){
                            message = obj.getString(getString(R.string.message));
                        } else {
                            String fieldErrors = obj.getString(getString(R.string.fieldErrors));
                            JSONObject fieldError = new JSONObject(fieldErrors);
                            if(fieldError.has(getString(R.string.email))){
                                Log.d("***+++email+++***", fieldError.getString("email"));
                                JSONArray ja = fieldError.getJSONArray(getString(R.string.email));
                                Log.d("***email(index)***", ja.getString(0));
                                message = ja.getString(0);
                            }
                            if(fieldError.has(getString(R.string.emailConfirm))){
                                JSONArray ja = fieldError.getJSONArray(getString(R.string.emailConfirm));
                                Log.d("***emailConfirm***", ja.getString(0));
                                message = ja.getString(0);
                            }
                        }
                        alertdialog(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data, mAesKey);
        Log.d("***+++screenflg+++***", mScreenflg);
        Log.d("***screenflg-datas***", datas);
        Gson mGson = new Gson();
        PostDate PDate = mGson.fromJson(datas, PostDate.class);
        edinputA.setText("");
        edinputB.setText("");
        switch (mScreenflg){
            case "sendValidateEmail":
                mEmail = PDate.getEmail();
                mPreferenceUtils.setemail(mEmail);
                checkValidateCode();
                break;
            case "checkValidateCode":
                mEmail = PDate.getEmail();
                mToken = PDate.getToken();
                mPreferenceUtils.settoken(mToken);
                setPassword();
                break;
            case "setPassword":
                UserToken UserToken = PDate.getUserToken();
                String userID = UserToken.getUser_id();
                mPreferenceUtils.setuserId(userID);
                MoveIntent(getString(R.string.setPassword));
                break;
        }
    }

    //结果提示
    private void alertdialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = message;
        if(mScreenflg.equals("setPassword")){
            msg = getString(R.string.passworderror);
        }
        builder.setTitle(getString(R.string.error)).setMessage(msg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }

}
