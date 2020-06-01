package jp.kinwork;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ClassDdl.ProfessionalCareer;
import jp.kinwork.Common.ClassDdl.Qualification;
import jp.kinwork.Common.ClassDdl.Resume;
import jp.kinwork.Common.ClassDdl.SchoolCareer;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class ResumeActivity extends AppCompatActivity {

    final static String PARAM_AddResume = "/ResumesMobile/addDefaultResume";
    final static String PARAM_UpdateResume = "/ResumesMobile/updateResume";
    final static String PARAM_GetResumeInfo = "/MypagesMobile/personalResumeList";
    final static String PARAM_delResume = "/ResumesMobile/deleteResume";
    final static String PARAM_delPC = "/ResumesMobile/deleteProfessionalCareer";
    final static String PARAM_delSC = "/ResumesMobile/deleteSchoolCareer";
    final static String PARAM_delQC = "/ResumesMobile/deleteQualification";
    final static String PARAM_ResumeName = "/ResumesMobile/updateResumeName";

    private MyApplication myApplication;
    private ScrollView slresume;
    private CheckBox cbone;
    private CheckBox cbtwo;
    private CheckBox cbthre;
    private CheckBox cbfour;
    private CheckBox cbfive;
    private CheckBox cbsix;
    private CheckBox cbseve;
    private CheckBox cbeigh;
    private CheckBox cbnine;
    private CheckBox cbcanmove;

    private TableLayout tlResumename;
    private TableLayout tlrenameresume;
    private TableLayout tlapplicationinformation;
    private TableLayout lltleducational;
    private TableLayout lltlemploymenthistory;
    private TableLayout lltlqualification;
    private TableLayout tlbasicinformation;
    private TableLayout tljob;
    private TableLayout tleducational;
    private TableLayout tlqualification;
    private TableLayout tlPrSkill;

    private String resume_status;
    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String resumeId;
    private String IresumeIdflg;
    private String canmove = "0";
    private String createFlg = "0";
    private String saveID = "";
    private String Jobtypeexpectations = "";
    private String Neareststation = "";
    private String Ismovingok = "0";
    private String AspirationPR = "";
    private String hobbySkill = "";
    private String ActCation = "";


    private ImageView bu_info_education;
    private ImageView bu_info_employment;
    private ImageView bu_info_qualification;

    private ImageView ivpersonalsettings;
    private TextView tvpersonalsettings;

    private TextView tvapplicationinformation;
    private TextView tvbasicinformation;
    private TextView tl_tr_tv_educational;
    private TextView tl_tr_tv_employmenthistory;
    private TextView tl_tr_tv_qualification;
    private TextView tv_PrSkill;
    private TextView tv_resume_title;
    private TextView tvcancel;
    private TextView tvconfirmation;


    private TextView tvresumetitle;
    private TextView tltrtvname;
    private TextView tltrtvkananame;
    private TextView tltrtvsex;
    private TextView tltrtvbirthday;
    private TextView tltrtvcountry;
    private TextView tltrtvaddress;
    private TextView tltrtvphone;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;

    private EditText etresumename;
    private EditText ethopeJobcategory;
    private EditText etneareststation;
    private EditText etAspirationPR;
    private EditText ethobbySkill;

    private Button bu_cancel;
    private Button bu_create;

    private int one_1          = 0;
    private int two_2          = 0;
    private int three_4        = 0;
    private int four_8         = 0;
    private int five_16        = 0;
    private int six_32         = 0;
    private int seven_64       = 0;
    private int eight_128      = 0;
    private int nine_256       = 0;
    private int status_sum         = 0;
    private int DeleteIndex        = 0;
    private int resumeNumber       = 0;
    private int iIndex             = 0;
    private int employmentstatus   = 0;

    private Resume Resume;
    private jp.kinwork.Common.PreferenceUtils PreferenceUtils;
    private SchoolCareer schoolCareer;
    private ProfessionalCareer professionalCareer;
    private Qualification qualification;
    private ProgressDialog dialog ;

    private boolean processResult = false;

    private LinkedList<ImageView> listIBTNAdd_employment;
    private LinkedList<ImageView> listIBTNDel_employment;
    private LinkedList<String> listIdDel_employment;

    private LinkedList<ImageView> listIBTNAdd_education;
    private LinkedList<ImageView> listIBTNDel_education;
    private LinkedList<String> listIdDel_education;

    private LinkedList<ImageView> listIBTNAdd_qualification;
    private LinkedList<ImageView> listIBTNDel_qualification;
    private LinkedList<String> listIdDel_qualification;

    private JSONObject JSONObject_resume;
    private JSONArray JSONArray_resume;
    private JSONArray JSONArray_employment;
    private JSONArray JSONArray_education;
    private JSONArray JSONArray_qualification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog = new ProgressDialog(this) ;
        //dialog.setTitle("提示") ;
        dialog.setMessage(getString(R.string.Loading)) ;
        Intent intent = getIntent();
        myApplication = (MyApplication) getApplication();
        IresumeIdflg = myApplication.getResumeId();
        resume_status = myApplication.getresume_status();
        ActCation = myApplication.getActCation();
        Initialization();
        load();
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
    public void Initialization(){
        slresume = (ScrollView) findViewById(R.id.sl_resume);
        cbone       = (CheckBox) findViewById(R.id.cb_one);
        cbtwo         = (CheckBox) findViewById(R.id.cb_two);
        cbthre          = (CheckBox) findViewById(R.id.cb_three);
        cbfour  = (CheckBox) findViewById(R.id.cb_four);
        cbfive        = (CheckBox) findViewById(R.id.cb_five);
        cbsix      = (CheckBox) findViewById(R.id.cb_six);
        cbseve       = (CheckBox) findViewById(R.id.cb_seven);
        cbeigh        = (CheckBox) findViewById(R.id.cb_eight);
        cbnine            = (CheckBox) findViewById(R.id.cb_nine);
        cbcanmove         = (CheckBox) findViewById(R.id.cb_canmove);
        cbone.setOnClickListener(checkListener);
        cbtwo.setOnClickListener(checkListener);
        cbthre.setOnClickListener(checkListener);
        cbfour.setOnClickListener(checkListener);
        cbfive.setOnClickListener(checkListener);
        cbsix.setOnClickListener(checkListener);
        cbseve.setOnClickListener(checkListener);
        cbeigh.setOnClickListener(checkListener);
        cbnine.setOnClickListener(checkListener);
        cbcanmove.setOnClickListener(checkListener);

        bu_info_education= findViewById(R.id.bu_info_education);
        bu_info_employment=findViewById(R.id.bu_info_employment);
        bu_info_qualification=findViewById(R.id.bu_info_qualification);
        bu_info_education.setOnClickListener(createListener);
        bu_info_employment.setOnClickListener(createListener);
        bu_info_employment.setOnClickListener(createListener);



        tlResumename = (TableLayout) findViewById(R.id.tl_Resume_name);
        tlrenameresume = (TableLayout) findViewById(R.id.tl_rename_resume);
        lltleducational = (TableLayout) findViewById(R.id.ll_tl_educational);
        lltlemploymenthistory = (TableLayout) findViewById(R.id.ll_tl_employmenthistory);
        lltlqualification = (TableLayout) findViewById(R.id.ll_tl_qualification);
        tlapplicationinformation = (TableLayout) findViewById(R.id.tl_applicationinformation);
        tlbasicinformation = (TableLayout) findViewById(R.id.tl_basicinformation);
        tljob = (TableLayout) findViewById(R.id.tl_job);
        tleducational = (TableLayout) findViewById(R.id.tl_educational);
        tlqualification = (TableLayout) findViewById(R.id.tl_qualification);
        tlPrSkill = (TableLayout) findViewById(R.id.tl_PrSkill);
        tvresumetitle   = (TextView) findViewById(R.id.tv_resume_title);
        tvcancel=findViewById(R.id.tv_Cancel);
        tvconfirmation=findViewById(R.id.tv_Confirmation);
        tvresumetitle.setOnClickListener(Listener);
        tvcancel.setOnClickListener(Listener);
        tvconfirmation.setOnClickListener(Listener);
        tvapplicationinformation=findViewById(R.id.tv_applicationinformation);
        tvbasicinformation=findViewById(R.id.tv_basicinformation);
        tl_tr_tv_educational=findViewById(R.id.tl_tr_tv_educational);
        tl_tr_tv_employmenthistory=findViewById(R.id.tl_tr_tv_employmenthistory);
        tl_tr_tv_qualification=findViewById(R.id.tl_tr_tv_qualification);
        tv_PrSkill=findViewById(R.id.tv_PrSkill);
        tvapplicationinformation.setOnClickListener(onListener);
        tvbasicinformation.setOnClickListener(onListener);
        tl_tr_tv_educational.setOnClickListener(onListener);
        tl_tr_tv_employmenthistory.setOnClickListener(onListener);
        tl_tr_tv_qualification.setOnClickListener(onListener);
        tv_PrSkill.setOnClickListener(onListener);


        etresumename    = (EditText) findViewById(R.id.et_resume_name);
        tltrtvname      = (TextView) findViewById(R.id.tl_tr_tv_name);
        tltrtvkananame  = (TextView) findViewById(R.id.tl_tr_tv_kananame);
        tltrtvsex       = (TextView) findViewById(R.id.tl_tr_tv_sex);
        tltrtvbirthday  = (TextView) findViewById(R.id.tl_tr_tv_birthday);
        tltrtvcountry   = (TextView) findViewById(R.id.tl_tr_tv_country);
        tltrtvaddress   = (TextView) findViewById(R.id.tl_tr_tv_address);
        tltrtvphone     = (TextView) findViewById(R.id.tl_tr_tv_phone);
        tvback          = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle               = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.personalsettings));
        tvbackdummy.setText(getString(R.string.personalsettings));
        tvbacktitle.setText(getString(R.string.editprofile));
        bu_cancel=findViewById(R.id.bu_cancel);
        bu_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_cancel();
            }
        });
        bu_create=findViewById(R.id.bu_create);
        bu_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_create();
            }
        });
        ivpersonalsettings = (ImageView) findViewById(R.id.iv_b_personalsettings);
        tvpersonalsettings = (TextView) findViewById(R.id.tv_b_personalsettings);
        ivpersonalsettings.setImageResource(R.mipmap.blue_personalsettings);
        tvpersonalsettings.setTextColor(Color.parseColor("#5EACE2"));
        ethopeJobcategory = (EditText) findViewById(R.id.et_hopeJobcategory);
        etneareststation = (EditText) findViewById(R.id.et_neareststation);
        etAspirationPR = (EditText) findViewById(R.id.et_Aspiration_PR);
        ethobbySkill = (EditText) findViewById(R.id.et_hobby_Skill);
        listIBTNAdd_employment        = new LinkedList<ImageView>();
        listIBTNDel_employment        = new LinkedList<ImageView>();
        listIdDel_employment          = new LinkedList<String>();
        listIBTNAdd_education         = new LinkedList<ImageView>();
        listIBTNDel_education         = new LinkedList<ImageView>();
        listIdDel_education           = new LinkedList<String>();
        listIBTNAdd_qualification     = new LinkedList<ImageView>();
        listIBTNDel_qualification     = new LinkedList<ImageView>();
        listIdDel_qualification       = new LinkedList<String>();
        PreferenceUtils = new PreferenceUtils(ResumeActivity.this);
        JSONArray_employment = new JSONArray();
        JSONArray_education = new JSONArray();
        JSONArray_qualification = new JSONArray();
        saveID = PreferenceUtils.getsaveid();
        slresume.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                if(ActCation.length() > 0){
                    tlapplicationinformation.setVisibility(View.GONE);
                    tlbasicinformation.setVisibility(View.GONE);
                }
                if(ActCation.equals(getString(R.string.edu))){
                    lltleducational.getLocationOnScreen(location);
                    tleducational.setVisibility(View.VISIBLE);
                } else if(ActCation.equals(getString(R.string.emp))){
                    lltlemploymenthistory.getLocationOnScreen(location);
                    tljob.setVisibility(View.VISIBLE);
                } else if(ActCation.equals(getString(R.string.qua))){
                    lltlqualification.getLocationOnScreen(location);
                    tlqualification.setVisibility(View.VISIBLE);
                }
                int offset = location[1] - slresume.getMeasuredHeight();
                if (offset < 0) {
                    offset = 0;
                }
                slresume.smoothScrollTo(0, offset);
            }
        });

    }
    //本地保存情报取得
    public void load(){
        resumeNumber = PreferenceUtils.getresume_number();
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
        if(myApplication.getpersonalset(0).equals("0")){
            if(resumeId.equals("A")){
                addDefaultResume(userId,token);
            } else {
                getbasici();
            }
        } else {
            resumeId = myApplication.getpersonalset(8);
            resume_status =  myApplication.getpersonalset(13);
            tvresumetitle.setText(myApplication.getpersonalset(1));
            myApplication.setresume_name(myApplication.getpersonalset(1),IresumeIdflg);
            employmentstatus = Integer.parseInt(myApplication.getpersonalset(2),10);
            String status = Integer.toBinaryString(employmentstatus);
            getstatus(status);
            Jobtypeexpectations = myApplication.getpersonalset(3);
            ethopeJobcategory.setText(Jobtypeexpectations);
            Neareststation = myApplication.getpersonalset(4);
            etneareststation.setText(Neareststation);
            if(myApplication.getpersonalset(5).equals("1")){
                Ismovingok = myApplication.getpersonalset(5);
                cbcanmove.setChecked(true);
                canmove = Ismovingok;
            }
            AspirationPR = myApplication.getpersonalset(6);
            etAspirationPR.setText(AspirationPR);
            hobbySkill = myApplication.getpersonalset(7);
            ethobbySkill.setText(hobbySkill);
            try {
                JSONObject_resume = new JSONObject(myApplication.getpersonalset(9));
                Setbasici(JSONObject_resume);
                if(!resume_status.equals(getString(R.string.add))){
                    JSONArray_education = new JSONArray(myApplication.getpersonalset(10));
                    JSONArray_employment = new JSONArray(myApplication.getpersonalset(11));
                    JSONArray_qualification = new JSONArray(myApplication.getpersonalset(12));
                    educationInfo(JSONArray_education);
                    employmentInfo(JSONArray_employment);
                    qualificationInfo(JSONArray_qualification);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //履歴書番号作成
    public void Click_create(){
        if(resume_status.equals(getString(R.string.add))){
            resumeNumber = resumeNumber + 1;
            PreferenceUtils.setresume_number(resumeNumber);
        }
        Create_reaume(userId,token);
    }
    //"×"按钮触发删除事件
    public void Click_cancel(){
        Delalertdialog(getString(R.string.Delalertdialog),getString(R.string.resume));
    }
    //項目の表示
    private View.OnClickListener onListener =new View.OnClickListener() {
        public void onClick(View ClikcView) {
            switch (ClikcView.getId()) {
                case R.id.tv_applicationinformation:
                    if (tlapplicationinformation.getVisibility() == View.GONE) {
                        tlapplicationinformation.setVisibility(View.VISIBLE);
                    } else {
                        tlapplicationinformation.setVisibility(View.GONE);
                    }
                    break;
                case R.id.tv_basicinformation:
                    if (tlbasicinformation.getVisibility() == View.GONE) {
                        tlbasicinformation.setVisibility(View.VISIBLE);
                    } else {
                        tlbasicinformation.setVisibility(View.GONE);
                    }
                    break;
                case R.id.tl_tr_tv_employmenthistory:
                    if (JSONArray_employment.length() > 0) {
                        if (tljob.getVisibility() == View.GONE) {
                            tljob.setVisibility(View.VISIBLE);
                        } else {
                            tljob.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.tl_tr_tv_educational:
                    if (JSONArray_education.length() > 0) {
                        if (tleducational.getVisibility() == View.GONE) {
                            tleducational.setVisibility(View.VISIBLE);
                        } else {
                            tleducational.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.tl_tr_tv_qualification:
                    if (JSONArray_qualification.length() > 0) {
                        if (tlqualification.getVisibility() == View.GONE) {
                            tlqualification.setVisibility(View.VISIBLE);
                        } else {
                            tlqualification.setVisibility(View.GONE);
                        }
                    }
                    break;
                case R.id.tv_PrSkill:
                    if (tlPrSkill.getVisibility() == View.GONE) {
                        tlPrSkill.setVisibility(View.VISIBLE);
                    } else {
                        tlPrSkill.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
    //チェックボックス
    private View.OnClickListener checkListener =new View.OnClickListener() {
        public void onClick(View View) {
            switch (View.getId()) {
                case R.id.cb_one:
                    cbone.isChecked();
                    if (cbone.isChecked() == true) {
                        one_1 = 1;
                    } else {
                        one_1 = 0;
                    }
                    break;
                case R.id.cb_two:
                    cbtwo.isChecked();
                    if (cbtwo.isChecked() == true) {
                        two_2 = 2;
                    } else {
                        two_2 = 0;
                    }
                    break;
                case R.id.cb_three:
                    cbthre.isChecked();
                    if (cbthre.isChecked() == true) {
                        three_4 = 4;
                    } else {
                        three_4 = 0;
                    }
                    break;
                case R.id.cb_four:
                    cbfour.isChecked();
                    if (cbfour.isChecked() == true) {
                        four_8 = 8;
                    } else {
                        four_8 = 0;
                    }
                    break;
                case R.id.cb_five:
                    cbfive.isChecked();
                    if (cbfive.isChecked() == true) {
                        five_16 = 16;
                    } else {
                        five_16 = 0;
                    }
                    break;
                case R.id.cb_six:
                    cbsix.isChecked();
                    if (cbsix.isChecked() == true) {
                        six_32 = 32;
                    } else {
                        six_32 = 0;
                    }
                    break;
                case R.id.cb_seven:
                    cbseve.isChecked();
                    if (cbseve.isChecked() == true) {
                        seven_64 = 64;
                    } else {
                        seven_64 = 0;
                    }
                    break;
                case R.id.cb_eight:
                    cbeigh.isChecked();
                    if (cbeigh.isChecked() == true) {
                        eight_128 = 128;
                    } else {
                        eight_128 = 0;
                    }
                    break;
                case R.id.cb_nine:
                    cbnine.isChecked();
                    if (cbnine.isChecked() == true) {
                        nine_256 = 256;
                    } else {
                        nine_256 = 0;
                    }
                    break;
                case R.id.cb_canmove:
                    cbcanmove.isChecked();
                    if (cbcanmove.isChecked() == true) {
                        canmove = "1";
                    } else {
                        canmove = "0";
                    }
                    break;
            }
        }
    };
    //基本情報を取得
    public void getbasici(){
        PostDate Pdata = new PostDate();
        Pdata.setUserId(userId);
        Pdata.setToken(token);
        Pdata.setResumeId(resumeId);
        String data = JsonChnge(AesKey,Pdata);
        Map<String,String>param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_GetResumeInfo);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
    //履歴書情報を取得
    public void addDefaultResume(String userid,String token){
        PostDate Pdata = new PostDate();
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        String data = JsonChnge(AesKey,Pdata);
        Map<String,String>param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_AddResume);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d("***sdPdata***", sdPdata);
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

        String name= "";

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
            name = map.get(getString(R.string.name));
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
                Log.d("***name***", name);
                dialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    processResult = obj.getBoolean(getString(R.string.processResult));
                    String meg = obj.getString(getString(R.string.message));
                    String errCode = obj.getString(getString(R.string.errorCode));
                    String returnData = obj.getString(getString(R.string.returnData));
                    if(processResult == true) {
                        if(createFlg.equals("1")){
                            NewIntent();
                        }
                        if(name.equals(getString(R.string.ResumeName))){
                            alertdialog("",meg,"");
                        }else if(! returnData.equals("null")){
                            decryptchange(returnData);
                        }
                        if(name.equals(getString(R.string.employment))){
                            listIBTNAdd_employment.remove(iIndex);
                            listIBTNDel_employment.remove(iIndex);
                            tljob.removeViewAt(iIndex);
                        } else if(name.equals(getString(R.string.education))){
                            listIBTNAdd_education.remove(iIndex);
                            listIBTNDel_education.remove(iIndex);
                            tleducational.removeViewAt(iIndex);
                        } else if(name.equals(getString(R.string.qualification))){
                            listIBTNAdd_qualification.remove(iIndex);
                            listIBTNDel_qualification.remove(iIndex);
                            tlqualification.removeViewAt(iIndex);
                        }
                    } else {
                        if(createFlg.equals("1")){
                            if(!obj.getString(getString(R.string.fieldErrors)).equals("null")){
                                showError(obj.getString(getString(R.string.fieldErrors)));
                            }else {
                                alertdialog(getString(R.string.error),meg,errCode);
                            }

                        } else {
                            alertdialog(getString(R.string.error),meg,errCode);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data,AesKey);
        Log.d("***datas***", datas);
        try {
            JSONObject obj = new JSONObject(datas);
            if(obj.has(getString(R.string.addedResume))){
                addedResumeid(obj.getString(getString(R.string.addedResume)));
            } else if(obj.has(getString(R.string.resumeInfo))){
                JSONArray_resume = obj.getJSONArray(getString(R.string.resumeInfo));
                JSONArray_employment = obj.getJSONArray(getString(R.string.professionalCareer));
                JSONArray_education = obj.getJSONArray(getString(R.string.schoolCareer));
                JSONArray_qualification = obj.getJSONArray(getString(R.string.qualification));
                resumeinfo(JSONArray_resume);
                educationInfo(JSONArray_education);
                employmentInfo(JSONArray_employment);
                qualificationInfo(JSONArray_qualification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //通信结果提示
    private void alertdialog(String Title,String meg,final String code){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title).setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                if(code.equals("2001")){
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(ResumeActivity.this, BasicinfoeditActivity.class);
                    startActivity(intent);
                }
            }
        }).show();
    }
    //雇佣形态設定
    public void getstatus(String data){
        int number = 0;
        for(int i = data.length(); i > 0; i--){
            Log.i("转换内容的", "第" + i + "位:" + data.charAt(i-1));
            String num= String.valueOf(data.charAt(i-1));
            Log.i("转换内容的", "num:" + num);
            number = number + 1;
            if(number == 1 && num.equals("1")){
                cbone.setChecked(true);
                one_1 = 1;
            }
            if(number == 2 && num.equals("1")){
                cbtwo.setChecked(true);
                two_2 = 2;
            }
            if(number == 3 && num.equals("1")){
                cbthre.setChecked(true);
                three_4 = 4;
            }
            if(number == 4 && num.equals("1")){
                cbfour.setChecked(true);
                four_8 = 8;
            }
            if(number == 5 && num.equals("1")){
                cbfive.setChecked(true);
                five_16 = 16;
            }
            if(number == 6 && num.equals("1")){
                cbsix.setChecked(true);
                six_32 = 32;
            }
            if(number == 7 && num.equals("1")){
                cbseve.setChecked(true);
                seven_64 = 64;
            }
            if(number == 8 && num.equals("1")){
                cbeigh.setChecked(true);
                eight_128 = 128;
            }
            if(number == 9 && num.equals("1")){
                cbnine.setChecked(true);
                nine_256 = 256;
            }
        }
    }
    //履歴書作成
    public void Create_reaume(String userid,String token){
        createFlg = "1";
        status_sum = one_1      +
                     two_2      +
                     three_4    +
                     four_8     +
                     five_16    +
                     six_32     +
                     seven_64   +
                     eight_128  +
                     nine_256;
        String employment_status = String.valueOf(status_sum);
        Log.d("employment_status", employment_status);
        myApplication.setresume_name(tvresumetitle.getText().toString(),IresumeIdflg);
        PostDate Pdata = new PostDate();
        Resume Resume = new Resume();
        Resume.setId(resumeId);
        Resume.setresume_name(tvresumetitle.getText().toString());
        Resume.setEmployment_status(employment_status);
        Resume.setJob_type_expectations(ethopeJobcategory.getText().toString());
        Resume.setNearest_station(etneareststation.getText().toString());
        Resume.setIs_moving_ok(canmove);
        Resume.setindividual_pr(etAspirationPR.getText().toString());
        Resume.sethoby(ethobbySkill.getText().toString());
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        Pdata.setResume(Resume);
        String data = JsonChnge(AesKey,Pdata);
        Map<String,String>param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_UpdateResume);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
    //職歴追加
    private View.OnClickListener createListener =new View.OnClickListener() {

        public void onClick(View View){
        switch (View.getId()) {
            //職歴画面に
            case R.id.bu_info_employment:
//                myApplication.setnum(listIBTNDel_employment.size());
                myApplication.setActCation(getString(R.string.emp));
                Intent intent_employment = new Intent();
                intent_employment.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent_employment.setClass(ResumeActivity.this, EmploymentActivity.class);
                intent_employment.putExtra(getString(R.string.status), getString(R.string.add));
                startActivity(intent_employment);
                break;
            //学歴画面に
            case R.id.bu_info_education:
//                myApplication.setnum(listIBTNDel_employment.size());
                myApplication.setActCation(getString(R.string.edu));
                Intent intent_education = new Intent();
                intent_education.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent_education.setClass(ResumeActivity.this, EducationActivity.class);
                intent_education.putExtra(getString(R.string.status), getString(R.string.add));
                startActivity(intent_education);
                break;
            //資格画面に
            case R.id.bu_info_qualification:
//                myApplication.setnum(listIBTNDel_employment.size());
                myApplication.setActCation(getString(R.string.qua));
                Intent intent_qualification = new Intent();
                intent_qualification.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent_qualification.setClass(ResumeActivity.this, QualificationActivity.class);
                intent_qualification.putExtra(getString(R.string.status), getString(R.string.add));
                startActivity(intent_qualification);
                break;
        }

        }
    };
    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    //基本情報を取得
    public void Setbasici(JSONObject obj){
        Log.d("objtoString", obj.toString());
        try {
            //姓名
            if(! obj.getString(getString(R.string.last_name)).equals("") && ! obj.getString(getString(R.string.first_name)).equals("")){
                tltrtvname.setText(getString(R.string.seimei_) + obj.getString(getString(R.string.last_name)) + " " + obj.getString(getString(R.string.first_name)));
            }
            //セイメイ
            if(! obj.getString(getString(R.string.last_name_kana)).equals("") && ! obj.getString(getString(R.string.first_name_kana)).equals("")){
                tltrtvkananame.setText(getString(R.string.seimei_kana) + obj.getString(getString(R.string.last_name_kana)) + " " + obj.getString(getString(R.string.first_name_kana)));
            }
            //性別
            if(! obj.getString(getString(R.string.sex_div)).equals("")){
                if(obj.getString(getString(R.string.sex_div)).equals("1")){
                    tltrtvsex.setText(getString(R.string.seibetsu) + getString(R.string.sex_man));
                }else if(obj.getString(getString(R.string.sex_div)).equals("2")){
                    tltrtvsex.setText(getString(R.string.seibetsu) + getString(R.string.sex_woman));
                }
            }
            //生年月日
            if(! obj.getString(getString(R.string.birthday)).equals("")){
                tltrtvbirthday.setText(getString(R.string.birthday_nihongo) + obj.getString(getString(R.string.birthday)));
            }

            //国籍
//            if(! obj.getString("").equals("")){
                tltrtvcountry.setText(getString(R.string.kokuseki) + myApplication.getcountry());
//            }
            //住所
            if(! obj.getString(getString(R.string.add_1)).equals("") && ! obj.getString(getString(R.string.add_2)).equals("") && ! obj.getString(getString(R.string.add_3)).equals("")){
                tltrtvaddress.setText(getString(R.string.address_nihongo) + obj.getString(getString(R.string.add_1)) + obj.getString(getString(R.string.add_2)) + obj.getString(getString(R.string.add_3)) + obj.getString(getString(R.string.add_4)));
            }
            //電話番号
            if(! obj.getString(getString(R.string.phone_number)).equals("")){
                tltrtvphone.setText(getString(R.string.phone_number_nihongo) + obj.getString(getString(R.string.phone_number)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //新履历ID取得
    public void addedResumeid(String data){
        try {
            JSONObject obj = new JSONObject(data);
            Log.d("***datas***", data);
            JSONObject_resume = new JSONObject(obj.getString(getString(R.string.Resume)));
            resumeId = JSONObject_resume.getString(getString(R.string.id));
            tvresumetitle.setText(JSONObject_resume.getString(getString(R.string.resume_name)));
            Setbasici(JSONObject_resume);
            if(IresumeIdflg.equals("1")){
                PreferenceUtils.setresumeid_1(resumeId);
            } else if (IresumeIdflg.equals("2")){
                PreferenceUtils.setresumeid_2(resumeId);
            } else if (IresumeIdflg.equals("3")){
                PreferenceUtils.setresumeid_3(resumeId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //已登录履历信息取得
    private void resumeinfo(JSONArray data){
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***resumeinfo1***", data.getString(i));
                Log.d("***resumeinfo2***", obj.getString("Resume"));
                JSONObject_resume = new JSONObject(obj.getString(getString(R.string.Resume)));
                if(JSONObject_resume.getString(getString(R.string.id)).equals(resumeId)){
                    Log.d("***Resume***", obj.getString("Resume"));
                    if(! JSONObject_resume.getString(getString(R.string.resume_name)).equals("") && ! JSONObject_resume.getString(getString(R.string.resume_name)).equals("null")){
                        tvresumetitle.setText(JSONObject_resume.getString(getString(R.string.resume_name)));
                        myApplication.setresume_name(JSONObject_resume.getString(getString(R.string.resume_name)),IresumeIdflg);
                    }
                    Log.d("***ResumegetString***", "["+JSONObject_resume.getString(getString(R.string.employment_status)) + "]");
                    if(! JSONObject_resume.getString(getString(R.string.employment_status)).equals("") && ! JSONObject_resume.getString(getString(R.string.employment_status)).equals("null")){
                        employmentstatus = Integer.parseInt(JSONObject_resume.getString(getString(R.string.employment_status)),10);
                        String status = Integer.toBinaryString(employmentstatus);
                        getstatus(status);
                    }
                    if(! JSONObject_resume.getString(getString(R.string.job_type_expectations)).equals("") && ! JSONObject_resume.getString(getString(R.string.job_type_expectations)).equals("null")){
                        Jobtypeexpectations = JSONObject_resume.getString(getString(R.string.job_type_expectations));
                        ethopeJobcategory.setText(Jobtypeexpectations);
                    }
                    if(! JSONObject_resume.getString(getString(R.string.nearest_station)).equals("") && ! JSONObject_resume.getString(getString(R.string.nearest_station)).equals("null")){
                        Neareststation = JSONObject_resume.getString(getString(R.string.nearest_station));
                        etneareststation.setText(Neareststation);
                    }
                    if(! JSONObject_resume.getString(getString(R.string.is_moving_ok)).equals("") && ! JSONObject_resume.getString(getString(R.string.is_moving_ok)).equals("null")){
                        if(JSONObject_resume.getString(getString(R.string.is_moving_ok)).equals("1")){
                            Ismovingok = JSONObject_resume.getString(getString(R.string.employment_status));
                            cbcanmove.setChecked(true);
                            canmove = Ismovingok;
                        }
                    }
                    if(! JSONObject_resume.getString(getString(R.string.individual_pr)).equals("") && ! JSONObject_resume.getString(getString(R.string.individual_pr)).equals("null")){
                        AspirationPR = JSONObject_resume.getString(getString(R.string.individual_pr));
                        etAspirationPR.setText(AspirationPR);
                    }
                    if(! JSONObject_resume.getString(getString(R.string.hoby)).equals("") && ! JSONObject_resume.getString(getString(R.string.hoby)).equals("null")){
                        hobbySkill = JSONObject_resume.getString(getString(R.string.hoby));
                        ethobbySkill.setText(hobbySkill);
                    }
                    Setbasici(JSONObject_resume);
                };
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录職歴信息取得
    public void employmentInfo(JSONArray data){
        Gson gson = new Gson();
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***employmentInfo***", data.getString(i));
                professionalCareer = gson.fromJson(obj.getString(getString(R.string.ProfessionalCareer)),ProfessionalCareer.class);
                Log.d("***Resume.getId***", resumeId);
                int top= dp2px(this, 10);
                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
                tlparams.setMargins(0,0,0,top);
                View add_employment = getLayoutInflater().inflate(R.layout.include_employment, null);
                TextView jobtitle = (TextView) add_employment.findViewById(R.id.I_tl_tr_tv_jobtitle);
                TextView companyname = (TextView) add_employment.findViewById(R.id.I_tl_tr_tv_companyname);
                //TextView companylocation = (TextView) add_employment.findViewById(R.id.I_tl_tr_tv_companylocation);
                TextView employmentperiod = (TextView) add_employment.findViewById(R.id.I_tl_tr_tv_employmentperiod);
                ImageView employmentCr = (ImageView) add_employment.findViewById(R.id.I_ibu_jobcreate);
                ImageView employmentDe = (ImageView) add_employment.findViewById(R.id.I_ibu_jobdelete);
                jobtitle.setText(getString(R.string.shokushuumei) + professionalCareer.getJob_name());
                companyname.setText(getString(R.string.kaishamei) + professionalCareer.getCompany_name());
                String getFrom_year = "";
                String getFrom_month = "";
                String getTo_year = "";
                String getTo_month = "";
                String year_month = "";
                getFrom_year = professionalCareer.getFrom_year();
                getFrom_month = professionalCareer.getFrom_month();
                getTo_year = professionalCareer.getTo_year();
                getTo_month = professionalCareer.getTo_month();
                if(getFrom_year != null && getFrom_month != null){
                    year_month = getFrom_year + "/" + getFrom_month;
                }
                if(getTo_year != null && getTo_month != null){
                    year_month = year_month + "--" + getTo_year + "/" + getTo_month;
                }
                employmentperiod.setText(getString(R.string.shuushokukikan) + year_month);
                add_employment.setLayoutParams(tlparams);
                tljob.addView(add_employment,i);
                listIBTNAdd_employment.add(i,employmentCr);
                listIBTNDel_employment.add(i,employmentDe);
                listIdDel_employment.add(i,professionalCareer.getId().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录職歴信息変更
    public void Updemployment(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNAdd_employment.size(); i++) {
            if (listIBTNAdd_employment.get(i) == View) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            try {
                myApplication.setActCation(getString(R.string.emp));
                JSONObject obj = JSONArray_employment.getJSONObject(iIndex);
                Gson gson = new Gson();
                professionalCareer = gson.fromJson(obj.getString(getString(R.string.professionalCareer)),ProfessionalCareer.class);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(ResumeActivity.this, EmploymentActivity.class);
                intent.putExtra(getString(R.string.status), getString(R.string.upd));
                intent.putExtra(getString(R.string.professionalCareerId), professionalCareer.getId());
                intent.putExtra(getString(R.string.jobName), professionalCareer.getJob_name());
                intent.putExtra(getString(R.string.Companyname), professionalCareer.getJob_name());
                if(professionalCareer.getFrom_year() != null && professionalCareer.getFrom_month() !=null){
                    intent.putExtra(getString(R.string.Start_Y), professionalCareer.getFrom_year());
                    intent.putExtra(getString(R.string.Start_M), professionalCareer.getFrom_month());
                }else{
                    intent.putExtra(getString(R.string.Start_Y), "");
                    intent.putExtra(getString(R.string.Start_M), "");
                }
                if(professionalCareer.getTo_year() != null && professionalCareer.getTo_month() !=null){
                    intent.putExtra(getString(R.string.End_Y), professionalCareer.getTo_year());
                    intent.putExtra(getString(R.string.End_M), professionalCareer.getTo_month());
                }else{
                    intent.putExtra(getString(R.string.End_Y), "");
                    intent.putExtra(getString(R.string.End_M), "");
                }
                intent.putExtra(getString(R.string.CheckBox), professionalCareer.getIs_working_till_now());
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录職歴信息削除
    public void Delemployment(View v){
        if (v == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNDel_employment.size(); i++) {
            if (listIBTNDel_employment.get(i) == v) {
                iIndex = i;
                DeleteIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            Delalertdialog(getString(R.string.Delalertdialog2),getString(R.string.employment));
        }
    }
    //已登录学歴信息取得
    public void educationInfo(JSONArray data){
        Gson gson = new Gson();
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***educationInfo***", data.getString(i));
                schoolCareer = gson.fromJson(obj.getString(getString(R.string.SchoolCareer)),SchoolCareer.class);
                int top= dp2px(this, 10);
                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
                tlparams.setMargins(0,0,0,top);
                View add_education = getLayoutInflater().inflate(R.layout.include_education, null);
                TextView degree = (TextView) add_education.findViewById(R.id.I_tl_tr_tv_degree);
                TextView schoolname = (TextView) add_education.findViewById(R.id.I_tl_tr_tv_schoolname);
                TextView majorfield = (TextView) add_education.findViewById(R.id.I_tl_tr_tv_majorfield);
                //TextView schoollocation = (TextView) add_education.findViewById(R.id.I_tl_tr_tv_schoollocation);
                TextView durationofentrance = (TextView) add_education.findViewById(R.id.I_tl_tr_tv_durationofentrance);
                ImageView educationcreate = (ImageView) add_education.findViewById(R.id.I_ibu_educationcreate);
                ImageView educationdelete = (ImageView) add_education.findViewById(R.id.I_ibu_educationdelete);
                schoolname.setText(getString(R.string.gakkomei) + schoolCareer.getSchool_name());
                if(schoolCareer.getDegree().length() > 0 && ! schoolCareer.getDegree().equals("null")){
                    degree.setText(getString(R.string.gakui) + schoolCareer.getDegree());
                }
                if(schoolCareer.getMajor_field().length() > 0 && ! schoolCareer.getMajor_field().equals("null")){
                    majorfield.setText(getString(R.string.senkoubunya) + schoolCareer.getMajor_field());
                }

                String getFrom_year = "";
                String getFrom_month = "";
                String getTo_year = "";
                String getTo_month = "";
                String year_month = "";
                getFrom_year = schoolCareer.getFrom_year();
                getFrom_month = schoolCareer.getFrom_month();
                getTo_year = schoolCareer.getTo_year();
                getTo_month = schoolCareer.getTo_month();

                if( getFrom_year != null && getFrom_month != null){
                    year_month = getFrom_year + "/" + getFrom_month;
                }
                if( getTo_year != null && getTo_month != null){
                    year_month = year_month + "--" + getTo_year + "/" + getTo_month;;
                }
                durationofentrance.setText(getString(R.string.zaigakukikan) + year_month);
                add_education.setLayoutParams(tlparams);
                tleducational.addView(add_education,i);
                listIBTNAdd_education.add(i,educationcreate);
                listIBTNDel_education.add(i,educationdelete);
                listIdDel_education.add(i,schoolCareer.getId().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录学歴信息変更
    public void Updeducation(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNAdd_education.size(); i++) {
            if (listIBTNAdd_education.get(i) == View) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            try {
                myApplication.setActCation(getString(R.string.edu));
                JSONObject obj = JSONArray_education.getJSONObject(iIndex);
                Gson gson = new Gson();
                schoolCareer = gson.fromJson(obj.getString(getString(R.string.SchoolCareer)),SchoolCareer.class);
                Log.d("SchoolCareer", obj.getString("SchoolCareer"));
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(ResumeActivity.this, EducationActivity.class);
                intent.putExtra(getString(R.string.status), getString(R.string.upd));
                intent.putExtra(getString(R.string.schoolCareerId), schoolCareer.getId());
                intent.putExtra(getString(R.string.School_name), schoolCareer.getSchool_name());
                if(schoolCareer.getDegree() != null){
                    intent.putExtra(getString(R.string.Degree), schoolCareer.getDegree());
                } else {
                    intent.putExtra(getString(R.string.Degree), "");
                }
                if(schoolCareer.getMajor_field() != null){
                    intent.putExtra(getString(R.string.Major_field), schoolCareer.getMajor_field());

                } else {
                    intent.putExtra(getString(R.string.Major_field), "");
                }
                if(schoolCareer.getFrom_year() != null && schoolCareer.getFrom_month() != null){
                    intent.putExtra(getString(R.string.Start_Y), schoolCareer.getFrom_year());
                    intent.putExtra(getString(R.string.Start_M), schoolCareer.getFrom_month());
                } else {
                    intent.putExtra(getString(R.string.Start_Y), "");
                    intent.putExtra(getString(R.string.Start_M), "");
                }
                if(schoolCareer.getTo_year() != null && schoolCareer.getTo_month() != null){
                    intent.putExtra(getString(R.string.End_Y), schoolCareer.getTo_year());
                    intent.putExtra(getString(R.string.End_M), schoolCareer.getTo_month());
                } else {
                    intent.putExtra(getString(R.string.End_Y), "");
                    intent.putExtra(getString(R.string.End_M), "");
                }
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录学歴信息削除
    public void Deleducation(View v){
        if (v == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNDel_education.size(); i++) {
            if (listIBTNDel_education.get(i) == v) {
                iIndex = i;
                DeleteIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            Delalertdialog(getString(R.string.Delalertdialog3),getString(R.string.education));
        }
    }
    //已登录資格信息取得
    public void qualificationInfo(JSONArray data){
        Gson gson = new Gson();
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***qualificationInfo***", data.getString(i));
                qualification = gson.fromJson(obj.getString(getString(R.string.Qualification)),Qualification.class);
                int top= dp2px(this, 10);
                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
                tlparams.setMargins(0,0,0,top);
                View add_qualification = getLayoutInflater().inflate(R.layout.include_qualification, null);
                TextView qualificationname = (TextView) add_qualification.findViewById(R.id.I_tl_tr_tv_qualificationname);
                TextView period = (TextView) add_qualification.findViewById(R.id.I_tl_tr_tv_period);
                ImageView qualificationCr = (ImageView) add_qualification.findViewById(R.id.I_ibu_qualificationcreate);
                ImageView qualificationDe = (ImageView) add_qualification.findViewById(R.id.I_ibu_qualificationdelete);
                listIBTNAdd_qualification.add(i,qualificationCr);
                listIBTNDel_qualification.add(i,qualificationDe);
                listIdDel_qualification.add(i,qualification.getId());
                String From_date = "";
                From_date = qualification.getFrom_date();
                qualificationname.setText(getString(R.string.shikakumeishou) + qualification.getQualification_name());
                if (From_date != null){
                    period.setText(getString(R.string.shutokubi) + From_date);
                } else {
                    period.setText(getString(R.string.shutokubi));
                }
                add_qualification.setLayoutParams(tlparams);
                tlqualification.addView(add_qualification,i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录資格信息変更
    public void Updqualification(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNAdd_qualification.size(); i++) {
            if (listIBTNAdd_qualification.get(i) == View) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            try {
                myApplication.setActCation("qua");
                JSONObject obj = JSONArray_qualification.getJSONObject(iIndex);
                Log.d("***Updqualification***", obj.getString(getString(R.string.Qualification)));
                Gson gson = new Gson();
                qualification = gson.fromJson(obj.getString(getString(R.string.Qualification)),Qualification.class);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(ResumeActivity.this, QualificationActivity.class);
                intent.putExtra(getString(R.string.status), getString(R.string.upd));
                intent.putExtra(getString(R.string.Id_resume), qualification.getId_resume());
                intent.putExtra(getString(R.string.User_id), qualification.getUser_id());
                intent.putExtra(getString(R.string.qualificationId), qualification.getId());
                intent.putExtra(getString(R.string.Qualification_name), qualification.getQualification_name());
                if(qualification.getFrom_date() != null){
                    intent.putExtra(getString(R.string.From_date), qualification.getFrom_date());
                }else {
                    intent.putExtra(getString(R.string.From_date), "");
                }
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //已登录資格信息削除
    public void Delqualification(View v){
        if (v == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTNDel_qualification.size(); i++) {
            if (listIBTNDel_qualification.get(i) == v) {
                iIndex = i;
                DeleteIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            Delalertdialog(getString(R.string.Delalertdialog4),getString(R.string.qualification));
        }
    }
    //削除提示
    private void Delalertdialog(String meg, final String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                myApplication.setpersonalset("0",0);
                Deleteprocessing(name,DeleteIndex);
            }
        }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消按钮的点击事件
            }
        }).show();
    }
    //信息删除处理
    public void Deleteprocessing(String name,int Index){
        Log.d("***name***", "[" + name + "]" );
        Log.d("***Del_number***", "[" + resumeNumber + "]" );
        iIndex = Index;
        if(name.equals(getString(R.string.resume))){
            DeleteInfo(getString(R.string.resume),resumeId);
            if(processResult == true) {
                if(resume_status.equals(getString(R.string.upd))){
                    resumeNumber = resumeNumber - 1;
                    Log.d("***resume_Del_number***", "[" + resumeNumber + "]" );
                    PreferenceUtils.setresume_number(resumeNumber);
                }
                String resumeid_1 = PreferenceUtils.getresumeid_1();
                String resumeid_2 = PreferenceUtils.getresumeid_2();
                String resumeid_3 = PreferenceUtils.getresumeid_3();
                PreferenceUtils.delresumeid();
                if(IresumeIdflg.equals("1")){
                    if(! resumeid_2.equals("A") && ! resumeid_3.equals("A")){
                        PreferenceUtils.setresumeid_1(resumeid_2);
                        PreferenceUtils.setresumeid_2(resumeid_3);
                    } else if(resumeid_3.equals("A")){
                        PreferenceUtils.setresumeid_1(resumeid_2);
                    }
                } else if(IresumeIdflg.equals("2")){
                    if(! resumeid_3.equals("A")){
                        PreferenceUtils.setresumeid_2(resumeid_3);
                    } else {
                        PreferenceUtils.setresumeid_1(resumeid_1);
                    }
                } else {
                    PreferenceUtils.setresumeid_1(resumeid_1);
                    PreferenceUtils.setresumeid_2(resumeid_2);
                }
                NewIntent();
            }
        } else if(name.equals(getString(R.string.Back))){
            if(resume_status.equals(getString(R.string.add))){
                DeleteInfo(getString(R.string.resume),resumeId);
                if(PreferenceUtils.getresumeid_1().equals(resumeId)){
                    PreferenceUtils.del(getString(R.string.resumeid_1));
                } else if(PreferenceUtils.getresumeid_2().equals(resumeId)){
                    PreferenceUtils.del(getString(R.string.resumeid_1));
                } else if(PreferenceUtils.getresumeid_3().equals(resumeId)){
                    PreferenceUtils.del(getString(R.string.resumeid_1));
                }
            }
            Log.d("***Back_Del_number***", "[" + resumeNumber + "]" );
            PreferenceUtils.setresume_number(resumeNumber);
            NewIntent();
        } else if(name.equals(getString(R.string.employment))){
            String professionalCareerId = listIdDel_employment.get(Index);
            DeleteInfo(name,professionalCareerId);
        } else if(name.equals(getString(R.string.education))){
            String educationId = listIdDel_education.get(Index);
                DeleteInfo(name,educationId);
        } else if(name.equals(getString(R.string.qualification))){
                String qualificationId = listIdDel_qualification.get(Index);
                DeleteInfo(name,qualificationId);
        }
    }
    //服务器数据信息删除
    public void DeleteInfo(String name,String ID){
        PostDate Pdata = new PostDate();
        Map<String,String>param = new HashMap<String, String>();
        Pdata.setUserId(userId);
        Pdata.setToken(token);
        if(name.equals(getString(R.string.resume))){
            param.put(getString(R.string.file),PARAM_delResume);
            Pdata.setResumeId(ID);
        } else if(name.equals(getString(R.string.employment))){
            param.put(getString(R.string.file),PARAM_delPC);
            Pdata.setProfessionalCareerId(ID);
        } else if(name.equals(getString(R.string.education))){
            param.put(getString(R.string.file),PARAM_delSC);
            Pdata.setSchoolCareerId(ID);
        } else if(name.equals(getString(R.string.qualification))){
            param.put(getString(R.string.file),PARAM_delQC);
            Pdata.setQualificationId(ID);
        }
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),name);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);

    }
    //個人設定画面に移動
    public void NewIntent(){
        myApplication.setActCation("");
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        if(saveID.equals("SelectResume")){
//            intent.setClass(ResumeActivity.this, SelectResumeActivity.class);
//        } else {
//
//        }
        intent.setClass(ResumeActivity.this, PersonalSetActivity.class);
        startActivity(intent);
    }
    //返回按钮
    public void Click_back(){
        status_sum = one_1      +
                     two_2      +
                     three_4    +
                     four_8     +
                     five_16    +
                     six_32     +
                     seven_64   +
                     eight_128  +
                     nine_256;
        String a = "";
        String b = "";
        String c = "";
        String d = "";
        if(ethopeJobcategory.getText().length() > 0){
            a = ethopeJobcategory.getText().toString();
        }
        if(etneareststation.getText().length() > 0){
            b = etneareststation.getText().toString();
        }
        if(etAspirationPR.getText().length() > 0){
            c = etAspirationPR.getText().toString();
        }
        if(ethobbySkill.getText().length() > 0){
            d = ethobbySkill.getText().toString();
        }
        if(resume_status.equals(getString(R.string.add))){
            Delalertdialog(getString(R.string.Delalertdialog5),getString(R.string.resume));
        } else {
            if(employmentstatus != status_sum || ! a.equals(Jobtypeexpectations) || ! b.equals(Neareststation) || ! c.equals(AspirationPR) || ! d.equals(hobbySkill) || ! canmove.equals(Ismovingok))
            {
                Delalertdialog(getString(R.string.Delalertdialog6),getString(R.string.Back));
            } else {
                NewIntent();
            }
        }

    }
    //错误信息提示
    private void showError(String data){
        try {
            JSONObject obj = new JSONObject(data);
            Gson gson = new Gson();
            Resume ResumeData = gson.fromJson(obj.getString(getString(R.string.Resume)),Resume.class);
            String meg ="";
            if(! ResumeData.getEmployment_status().equals("")){
                meg = ResumeData.getEmployment_status();
            } else if(! ResumeData.getJob_type_expectations().equals("")){
                meg = ResumeData.getJob_type_expectations();
            } else if(! ResumeData.getIs_moving_ok().equals("")){
                meg = ResumeData.getIs_moving_ok();
            } else if(! ResumeData.getOther_info().equals("")){
                meg = ResumeData.getOther_info();
            }
            alertdialog(getString(R.string.error),meg,"");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //履历书名字设定
    private View.OnClickListener Listener =new View.OnClickListener() {

        public void onClick(View View) {
            String ResumeName_old = "";
            String ResumeName_new = "";
            switch (View.getId()) {
                case R.id.tv_resume_title:
                    ResumeName_old = tvresumetitle.getText().toString().replace(" ", "").replace("　", "");
                    etresumename.setText(tvresumetitle.getText().toString());
                    tlResumename.setVisibility(android.view.View.GONE);
                    tlrenameresume.setVisibility(android.view.View.VISIBLE);
                    break;
                case R.id.tv_Cancel:
                    tlResumename.setVisibility(android.view.View.VISIBLE);
                    tlrenameresume.setVisibility(android.view.View.GONE);
                    break;
                case R.id.tv_Confirmation:
                    ResumeName_new = etresumename.getText().toString().replace(" ", "").replace("　", "");
                    tvresumetitle.setText(ResumeName_new);
                    myApplication.setresume_name(tvresumetitle.getText().toString(), IresumeIdflg);
                    tlResumename.setVisibility(android.view.View.VISIBLE);
                    tlrenameresume.setVisibility(android.view.View.GONE);
                    if (!ResumeName_old.equals(ResumeName_new)) {
                        PostDate Pdata = new PostDate();
                        Pdata.setUserId(userId);
                        Pdata.setToken(token);
                        Pdata.setResumeId(resumeId);
                        Pdata.setresumeName(ResumeName_new);
                        String data = JsonChnge(AesKey, Pdata);
                        Map<String, String> param = new HashMap<String, String>();
                        param.put(getString(R.string.file),PARAM_ResumeName);
                        param.put(getString(R.string.data),data);
                        param.put(getString(R.string.name),getString(R.string.ResumeName));
                        //数据通信处理（访问服务器，并取得访问结果）
                        new GithubQueryTask().execute(param);
                    }
                    break;
            }
        }
    };

    //菜单栏按钮
    public void ll_Click(View View){
        status_sum = one_1      +
                    two_2      +
                    three_4    +
                    four_8     +
                    five_16    +
                    six_32     +
                    seven_64   +
                    eight_128  +
                    nine_256;
        myApplication.setpersonalset("3",0);
        myApplication.setpersonalset(tvresumetitle.getText().toString(),1);//履歴書名
        myApplication.setpersonalset(String.valueOf(status_sum),2);//雇用形態
        myApplication.setpersonalset(ethopeJobcategory.getText().toString(),3);//希望の職種
        myApplication.setpersonalset(etneareststation.getText().toString(),4);//最寄駅
        myApplication.setpersonalset(canmove,5);//転居可能フラグ
        myApplication.setpersonalset(etAspirationPR.getText().toString(),6);//志望動機・自己RP
        myApplication.setpersonalset(ethobbySkill.getText().toString(),7);//趣味・特技
        myApplication.setpersonalset(resumeId,8);//履歴書ID
        if(!resume_status.equals(getString(R.string.add))){
            myApplication.setpersonalset(JSONObject_resume.toString(),9);//基本情報
            myApplication.setpersonalset(JSONArray_education.toString(),10);//学歴
            myApplication.setpersonalset(JSONArray_employment.toString(),11);//職歴
            myApplication.setpersonalset(JSONArray_qualification.toString(),12);//資格
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_b_search:
                myApplication.setAct(getString(R.string.Search));
                if(myApplication.getSURL(0).equals("0")){
                    if(myApplication.getSApply(0).equals("0")){
                        if(myApplication.getSearchResults(0).equals("0")){
                            intent.setClass(ResumeActivity.this, SearchActivity.class);
                            intent.putExtra(getString(R.string.act),"");
                        } else {
                            intent.setClass(ResumeActivity.this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(ResumeActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(ResumeActivity.this, WebActivity.class);
                    Initialization();
                }
                break;
            //Myリスト画面に移動
            case R.id.ll_b_contact:
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(ResumeActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(ResumeActivity.this, ContactDialogActivity.class);
                }
                break;
            case R.id.ll_b_mylist:
                myApplication.setAct(getString(R.string.Apply));
                if(myApplication.getMURL(0).equals("0")){
                    if(myApplication.getMApply(0).equals("0")){
                        intent.setClass(ResumeActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(ResumeActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(ResumeActivity.this, WebActivity.class);
                }
                break;
            //跳转个人设定画面
            case R.id.ll_b_personalsettings:
                intent.setClass(ResumeActivity.this, ResumeActivity.class);
                break;
        }
        startActivity(intent);
    }
}
