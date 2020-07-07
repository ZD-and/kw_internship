package jp.kinwork;

import android.content.Intent;
import android.graphics.Color;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
import com.twitter.sdk.android.core.TwitterCore;

import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PreferenceUtils;

public class PersonalSetActivity extends AppCompatActivity {


    private TextView tvtitle;
    private TextView tvResumeSet1;
    private TextView tvResumeSet2;
    private TextView tvResumeSet3;

    private TableLayout tlResumeSet;

    private TableRow tr_basicinfoedit;
    private TableRow tr_changpw;
    private TableRow tr_LoginOut;
    private TableRow tr_Resume;

    private ImageView ivpersonalsettings;
    private TextView tvpersonalsettings;

//    private String deviceId;
    private TextView tvname;
    private TextView tvemail;

    private MyApplication myApplication;
    private PreferenceUtils PreferenceUtils;
    private LineApiClient mLineApiClient;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalset);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Initialization();
        load();
    }

    //初始化
    public void Initialization(){
        tvname = (TextView) findViewById(R.id.tv_userinfo_name);
        tvemail = (TextView) findViewById(R.id.tv_userinfo_email);
        tr_basicinfoedit=findViewById(R.id.tr_basicinfoedit);
        tr_changpw=findViewById(R.id.tr_changpw);
        tr_LoginOut=findViewById(R.id.tr_LoginOut);
        tr_basicinfoedit.setOnClickListener(Listener);
        tr_changpw.setOnClickListener(Listener);
        tr_LoginOut.setOnClickListener(Listener);
        tr_Resume=findViewById(R.id.tr_Resume);
        tr_Resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Resume();
            }
        });
        tlResumeSet = (TableLayout) findViewById(R.id.tl_ResumeSet);
        tvResumeSet1 = (TextView) findViewById(R.id.tv_ResumeSet_1);
        tvResumeSet2 = (TextView) findViewById(R.id.tv_ResumeSet_2);
        tvResumeSet3 = (TextView) findViewById(R.id.tv_ResumeSet_3);
        tvResumeSet1.setOnClickListener(resumeListener);
        tvResumeSet2.setOnClickListener(resumeListener);
        tvResumeSet3.setOnClickListener(resumeListener);
        tvtitle      = (TextView) findViewById(R.id.tv_title_b_name);
        tvtitle.setText(getString(R.string.personalsettings));
        ivpersonalsettings = (ImageView) findViewById(R.id.iv_b_personalsettings);
        tvpersonalsettings = (TextView) findViewById(R.id.tv_b_personalsettings);
        ivpersonalsettings.setImageResource(R.mipmap.blue_personalsettings);
        tvpersonalsettings.setTextColor(Color.parseColor("#5EACE2"));
        findViewById(R.id.tr_licenses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PersonalSetActivity.this, OssLicensesMenuActivity.class);
                startActivity(intent);
            }
        });
        myApplication = (MyApplication) getApplication();
        PreferenceUtils = new PreferenceUtils(PersonalSetActivity.this);
        mLineApiClient = new LineApiClientBuilder(getApplicationContext(), getString(R.string.line_client_id)).build();
        mAuth = FirebaseAuth.getInstance();
    }
    //菜单栏按钮
    public void ll_Click(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_b_search:
                myApplication.setAct(getString(R.string.Search));
                if(myApplication.getSURL(0).equals("0")){
                    if(myApplication.getSApply(0).equals("0")){
                        if(myApplication.getSearchResults(0).equals("0")){
                            intent.setClass(PersonalSetActivity.this, SearchActivity.class);
                            intent.putExtra(getString(R.string.act),"");
                        } else {
                            intent.setClass(PersonalSetActivity.this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(PersonalSetActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(PersonalSetActivity.this, WebActivity.class);
                    Initialization();
                }
                break;
            //Myリスト画面に移動
            case R.id.ll_b_contact:
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(PersonalSetActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(PersonalSetActivity.this, ContactDialogActivity.class);
                }
                break;
            case R.id.ll_b_mylist:
                myApplication.setAct(getString(R.string.Apply));
                if(myApplication.getMURL(0).equals("0")){
                    if(myApplication.getMApply(0).equals("0")){
                        intent.setClass(PersonalSetActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(PersonalSetActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(PersonalSetActivity.this, WebActivity.class);
                }
                break;
            //跳转个人设定画面
            case R.id.ll_b_personalsettings:
                intent.setClass(PersonalSetActivity.this, PersonalSetActivity.class);
                break;
        }
        startActivity(intent);
    }
    //子功能画面按钮
    private View.OnClickListener Listener =new View.OnClickListener() {
        public void onClick(View View) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            switch (View.getId()) {
                //跳转基本情报设定画面
                case R.id.tr_basicinfoedit:
                    intent.setClass(PersonalSetActivity.this, BasicinfoeditActivity.class);
                    intent.putExtra("Act", "person");
                    intent.putExtra("resume_status", "");
                    intent.putExtra("resume_Num", "");
                    break;
                //跳转密码变更画面
                case R.id.tr_changpw:
                    intent.setClass(PersonalSetActivity.this, ChangepwActivity.class);
                    break;
                //退出登录
                case R.id.tr_LoginOut:
                    switch (PreferenceUtils.getLoginFlag()){
                        case "1":
                            mAuth.signOut();//googleのログアウト
                            break;
                        case "2":
                            LoginManager.getInstance().logOut();//facebookのログアウト
                            break;
                        case "4":
                            TwitterCore.getInstance().getSessionManager().clearActiveSession();//twitterのログアウト
                            break;
                        case "5":
                            new Thread(new Runnable() {
                                public void run() {
                                    if(mLineApiClient.getProfile().isSuccess()){
                                        mLineApiClient.logout();
                                    }
                                }
                            }).start();
                            break;
                    }
                    PreferenceUtils.clear();
                    intent.setClass(PersonalSetActivity.this, SearchActivity.class);
                    intent.putExtra("act", "");
                    break;
            }
            startActivity(intent);
        }
    };
    //履歴書画面按钮
    private View.OnClickListener resumeListener =new View.OnClickListener() {
        public void onClick(View View) {
            String ResumeIdNum = "";
            String ResumeStatus = "";
            PreferenceUtils.setsaveid(getString(R.string.PreferenceUtils));
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(PersonalSetActivity.this, ResumeActivity.class);
            switch (View.getId()) {
                //検索画面に
                case R.id.tv_ResumeSet_1:
                    ResumeIdNum = "1";
                    if(tvResumeSet1.getText().toString().equals(getString(R.string.tvResumeSet))){
                        ResumeStatus = getString(R.string.add);
                    } else {
                        ResumeStatus = getString(R.string.upd);
                    }
                    break;
                case R.id.tv_ResumeSet_2:
                    ResumeIdNum = "2";
                    if(tvResumeSet2.getText().toString().equals(getString(R.string.tvResumeSet))){
                        ResumeStatus = getString(R.string.add);
                    } else {
                        ResumeStatus = getString(R.string.upd);
                    }
                    break;
                case R.id.tv_ResumeSet_3:
                    ResumeIdNum = "3";
                    if(tvResumeSet3.getText().toString().equals(getString(R.string.tvResumeSet))){
                        ResumeStatus = getString(R.string.add);
                    } else {
                        ResumeStatus = getString(R.string.upd);
                    }
                    break;
            }
            myApplication.setResumeId(ResumeIdNum);
            myApplication.setresume_status(ResumeStatus);
            startActivity(intent);
        }
    };

    //履歴書设定
    public void load(){
        int resumeNumber = PreferenceUtils.getresume_number();
        String Email = PreferenceUtils.getEmail();
//        deviceId = PreferenceUtils.getdeviceId();

        if(myApplication.getlast_name().length() > 0){
            String name = myApplication.getlast_name() + myApplication.getfirst_name() + " 様";
            Log.d("PersonalSetActivity", "name:" + name);
            tvname.setText(name);
        }
        tvemail.setText(Email);
        Log.d("***resumeNumber***", "" + resumeNumber);
        switch (resumeNumber){
            case 0:
                tvResumeSet1.setText(getString(R.string.tvResumeSet));
                tvResumeSet2.setVisibility(View.GONE);
                tvResumeSet3.setVisibility(View.GONE);
                break;
            case 1:
                tvResumeSet1.setText(myApplication.getresume_name("1"));
                tvResumeSet2.setText(getString(R.string.tvResumeSet));
                tvResumeSet3.setVisibility(View.GONE);
                break;
            case 2:
                tvResumeSet1.setText(myApplication.getresume_name("1"));
                tvResumeSet2.setText(myApplication.getresume_name("2"));
                tvResumeSet3.setText(getString(R.string.tvResumeSet));
                break;
            case 3:
                tvResumeSet1.setText(myApplication.getresume_name("1"));
                tvResumeSet2.setText(myApplication.getresume_name("2"));
                tvResumeSet3.setText(myApplication.getresume_name("3"));
                break;
        }
    }

    //履歴書隐藏/显示
    public void Click_Resume(){
        if(tlResumeSet.getVisibility() == View.VISIBLE){
            tlResumeSet.setVisibility(View.GONE);
        } else {
            tlResumeSet.setVisibility(View.VISIBLE);
        }

    }
}
