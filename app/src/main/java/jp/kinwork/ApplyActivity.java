package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PreferenceUtils;

public class ApplyActivity extends AppCompatActivity {
    final static String PARAM_sendMessage = "/SessionMessageMobile/sendMessage";

    private TableLayout tlapplyemploymentstatus;
    private TableLayout tlapplySalary;
    private TextView tvapplyJobcategory;
    private TextView tvapplycompanyname;
    private TextView tvapplymail;
    private TextView tvapplyphone;
    private TextView tvapplyNeareststation;
    private TextView tvapplylocation;
    private TextView tvapplyJobCon_AplPoi;
    private TextView tvapplyTalentedpeople;
    private TextView tvapplyWorkinghours;
    private TextView tvapplyaccess;
    private TextView tvapplytreatment;
    private TextView tvapplyOther;
    //    private TextView tvAToCompanyName;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;

    //    private EditText etAtitle;
//    private EditText etAmeg;
//
//    private String deviceId;
//    private String AesKey;
//    private String userid;
//    private String token;
    private String JobInfo;
    private String JobId;
    private String UserFlg;
    private String Act;
    private String jobflg;
    private String employerID;
    private String companyname;
    private String address;
    private String backflg = "";

    private Button bucreatetop;
    private Button bucreateBottom;
    private PreferenceUtils mPreferenceUtils;
    private MyApplication mMyApplication;

    private String[] employmentstatus = new String[]{"正社員","契約社員","アルバイト･パート","派遣社員","業務委託","嘱託社員","ボランティア","請負","インターン"};
    private String[] salary_type = new String[]{" ","月給","年給","周給","日給","時給"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Initialization();
    }

    //初期化
    private void Initialization(){
        tvback                    = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle               = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);
        tlapplyemploymentstatus   = (TableLayout) findViewById(R.id.tl_apply_employmentstatus);
        tlapplySalary             = (TableLayout) findViewById(R.id.tl_apply_Salary);
        tvapplyJobcategory        = (TextView) findViewById(R.id.tv_apply_Jobcategory     );
        tvapplycompanyname        = (TextView) findViewById(R.id.tv_apply_companyname     );
        tvapplymail               = (TextView) findViewById(R.id.tv_apply_mail            );
        tvapplyphone              = (TextView) findViewById(R.id.tv_apply_phone           );
        tvapplyNeareststation     = (TextView) findViewById(R.id.tv_apply_Neareststation  );
        tvapplylocation           = (TextView) findViewById(R.id.tv_apply_location        );
        tvapplyJobCon_AplPoi      = (TextView) findViewById(R.id.tv_apply_JobCon_AplPoi   );
        tvapplyTalentedpeople     = (TextView) findViewById(R.id.tv_apply_Talentedpeople  );
        tvapplyWorkinghours       = (TextView) findViewById(R.id.tv_apply_Workinghours    );
        tvapplyaccess             = (TextView) findViewById(R.id.tv_apply_access          );
        tvapplytreatment          = (TextView) findViewById(R.id.tv_apply_treatment       );
        tvapplyOther              = (TextView) findViewById(R.id.tv_apply_Other           );
        bucreatetop                  = (Button) findViewById(R.id.bu_create_top);
        bucreatetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_selectresume();
            }
        });
        bucreateBottom                  = (Button) findViewById(R.id.bu_create_Bottom);
        bucreateBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_selectresume();
            }
        });
        mPreferenceUtils = new PreferenceUtils(ApplyActivity.this);
        UserFlg                   = mPreferenceUtils.getUserFlg();
        mMyApplication = (MyApplication) getApplication();
        Act = mMyApplication.getAct();
        JobInfo = mMyApplication.getjobinfo();
        jobflg = mMyApplication.getMyjob();
        if(Act.equals(getString(R.string.Search))){
            tvback.setText(getString(R.string.SearchResults));
            tvbackdummy.setText(getString(R.string.SearchResults));
            if(mMyApplication.getSApply(0).equals("1")){
                JobInfo = mMyApplication.getSApply(1);
                Act = mMyApplication.getSApply(2);
                jobflg = mMyApplication.getSApply(3);
            }

        } else {
            tvback.setText(getString(R.string.mylist));
            tvbackdummy.setText(getString(R.string.mylist));
            if(mMyApplication.getMApply(0).equals("1")){
                JobInfo = mMyApplication.getMApply(1);
                Act = mMyApplication.getMApply(2);
                jobflg = mMyApplication.getMApply(3);
            }
        }
        if(jobflg.equals(getString(R.string.Enteredjob))){
            tvbacktitle.setText(getString(R.string.jobinformation));
        } else {
            tvbacktitle.setText(getString(R.string.detailedinformation));
        }
        bucreatetop.setText(getString(R.string.sendanemailtocompany));
        bucreateBottom.setText(getString(R.string.sendanemailtocompany));
        Log.d("jobflg", jobflg);
        Log.d("JobInfo", JobInfo);
        setjobinfo(JobInfo);
    }

    public void Click_selectresume(){
        mMyApplication.setjobinfo(JobInfo);
        mMyApplication.setJobId(JobId);
        mMyApplication.setcompany_name(tvapplycompanyname.getText().toString());
        mMyApplication.setJobname(tvapplyJobcategory.getText().toString());
        mMyApplication.setemployerID(employerID);
        if(UserFlg.equals("1")){
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(ApplyActivity.this, SelectResumeActivity.class);
            startActivity(intent);

        } else {
            TextView msg = new TextView(this);
            msg.setText(getString(R.string.checkloginstatus));
            msg.setGravity(Gravity.CENTER);
            msg.setTextSize(15);
            msg.setTextColor(Color.parseColor("#000000"));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("").setView(msg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //确定按钮的点击事件
                    mPreferenceUtils.setsaveid(getString(R.string.Apply));
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(ApplyActivity.this, LoginActivity.class);
                    intent.putExtra(getString(R.string.Activity),getString(R.string.Apply));
                    startActivity(intent);

                }
            }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //取消按钮的点击事件
                }
            }).show();
        }
    }

    //返回检索画面
    public void Click_back(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(Act.equals(getString(R.string.Search))){
            mMyApplication.setSApply("0",0);
            intent.setClass(ApplyActivity.this, SearchResultsActivity.class);
        } else {
            mMyApplication.setMApply("0",0);
            intent.setClass(ApplyActivity.this, MylistActivity.class);
        }
        startActivity(intent);
    }
    //工作情报内容
    public void setjobinfo(String data){
        if (data != null && !data.equals("")) {
            try {
                Log.d("---jobflg---", jobflg);
                JSONObject obj = new JSONObject(data);
                Log.d("---obj---", obj.toString());
                employerID = obj.getString("user_id");
                companyname = obj.getString("company_name");
                address = obj.getString("add_1") + obj.getString("add_2") + obj.getString("add_3") + obj.getString("add_4");
                JobId = obj.getString("id");
                tvapplyJobcategory.setText(obj.getString("occupation_name"));
                tvapplycompanyname.setText(obj.getString("company_name"));
                tvapplymail.setText(obj.getString("used_email"));
                tvapplyphone.setText(obj.getString("company_tel"));
                tvapplyNeareststation.setText(obj.getString("nearest_station"));
                tvapplylocation.setText("〒" + obj.getString("post_code") + " " + address);
//                tvAToCompanyName.setText(obj.getString("company_name"));
                int i = Integer.parseInt(obj.getString("salary_type"));
                TextView salarytype = new TextView(ApplyActivity.this);
                salarytype.setText("[" + salary_type[i] + "]");
                salarytype.setTextSize(15);
                salarytype.setTextColor(Color.parseColor("#000000"));
                TextView salary_from_to = new TextView(ApplyActivity.this);
                salary_from_to.setText(obj.getString("salary_from") + " ~ " + obj.getString("salary_to"));
                salary_from_to.setTextSize(15);
                salary_from_to.setTextColor(Color.parseColor("#000000"));
                tlapplySalary.addView(salarytype);
                tlapplySalary.addView(salary_from_to);
                int intdata = Integer.parseInt(obj.getString("employment_status"),10);
                String Stringdata = Integer.toBinaryString(intdata);
                setstatus(Stringdata);
                tvapplyJobCon_AplPoi.setText(obj.getString("job_describe1"));
                tvapplyTalentedpeople.setText(obj.getString("job_describe2"));
                tvapplyWorkinghours.setText(obj.getString("job_describe3"));
                tvapplyaccess.setText(obj.getString("job_describe4"));
                tvapplytreatment.setText(obj.getString("job_describe5"));
                tvapplyOther.setText(obj.getString("job_describe6"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //雇佣形态設定
    public void setstatus(String data){
        Log.i("转换内容的data", data);
        int iIndex = -1;
        for(int i = data.length(); i > 0; i--){
            Log.i("转换内容的", "第" + i + "位:" + data.charAt(i-1));
            String num= String.valueOf(data.charAt(i-1));
            Log.i("转换内容的", "num:" + num);
            iIndex = iIndex + 1;
            if(num.equals("1") && iIndex < 9){
                TextView tvstatus = new TextView(ApplyActivity.this);
                tvstatus.setText("・" + employmentstatus[iIndex]);
                tvstatus.setTextSize(15);
                tvstatus.setTextColor(Color.parseColor("#000000"));
                tlapplyemploymentstatus.addView(tvstatus);
            }
        }
    }
}
