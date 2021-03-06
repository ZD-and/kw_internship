package jp.kinwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TableLayout;
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
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.NetworkUtils;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

public class MakeUserActivity extends AppCompatActivity implements View.OnClickListener{

    final static String PARAM_sendValidateEmail = "/usersMobile/sendValidateEmail";
    final static String PARAM_checkValidateCode = "/usersMobile/checkValidateCode";
    final static String PARAM_setPassword = "/usersMobile/setPassword";
    private String deviceId;
    private String AesKey;
    private String screenflg = "";
    private String privacypolicyflg = "";
    private String termsofserviceflg = "";
    private String Email = "";
    private String token = "";

    private TextView tvinputA;
    private EditText edinputA;
    private TextView tvinputB;
    private EditText edinputB;

    private TableLayout tlvalidateCode;
    private TableLayout tlmudummyview;

    private ImageView ivprivacypolicy;
    private TextView tvprivacypolicy;

    private ImageView ivtermsofservice;
    private TextView tvtermsofservice;

    private Button bubutton;

    private PreferenceUtils PreferenceUtils;
    private MyApplication MyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_user);
        ActivityCollector.addActivity(this);
        load();
        Initialization();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    public void Click_cancel(){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("").setMessage(getString(R.string.buildermessage)).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtils.deluserinfo();
                        MyApplication.setscreenflg("");
                        MyApplication.settermsofserviceflg("");
                        MyApplication.setprivacypolicyflg("");
                        MyApplication.setinputA("");
                        MyApplication.setinputB("");
                        Intent intentClose = new Intent();
                        intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intentClose.setClass(MakeUserActivity.this, MainKinWork.class);
                        startActivity(intentClose);
                    }
                }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void Initialization(){
        tvinputA = (TextView) findViewById(R.id.tv_input_A);
        edinputA = (EditText) findViewById(R.id.ed_input_A);
        tvinputB = (TextView) findViewById(R.id.tv_input_B);
        edinputB = (EditText) findViewById(R.id.ed_input_B);
        edinputA.setOnTouchListener(touchListener);
        edinputB.setOnTouchListener(touchListener);
        tlvalidateCode = (TableLayout) findViewById(R.id.tl_validateCode);
        tlmudummyview=findViewById(R.id.tl_mu_dummyview);
        tlmudummyview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_cancel();
            }
        });
        ivprivacypolicy = (ImageView) findViewById(R.id.iv_privacypolicy);
        tvprivacypolicy = (TextView) findViewById(R.id.tv_privacypolicy);
        ivtermsofservice = (ImageView) findViewById(R.id.iv_termsofservice);
        tvtermsofservice = (TextView) findViewById(R.id.tv_termsofservice);
        ivprivacypolicy.setOnClickListener(this);
        tvprivacypolicy.setOnClickListener(this);
        ivtermsofservice.setOnClickListener(this);
        tvtermsofservice.setOnClickListener(this);

        bubutton = (Button) findViewById(R.id.bu_button);
        bubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_Click();
            }
        });
        PreferenceUtils = new PreferenceUtils(MakeUserActivity.this);
        Email = PreferenceUtils.getEmail();
        token = PreferenceUtils.gettoken();
        MyApplication = (MyApplication) getApplication();
        screenflg = MyApplication.getscreenflg();
        termsofserviceflg = MyApplication.gettermsofserviceflg();
        privacypolicyflg = MyApplication.getprivacypolicyflg();
        if(MyApplication.getinputA().length() > 0){
            edinputA.setText(MyApplication.getinputA());
        }
        if(MyApplication.getinputB().length() > 0){
            edinputB.setText(MyApplication.getinputB());
        }
        if(termsofserviceflg.equals("1")){
            ivtermsofservice.setImageResource(R.drawable.ic_check_box);
        }
        if(privacypolicyflg.equals("1")) {
            ivprivacypolicy.setImageResource(R.drawable.ic_check_box);
        }
        if(screenflg.equals("") || screenflg.equals(getString(R.string.sendValidateEmail))){
            sendValidateEmail();
        } else if(screenflg.equals(getString(R.string.checkValidateCode))){
            checkValidateCode();
        } else {
            setPassword();
        }
    }
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (view.getId()){
                case R.id.ed_input_A:
                    edinputA.setBackgroundResource(R.drawable.ic_shape_blue);
                    edinputB.setBackgroundResource(R.drawable.ic_shape);
                    break;
                case R.id.ed_input_B:
                    edinputA.setBackgroundResource(R.drawable.ic_shape);
                    edinputB.setBackgroundResource(R.drawable.ic_shape_blue);
                    break;
            }
            return false;
        }
    };
    public void load(){
        SharedPreferences Initial_object = getSharedPreferences(getString(R.string.Initial), Context.MODE_PRIVATE);
        deviceId = Initial_object.getString(getString(R.string.deviceid),"A");
        AesKey = Initial_object.getString(getString(R.string.Information_Name_aesKey),"A");

    }
    //注册账号画面设定
    public void sendValidateEmail(){
        tvinputA.setText(getString(R.string.mailaddressnihongo));
        tvinputB.setText(getString(R.string.mailaddressnihongo));
        tlvalidateCode.setVisibility(View.GONE);
        bubutton.setText(getString(R.string.bubutton));
        screenflg = getString(R.string.sendValidateEmail);
        SpannableString ee = new SpannableString(getString(R.string.mailaddressnihongo));
        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edinputA.setHint(new SpannedString(ee));
        SpannableString eec = new SpannableString(getString(R.string.Spannable));
        AbsoluteSizeSpan aeec = new AbsoluteSizeSpan(12, true);
        eec.setSpan(aeec, 0, eec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edinputB.setHint(new SpannedString(eec));
    }
    public void checkValidateCode(){
        tvinputA.setText(getString(R.string.tvinputA));
        tvinputB.setVisibility(View.GONE);
        edinputB.setVisibility(View.GONE);
        tlvalidateCode.setVisibility(View.VISIBLE);
        bubutton.setText(getString(R.string.bubutton2));
        screenflg = getString(R.string.checkValidateCode);
        SpannableString ee = new SpannableString(getString(R.string.Spannableee));
        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edinputA.setHint(new SpannedString(ee));//转码
    }
    public void setPassword(){
        tvinputA.setText(getString(R.string.password));
        tvinputB.setText(getString(R.string.password));
        tvinputB.setVisibility(View.VISIBLE);
        edinputB.setVisibility(View.VISIBLE);
        edinputA.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edinputB.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tlvalidateCode.setVisibility(View.GONE);
        bubutton.setText(getString(R.string.kakunin));
        screenflg = getString(R.string.setPassword);
        SpannableString ee = new SpannableString(getString(R.string.password));
        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edinputA.setHint(new SpannedString(ee));//转码
        SpannableString eec = new SpannableString(getString(R.string.Spannable));
        AbsoluteSizeSpan aeec = new AbsoluteSizeSpan(12, true);
        eec.setSpan(aeec, 0, eec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        edinputB.setHint(new SpannedString(eec));
    }

    public void bt_Click(){
        String inputA = edinputA.getText().toString();
        String inputB = edinputB.getText().toString();
        PreferenceUtils.setdeviceId(deviceId);
        PreferenceUtils.setAesKey(AesKey);
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        switch (screenflg){
            case "sendValidateEmail":
                Pdata.setEmail(inputA);
                Pdata.setEmailConform(inputB);
                param.put("file",PARAM_sendValidateEmail);
                break;
            case "checkValidateCode":
                Pdata.setEmail(Email);
                Pdata.setValidateCode(inputA);
                param.put("file",PARAM_checkValidateCode);
                break;
            case "setPassword":
                Pdata.setEmail(Email);
                Pdata.setToken(token);
                Pdata.setPassword(inputA);
                Pdata.setPasswordConform(inputB);
                param.put("file",PARAM_setPassword);
                break;
        }
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),screenflg);
        Log.d("***screenflg", screenflg);
        Log.d("***Email", Email);
        Log.d("***token", token);
        if(screenflg.equals(getString(R.string.setPassword)) && (! termsofserviceflg.equals("1") || ! privacypolicyflg.equals("1"))){
            alertdialog(getString(R.string.alertdialog10));
        } else {
            new GithubQueryTask().execute(param);
        }
    }

    public void onClick(View View){
        String Agreement = "";
        switch (View.getId()){
            case R.id.iv_privacypolicy:
            case R.id.tv_privacypolicy:
                if(privacypolicyflg.equals("1")){
                    ivprivacypolicy.setImageResource(R.drawable.ic_check_box_outline);
                    privacypolicyflg = "0";
                    MyApplication.setprivacypolicyflg("0");
                } else {
                    Agreement = getString(R.string.privacypolicy);
                }
                break;
            case R.id.iv_termsofservice:
            case R.id.tv_termsofservice:
                if(termsofserviceflg.equals("1")){
                    ivtermsofservice.setImageResource(R.drawable.ic_check_box_outline);
                    termsofserviceflg = "0";
                    MyApplication.settermsofserviceflg("0");
                } else {
                    Agreement = getString(R.string.termsofservice);
                }
                break;
        }
        Log.d("Agreement", Agreement);
        if(!Agreement.equals("")){
            MyApplication.setAgreement(Agreement);
            MyApplication.setscreenflg(screenflg);
            if(edinputA.getText().length() > 0){
                MyApplication.setinputA(edinputA.getText().toString());
            }
            if(edinputB.getText().length() > 0){
                MyApplication.setinputB(edinputB.getText().toString());
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
            intent.setClass(MakeUserActivity.this, AgreementActivity.class);
        }
        startActivity(intent);
    }

    public static String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d("sdPdata", sdPdata);
//        AES mAes = new AES();
//        byte[] mBytes = null;
//        try {
//            mBytes = sdPdata.getBytes("UTF8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String enString = mAes.encrypt(mBytes,AesKey);
//        String encrypt = enString.replace("\n", "").replace("+","%2B");
//        return encrypt;
        AESprocess AESprocess = new AESprocess();
        String encrypt = AESprocess.getencrypt(sdPdata,AesKey);
        return encrypt;
    }
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            URL searchUrl = NetworkUtils.buildUrl(file);
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl,data,deviceId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d("***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = "";
                    if(processResult == true) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        decryptchange(returnData);
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
        String datas = AESprocess.getdecrypt(data,AesKey);
        Log.d("***+++screenflg+++***", screenflg);
        Log.d("***screenflg-datas***", datas);
        Gson mGson = new Gson();
        PostDate PDate = mGson.fromJson(datas, PostDate.class);
        edinputA.setText("");
        edinputB.setText("");
        switch (screenflg){
            case "sendValidateEmail":
                Email = PDate.getEmail();
                PreferenceUtils.setemail(Email);
                checkValidateCode();
                break;
            case "checkValidateCode":
                Email = PDate.getEmail();
                token = PDate.getToken();
                PreferenceUtils.settoken(token);
                setPassword();
                break;
            case "setPassword":
                UserToken UserToken = PDate.getUserToken();
                String userID = UserToken.getUser_id();
                PreferenceUtils.setuserId(userID);
                MoveIntent(getString(R.string.setPassword));
                break;
        }
    }

    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.teiji)).setMessage(meg).setPositiveButton(getString(R.string.kakutei), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }

}
