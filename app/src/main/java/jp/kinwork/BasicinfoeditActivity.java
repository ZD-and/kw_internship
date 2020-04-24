package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
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
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.ClassDdl.UserBasic;
import jp.kinwork.Common.DatePickerDialog;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class BasicinfoeditActivity extends AppCompatActivity {
    final static String PARAM_personaInfo = "/MypagesMobile/personalInfo";
    final static String PARAM_personaInfoUpdate = "/MypagesMobile/personalInfoUpdate";
    final static String PARAM_Address = "/CommonMobile/getAddressByPostcode";
    private ScrollView slBasicinfo;
    private ImageView ivpersonalsettings;
    private TextView tvpersonalsettings;
    private TextView tvname;
    private TextView tvphonetic;
    private TextView tvsex;
    private TextView tvbirthday;
    private TextView tvcountry;
    private TextView tvphone;
    private TextView tvpostalcode;
    private TextView tvprefectures;
    private TextView tvmunicipality;
    private TextView tvtown;
    private TextView tvbumansionroom;
    private TextView ttsex;
    private TextView ttbirthday;
    private TextView ttcountry;
    private TextView tvdummy;
    private TextView tvkanadummy;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;

    private EditText etfname;
    private EditText etlname;
    private EditText etfname_kana;
    private EditText etlname_kana;
    private EditText etpostalcode;
    private EditText etprefectures;
    private EditText etmunicipality;
    private EditText ettown;
    private EditText etbu_mansion_room;
    private EditText etphone;

    private Button bupostalcode;
    private Button buRegistration;

    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String basicInfo;
    private String buttonflg;
    private String BasicinfoData;
    private String Act;
    private String resume_status;
    private String resume_Num;

    private String[] sexArry = new String[]{ "未選択","男","女"};// 性别选择
    private String[] CountryData;

    private int mYear, mMonth, mDay;
    final int DATE_DIALOG = 1;
    private int selectedFruitIndex = 3;
    private int CountryDataNum = 0;

    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_basicinfoedit);
        Intent intent = getIntent();
        Act = intent.getStringExtra("Act");
        Initialization();
        showBirthday();
        showSex();
        showCountry();
        setBasicinfo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

    //初期化
    public void Initialization(){
        slBasicinfo        = (ScrollView) findViewById(R.id.sl_Basicinfo);
        tvback             = (TextView) findViewById(R.id.tv_back);
        tvbacktitle        = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy        = (TextView) findViewById(R.id.tv_back_dummy);
        tvbacktitle.setText("個人情報編集");

//        ViewGroup.LayoutParams params = slBasicinfo.getLayoutParams();
//        ViewGroup.MarginLayoutParams marginParams = null;
//        marginParams = (ViewGroup.MarginLayoutParams) params;
//        View include_title = (View) findViewById(R.id.include_b_title);

//        if(Act.equals("person")){
        tvback.setText("個人設定");
        tvbackdummy.setText("個人設定");
//        }
//        else {
//            tvback.setText("履歴書");
//            tvbackdummy.setText("履歴書");
//            include_title.setVisibility(View.GONE);
//            int dptopx= dp2px(this, 50);
//            marginParams.setMargins(0, dptopx, 0, 0);
//            slBasicinfo.setLayoutParams(marginParams);
//        }
        ivpersonalsettings = (ImageView) findViewById(R.id.iv_b_personalsettings);
        tvpersonalsettings = (TextView) findViewById(R.id.tv_b_personalsettings);
        ivpersonalsettings.setImageResource(R.mipmap.blue_personalsettings);
        tvpersonalsettings.setTextColor(Color.parseColor("#5EACE2"));
        tvdummy            = (TextView) findViewById(R.id.tv_dummy);
        tvkanadummy        = (TextView) findViewById(R.id.tv_kana_dummy);

        tvname             = (TextView) findViewById(R.id.tv_name);
        tvphonetic         = (TextView) findViewById(R.id.tv_phonetic);
        tvsex              = (TextView) findViewById(R.id.tv_sex);
        tvbirthday         = (TextView) findViewById(R.id.tv_birthday);
        tvcountry          = (TextView) findViewById(R.id.tv_country);
        tvphone            = (TextView) findViewById(R.id.tv_phone);
        tvpostalcode      = (TextView) findViewById(R.id.tv_postalcode);
        tvprefectures     = (TextView) findViewById(R.id.tv_prefectures);
        tvmunicipality    = (TextView) findViewById(R.id.tv_municipality);
        tvtown            = (TextView) findViewById(R.id.tv_town);
        tvbumansionroom = (TextView) findViewById(R.id.tv_bu_mansion_room);
        SetStyle(tvname,"（必須）","0");
        SetStyle(tvphonetic,"（必須）","0");
        SetStyle(tvsex,"（必須）","0");
        SetStyle(tvbirthday,"（必須）","0");
        SetStyle(tvcountry,"（必須）","0");
        SetStyle(tvphone,"（必須）","0");
        SetStyle(tvpostalcode,"（任意）","1");
        SetStyle(tvprefectures,"（必須）","0");
        SetStyle(tvmunicipality,"（必須）","0");
        SetStyle(tvtown,"（任意）","1");
        SetStyle(tvbumansionroom,"（任意）","1");

        etfname            = (EditText) findViewById(R.id.et_fname);//名
        etlname            = (EditText) findViewById(R.id.et_lname);//姓
        etfname_kana       = (EditText) findViewById(R.id.et_fname_kana);//メイ
        etlname_kana       = (EditText) findViewById(R.id.et_lname_kana);//セイ
        ttsex              = (TextView) findViewById(R.id.tt_sex);
        ttbirthday         = (TextView) findViewById(R.id.tt_birthday);
        ttcountry          = (TextView) findViewById(R.id.tt_country);//国籍
        etpostalcode       = (EditText) findViewById(R.id.et_postalcode);//郵便番号
        etprefectures      = (EditText) findViewById(R.id.et_prefectures);//都道府県
        etmunicipality     = (EditText) findViewById(R.id.et_municipality);//市区町村
        ettown             = (EditText) findViewById(R.id.et_town);//町目番地
        etbu_mansion_room  = (EditText) findViewById(R.id.et_bu_mansion_room);//ビル・マンション・号室
        etphone            = (EditText) findViewById(R.id.et_phone);//電話番号
        //住所検索
        bupostalcode       = (Button) findViewById(R.id.bu_postalcode);
        bupostalcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResidence();
            }
        });
        //情報登録
        buRegistration     = (Button) findViewById(R.id.bu_Registration);
        buRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Registration();
            }
        });
        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(BasicinfoeditActivity.this);
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userId = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        basicInfo = mPreferenceUtils.getbasicInfoID();

        tvdummy.post(new Runnable() {
            @Override
            public void run() {
                int Width = tvdummy.getMeasuredWidth();
                String sfname = "";
                String slname = "";
                if(mMyApplication.getpersonalset(0).equals("0")){
                    sfname = mMyApplication.getfirst_name();//必須項目_名
                    slname = mMyApplication.getlast_name();//必須項目_姓
                } else {
                    sfname = mMyApplication.getpersonalset(1);
                    slname = mMyApplication.getpersonalset(2);
                }
                setHW(etfname,Width,sfname);
                setHW(etlname,Width,slname);
            }
        });

        tvkanadummy.post(new Runnable() {
            @Override
            public void run() {
                int Width = tvkanadummy.getMeasuredWidth();
                String sfname_kana = "";
                String slname_kana = "";
                if(mMyApplication.getpersonalset(0).equals("0")){
                    sfname_kana = mMyApplication.getfirst_name_kana();//必須項目_名（カタカナ）
                    slname_kana = mMyApplication.getlast_name_kana();//必須項目_姓（カタカナ）
                } else {
                    sfname_kana = mMyApplication.getpersonalset(3);
                    slname_kana = mMyApplication.getpersonalset(4);
                }
                setHW(etfname_kana,Width,sfname_kana);
                setHW(etlname_kana,Width,slname_kana);
            }
        });
        Resources res = getResources();
        CountryData= res.getStringArray(R.array.CountryData);
        resume_status = mMyApplication.getresume_status();
        resume_Num = mMyApplication.getResumeId();
    }

    public void setHW(EditText name,int w,String data){
        int Width = w / 2;
        ViewGroup.LayoutParams lp = name.getLayoutParams();
        lp.width = Width;
        name.setLayoutParams(lp);
        name.setText(data);
    }
    //基本情報を登録
    public void Click_Registration(){
        buttonflg = "1";
        String sfname            = etfname.getText().toString();//必須項目
        String slname            = etlname.getText().toString();//必須項目
        String sfname_kana       = etfname_kana.getText().toString();//必須項目
        String slname_kana       = etlname_kana.getText().toString();//必須項目
        String birthday          = ttbirthday.getText().toString();//必須項目
        String scountry          = ttcountry.getText().toString();//必須項目
        String spostalcode       = etpostalcode.getText().toString();
        String sprefectures      = etprefectures.getText().toString();
        String smunicipality     = etmunicipality.getText().toString();
        String stown             = ettown.getText().toString();
        String sbu_mansion_room  = etbu_mansion_room.getText().toString();
        String sphone            = etphone.getText().toString();//必須項目

        PostDate Pdata = new PostDate();
        UserBasic PdataUserBasic = new UserBasic();
        //内容設定
        if(! basicInfo.equals("A")){
            PdataUserBasic.setId(basicInfo);
        }
        PdataUserBasic.setUser_id(userId);
        if(slname.length() ==0 ){
            alertdialog("姓を入力してください。");
        }
        else if(sfname.length() ==0 ){
            alertdialog("名を入力してください。");
        }
        else if(sfname_kana.length() ==0 ){
            alertdialog("姓（カタカナ）を入力してください。");
        }
        else if(slname_kana.length() ==0 ){
            alertdialog("名（カタカナ）を入力してください。");
        }
        else if(selectedFruitIndex == 0 || selectedFruitIndex == 3) {
            alertdialog("性別を選択してください。");
        }
        else if(birthday.equals("") ){
            alertdialog("生年月日を選択してください。");
        }
        else if(scountry.length() ==0 ){
            alertdialog("国籍を入力してください。");
        }
        else if(spostalcode.length() ==0 ){
            alertdialog("郵便番号を入力してください。");
        }
        else if(sprefectures.length() ==0 ){
            alertdialog("都道府県を入力してください。");
        }
        else if(smunicipality.length() ==0 ){
            alertdialog("市区町村を入力してください。");
        }
        else if(stown.length() ==0 ){
            alertdialog("町目番地を入力してください。");
        }
        else if(sphone.length() ==0 ){
            alertdialog("電話番号を入力してください。");
        }else {
            PdataUserBasic.setFirst_name(sfname);
            PdataUserBasic.setLast_name(slname);
            PdataUserBasic.setFirst_name_kana(sfname_kana);
            PdataUserBasic.setLast_name_kana(slname_kana);
            if(selectedFruitIndex == 1) {
                PdataUserBasic.setSex_div("1");
            } else if (selectedFruitIndex == 2){
                PdataUserBasic.setSex_div("2");
            }
            PdataUserBasic.setBirthday(birthday);
            PdataUserBasic.setCountry(scountry);
            PdataUserBasic.setPost_code(spostalcode);
            PdataUserBasic.setAdd_1(sprefectures);
            PdataUserBasic.setAdd_2(smunicipality);
            PdataUserBasic.setAdd_3(stown);
            PdataUserBasic.setAdd_4(sbu_mansion_room);
            PdataUserBasic.setPhone_number(sphone);

            mMyApplication.setfirst_name(sfname);
            mMyApplication.setlast_name(slname);
            mMyApplication.setfirst_name_kana(sfname_kana);
            mMyApplication.setlast_name_kana(slname_kana);
            mMyApplication.setsex_div(String.valueOf(selectedFruitIndex));
            mMyApplication.setbirthday(birthday);
            mMyApplication.setcountry(scountry);
            mMyApplication.setpost_code(spostalcode);
            mMyApplication.setadd_1(sprefectures);
            mMyApplication.setadd_2(smunicipality);
            mMyApplication.setadd_3(stown);
            mMyApplication.setadd_4(sbu_mansion_room);
            mMyApplication.setphone_number(sphone);

            Pdata.setUserId(userId);
            Pdata.setToken(token);
            Pdata.setUserBasic(PdataUserBasic);
            Gson mGson = new Gson();
            BasicinfoData = mGson.toJson(Pdata,PostDate.class);
            Log.d("***sdPdata***", BasicinfoData);
            //Json格式转换并且加密
            String data = JsonChnge(AesKey,BasicinfoData);
            Log.d("***AES加密data***", data);
            Map<String,String> param = new HashMap<String, String>();
            param.put("file",PARAM_personaInfoUpdate);
            param.put("data",data);
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
        }

    }
    //返回按钮
    public void Click_back(View View){
        mMyApplication.setpersonalset("0",0);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(BasicinfoeditActivity.this, PersonalSetActivity.class);
        startActivity(intent);
    }
    ////生年月日選択 start
    private void showBirthday() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        //监听事件
        ttbirthday.setOnClickListener(new View.OnClickListener() {

            Calendar SYScalendar = Calendar.getInstance();
            int sysYear = SYScalendar.get(Calendar.YEAR);
            int sysMonth = SYScalendar.get(Calendar.MONTH) + 1;
            int sysDay = SYScalendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                new DatePickerDialog(BasicinfoeditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth) {
                        if (startYear > sysYear ||
                                (startYear == sysYear && startMonthOfYear +1  > sysMonth) ||
                                (startYear == sysYear && startMonthOfYear +1 == sysMonth && startDayOfMonth >= sysDay)
                        ) {
                            ttbirthday.setText("");
                            alertdialog("未来の年月を選択できません。");
                        } else {
                            mYear = startYear;
                            mMonth = startMonthOfYear + 1;
                            mDay = startDayOfMonth;
                            String Startdate = String.valueOf(startYear) + "-" + String.valueOf(startMonthOfYear + 1) + "-" + String.valueOf(startDayOfMonth);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            ParsePosition pos = new ParsePosition(0);
                            Date strtodate = formatter.parse(Startdate, pos);
                            Startdate = formatter.format(strtodate);
                            ttbirthday.setText(Startdate);
                        }
                    }
                }, mYear, mMonth - 1, mDay).show();
            }
        });
    }
    ////生年月日選択 end
    //性別選択 start
    private void showSex() {
        //监听事件
        ttsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSexChooseDialog();
            }
        })
        ;

    }
    private void showSexChooseDialog() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(sexArry, selectedFruitIndex, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                selectedFruitIndex = which;
                Log.d("selectedFruitIndex", ": " +which);
                ttsex.setText(sexArry[which]);
                mMyApplication.setsex_div(String.valueOf(which));
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder3.show();// 让弹出框显示
    }
    //性別選択 end
    //国籍選択 start
    private void showCountry() {
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        //监听事件
        ttcountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryChooseDialog();
            }
        })
        ;

    }
    private void showCountryChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(CountryData, CountryDataNum, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                CountryDataNum = which;
                Log.d("CountryData", ": " +which);
                ttcountry.setText(CountryData[which]);
                mMyApplication.setcountry(String.valueOf(which));
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();// 让弹出框显示
    }
    //国籍選択 end

    //既に存在しますの情報をセット
    private void setBasicinfo(){
        String ssex              = "";
        String sbirthday         = "";
        String scountry          = "";
        String spostalcode       = "";
        String sprefectures      = "";
        String smunicipality     = "";
        String stown             = "";
        String sbu_mansion_room  = "";
        String sphone            = "";

        if(!mMyApplication.getpersonalset(0).equals("0")){
            sbirthday         = mMyApplication.getpersonalset(5);//必須項目_生年月日
            scountry          = mMyApplication.getpersonalset(6);//必須項目_国籍
            ssex              = mMyApplication.getpersonalset(7);//必須項目_性別
            spostalcode       = mMyApplication.getpersonalset(8);//郵便番号
            sprefectures      = mMyApplication.getpersonalset(9);//都道府県
            smunicipality     = mMyApplication.getpersonalset(10);//市区町村
            stown             = mMyApplication.getpersonalset(11);//町目番地
            sbu_mansion_room  = mMyApplication.getpersonalset(12);
            sphone            = mMyApplication.getpersonalset(13);//必須項目_電話番号

        } else {
            ssex              = mMyApplication.getsex_div();//必須項目_性別
            sbirthday         = mMyApplication.getbirthday();//必須項目_生年月日
            scountry          = mMyApplication.getcountry();//必須項目_国籍
            spostalcode       = mMyApplication.getpost_code();//郵便番号
            sprefectures      = mMyApplication.getadd_1();//都道府県
            smunicipality     = mMyApplication.getadd_2();//市区町村
            stown             = mMyApplication.getadd_3();//町目番地
            sbu_mansion_room  = mMyApplication.getadd_4();
            sphone            = mMyApplication.getphone_number();//必須項目_電話番号
        }

        if(ssex.length() > 0){
            selectedFruitIndex = Integer.parseInt(ssex);
            ttsex.setText(sexArry[selectedFruitIndex]);
        }
        if(sbirthday.length() > 0){
            ttbirthday.setText(sbirthday);
            mYear = Integer.parseInt(sbirthday.substring(0,4));
            mMonth = Integer.parseInt(sbirthday.substring(5,7));
            mDay = Integer.parseInt(sbirthday.substring(8,10));
        }
        if(scountry.length() > 0){
            ttcountry.setText(scountry);
            for(int i= 0;i < CountryData.length; i ++){
                if(scountry.equals(CountryData[i])){
                    CountryDataNum = i;
                    break;
                }
            }
        }
        if(spostalcode.length() > 0){
            etpostalcode.setText(spostalcode);
        }
        if(sprefectures.length() > 0){
            etprefectures.setText(sprefectures);
        }
        if(smunicipality.length() > 0){
            etmunicipality.setText(smunicipality);
        }
        if(stown.length() > 0){
            ettown.setText(stown);
        }
        if(sbu_mansion_room.length() > 0){
            etbu_mansion_room.setText(sbu_mansion_room);
        }
        if(sphone.length() > 0){
            etphone.setText(sphone);
        }
    }

    //POSTより住所を取得
    public void getResidence(){
        buttonflg = "0";
        String postcode = etpostalcode.getText().toString();
        PostDate Pdata = new PostDate();
        Pdata.setPostcode(postcode);
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Pdata,PostDate.class);
        //Json格式转换并且加密
        String data = JsonChnge(AesKey,sdPdata);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_Address);
        param.put("data",data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,String data) {
        Log.d("***AES加密sdPdata***", data);
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
            String file = map.get("file");
            String data = map.get("data");
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
                    boolean processResult = obj.getBoolean("processResult");
                    String message = obj.getString("message");
                    if(processResult == true) {
                        String returnData = obj.getString("returnData");
                        decryptchange(returnData);
                    } else {
                        showErrors(obj.getString("fieldErrors"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    //解密，并且保存得到的数据
    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data,AesKey);
        Log.d("***datas***", datas);
        if(buttonflg.equals("0")){
            try {
                JSONObject obj = new JSONObject(datas);
                etprefectures.setText(obj.getString("add_1"));
                etmunicipality.setText(obj.getString("add_2"));
                ettown.setText(obj.getString("add_3"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent();
//            if(Act.equals("person")){
            intent.setClass(BasicinfoeditActivity.this, PersonalSetActivity.class);
//            } else {
//                intent.setClass(BasicinfoeditActivity.this, ResumeActivity.class);
//                mMyApplication.setresume_status(resume_status);
//                mMyApplication.setResumeId(resume_Num);
//            }
            startActivity(intent);
        }

    }

    //エラーメッセージを設定
    private void showErrors(String error){
        try {
            JSONObject obj = new JSONObject(error);
            if(obj.has("first_name")){
                JSONArray ja = obj.getJSONArray("first_name");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("last_name")){
                JSONArray ja = obj.getJSONArray("last_name");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("first_name_kana")){
                JSONArray ja = obj.getJSONArray("first_name_kana");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("last_name_kana")){
                JSONArray ja = obj.getJSONArray("last_name_kana");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("post_code")){
                JSONArray ja = obj.getJSONArray("post_code");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("add_1")){
                JSONArray ja = obj.getJSONArray("add_1");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("add_2")){
                JSONArray ja = obj.getJSONArray("add_2");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("add_3")){
                JSONArray ja = obj.getJSONArray("add_3");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("add_4")){
                JSONArray ja = obj.getJSONArray("add_4");
                alertdialog(ja.getString(0));
            }
            else if(obj.has("phone_number")){
                JSONArray ja = obj.getJSONArray("phone_number");
                alertdialog(ja.getString(0));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //通信结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("エラー").setMessage(meg).setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }

    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    //特定字体设置
    private void SetStyle(TextView View,String A,String flg){
        String String =  View.getText().toString() + A;
        int length1 = String.indexOf(A);
        int length2 = length1 + A.length();
        int SizeSpan= dp2px(this, 10);
        SpannableStringBuilder style_name=new SpannableStringBuilder(String);
        if(flg.equals("0")){
            style_name.setSpan(new ForegroundColorSpan(Color.RED),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }else {
            style_name.setSpan(new ForegroundColorSpan(Color.BLACK),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        style_name.setSpan(new AbsoluteSizeSpan(SizeSpan),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        View.setText(style_name);
    }

    //菜单栏按钮
    public void ll_Click(View View){
        mMyApplication.setpersonalset("1",0);//姓
        mMyApplication.setpersonalset(etfname.getText().toString(),1);//姓
        mMyApplication.setpersonalset(etlname.getText().toString(),2);//名
        mMyApplication.setpersonalset(etfname_kana.getText().toString(),3);//セイ
        mMyApplication.setpersonalset(etlname_kana.getText().toString(),4);//メイ
        mMyApplication.setpersonalset(ttbirthday.getText().toString(),5);//生年月日
        mMyApplication.setpersonalset(ttcountry.getText().toString(),6);//国籍
        mMyApplication.setpersonalset(String.valueOf(selectedFruitIndex),7);//性別
        mMyApplication.setpersonalset(etpostalcode.getText().toString(),8);//郵便番号
        mMyApplication.setpersonalset(etprefectures.getText().toString(),9);//都道府県
        mMyApplication.setpersonalset(etmunicipality.getText().toString(),10);//市区町村
        mMyApplication.setpersonalset(ettown.getText().toString(),11);//町目番地
        mMyApplication.setpersonalset(etbu_mansion_room.getText().toString(),12);//ビル・マンション・号室
        mMyApplication.setpersonalset(etphone.getText().toString(),13);//電話
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_b_search:
                mMyApplication.setAct("Search");
                if(mMyApplication.getSURL(0).equals("0")){
                    if(mMyApplication.getSApply(0).equals("0")){
                        if(mMyApplication.getSearchResults(0).equals("0")){
                            intent.setClass(BasicinfoeditActivity.this, SearchActivity.class);
                            intent.putExtra("act","");
                        } else {
                            intent.setClass(BasicinfoeditActivity.this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(BasicinfoeditActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(BasicinfoeditActivity.this, WebActivity.class);
                    Initialization();
                }
                break;
            //Myリスト画面に移動
            case R.id.ll_b_contact:
                if(mMyApplication.getContactDialog(0).equals("0")){
                    intent.setClass(BasicinfoeditActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(BasicinfoeditActivity.this, ContactDialogActivity.class);
                }
                break;
            case R.id.ll_b_mylist:
                Log.d("getMURL", mMyApplication.getMURL(0));
                Log.d("getMURL", mMyApplication.getMApply(0));
                mMyApplication.setAct("Apply");
                if(mMyApplication.getMURL(0).equals("0")){
                    if(mMyApplication.getMApply(0).equals("0")){
                        intent.setClass(BasicinfoeditActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(BasicinfoeditActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(BasicinfoeditActivity.this, WebActivity.class);
                }
                break;
            //跳转个人设定画面
            case R.id.ll_b_personalsettings:
                intent.setClass(BasicinfoeditActivity.this, BasicinfoeditActivity.class);
                break;
        }
        startActivity(intent);
    }
}
