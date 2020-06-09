package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.ClassDdl.SchoolCareer;
import jp.kinwork.Common.DatePickerDialog;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.NetworkUtils;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class EducationActivity extends AppCompatActivity {

    final static String PARAM_add = "/ResumesMobile/addSchoolCareer";
    final static String PARAM_Upd = "/ResumesMobile/updateSchoolCareer";
    private int Start_mYear, Start_mMonth = 0;
    private int End_mYear, End_mMonth = 0;

    private TextView tveducationstart;
    private TextView tveducationend;
    private TextView tvSchoolname;
    private TextView tvDegree;
    private TextView tvMajorfield;
    private TextView tvdateeducation;

    private TextView cancel;

    private EditText etDegree;
    private EditText etSchoolname;
    private EditText etMajorfield;
//    private EditText etSchoollocation;

    private Button save;

    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String resumeId;
    private String IresumeIdflg;
    private String status;
    private String schoolCareerId;
    private String resumestatus;
    private PreferenceUtils mPreferenceUtils;
    private Intent intent;
    private MyApplication mMyApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        intent = getIntent();
        status = intent.getStringExtra(getString(R.string.status));
        mMyApplication = (MyApplication) getApplication();
        IresumeIdflg = mMyApplication.getResumeId();
        resumestatus = mMyApplication.getresume_status();
        Initialization();
        load();
        YearmonthselectionStart();
        YearmonthselectionEnd();
        getstatus(status);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
    public void Initialization(){
        cancel=findViewById(R.id.bu_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_cancel();
            }
        });
        save=findViewById(R.id.bu_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveurl();
            }
        });

        tvSchoolname = (TextView) findViewById(R.id.tv_School_name);
        SetStyle(tvSchoolname,"（必須）","0");
        tvDegree = (TextView) findViewById(R.id.tv_Degree);
        SetStyle(tvDegree,"（任意）","1");
        tvMajorfield = (TextView) findViewById(R.id.tv_Major_field);
        SetStyle(tvMajorfield,"（任意）","1");
        tvdateeducation = (TextView) findViewById(R.id.tv_date_education);
        SetStyle(tvdateeducation,"（任意）","1");
        etDegree         = (EditText) findViewById(R.id.et_Degree);//学位
        etSchoolname     = (EditText) findViewById(R.id.et_School_name);//学校名
        etMajorfield     = (EditText) findViewById(R.id.et_Major_field);//専攻分野
//        etSchoollocation = (EditText) findViewById(R.id.et_School_location);//学校所在地
        tveducationstart = (TextView) findViewById(R.id.tv_education_start);
        tveducationend   = (TextView) findViewById(R.id.tv_education_end);
        mPreferenceUtils = new PreferenceUtils(this);

    }

    //履歴書作成画面に
    public void NewIntent(){
        mMyApplication.setresume_status(resumestatus);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(EducationActivity.this, ResumeActivity.class);
        startActivity(intent);
    }

    //通信情報取得
    public  void load(){
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userId = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        if(IresumeIdflg.equals("1")){
            resumeId = mPreferenceUtils.getresumeid_1();
        } else if (IresumeIdflg.equals("2")){
            resumeId = mPreferenceUtils.getresumeid_2();
        } else if (IresumeIdflg.equals("3")){
            resumeId = mPreferenceUtils.getresumeid_3();
        }
    }

    public void YearmonthselectionStart(){

        Calendar calendar = Calendar.getInstance();
        Start_mYear = calendar.get(Calendar.YEAR);
        Start_mMonth = calendar.get(Calendar.MONTH) + 1 ;

        tveducationstart.setOnClickListener(new View.OnClickListener() {
            Calendar SYScalendar = Calendar.getInstance();
            int sysYear = SYScalendar.get(Calendar.YEAR);
            int sysMonth = SYScalendar.get(Calendar.MONTH) + 1;

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EducationActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth,boolean hidetheDay) {
                        if(startYear != 0 && startMonthOfYear != 0){
                            String textString = String.valueOf(startYear) + getString(R.string.Year) + String.valueOf(startMonthOfYear + 1) + getString(R.string.Months);
                            if ((startYear > sysYear) || (startYear == sysYear && startMonthOfYear + 1 >= sysMonth)) {
                                tveducationstart.setText("");
                                alertdialog(getString(R.string.alertdialog2));
                            } else {
                                Start_mYear = startYear;
                                Start_mMonth = startMonthOfYear + 1;
                                tveducationstart.setText(textString);
                            }
                        } else {
                            tveducationstart.setText("");
                        }
                    }
                }, Start_mYear, Start_mMonth, 0,true).show();
            }
        });
    }

    public void YearmonthselectionEnd(){
        Calendar calendar = Calendar.getInstance();
        End_mYear = calendar.get(Calendar.YEAR);
        End_mMonth = calendar.get(Calendar.MONTH) + 1;

        tveducationend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EducationActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth,boolean hidetheDay) {
                        if(endYear != 0 && endMonthOfYear != 0){
                            String textString = String.valueOf(endYear) + getString(R.string.Year) + String.valueOf(endMonthOfYear + 1) + getString(R.string.Months);
                            if ((Start_mYear > endYear) || (Start_mYear == endYear && Start_mMonth >= endMonthOfYear + 1)) {
                                tveducationend.setText("");
                                alertdialog(getString(R.string.alertdialog3));
                            } else {
                                End_mYear = endYear;
                                End_mMonth = endMonthOfYear + 1;
                                tveducationend.setText(textString);
                            }
                        } else {
                            tveducationend.setText("");
                        }
                    }
                }, End_mYear, End_mMonth, 0,true).show();
            }
        });
    }

    //通信结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }

    //关闭，履历书画面
    public void Click_cancel(){
        NewIntent();
    }
    //内容取得、通信
    public void saveurl() {
        if(etSchoolname.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog4));
        } else {
            //Json格式转换
            Gson Gson = new Gson();
            PostDate postdate = new PostDate();
            SchoolCareer SchoolCareer = new SchoolCareer();
            if(status.equals(getString(R.string.upd))){
                SchoolCareer.setId(schoolCareerId);
            }
            SchoolCareer.setId_resume(resumeId);
            SchoolCareer.setUser_id(userId);
            SchoolCareer.setDegree(etDegree.getText().toString());
            SchoolCareer.setSchool_name(etSchoolname.getText().toString());
            SchoolCareer.setMajor_field(etMajorfield.getText().toString());
//            SchoolCareer.setCountry_of_school(etSchoollocation.getText().toString());
            SchoolCareer.setFrom_year(String.valueOf(Start_mYear));
            SchoolCareer.setFrom_month(String.valueOf(Start_mMonth));
            SchoolCareer.setTo_year(String.valueOf(End_mYear));
            SchoolCareer.setTo_month(String.valueOf(End_mMonth));
            postdate.setUserId(userId);
            postdate.setToken(token);
            postdate.setSchoolCareer(SchoolCareer);
            String sdPdata = Gson.toJson(postdate,PostDate.class);
            //AES加密
            String data = AesChnge(AesKey, sdPdata);
            Map<String,String> param = new HashMap<String, String>();
            if (status.equals(getString(R.string.add))){
                param.put(getString(R.string.file),PARAM_add);
            } else if (status.equals(getString(R.string.upd))){
                param.put(getString(R.string.file),PARAM_Upd);
            }
            param.put(getString(R.string.data),data);
            param.put(getString(R.string.deviceid),deviceId);
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
        }

    }

    //AES加密
    public static String AesChnge(String AesKey, String data) {
        AES mAes = new AES();
        byte[] mBytes = null;
        try {
            mBytes = data.getBytes("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String enString = mAes.encrypt(mBytes,AesKey);
        String encrypt = enString.replace("\n", "").replace("+","%2B");
        return encrypt;
    }

    //访问服务器，并取得访问结果
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            String deviceId = map.get(getString(R.string.deviceid));
            URL searchUrl = buildUrl(file);
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
                    Boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    if(processResult == true) {
                        NewIntent();
                    } else {
                        Log.d("githubSearchResults:", githubSearchResults);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    //创建、更新判定
    public void getstatus(String data){
        if(data.equals(getString(R.string.upd))){
            schoolCareerId = intent.getStringExtra(getString(R.string.schoolCareerId));
            etSchoolname.setText(intent.getStringExtra(getString(R.string.School_name)));//学校名
            //学位
            if(intent.getStringExtra(getString(R.string.Degree)).length() > 0){
                etDegree.setText(intent.getStringExtra(getString(R.string.Degree)));
            }
            //専攻分野
            if(intent.getStringExtra(getString(R.string.Major_field)).length() > 0){
                etMajorfield.setText(intent.getStringExtra(getString(R.string.Major_field)));
            }
            //学校所在地
//            if(intent.getStringExtra("Country_of_school").length() > 0){
//                etSchoollocation.setText(intent.getStringExtra("Country_of_school"));
//            }
            //开始年月
            if(intent.getStringExtra(getString(R.string.Start_Y)).length() > 0 && intent.getStringExtra(getString(R.string.Start_M)).length() > 0){
                Start_mYear = Integer.parseInt(intent.getStringExtra(getString(R.string.Start_Y)));
                Start_mMonth = Integer.parseInt(intent.getStringExtra(getString(R.string.Start_M)));
                tveducationstart.setText(intent.getStringExtra(getString(R.string.Start_Y)) + getString(R.string.Year) + intent.getStringExtra(getString(R.string.Start_M)) + getString(R.string.Months));
            }
            //结束年月
            if(intent.getStringExtra(getString(R.string.End_Y)).length() > 0 && intent.getStringExtra(getString(R.string.End_M)).length() > 0){
                End_mYear = Integer.parseInt(intent.getStringExtra(getString(R.string.End_Y)));
                End_mMonth = Integer.parseInt(intent.getStringExtra(getString(R.string.End_M)));
                tveducationend.setText(intent.getStringExtra(getString(R.string.End_Y) + getString(R.string.Year) + intent.getStringExtra(getString(R.string.End_M)) + getString(R.string.Months)));
            }
        }
    }

    public void SetStyle(TextView View,String A,String flg){
        String String =  View.getText().toString() + A;
        int length1 = String.indexOf(A);
        int length2 = length1 + A.length();

        SpannableStringBuilder style_name=new SpannableStringBuilder(String);
        if(flg.equals("0")){
            style_name.setSpan(new ForegroundColorSpan(Color.RED),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }else {
            style_name.setSpan(new ForegroundColorSpan(Color.BLACK),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        style_name.setSpan(new AbsoluteSizeSpan(35),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        View.setText(style_name);
    }
}
