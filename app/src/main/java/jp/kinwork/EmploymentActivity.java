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
import android.widget.CheckBox;
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
import jp.kinwork.Common.ClassDdl.ProfessionalCareer;
import jp.kinwork.Common.DatePickerDialog;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class EmploymentActivity extends AppCompatActivity {

    final static String PARAM_add = "/ResumesMobile/addProfessionalCareer";
    final static String PARAM_Upd = "/ResumesMobile/updateProfessionalCareer";
    private int Start_mYear, Start_mMonth = 0;
    private int End_mYear, End_mMonth = 0;
    private int ChecksysYear, ChecksysMonth = 0;

    private TextView tvEmploymentStartYM;
    private TextView tvEmploymentEndYM;
    private TextView tv_job_name;
    private TextView tv_Company_name;
    private TextView tv_date_Employment;
    private TextView cancel;

    private Button save;

    private EditText etJobname;
    private EditText etCompanyname;
//    private EditText etCompanyaddress;

    private CheckBox cbcurrent;

    private MyApplication mMyApplication;

    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String resumeId;
    private String IresumeIdflg;
    private String status;
    private String professionalCareerId;
    private String resumestatus;
    private PreferenceUtils PreferenceUtils;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employment);
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
        tv_job_name  = (TextView) findViewById(R.id.tv_job_name);
        tv_Company_name  = (TextView) findViewById(R.id.tv_Company_name);
        tv_date_Employment  = (TextView) findViewById(R.id.tv_date_Employment);
        SetStyle(tv_job_name,"（必須）","0");
        SetStyle(tv_Company_name,"（必須）","0");
        SetStyle(tv_date_Employment,"（任意）","1");
        etJobname            = (EditText) findViewById(R.id.et_Job_name);//職種名
        etCompanyname        = (EditText) findViewById(R.id.et_Company_name);//会社名
//        etCompanyaddress     = (EditText) findViewById(R.id.et_Company_address);//会社所在地
        tvEmploymentStartYM  = (TextView) findViewById(R.id.tv_Employment_StartYM);
        tvEmploymentEndYM    = (TextView) findViewById(R.id.tv_Employment_EndYM);
        cbcurrent            = (CheckBox) findViewById(R.id.cb_current);
        PreferenceUtils      = new PreferenceUtils(this);
        ChecksysYear         = Calendar.getInstance().get(Calendar.YEAR);
        ChecksysMonth        = Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public void YearmonthselectionStart(){

        Calendar calendar = Calendar.getInstance();
        Start_mYear = calendar.get(Calendar.YEAR);
        Start_mMonth = calendar.get(Calendar.MONTH) + 1 ;

        tvEmploymentStartYM.setOnClickListener(new View.OnClickListener() {
            Calendar SYScalendar = Calendar.getInstance();
            int sysYear = SYScalendar.get(Calendar.YEAR);
            int sysMonth = SYScalendar.get(Calendar.MONTH) + 1;


            @Override
            public void onClick(View v) {
                new DatePickerDialog(EmploymentActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth,boolean hidetheDay) {
                        if(startYear != 0 && startMonthOfYear != 0){
                            String textString = String.valueOf(startYear) + getString(R.string.Year) + String.valueOf(startMonthOfYear + 1) + getString(R.string.Months);
                            if ((startYear > sysYear) || (startYear == sysYear && startMonthOfYear + 1 >= sysMonth)) {
                                tvEmploymentStartYM.setText("");
                                alertdialog(getString(R.string.alertdialog2));
                            } else {
                                Start_mYear = startYear;
                                Start_mMonth = startMonthOfYear + 1;
                                tvEmploymentStartYM.setText(textString);
                            }
                        } else {
                            tvEmploymentStartYM.setText("");
                        }
                    }
                }, Start_mYear, Start_mMonth, 0,false).show();
            }
        });
    }

    public void YearmonthselectionEnd(){
        Calendar calendar = Calendar.getInstance();
        End_mYear = calendar.get(Calendar.YEAR);
        End_mMonth = calendar.get(Calendar.MONTH) + 1;

        tvEmploymentEndYM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EmploymentActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth,boolean hidetheDay) {
                        if(endYear != 0 && endMonthOfYear != 0){
                            String textString = String.valueOf(endYear) + getString(R.string.Year) + String.valueOf(endMonthOfYear + 1) + getString(R.string.Months);
                            if ((Start_mYear > endYear) || (Start_mYear == endYear && Start_mMonth >= endMonthOfYear + 1)) {
                                tvEmploymentEndYM.setText("");
                                alertdialog(getString(R.string.alertdialog3));
                            } else {
                                End_mYear = endYear;
                                End_mMonth = endMonthOfYear + 1;
                                tvEmploymentEndYM.setText(textString);
                            }
                        } else {
                            tvEmploymentStartYM.setText("");
                        }
                    }
                }, End_mYear, End_mMonth, 0,false).show();
            }
        });
    }

    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }



    public void Click_cancel(){
        NewIntent();
    }

    //通信情報取得
    public  void load(){
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
    //履歴書作成画面に
    public void NewIntent(){
        mMyApplication.setresume_status(resumestatus);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(EmploymentActivity.this, ResumeActivity.class);
        startActivity(intent);
    }

    public void saveurl() {
        if(etJobname.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog5));
        } else if(etCompanyname.getText().toString().equals("")){
            alertdialog(getString(R.string.alertdialog6));
        } else if(! tvEmploymentEndYM.getText().toString().equals("") &&
                (ChecksysYear < End_mYear || (ChecksysYear == End_mYear && ChecksysMonth <= End_mMonth)) &&
                cbcurrent.isChecked() == false){
            alertdialog(getString(R.string.alertdialog7));
        } else {
            Gson Gson = new Gson();
            PostDate postdate = new PostDate();
            ProfessionalCareer professionalcareer = new ProfessionalCareer();
            if(status.equals(getString(R.string.upd))){
                professionalcareer.setId(professionalCareerId);
            }
            professionalcareer.setId_resume(resumeId);
            professionalcareer.setUser_id(userId);
            professionalcareer.setJob_name(etJobname.getText().toString());
            professionalcareer.setCompany_name(etCompanyname.getText().toString());
//            professionalcareer.setCountry_of_professional(etCompanyaddress.getText().toString());
            professionalcareer.setFrom_year(String.valueOf(Start_mYear));
            professionalcareer.setFrom_month(String.valueOf(Start_mMonth));
            professionalcareer.setTo_year(String.valueOf(End_mYear));
            professionalcareer.setTo_month(String.valueOf(End_mMonth));
            if(cbcurrent.isChecked() == true){
                professionalcareer.setIs_working_till_now("1");
            }
            postdate.setUserId(userId);
            postdate.setToken(token);
            postdate.setProfessionalCareer(professionalcareer);
            String sdPdata = Gson.toJson(postdate,PostDate.class);
            Log.d("**userId**:", userId);
            Log.d("**token**:", token);
            Log.d("**sdPdata**:", sdPdata);
            String data = AesChnge(AesKey, sdPdata);
            Map<String,String> param = new HashMap<String, String>();
            if (status.equals(getString(R.string.add))){
                param.put(getString(R.string.file),PARAM_add);
            } else if (status.equals(getString(R.string.upd))){
                param.put(getString(R.string.file),PARAM_Upd);
            }
            param.put(getString(R.string.data),data);
            param.put(getString(R.string.deviceid),deviceId);
            new GithubQueryTask().execute(param);
        }

    }

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

    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            String deviceId = map.get(getString(R.string.deviceid));
            Log.d("**file**:", file);
            Log.d("**data**:", data);
            Log.d("**deviceId**:", deviceId);
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
                        Log.d("githubSearchResults:", githubSearchResults);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    public void getstatus(String data){
        if(data.equals(getString(R.string.upd))){
            professionalCareerId = intent.getStringExtra(getString(R.string.professionalCareerId));
            etJobname.setText(intent.getStringExtra(getString(R.string.jobName)));
            etCompanyname.setText(intent.getStringExtra(getString(R.string.Companyname)));
//            etCompanyaddress.setText(intent.getStringExtra("Companyaddress"));
            if(intent.getStringExtra(getString(R.string.Start_Y)).length() > 0 && intent.getStringExtra(getString(R.string.Start_M)).length() > 0){
                Start_mYear = Integer.parseInt(intent.getStringExtra(getString(R.string.Start_Y)));
                Start_mMonth = Integer.parseInt(intent.getStringExtra(getString(R.string.Start_M)));
                tvEmploymentStartYM.setText(intent.getStringExtra(getString(R.string.Start_Y)) + getString(R.string.Year) + intent.getStringExtra(getString(R.string.Start_M)) + getString(R.string.Months));
            }
            if(intent.getStringExtra(getString(R.string.End_Y)).length() > 0 && intent.getStringExtra(getString(R.string.End_M)).length() > 0){
                End_mYear = Integer.parseInt(intent.getStringExtra(getString(R.string.End_Y)));
                End_mMonth = Integer.parseInt(intent.getStringExtra(getString(R.string.End_M)));
                tvEmploymentEndYM.setText(intent.getStringExtra(getString(R.string.End_Y)) + getString(R.string.Year) + intent.getStringExtra(getString(R.string.End_M)) + getString(R.string.Months));
            }
            if(intent.getStringExtra(getString(R.string.CheckBox)).equals("1") && cbcurrent.isChecked() == false){
                Log.d("CheckBox", intent.getStringExtra("CheckBox"));
                cbcurrent.setChecked(true);
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
