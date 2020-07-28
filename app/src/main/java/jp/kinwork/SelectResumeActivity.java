package jp.kinwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.NetworkUtils;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

public class SelectResumeActivity extends AppCompatActivity {

    final static String PARAM = "/JobInfosMobile/jobApplySubmit";
    final static String PARAM_sendMessage = "/SessionMessageMobile/sendMessage";
    private jp.kinwork.Common.PreferenceUtils PreferenceUtils;
    private MyApplication myApplication;
    private EditText ettitle;
    private EditText etmessage;
    private TextView tvback;
    private TextView tvbackdummy;
    private TextView tvToCompanyName;

    private LinearLayout ll_sendresume;
    private TableLayout tltlresume;

    private TableRow trresume1;
    private TableRow trresume2;
    private TableRow trresume3;

    private CheckBox cbresume1;
    private CheckBox cbresume2;
    private CheckBox cbresume3;

    private TextView tvresume1;
    private TextView tvresume2;
    private TextView tvresume3;

    private String resumeId_1;
    private String resumeId_2;
    private String resumeId_3;
    private String cbresumeId = "";
    private String deviceId;
    private String AesKey;
    private String userid;
    private String token;
    private String JobId;
    private String Act;
    private String companyname;
    private String Jobname;
    private String resume_Num = "0";
    private String employerID = "";

    private String errormeg = "";
    private int resume_number;

    private String SetCompanyName="";
    private String SetTitle = "";
    private String SetMeg = "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_select_resume);
        Initialization();
        if(myApplication.getContactDialog(0).equals("1")){
            employerID=myApplication.getemployerID();
            Intent intent = getIntent();
            String companyname = intent.getStringExtra("companyname");
            String mailtitle = intent.getStringExtra("mailtitle");
            String mailmeg = intent.getStringExtra("mailmeg");
            tvToCompanyName.setText(companyname);
            ettitle.setText(mailtitle);
            etmessage.setText(mailmeg);
            tvback.setText("メール一覧");
            tvback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoveIntent("mymail");
                }
            });
            tvbackdummy.setText("送信");
            tvbackdummy.setOnClickListener(Click_setSendMeg);
            tltlresume.setVisibility(View.GONE);
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


    //初期化
    private void Initialization(){
        tvback          = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.detailedinformation));
        tvbackdummy.setText("応募");
        tvbackdummy.setTextColor(Color.parseColor("#0196FF"));
        tvbackdummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Application();
            }
        });
        ll_sendresume = (LinearLayout) findViewById(R.id.tl_sendresume);
        tvToCompanyName=(TextView)findViewById(R.id.tv_ToCompanyName);
        tltlresume=(TableLayout)findViewById(R.id.tl_tl_resume);
        ettitle   = (EditText) findViewById(R.id.et_title);
        etmessage = (EditText) findViewById(R.id.et_message);
        trresume1 = (TableRow) findViewById(R.id.tr_resume1);
        trresume2 = (TableRow) findViewById(R.id.tr_resume2);
        trresume3 = (TableRow) findViewById(R.id.tr_resume3);
        cbresume1 = (CheckBox) findViewById(R.id.cb_resume1);
        cbresume2 = (CheckBox) findViewById(R.id.cb_resume2);
        cbresume3 = (CheckBox) findViewById(R.id.cb_resume3);
        tvresume1 = (TextView) findViewById(R.id.tv_resume1);
        tvresume2 = (TextView) findViewById(R.id.tv_resume2);
        tvresume3 = (TextView) findViewById(R.id.tv_resume3);

        cbresume1.setOnClickListener(Click_CheckBox);
        cbresume2.setOnClickListener(Click_CheckBox);
        cbresume3.setOnClickListener(Click_CheckBox);
        tvresume1.setOnClickListener(Click_TextView);
        tvresume2.setOnClickListener(Click_TextView);
        tvresume3.setOnClickListener(Click_TextView);

        myApplication = (MyApplication) getApplication();
        String flg = myApplication.getMyjob();
        companyname = myApplication.getcompany_name();
        Jobname = myApplication.getJobname();
        employerID = myApplication.getemployerID();

        JobId = myApplication.getJobId();
        Log.d("**JobId**", JobId);
        Act = myApplication.getAct();
        PreferenceUtils = new PreferenceUtils(SelectResumeActivity.this);
        resume_number = PreferenceUtils.getresume_number();
        resumeId_1 = PreferenceUtils.getresumeid_1();
        resumeId_2 = PreferenceUtils.getresumeid_2();
        resumeId_3 = PreferenceUtils.getresumeid_3();
        deviceId = PreferenceUtils.getdeviceId();
        AesKey = PreferenceUtils.getAesKey();
        userid = PreferenceUtils.getuserId();
        token = PreferenceUtils.gettoken();
        if(resume_number >= 1 && ! resumeId_1.equals("A")){
            trresume1.setVisibility(View.VISIBLE);
            tvresume1.setText(myApplication.getresume_name("1"));
        }
        if(resume_number >= 2 && ! resumeId_2.equals("A")){
            trresume2.setVisibility(View.VISIBLE);
            tvresume2.setText(myApplication.getresume_name("2"));
        }
        if(resume_number == 3 && ! resumeId_3.equals("A")){
            trresume3.setVisibility(View.VISIBLE);
            tvresume3.setText(myApplication.getresume_name("3"));
        }
        if(resume_number == 0){
            Erroralertdialog(getString(R.string.Erroralertdialog));
        }
        //会社名
        SetCompanyName="To:"+companyname;
        //件名设定
        SetTitle = Jobname + getString(R.string.SetTitle) + myApplication.getlast_name() + " " + myApplication.getfirst_name() + "（" + myApplication.getlast_name_kana() + " " + myApplication.getfirst_name_kana() + "）";
        //送信内容设定
        SetMeg = companyname + getString(R.string.SetMeg1) +
                "\n" +
                getString(R.string.SetMeg2) +
                myApplication.getlast_name() + " " + myApplication.getfirst_name() + "（" + myApplication.getlast_name_kana() + " " + myApplication.getfirst_name_kana() + getString(R.string.SetMeg3) +
                "\n" +
                getString(R.string.SetMeg4) +
                getString(R.string.SetMeg5)+
                getString(R.string.SetMeg6) +
                getString(R.string.SetMeg7) +
                "\n" +
                getString(R.string.SetMeg8);
        tvToCompanyName.setText(SetCompanyName);
        ettitle.setText(SetTitle);
        etmessage.setText(SetMeg);
    }

    //履歴書選択按钮
    private View.OnClickListener Click_CheckBox =new View.OnClickListener() {
        public void onClick(View View) {
            switch (View.getId()) {
                case R.id.cb_resume1:
                    cbresumeId = resumeId_1;
                    cbresume1.setChecked(true);
                    cbresume2.setChecked(false);
                    cbresume3.setChecked(false);
                    break;
                case R.id.cb_resume2:
                    cbresumeId = resumeId_2;
                    cbresume1.setChecked(false);
                    cbresume2.setChecked(true);
                    cbresume3.setChecked(false);
                    break;
                case R.id.cb_resume3:
                    cbresumeId = resumeId_3;
                    cbresume1.setChecked(false);
                    cbresume2.setChecked(false);
                    cbresume3.setChecked(true);
                    break;
            }
        }
    };

    //履歴書選択按钮
    private View.OnClickListener Click_TextView =new View.OnClickListener() {
        public void onClick(View View) {
            String ResumeIdNum = "";
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            switch (View.getId()) {
                case R.id.tv_resume1:
                    ResumeIdNum = "1";
                    break;
                case R.id.tv_resume2:
                    ResumeIdNum = "2";
                    break;
                case R.id.tv_resume3:
                    ResumeIdNum = "3";
                    break;
                default:break;
            }
            myApplication.setResumeId(ResumeIdNum);
            intent.setClass(SelectResumeActivity.this, ResumeActivity.class);
            startActivity(intent);
        }
    };

    //返回检索画面
    public void Click_back(){
        MoveIntent(getString(R.string.back));

    }

    //应聘按钮
    public void Click_Application(){
        if(cbresumeId.equals("")){
            alertdialog(getString(R.string.alertdialog16),"");
        } else {
            setApplication();
        }
    }


    //执行应聘
    public void setApplication(){
        if(ettitle.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog17),"");
        } else if(etmessage.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog18),"");
        } else {
            PostDate Pdata = new PostDate();
            Map<String,String> param = new HashMap<String, String>();
            Pdata.setUserId(userid);
            Pdata.setToken(token);
            Pdata.setjobId(JobId);
            Pdata.setResumeId(cbresumeId);
            Pdata.setmailTitle(ettitle.getText().toString());
            Pdata.setmailContent(etmessage.getText().toString());
            String data = JsonChnge(AesKey,Pdata);
            param.put(getString(R.string.file),PARAM);
            param.put(getString(R.string.data),data);
            param.put(getString(R.string.name),getString(R.string.Application));
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
        }
    }
    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d("sdPdata", sdPdata);
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
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        String name = "";
        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            name = map.get(getString(R.string.name));
            Log.d("***file***", file);
            Log.d("***data***", data);
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
                    String message = obj.getString(getString(R.string.message));
                    String errorCode = obj.getString(getString(R.string.errorCode));
                    if(processResult == true) {
                        MoveIntent(getString(R.string.Application));
                    } else {
                        if(errorCode.equals("100")){
                            message = "他の端末から既にログインしています。もう一度ログインしてください。";
                        }
                        alertdialog(message,errorCode);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //通信结果提示
    private void alertdialog(String meg,String errorCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                Log.d("**errormeg**", errormeg);
                Log.d("**JobId**", JobId);
                if(meg.equals(getString(R.string.errormeg))){
                    MoveIntent(getString(R.string.Application));
                }
                if(errorCode.equals("100")){
                    PreferenceUtils.clear();
                    myApplication.clear();
                    Intent intentClose = new Intent();
                    intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    myApplication.setAct(getString(R.string.Search));
                    intentClose.setClass(SelectResumeActivity.this, SearchActivity.class);
                    intentClose.putExtra("act", "");
                    startActivity(intentClose);
                }
            }
        }).show();
    }

    //通信结果提示
    private void Erroralertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.sakusei), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                MoveIntent(getString(R.string.resume));
            }
        }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消按钮的点击事件
                MoveIntent(getString(R.string.back));
            }
        }).show();
    }

    //画面移动
    public void MoveIntent(String name){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (name){
            case "back":
                intent.setClass(SelectResumeActivity.this, ApplyActivity.class);
                break;
            case "resume":
                myApplication.setResumeId("1");
                myApplication.setresume_status(getString(R.string.add));
                intent.setClass(SelectResumeActivity.this, ResumeActivity.class);
                break;
            case "Search":
                intent.setClass(SelectResumeActivity.this, SearchResultsActivity.class);
                break;
            case "Application":
                myApplication.setAct(getString(R.string.SelectResume));
                intent.setClass(SelectResumeActivity.this, MylistActivity.class);
                break;
            case "mymail":
                intent.setClass(SelectResumeActivity.this, ContactDialogActivity.class);
                break;
        }
        startActivity(intent);
    }

    //送信处理
    private View.OnClickListener Click_setSendMeg = new View.OnClickListener() {
        public void onClick(View View) {
            PostDate Pdata = new PostDate();
            Map<String, String> param = new HashMap<String, String>();
            Pdata.setUserId(userid);
            Pdata.setToken(token);
            Pdata.setjobId(JobId);
            Pdata.setemployerId(employerID);
            Pdata.setmailTitle(ettitle.getText().toString());
            Pdata.setmailContent(etmessage.getText().toString());
            String data = JsonChnge(AesKey, Pdata);
            param.put(getString(R.string.file), PARAM_sendMessage);
            param.put(getString(R.string.data), data);
            param.put(getString(R.string.name), getString(R.string.SendMeg));
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
            sendMegalertdialog("送信しました。","");
        }
    };

    //送信通信提示
    private void sendMegalertdialog(String meg,String errorCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                if(errorCode.equals("100")){
                    PreferenceUtils.clear();
                    myApplication.clear();
                    Intent intentClose = new Intent();
                    intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    myApplication.setAct(getString(R.string.Search));
                    intentClose.setClass(SelectResumeActivity.this, SearchActivity.class);
                    intentClose.putExtra("act", "");
                    startActivity(intentClose);
                } else {
                    finish();
                }
            }
        }).show();
    }
}
