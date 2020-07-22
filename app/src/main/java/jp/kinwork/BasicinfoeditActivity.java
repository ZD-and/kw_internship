package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableRow;
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
    private TextView ttsex;
    private TextView ttbirthday;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private TextView ttCategorymap1,ttCategorymap2,ttCategorymap3;
    private TableRow trCategorymap1,trCategorymap2,trCategorymap3;

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

    private String[] sexArry = new String[]{"未選択", "男", "女"};// 性别选择
    private String[] mCategoryMap;


    private int mYear, mMonth, mDay;
    private int selectedFruitIndex = 3;
    private int mCategoryMapNum1 = 0;
    private int mCategoryMapNum2 = 0;
    private int mCategoryMapNum3 = 0;

    private ProgressDialog dialog;
    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;

    private String TAG = "BasicinfoeditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_basicinfoedit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Initialization();
        showBirthday();
        showSex();
        showCategoryMap();
        setBasicinfo();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        dialog = new ProgressDialog(this) ;
        dialog.setMessage("通信中");
        tvback             = (TextView) findViewById(R.id.tv_back);
        tvbacktitle        = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy        = (TextView) findViewById(R.id.tv_back_dummy);
        tvbacktitle.setText(getString(R.string.profilechange));
        tvback.setText(getString(R.string.personalsettings));
        tvbackdummy.setText(getString(R.string.personalsettings));
        etfname            = findViewById(R.id.et_fname);//名
        etlname            = findViewById(R.id.et_lname);//姓
        etfname_kana       = findViewById(R.id.et_fname_kana);//メイ
        etlname_kana       = findViewById(R.id.et_lname_kana);//セイ
        ttsex              = findViewById(R.id.tt_sex);
        ttbirthday         = findViewById(R.id.tt_birthday);
        etpostalcode       = findViewById(R.id.et_postalcode);//郵便番号
        etprefectures      = findViewById(R.id.et_prefectures);//都道府県
        etmunicipality     = findViewById(R.id.et_municipality);//市区町村
        ettown             = findViewById(R.id.et_town);//町目番地
        etbu_mansion_room  = findViewById(R.id.et_bu_mansion_room);//ビル・マンション・号室
        etphone            = findViewById(R.id.et_phone);//電話番号
        trCategorymap1     = findViewById(R.id.tr_categorymap_1);//業種１
        trCategorymap2     = findViewById(R.id.tr_categorymap_2);//業種１
        trCategorymap3     = findViewById(R.id.tr_categorymap_3);//業種１

        ttCategorymap1     = findViewById(R.id.tt_categorymap_1);//業種１
        ttCategorymap2     = findViewById(R.id.tt_categorymap_2);//業種１
        ttCategorymap3     = findViewById(R.id.tt_categorymap_3);//業種１


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
        Resources res = getResources();
        mCategoryMap= res.getStringArray(R.array.categorymap);
        //backボタン
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back(v);
            }
        });

        String sfname = "";
        String slname = "";
        String sfname_kana = "";
        String slname_kana = "";
        if(mMyApplication.getpersonalset(0).equals("0")){
            sfname = mMyApplication.getfirst_name();//必須項目_名
            slname = mMyApplication.getlast_name();//必須項目_姓
            sfname_kana = mMyApplication.getfirst_name_kana();//必須項目_名（カタカナ）
            slname_kana = mMyApplication.getlast_name_kana();//必須項目_姓（カタカナ）
        } else {
            sfname = mMyApplication.getpersonalset(1);
            slname = mMyApplication.getpersonalset(2);
            sfname_kana = mMyApplication.getpersonalset(3);
            slname_kana = mMyApplication.getpersonalset(4);
        }
        etfname.setText(sfname);
        etlname.setText(slname);
        etfname_kana.setText(sfname_kana);
        etlname_kana.setText(slname_kana);
    }


    //基本情報を登録
    public void Click_Registration(){
        buttonflg = "1";
        String sfname            = etfname.getText().toString();//必須項目
        String slname            = etlname.getText().toString();//必須項目
        String sfname_kana       = etfname_kana.getText().toString();//必須項目
        String slname_kana       = etlname_kana.getText().toString();//必須項目
        String birthday          = ttbirthday.getText().toString();//必須項目
        String spostalcode       = etpostalcode.getText().toString();
        String sprefectures      = etprefectures.getText().toString();
        String smunicipality     = etmunicipality.getText().toString();
        String stown             = ettown.getText().toString();
        String sbu_mansion_room  = etbu_mansion_room.getText().toString();
        String sphone            = etphone.getText().toString();//必須項目
        String sCategory_Map     = "";//必須項目

        PostDate Pdata = new PostDate();
        UserBasic PdataUserBasic = new UserBasic();

        String meg ="";
        mErrorCode = "";
        //内容設定
        if(! basicInfo.equals("A")){
            PdataUserBasic.setId(basicInfo);
        }
        PdataUserBasic.setUser_id(userId);
        if(slname.length() ==0 ){
            meg = getString(R.string.slname);
        }
        else if(sfname.length() ==0 ){
            meg = getString(R.string.sfname);
        }
        else if(slname_kana.length() ==0 ){
            meg = getString(R.string.slname_kana);
        }
        else if(sfname_kana.length() ==0 ){
            meg = getString(R.string.sfname_kana);
        }
        else if(selectedFruitIndex == 0 || selectedFruitIndex == 3) {
            meg = getString(R.string.selectedFruitIndex);
        }
        else if(birthday.equals("") ){
            meg = getString(R.string.birthdayselect);
        }
        else if(sphone.length() ==0 ){
            meg = getString(R.string.sphone);
        }
        else if(spostalcode.length() ==0 ){
            meg = getString(R.string.spostalcode);
        }
        else if(sprefectures.length() ==0 ){
            meg = getString(R.string.sprefectures);
        }
        else if(smunicipality.length() ==0 ){
            meg = getString(R.string.smunicipality);
        }
        else if(mCategoryMapNum1 == 0 && mCategoryMapNum2 == 0 && mCategoryMapNum3 == 0 ){
            mErrorCode = String.valueOf(onClickNum);
            meg = getString(R.string.sCategoryMap);
        }

        if(!meg.equals("")){
            alertdialog(meg);
        }
        else {
            dialog.show();
            PdataUserBasic.setFirst_name(sfname);
            PdataUserBasic.setLast_name(slname);
            PdataUserBasic.setFirst_name_kana(sfname_kana);
            PdataUserBasic.setLast_name_kana(slname_kana);
            PdataUserBasic.setSex_div(String.valueOf(selectedFruitIndex));
            PdataUserBasic.setBirthday(birthday);
            PdataUserBasic.setPost_code(spostalcode);
            PdataUserBasic.setAdd_1(sprefectures);
            PdataUserBasic.setAdd_2(smunicipality);
            PdataUserBasic.setAdd_3(stown);
            PdataUserBasic.setAdd_4(sbu_mansion_room);
            PdataUserBasic.setPhone_number(sphone);
            for(int i = 1;i<22;i++){
               String Category_Map="0";
               if(i == mCategoryMapNum1 || i == mCategoryMapNum2 || i == mCategoryMapNum3){
                   Category_Map="1";
               }
               sCategory_Map = Category_Map + sCategory_Map;
            }
            PdataUserBasic.setCategory_Map(sCategory_Map);
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
                                          int startDayOfMonth,boolean hidetheDay) {
                        if (startYear > sysYear ||
                                (startYear == sysYear && startMonthOfYear +1  > sysMonth) ||
                                (startYear == sysYear && startMonthOfYear +1 == sysMonth && startDayOfMonth >= sysDay)
                        ) {
                            ttbirthday.setText("");
                            alertdialog(getString(R.string.alertdialog));
                        } else {
                            mYear = startYear;
                            mMonth = startMonthOfYear + 1;
                            mDay = startDayOfMonth;
                            String Startdate = mYear + "-" + mMonth + "-" + mDay;
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            ParsePosition pos = new ParsePosition(0);
                            Date strtodate = formatter.parse(Startdate, pos);
                            Startdate = formatter.format(strtodate);
                            ttbirthday.setText(Startdate);
                        }
                    }
                }, mYear, mMonth - 1, mDay,false).show();
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

    //業種選択 start
    int onClickNum= 0;
    private void showCategoryMap() {
        //监听事件
        ttCategorymap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum = 1;
                showCategoryMapChooseDialog(mCategoryMapNum1);
            }
        });
        ttCategorymap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum = 2;
                showCategoryMapChooseDialog(mCategoryMapNum2);
            }
        });
        ttCategorymap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNum = 3;
                showCategoryMapChooseDialog(mCategoryMapNum3);
            }
        });

    }
    private void showCategoryMapChooseDialog(int categoryMapNum ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(mCategoryMap, categoryMapNum, new DialogInterface.OnClickListener() {// 2默认的选中
            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                Log.d("CountryData", ": " +which);
                String textCategory = mCategoryMap[which];
                if(which == 0){
                    textCategory = "";
                }
                if(onClickNum ==1){
                    mCategoryMapNum1 = which;
                    ttCategorymap1.setText(textCategory);
                }
                if(onClickNum ==2){
                    mCategoryMapNum2 = which;
                    ttCategorymap2.setText(textCategory);
                }
                if(onClickNum ==3){
                    mCategoryMapNum3 = which;
                    ttCategorymap3.setText(textCategory);
                }
                changShowCategoryMap();
                checkCategoryMap();
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();// 让弹出框显示
    }

    private void changShowCategoryMap(){
        if(mCategoryMapNum1 == 0 && mCategoryMapNum2 == 0 && mCategoryMapNum3 == 0){
            onClickNum = 0;
            trCategorymap2.setVisibility(View.GONE);
            trCategorymap3.setVisibility(View.GONE);
        }
        else if(mCategoryMapNum1 > 0 && mCategoryMapNum2 == 0  && mCategoryMapNum3 == 0){
            trCategorymap2.setVisibility(View.VISIBLE);
        }
        else if(mCategoryMapNum1 > 0 && mCategoryMapNum2 > 0  && mCategoryMapNum3 == 0){
            trCategorymap3.setVisibility(View.VISIBLE);
        }
        else if(mCategoryMapNum1 > 0 && mCategoryMapNum2 == 0 && mCategoryMapNum3 > 0){
            ttCategorymap2.setText(ttCategorymap3.getText());
            ttCategorymap3.setText("");
            mCategoryMapNum2 = mCategoryMapNum3;
            mCategoryMapNum3 = 0;
            trCategorymap2.setVisibility(View.VISIBLE);
            trCategorymap3.setVisibility(View.VISIBLE);
        }
        else if(mCategoryMapNum1 == 0 && mCategoryMapNum2 > 0  && mCategoryMapNum3 > 0){
            ttCategorymap1.setText(ttCategorymap2.getText());
            ttCategorymap2.setText(ttCategorymap3.getText());
            ttCategorymap3.setText("");
            mCategoryMapNum1 = mCategoryMapNum2;
            mCategoryMapNum2 = mCategoryMapNum3;
            mCategoryMapNum3 = 0;
            trCategorymap2.setVisibility(View.VISIBLE);
            trCategorymap3.setVisibility(View.VISIBLE);
        }
        else if(mCategoryMapNum1 == 0 && mCategoryMapNum2 > 0  && mCategoryMapNum3 == 0){
            ttCategorymap1.setText(ttCategorymap2.getText());
            ttCategorymap2.setText(ttCategorymap3.getText());
            mCategoryMapNum1 = mCategoryMapNum2;
            mCategoryMapNum2 = mCategoryMapNum3;
            trCategorymap2.setVisibility(View.VISIBLE);
            trCategorymap3.setVisibility(View.GONE);
        }
        if(mCategoryMapNum1 == 0 && mCategoryMapNum2 == 0 && mCategoryMapNum3 > 0){
            ttCategorymap1.setText(ttCategorymap3.getText());
            ttCategorymap2.setText("");
            ttCategorymap3.setText("");
            mCategoryMapNum1 = mCategoryMapNum3;
            mCategoryMapNum3 = 0;
            trCategorymap2.setVisibility(View.VISIBLE);
            trCategorymap3.setVisibility(View.GONE);
        }
    }

    private void checkCategoryMap(){
        if((mCategoryMapNum1 >0 && mCategoryMapNum1 == mCategoryMapNum2) || (mCategoryMapNum1 >0 && mCategoryMapNum1 == mCategoryMapNum3) || (mCategoryMapNum2 >0 && mCategoryMapNum2 == mCategoryMapNum3)){
            mErrorCode = String.valueOf(onClickNum);
            alertdialog("すてに選択されたの業種です、\n他の業種を選択してください。");
        }
    }

    //業種選択 end

    //既に存在しますの情報をセット
    private void setBasicinfo(){
        String ssex              = mMyApplication.getsex_div();//必須項目_性別
        String sbirthday         = mMyApplication.getbirthday();//必須項目_生年月日
        String spostalcode       = mMyApplication.getpost_code();//郵便番号
        String sprefectures      = mMyApplication.getadd_1();//都道府県
        String smunicipality     = mMyApplication.getadd_2();//市区町村
        String stown             = mMyApplication.getadd_3();//町目番地
        String sbu_mansion_room  = mMyApplication.getadd_4();
        String sphone            = mMyApplication.getphone_number();//必須項目_電話番号
        String sCategoryMap      = mMyApplication.getCategoryMap();//必須項目_電話番号

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
        int num = 0;
        Log.d(TAG, "setBasicinfo sCategoryMap: " + sCategoryMap);
        for(int i= sCategoryMap.length();i > 0 ; i --){
            num ++;
            String strCategoryMapNum = String.valueOf(sCategoryMap.charAt(i-1));
            Log.d(TAG, "setBasicinfo sCategoryMap strCategoryMapNum: " + strCategoryMapNum);
            Log.d(TAG, "setBasicinfo sCategoryMap num: " + num);
            Log.d(TAG, "setBasicinfo sCategoryMap mCategoryMap[num]: " + mCategoryMap[num]);
            Log.d(TAG, "setBasicinfo sCategoryMap mCategoryMapNum1: " + mCategoryMapNum1);
            Log.d(TAG, "setBasicinfo sCategoryMap mCategoryMapNum2: " + mCategoryMapNum2);
            Log.d(TAG, "setBasicinfo sCategoryMap mCategoryMapNum3: " + mCategoryMapNum3);
            if(strCategoryMapNum.equals("1")){
                if(mCategoryMapNum1 == 0){
                    mCategoryMapNum1 = num;
                    ttCategorymap1.setText(mCategoryMap[num]);
                    trCategorymap2.setVisibility(View.VISIBLE);
                }
                else if(mCategoryMapNum2 == 0){
                    mCategoryMapNum2 = num;
                    ttCategorymap2.setText(mCategoryMap[num]);
                    trCategorymap3.setVisibility(View.VISIBLE);
                }
                else if(mCategoryMapNum3 == 0){
                    mCategoryMapNum3 = num;
                    ttCategorymap3.setText(mCategoryMap[num]);
                }
            }

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
        param.put(getString(R.string.file),PARAM_Address);
        param.put(getString(R.string.data),data);
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
            dialog.dismiss();
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d("***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = obj.getString(getString(R.string.message));
                    mErrorCode = obj.getString(getString(R.string.errorCode));
                    if(processResult == true) {
                        if(buttonflg.equals("0")){
                            String returnData = obj.getString(getString(R.string.returnData));
                            decryptchange(returnData);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(BasicinfoeditActivity.this, PersonalSetActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        if(mErrorCode.equals("100")){
                            mTitlt = "";
                            message = "他の端末から既にログインしています。もう一度ログインしてください。";
                            alertdialog(message);
                        } else {
                            showErrors(obj.getString(getString(R.string.showErrors)));
                        }

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
        try {
            JSONObject obj = new JSONObject(datas);
            etprefectures.setText(obj.getString(getString(R.string.add_1)));
            etmunicipality.setText(obj.getString(getString(R.string.add_2)));
            ettown.setText(obj.getString(getString(R.string.add_3)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //エラーメッセージを設定
    private void showErrors(String error){
        try {
            JSONObject obj = new JSONObject(error);
            if(obj.has(getString(R.string.first_name))){
                JSONArray ja = obj.getJSONArray(getString(R.string.first_name));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.last_name))){
                JSONArray ja = obj.getJSONArray(getString(R.string.last_name));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.first_name_kana))){
                JSONArray ja = obj.getJSONArray(getString(R.string.first_name_kana));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.last_name_kana))){
                JSONArray ja = obj.getJSONArray(getString(R.string.last_name_kana));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.post_code))){
                JSONArray ja = obj.getJSONArray(getString(R.string.post_code));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.add_1))){
                JSONArray ja = obj.getJSONArray(getString(R.string.add_1));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.add_2))){
                JSONArray ja = obj.getJSONArray(getString(R.string.add_2));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.add_3))){
                JSONArray ja = obj.getJSONArray(getString(R.string.add_3));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.add_4))){
                JSONArray ja = obj.getJSONArray(getString(R.string.add_4));
                alertdialog(ja.getString(0));
            }
            else if(obj.has(getString(R.string.phone_number))){
                JSONArray ja = obj.getJSONArray(getString(R.string.phone_number));
                alertdialog(ja.getString(0));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String mTitlt = "エラー";
    private String mErrorCode = "";
    //通信结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mTitlt).setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mErrorCode.equals("100")){
                    mPreferenceUtils.clear();
                    mMyApplication.clear();
                    Intent intentClose = new Intent();
                    intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mMyApplication.setAct(getString(R.string.Search));
                    intentClose.setClass(BasicinfoeditActivity.this, SearchActivity.class);
                    intentClose.putExtra("act", "");
                    startActivity(intentClose);
                }
                //确定按钮的点击事件
                else if(!mErrorCode.equals("")){
                    switch (onClickNum){
                        case 1:
                            ttCategorymap1.setText("");
                            showCategoryMapChooseDialog(mCategoryMapNum1);
                            break;
                        case 2:
                            ttCategorymap2.setText("");
                            showCategoryMapChooseDialog(mCategoryMapNum2);
                            break;
                        case 3:
                            ttCategorymap3.setText("");
                            showCategoryMapChooseDialog(mCategoryMapNum3);
                            break;
                    }
                }
            }
        }).show();
    }

    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

}
