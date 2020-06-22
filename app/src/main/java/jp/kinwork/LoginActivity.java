package jp.kinwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ActivityCollector;
import jp.kinwork.Common.ClassDdl.Resume;
import jp.kinwork.Common.ClassDdl.User;
import jp.kinwork.Common.ClassDdl.UserToken;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    final static String PARAM_login = "/usersMobile/login";
    final static String PARAM_Forgetwe = "/usersMobile/sendPasswordRecoverMail";
    final static String PARAM_init = "/MypagesMobile/initMypageData";

    private String deviceId;
    private String AesKey;
    private String Email;
    private String password;
    private String keyword;
    private String address;
    private String employmentStatus;
    private String yearlyIncome;
    private String page;
    private String flg;
    private String Activity = "";
    private String message = "******";
    private String JobInfo;
    private EditText edloginEmail;
    private EditText edpassword;
    private boolean processResult;
    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;
    private Intent intent;

    private Button LoginClick;

    private TextView Forgetpw;
    private TextView MakeNewuser_Click;
    private TextView NewuserSignIn_Click;
    private ProgressDialog dialog;

    Class<?> mAct;

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
        edloginEmail = (EditText) findViewById(R.id.ed_login_email);
        edpassword = (EditText) findViewById(R.id.ed_password);
        edloginEmail.setOnTouchListener(touchListener);
        edpassword.setOnTouchListener(touchListener);

        LoginClick=findViewById(R.id.bu_MainLoginClick);
        LoginClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainLoginClick();
            }
        });
        Forgetpw=findViewById(R.id.Click_Forgetpw);
        Forgetpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Forgetpw();
            }
        });
        MakeNewuser_Click=findViewById(R.id.MakeNewuser_Click);
        NewuserSignIn_Click=findViewById(R.id.NewuserSignIn_Click);
        MakeNewuser_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct = MakeUserActivity.class;
                MakeNewuser_Click(mAct);
            }
        });

        NewuserSignIn_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAct = NewSignInActivity.class;
                MakeNewuser_Click(mAct);
            }
        });
        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(LoginActivity.this);
        keyword = mMyApplication.getkeyword();
        address = mMyApplication.getaddress();
        employmentStatus = mMyApplication.getemploymentStatus();
        yearlyIncome = mMyApplication.getyearlyIncome();
        page = mMyApplication.getpage();
        JobInfo = mMyApplication.getjobinfo();
        intent = getIntent();
        Activity = intent.getStringExtra(getString(R.string.Activity));
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.login)) ;
    }

    //点击输入框变蓝
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (view.getId()){
                case R.id.ed_login_email:
                    edloginEmail.setBackgroundResource(R.drawable.ic_shape_blue);
                    edpassword.setBackgroundResource(R.drawable.ic_shape);
                    break;
                case R.id.ed_password:
                    edloginEmail.setBackgroundResource(R.drawable.ic_shape);
                    edpassword.setBackgroundResource(R.drawable.ic_shape_blue);
                    break;
            }
            return false;
        }
    };
    //登录处理
    public void MainLoginClick(){
        flg = "0";
        Email = edloginEmail.getText().toString();
        password = edpassword.getText().toString();
        urllodad(Email,password);
    }


    //密码忘记的时候，再取得
    public void Click_Forgetpw(){
        flg = "1";
        Email = edloginEmail.getText().toString();
        if(Email.equals("")){
            alertdialog(getString(R.string.error),getString(R.string.mailinput));
            return;
        }
        //Json格式转换并且加密
        String data = JsonChnge(AesKey,Email,"",flg);
        Map<String,String>param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_Forgetwe);
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //新账号作成画面移动
    public void MakeNewuser_Click(Class<?> act){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(LoginActivity.this, act);
        startActivity(intent);
    }

    //设备IDと対象Key取得
    private void load_id_key(){
        SharedPreferences object = getSharedPreferences("Initial", Context.MODE_PRIVATE);
        deviceId = object.getString(getString(R.string.deviceid),"A");
        AesKey = object.getString(getString(R.string.Information_Name_aesKey),"A");
        mPreferenceUtils.setdeviceId(deviceId);
        mPreferenceUtils.setAesKey(AesKey);
    }

    //内容取得、通信
    private void urllodad(String data_A, String data_B) {
        //Json格式转换并且加密
        String data = JsonChnge(AesKey,data_A,data_B,flg);
        Map<String,String>param = new HashMap<String, String>();
        if (flg.equals("0")){
            param.put("file",PARAM_login);
        } else {
            param.put(getString(R.string.file),PARAM_init);
        }
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //转换为Json格式并且AES加密
    private static String JsonChnge(String AesKey,String Data_a,String Data_b,String Flg) {
        PostDate Pdata = new PostDate();
        if(! Flg.equals("2")){
            Pdata.setEmail(Data_a);
            if(! Flg.equals("1")){
                Pdata.setPassword(Data_b);
            }
        } else {
            Pdata.setUserId(Data_a);
            Pdata.setToken(Data_b);
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
            dialog.show();
        };

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
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
                Log.d("***Results***", githubSearchResults);
                dialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    processResult = obj.getBoolean(getString(R.string.processResult));
                    message = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        if(flg.equals("0")){
                            decryptchange(returnData);
                        } else if(flg.equals("2")){
                            setmMyApplication(returnData);
                        } else {
                            alertdialog("",getString(R.string.alertdialog8));
                        }
                    } else {
                        alertdialog("エラー",message);
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
        String datas = AESprocess.getdecrypt(data,AesKey);
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
        flg = "2";
        String userid = mMyApplication.getuser_id();
        String token = mMyApplication.getToken();
        urllodad(userid,token);
    }

    //設定の基本情報をセット
    private void setmMyApplication(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data,AesKey);
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
        MoveScreen();
    }

    //画面移動
    private void MoveScreen(){
        String saveid = mPreferenceUtils.getsaveid();
        Log.d("**saveid**", saveid);
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
                mMyApplication.setkeyword(keyword);
                mMyApplication.setaddress(address);
                mMyApplication.setemploymentStatus(employmentStatus);
                mMyApplication.setyearlyIncome(yearlyIncome);
                mMyApplication.setpage(page);
                mMyApplication.setjobinfo(JobInfo);
                intent.setClass(LoginActivity.this, SearchResultsActivity.class);
                break;
            case "Apply":
                mMyApplication.setjobinfo(JobInfo);
                intent.setClass(LoginActivity.this, ApplyActivity.class);
                break;
        }
        startActivity(intent);
    }

    //返回检索結果画面
    public void onClick(View View){
        switch (View.getId()){
            case R.id.tv_back:
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                if(Activity == null || Activity.equals("")){
                    intent.setClass(LoginActivity.this, SearchActivity.class);
                    intent.putExtra(getString(R.string.act),"");
                } else if(Activity.equals(getString(R.string.Apply))){
                    intent.setClass(LoginActivity.this, ApplyActivity.class);
                } else if(Activity.equals(getString(R.string.SearchResults))){
                    intent.setClass(LoginActivity.this, SearchResultsActivity.class);
                }
                startActivity(intent);
                break;
        }

    }

}


