package jp.kinwork;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.ClassDdl.Qualification;
import jp.kinwork.Common.DatePickerDialog_2;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class QualificationActivity extends AppCompatActivity {

    final static String PARAM_add = "/ResumesMobile/addQualification";
    final static String PARAM_Upd = "/ResumesMobile/updateQualification";
    private int Start_mYear, Start_mMonth, Start_mDay = 0;

    private EditText etQualificationname;
    private TextView tvQualificationstart;
    private TextView tvQualificationname;
    private TextView tvdateQualification;

    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String resumeId;
    private String IresumeIdflg;
    private String status;
    private String QualificationId;
    private String resumestatus;
    private PreferenceUtils PreferenceUtils;
    private Intent intent;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualification);
        intent = getIntent();
        status = intent.getStringExtra(getString(R.string.status));
        myApplication = (MyApplication) getApplication();
        IresumeIdflg = myApplication.getResumeId();
        resumestatus = myApplication.getresume_status();
        Initialization();
        load();
        YearmonthselectionStart();
        getstatus(status);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    //初期化
    private void Initialization(){
        tvQualificationname = (TextView) findViewById(R.id.tv_Qualification_name);
        SetStyle(tvQualificationname,"（必須）","0");
        tvdateQualification = (TextView) findViewById(R.id.tv_date_Qualification);
        SetStyle(tvdateQualification,"（任意）","1");
        etQualificationname  = (EditText) findViewById(R.id.et_Qualification_name);//資格名称
        tvQualificationstart = (TextView) findViewById(R.id.tv_Qualification_start);//開始期間
        PreferenceUtils      = new PreferenceUtils(this);
    }


    //通信情報取得
    public void load(){
        deviceId = PreferenceUtils.getdeviceId();
        AesKey = PreferenceUtils.getAesKey();
        userId = PreferenceUtils.getuserId();
        token = PreferenceUtils.gettoken();
        if(IresumeIdflg.equals("1")){
            resumeId = PreferenceUtils.getresumeid_1();
        } else if (IresumeIdflg.equals("2")){
            resumeId = PreferenceUtils.getresumeid_2();
        } else if (IresumeIdflg.equals("3")){
            resumeId = PreferenceUtils.getresumeid_3();
        }
    }

    private void YearmonthselectionStart() {
        Calendar calendar = Calendar.getInstance();
        Start_mYear = calendar.get(Calendar.YEAR);
        Start_mMonth = calendar.get(Calendar.MONTH) +1;
        Start_mDay = calendar.get(Calendar.DAY_OF_MONTH);
        //监听事件
        tvQualificationstart.setOnClickListener(new View.OnClickListener() {

            Calendar SYScalendar = Calendar.getInstance();
            int sysYear = SYScalendar.get(Calendar.YEAR);
            int sysMonth = SYScalendar.get(Calendar.MONTH) + 1;
            int sysDay = SYScalendar.get(Calendar.DAY_OF_MONTH);


            @Override
            public void onClick(View v) {
                new DatePickerDialog_2(QualificationActivity.this, 0, new DatePickerDialog_2.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth) {
                        if(startYear == 0 && startMonthOfYear == 0 && startDayOfMonth == 0 ){
                            tvQualificationstart.setText("");
                        } else {
                            if (startYear > sysYear ||
                                    (startYear == sysYear && startMonthOfYear +1  > sysMonth) ||
                                    (startYear == sysYear && startMonthOfYear +1 == sysMonth && startDayOfMonth >= sysDay)
                                    ) {
                                tvQualificationstart.setText("");
                                alertdialog(getString(R.string.alertdialog2));
                            } else {
                                Start_mYear = startYear;
                                Start_mMonth = startMonthOfYear + 1;
                                Start_mDay = startDayOfMonth;
                                String Startdate = String.valueOf(startYear) + "-" + String.valueOf(startMonthOfYear + 1) + "-" + String.valueOf(startDayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.yyyyMMdd));
                                ParsePosition pos = new ParsePosition(0);
                                Date strtodate = formatter.parse(Startdate, pos);
                                Startdate = formatter.format(strtodate);
                                tvQualificationstart.setText(Startdate);
                            }
                        }
                    }
                }, Start_mYear, Start_mMonth - 1, Start_mDay).show();
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

    //按钮点击触发事件
    public void bt_Click(View View) {
        saveurl();
    }

    //关闭，履历书画面
    public void Click_cancel(View View){
        NewIntent();
    }
    //内容取得、通信
    public void saveurl() {
        if(etQualificationname.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog12));
        } else {
            //Json格式转换
            Gson Gson = new Gson();
            PostDate postdate = new PostDate();
            Qualification Qualification = new Qualification();
            if(status.equals(getString(R.string.upd))){
                Qualification.setId(QualificationId);
            }
            Qualification.setId_resume(resumeId);
            Qualification.setUser_id(userId);
            Qualification.setQualification_name(etQualificationname.getText().toString());
            Qualification.setFrom_date(tvQualificationstart.getText().toString());
            postdate.setUserId(userId);
            postdate.setToken(token);
            postdate.setQualification(Qualification);
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
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    Boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    if(processResult == true) {
                        NewIntent();
                    } else {
                        alertdialog(obj.getString(getString(R.string.message)));
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
            QualificationId = intent.getStringExtra(getString(R.string.qualificationId));
            String qualification_name = intent.getStringExtra(getString(R.string.Qualification_name));
            String From_date = intent.getStringExtra(getString(R.string.From_date));
            etQualificationname.setText(qualification_name);//資格名
            if(From_date.length() > 0){
                Start_mYear = Integer.parseInt(From_date.substring(0,4));
                Start_mMonth = Integer.parseInt(From_date.substring(5,7));
                Start_mDay = Integer.parseInt(From_date.substring(8,10));
                tvQualificationstart.setText(From_date);
            }
        }
    }

    //履歴書作成画面に
    public void NewIntent(){
        myApplication.setresume_status(resumestatus);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(QualificationActivity.this, ResumeActivity.class);
        startActivity(intent);
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
