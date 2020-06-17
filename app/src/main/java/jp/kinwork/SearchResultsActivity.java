package jp.kinwork;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener {

    final static String PARAM_index = "/SearchesMobile/index";
    final static String PARAM_jobDetail = "/JobInfosMobile/jobDetail";
    final static String PARAM_likeJob = "/JobInfosMobile/likeJob";
    final static String PARAM_deletelikeJob = "/MypagesMobile/deleteLikeJobByUrl";
    final static String PARAM_KeywordHint = "/SearchesMobile/getKeywordHint";
    final static String PARAM_AddressHint = "/SearchesMobile/getAddressHint";
    private String deviceId;
    private String AesKey;
    private String keyword;
    private String address;
    private String employmentStatus = "";
    private String yearlyIncome;
    private String page;
    private String urlFlg = "";
    private String LoginFlg = "";
    private String userid;
    private String token;
    private String Act = "";

    private int leftsearch = 0;
    private int theleft = 0;
    private int left = 0;
    private int centre = 0;
    private int right = 0;
    private int theright = 0;
    private int rightsearch = 0;
    private int OfPage = 0;
    private int buPage = 0;
    private int statusIndex = 0;

    private TextView tltrbuBefore;
    private TextView tltrbuNext;
    private TextView tltrbuleftsearch;
    private TextView tltrburightsearch;
    private TextView tltrbutheleft;
    private TextView tltrbuleft;
    private TextView tltrbucentre;
    private TextView tltrburight;
    private TextView tltrbutheright;

    private TextView tltrbuBeforeBottom;
    private TextView tltrbuNextBottom;
    private TextView tltrbuleftsearchBottom;
    private TextView tltrburightsearchBottom;
    private TextView tltrbutheleftBottom;
    private TextView tltrbuleftBottom;
    private TextView tltrbucentreBottom;
    private TextView tltrburightBottom;
    private TextView tltrbutherightBottom;

    private TextView tltrtvtitle;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private TextView tvsearchreset;

    private EditText etkeywordreset;
    private EditText etworklocationreset;
    private ImageView ivclearkeywordreset;
    private ImageView ivclearworklocationreset;

    private TableLayout tlSearchcontents;
    private TableLayout tlSearchcontentsBottom;
    private TableLayout tlresults;
    private TableLayout tlsearchreset;
    private TableLayout tladvancset;

    private ScrollView svsearch;
    private LinearLayout llsearcgresults;
    private MyApplication myApplication;
    private jp.kinwork.Common.PreferenceUtils PreferenceUtils;
    private LinkedList<JSONObject> listjobinfo;
    private LinkedList<TableLayout> listIBTN_info;
    private LinkedList<ImageView> listIBTN_job;
    private LinkedList<String> listlikejobflg;
    private JSONArray jobList;
    private JSONObject objjobinfo;
    private ImageView IBNlikejob;
    private ProgressDialog dialog;
    private Intent Intent;

    private TableRow trkeywordreset;
    private TableRow trworklocationreset;
    private TextView tvkeywordreset;
    private boolean blkeywordreset = false;
    private boolean blworklocationreset = false;

    private ArrayAdapter<String> adapterreset;

    private ListView lvgethintreset;

    private int keywordresetTop = 0;
    private int worklocationresetTop = 0;
    private int Width = 0;
    private String etnamereset = "";;

    String TAG = "SearchResultsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        CreateNew();
        Intent = getIntent();
        Act = Intent.getStringExtra(getString(R.string.Act));
        Initialization();
        initData();
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

    //数组初始化
    public void CreateNew(){
        listIBTN_info = new LinkedList<TableLayout>();
        listIBTN_job = new LinkedList<ImageView>();
        listjobinfo =  new LinkedList<JSONObject>();
        listlikejobflg =  new LinkedList<String>();
        jobList = new JSONArray();
        objjobinfo = new JSONObject();
    }
    //初期化
    public void Initialization(){
        llsearcgresults = (LinearLayout)findViewById(R.id.ll_searcg_results);
        lvgethintreset = (ListView)findViewById(R.id.lv_gethint_reset);
        svsearch = (ScrollView) findViewById(R.id.sv_search);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.kensakuchu));
        tvback          = findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle     = findViewById(R.id.tv_back_title);
        tvbackdummy     = findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.kensaku));
        tvbacktitle.setText(getString(R.string.kensakukekka));
        tvbackdummy.setText(getString(R.string.kensaku));
        tltrtvtitle = findViewById(R.id.tl_tr_tv_title);
        tladvancset = findViewById(R.id.tl_advancset);
        tvsearchreset=findViewById(R.id.tv_search_reset);
        tladvancset.setOnClickListener(Listener);
        tvsearchreset.setOnClickListener(Listener);

        trkeywordreset = findViewById(R.id.tr_keyword_reset);
        trworklocationreset = findViewById(R.id.tr_worklocation_reset);
        tvkeywordreset = findViewById(R.id.tv_keyword_reset);

        etkeywordreset = findViewById(R.id.et_keyword_reset);
        etworklocationreset = findViewById(R.id.et_worklocation_reset);
        ivclearkeywordreset = findViewById(R.id.iv_clear_keyword_reset);
        ivclearworklocationreset = findViewById(R.id.iv_clear_worklocation_reset);
        ivclearkeywordreset.setOnClickListener(this);
        ivclearworklocationreset.setOnClickListener(this);


        tlSearchcontents = findViewById(R.id.tl_Search_contents);
        tlSearchcontentsBottom = findViewById(R.id.tl_Search_contents_Bottom);
        tlresults = findViewById(R.id.tl_results);
        tlsearchreset = findViewById(R.id.tl_search_reset);

        tltrbuBefore = findViewById(R.id.tl_tr_bu_Before);
        tltrbuNext = findViewById(R.id.tl_tr_bu_Next);
        tltrbuleftsearch = findViewById(R.id.tl_tr_bu_leftsearch);
        tltrburightsearch = findViewById(R.id.tl_tr_bu_rightsearch);
        tltrbutheleft = findViewById(R.id.tl_tr_bu_theleft);
        tltrbuleft = findViewById(R.id.tl_tr_bu_left);
        tltrbucentre = findViewById(R.id.tl_tr_bu_centre);
        tltrburight = findViewById(R.id.tl_tr_bu_right);
        tltrbutheright = findViewById(R.id.tl_tr_bu_theright);
        tltrbuBeforeBottom = findViewById(R.id.tl_tr_bu_Before_Bottom);
        tltrbuNextBottom = findViewById(R.id.tl_tr_bu_Next_Bottom);
        tltrbuleftsearchBottom = findViewById(R.id.tl_tr_bu_leftsearch_Bottom);
        tltrburightsearchBottom = findViewById(R.id.tl_tr_bu_rightsearch_Bottom);
        tltrbutheleftBottom = findViewById(R.id.tl_tr_bu_theleft_Bottom);
        tltrbuleftBottom = findViewById(R.id.tl_tr_bu_left_Bottom);
        tltrbucentreBottom = findViewById(R.id.tl_tr_bu_centre_Bottom);
        tltrburightBottom = findViewById(R.id.tl_tr_bu_right_Bottom);
        tltrbutherightBottom = findViewById(R.id.tl_tr_bu_theright_Bottom);

        tltrbuBefore.setOnClickListener(Click_buttom);
        tltrbuNext.setOnClickListener(Click_buttom);
        tltrbuleftsearch.setOnClickListener(Click_buttom);
        tltrburightsearch.setOnClickListener(Click_buttom);
        tltrbutheleft.setOnClickListener(Click_buttom);
        tltrbuleft.setOnClickListener(Click_buttom);
        tltrbucentre.setOnClickListener(Click_buttom);
        tltrburight.setOnClickListener(Click_buttom);
        tltrbutheright.setOnClickListener(Click_buttom);
        tltrbuBeforeBottom.setOnClickListener(Click_buttom);
        tltrbuNextBottom.setOnClickListener(Click_buttom);
        tltrbuleftsearchBottom.setOnClickListener(Click_buttom);
        tltrburightsearchBottom.setOnClickListener(Click_buttom);
        tltrbutheleftBottom.setOnClickListener(Click_buttom);
        tltrbuleftBottom.setOnClickListener(Click_buttom);
        tltrbucentreBottom .setOnClickListener(Click_buttom);
        tltrburightBottom.setOnClickListener(Click_buttom);
        tltrbutherightBottom.setOnClickListener(Click_buttom);

        PreferenceUtils = new PreferenceUtils(SearchResultsActivity.this);
        LoginFlg = PreferenceUtils.getUserFlg();
        if(LoginFlg.equals("1")){
            userid = PreferenceUtils.getuserId();
            token = PreferenceUtils.gettoken();
        }
        SharedPreferences object = getSharedPreferences(getString(R.string.Initial), Context.MODE_PRIVATE);
        deviceId = object.getString(getString(R.string.deviceid),"A");
        AesKey = object.getString(getString(R.string.Information_Name_aesKey),"A");
        Log.d(TAG,"deviceId:"+ deviceId);
        Log.d(TAG,"AesKey:"+ AesKey);
        PreferenceUtils.setdeviceId(deviceId);
        PreferenceUtils.setAesKey(AesKey);
        myApplication = (MyApplication) getApplication();
        keyword = myApplication.getkeyword();
        address = myApplication.getaddress();
//        employmentStatus = myApplication.getemploymentStatus();
        yearlyIncome = myApplication.getyearlyIncome();
        page = myApplication.getpage();
        etkeywordreset.setText(keyword);
        etworklocationreset.setText(address);
        if(etkeywordreset.getText().length() > 0){
            ivclearkeywordreset.setImageResource(R.drawable.ic_cancel);
            ivclearkeywordreset.setTag(getString(R.string.clear));
        }
        if(etworklocationreset.getText().length() > 0){
            ivclearworklocationreset.setImageResource(R.drawable.ic_cancel);
            ivclearworklocationreset.setTag(getString(R.string.clear));
        }
        if(myApplication.getSearchResults(0).equals("0")){
            getSearchResults();
        } else {
            try {
                String numFound = myApplication.getSearchResults(1);
                String numOfPage = myApplication.getSearchResults(2);
                page = myApplication.getSearchResults(3);
                jobList = new JSONArray(myApplication.getSearchResults(4));
                displayresults(numFound,page,numOfPage);
                addresults(jobList);
                tltrtvtitle.setVisibility(View.VISIBLE);
                tlSearchcontents.setVisibility(View.VISIBLE);
                tlresults.setVisibility(View.VISIBLE);
                tlSearchcontentsBottom.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //获取距离边距
    private void getWH(){
        keywordresetTop= dp2px(SearchResultsActivity.this, 20);
        tladvancset.post(new Runnable() {
            @Override
            public void run() {
                keywordresetTop = keywordresetTop + tladvancset.getMeasuredHeight();
            }
        });

        trkeywordreset.post(new Runnable() {
            @Override
            public void run() {
                keywordresetTop = keywordresetTop + trkeywordreset.getMeasuredHeight();
            }
        });

        worklocationresetTop= dp2px(SearchResultsActivity.this, 5);
        trworklocationreset.post(new Runnable() {
            @Override
            public void run() {
                worklocationresetTop = worklocationresetTop + keywordresetTop + trworklocationreset.getMeasuredHeight();
            }
        });

        tvkeywordreset.post(new Runnable() {
            @Override
            public void run() {
                Width = tvkeywordreset.getMeasuredWidth();
                Log.w( TAG,"runWidth: "+Width);
            }
        });
    }
    //返回检索画面
    public void Click_back(){
        myApplication.setSearchResults("0",0);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(SearchResultsActivity.this, SearchActivity.class);
        intent.putExtra(getString(R.string.act),"");
        startActivity(intent);
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
    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    //获取搜索结果
    public void getSearchResults(){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        if(etnamereset.equals("")){
            CreateNew();
            Pdata.setkeyword(keyword);
            Pdata.setaddress(address);
            Pdata.setemploymentStatus(employmentStatus);
            Pdata.setyearlyIncome(yearlyIncome);
            Pdata.setpage(page);
            if(LoginFlg.equals("1")){
                Pdata.setUserId(userid);
            }
            param.put(getString(R.string.file),PARAM_index);
        } else if(etnamereset.equals(getString(R.string.keyword))){
            Pdata.setkeyword(keyword);
            param.put(getString(R.string.file),PARAM_KeywordHint);
        } else if(etnamereset.equals(getString(R.string.worklocation))){
            Pdata.setaddress(address);
            param.put(getString(R.string.file),PARAM_AddressHint);
        }
        String data = JsonChnge(AesKey,Pdata);
        param.put("data",data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
    //转换为Json格式并且AES加密
    public String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d(TAG,"sdPdata:"+sdPdata);
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

        @Override
        //在界面上显示进度条
        protected void onPreExecute() {
            if(etnamereset.equals("")){
                dialog.show();
            }
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
                Log.d(TAG,"Results:"+ githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String meg = obj.getString(getString(R.string.message));
                    Log.d(TAG,"returnData1:"+ obj.getString(getString(R.string.returnData)));
                    Log.d(TAG,"urlFlg:"+urlFlg);
                    Log.d(TAG,"url_name:"+ etnamereset);
                    Log.d(TAG,"meg:"+ meg);
                    if(processResult == true) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        if(etnamereset.equals("")) {
                            if (urlFlg.equals(getString(R.string.job))) {
                                decryptchange(returnData);
                            } else if (urlFlg.equals(getString(R.string.likejob))) {
                                alertdialog(getString(R.string.alertdialog13), getString(R.string.alertdialog14));
                                IBNlikejob.setImageResource(R.mipmap.app_like);
                            } else if (urlFlg.equals(getString(R.string.deletelikejob))) {
                                IBNlikejob.setImageResource(R.mipmap.app_no_like);
                                alertdialog(getString(R.string.alertdialog13), getString(R.string.alertdialog15));
                            } else {
                                JSONObject Data = new JSONObject(returnData);
                                String numFound = Data.getString(getString(R.string.numFound));
                                JSONObject paging = Data.getJSONObject(getString(R.string.pagenation));
                                String numOfPage = paging.getString(getString(R.string.numOfPage));
                                page = paging.getString(getString(R.string.currentPage));
                                displayresults(numFound, page, numOfPage);
                                jobList = Data.getJSONArray(getString(R.string.jobList));
                                myApplication.setSearchResults("1", 0);
                                myApplication.setSearchResults(numFound, 1);
                                myApplication.setSearchResults(numOfPage, 2);
                                myApplication.setSearchResults(page, 3);
                                myApplication.setSearchResults(jobList.toString(), 4);
                                addresults(jobList);
                                tltrtvtitle.setVisibility(View.VISIBLE);
                                tlSearchcontents.setVisibility(View.VISIBLE);
                                tlresults.setVisibility(View.VISIBLE);
                                tlSearchcontentsBottom.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d(TAG,"returnData2:"+ returnData);
                            ArrayList<String> stringArrayList = new ArrayList<String>();
                            if(returnData.length() > 0 && !returnData.equals("[]")){
                                JSONArray jsonReturnData = obj.getJSONArray(getString(R.string.returnData));
                                for (int i=0; i< returnData.length(); i++) {
                                    Log.d("***i:", i+"");
                                    if( i < 6){
                                        stringArrayList.add(jsonReturnData.getString(i)); //add to arraylist
                                    } else {
                                        break;
                                    }
                                }
                            }
                            String[] Stringdata = stringArrayList.toArray(new String[stringArrayList.size()]);
                            if(etnamereset.equals(getString(R.string.keyword))){
                                getItem(Stringdata,etnamereset);
                            } else {
                                getItem(Stringdata,etnamereset);
                            }

                        }
                    } else {
                        if(urlFlg.equals(getString(R.string.likejob))){
                            IBNlikejob.setImageResource(R.mipmap.app_like);
                            alertdialog(getString(R.string.error),meg);
                        } else if(etnamereset.equals(getString(R.string.keyword)) || etnamereset.equals(getString(R.string.worklocation))){
                            getItem(new String[0],etnamereset);
                        } else {
                            alertdialog(getString(R.string.error), getString(R.string.tsushinshierror));
                        }
                    }
                    dialog.dismiss();
                }catch (Exception e){
                    Log.d(TAG,"urlFlg:"+urlFlg);
                    Log.d(TAG,"url_name:"+ etnamereset);
                    Log.d(TAG,"e.getMessage():"+ e.getMessage());
                    if(etnamereset.equals("")){
                        alertdialog(getString(R.string.error),getString(R.string.tsushinshierror));
                        dialog.dismiss();
                    }
                    e.printStackTrace();
                }
            }
        }
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String decryptdata = AESprocess.getdecrypt(data,AesKey);
        Log.d("***decryptdata:", decryptdata);
        try {
            JSONObject obj = new JSONObject(decryptdata);
            myApplication.setMyjob(getString(R.string.likejob));
            myApplication.setjobinfo(obj.getString(getString(R.string.JobInfo)));
            myApplication.setAct(getString(R.string.Search));
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(SearchResultsActivity.this, ApplyActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //结果显示(件数)
    public void displayresults(String numFound,String currentPage,String numOfPage){
        OfPage = Integer.parseInt(numOfPage);
        buPage = Integer.parseInt(currentPage);
        tltrtvtitle.setText(getString(R.string.kensakukekka) + numFound + getString(R.string.ken)  + currentPage + getString(R.string.pagemei) + numOfPage + getString(R.string.page));
        Buttom_set(OfPage,Integer.parseInt(currentPage));
    }
    //结果显示(内容)
    public void addresults(JSONArray data){
        int top= dp2px(this, 10);
        TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
        tlparams.setMargins(0,0,0,top);
        tlresults.removeAllViews();
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***addresults:", data.getString(i));
                JSONArray jobName = obj.getJSONArray(getString(R.string.jobName));
                View searchresults;
                String likejobflg = "0";
                if(obj.getString(getString(R.string.isPaid)).equals("1")){
                    searchresults = getLayoutInflater().inflate(R.layout.include_searchresults_iskinwork, null);
                } else {
                    searchresults = getLayoutInflater().inflate(R.layout.include_searchresults_iskinwork, null);
                }

                TableLayout information = searchresults.findViewById(R.id.tl_information);
                information.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_job(v);
                    }
                });
                TextView tvtitle = searchresults.findViewById(R.id.tv_title);
                TextView tvcompanyname = searchresults.findViewById(R.id.tv_company_name);
                TextView tvJobname = searchresults.findViewById(R.id.tv_Jobname);
                TextView tvdate = searchresults.findViewById(R.id.tv_date);
                TextView tvPublishedcompany = searchresults.findViewById(R.id.tv_Published_company);
                TextView tvRecruitmentsite = searchresults.findViewById(R.id.tv_Recruitment_site);
                ImageView ibucontact = searchresults.findViewById(R.id.ibu_contact);
                ibucontact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_likejob(v);
                    }
                });
                HorizontalScrollView hsvtag = (HorizontalScrollView) searchresults.findViewById(R.id.hsv_tag);
                TableRow trtag = searchresults.findViewById(R.id.tr_tag);

                if(obj.getString(getString(R.string.isPaid)).equals("1")){
                    int w = dp2px(this, 100);
                    int h = dp2px(this, 20);
                    TextView tvtag = new TextView(this);
                    TableRow.LayoutParams tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    tvlp.setMargins(0,0,dp2px(this, 20),0);
                    tvtag.setLayoutParams(tvlp);
                    tvlp.width = dp2px(this, 100);
                    tvlp.height = dp2px(this, 20);
                    tvtag.setLayoutParams(tvlp);
                    tvtag.setTextSize(10);
                    tvtag.setGravity(Gravity.CENTER);
                    TextPaint tp = tvtag.getPaint();
                    tp.setFakeBoldText(true);
                    tvtag.setBackgroundResource(R.drawable.ic_background_red);
                    tvtag.setTextColor(Color.parseColor("#ffff4444"));
                    tvtag.setText(getString(R.string.kyuubo));
                    trtag.addView(tvtag);
                }
                if(obj.has(getString(R.string.kinworktag))){
                    JSONArray tag = obj.getJSONArray(getString(R.string.kinworktag));
                    for(int y=0; y < tag.length(); y++){
                        TextView tvtag = new TextView(this);
                        int w = dp2px(this, 100);
                        int h = dp2px(this, 20);
                        TableRow.LayoutParams tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                        tvlp.setMargins(0,0,dp2px(this, 20),0);
                        tvlp.width = dp2px(this, 100);
                        tvlp.height = dp2px(this, 20);
                        tvtag.setLayoutParams(tvlp);
                        tvtag.setTextSize(10);
                        tvtag.setGravity(Gravity.CENTER);
                        TextPaint tp = tvtag.getPaint();
                        tp.setFakeBoldText(true);
                        tvtag.setBackgroundResource(R.drawable.ic_background_orange);
                        tvtag.setTextColor(Color.parseColor("#ffff8800"));
                        tvtag.setText(tag.getString(y));
                        Log.d("***tvtag.setText:", tvtag.getText().toString());
                        trtag.addView(tvtag);
                    }
                } else {
                    hsvtag.setVisibility(View.GONE);
                }

                if(obj.has(getString(R.string.isLiked))){
                    if(obj.getBoolean(getString(R.string.isLiked)) == true){
                        ibucontact.setImageResource(R.mipmap.app_like);
                        likejobflg = "1";
                    }
                }
                tvtitle.setText(jobName.getString(0));
                tvcompanyname.setText(getString(R.string.kinmusaki)+ obj.getString(getString(R.string.address)));
                JSONArray contentKeyword = obj.getJSONArray(getString(R.string.highlightKeywords));
                String contentShorts = "";
                String date = "";
                for(int y = 0; y < contentKeyword.length(); y++ ){
                    date = "<font color=#000000><b>" + contentKeyword.getString(y) +"</b></font>";
                    contentShorts = obj.getString(getString(R.string.contentShorts)).replace(contentKeyword.getString(y),date) + "...";
                }
                tvJobname.setText(Html.fromHtml(contentShorts));
                tvdate.setText(getString(R.string.kessaibi) + obj.getString(getString(R.string.releaseDate)).substring(0,8));
                tvPublishedcompany.setText(getString(R.string.kessaikaisha)+ obj.getString(getString(R.string.company)));
                if(obj.has(getString(R.string.from))){
                    tvRecruitmentsite.setText(getString(R.string.kyujinsite) + obj.getString(getString(R.string.from)));
                }
                searchresults.setLayoutParams(tlparams);
                tlresults.addView(searchresults,i);
                listIBTN_info.add(i,information);
                listIBTN_job.add(i,ibucontact);
                listjobinfo.add(i,obj);
                listlikejobflg.add(i,likejobflg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //職歴信息取得
    public void Click_job(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTN_info.size(); i++) {
            if (listIBTN_info.get(i) == View) {
                iIndex = i;
                objjobinfo = listjobinfo.get(i);
                break;
            }
        }
        myApplication.setkeyword(keyword);
        myApplication.setaddress(address);
        myApplication.setemploymentStatus(employmentStatus);
        myApplication.setyearlyIncome(yearlyIncome);
        myApplication.setpage(page);
        try {
            if (iIndex >= 0) {
                urlFlg = getString(R.string.job);
                if(objjobinfo.getString(getString(R.string.isFromKinwork)).equals("1")){
                    PostDate Pdata = new PostDate();
                    Map<String,String> param = new HashMap<String, String>();
                    Pdata.setjobId(objjobinfo.getString(getString(R.string.jobId)));
                    String data = JsonChnge(AesKey,Pdata);
                    param.put(getString(R.string.file),PARAM_jobDetail);
                    param.put(getString(R.string.data),data);
                    //数据通信处理（访问服务器，并取得访问结果）
                    new GithubQueryTask().execute(param);
                } else {
                    myApplication.setURL(objjobinfo.getString(getString(R.string.url)));
                    myApplication.setAct(getString(R.string.Search));
                    Intent intent = new Intent();
                    intent.setClass(SearchResultsActivity.this, WebActivity.class);
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //気に入り追加与取消
    public void Click_likejob(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        String likejobfl = "";
        int iIndex = -1;
        for (int i = 0; i < listIBTN_job.size(); i++) {
            if (listIBTN_job.get(i) == View) {
                iIndex = i;
                IBNlikejob = listIBTN_job.get(i);
                objjobinfo = listjobinfo.get(i);
                likejobfl = listlikejobflg.get(i);
                break;
            }
        }
        myApplication.setkeyword(keyword);
        myApplication.setaddress(address);
        myApplication.setemploymentStatus(employmentStatus);
        myApplication.setyearlyIncome(yearlyIncome);
        myApplication.setpage(page);
        try {
            if (iIndex >= 0) {
                if(LoginFlg.equals("1")){
                    PostDate Pdata = new PostDate();
                    Map<String,String> param = new HashMap<String, String>();
                    if(likejobfl.equals("0")){
                        urlFlg = getString(R.string.likejob);
                        listlikejobflg.add(iIndex,"1");
                        JSONArray jobName = objjobinfo.getJSONArray(getString(R.string.jobName));
                        Pdata.setUserId(userid);
                        Pdata.setToken(token);
                        Log.d("objjobinfo", objjobinfo.toString());
                        Pdata.setjobId(objjobinfo.getString(getString(R.string.jobId)));
                        Pdata.seturl(objjobinfo.getString(getString(R.string.url)));
                        Pdata.settitle(jobName.getString(0));
                        Pdata.setisFromKinwork(objjobinfo.getString(getString(R.string.isFromKinwork)));
                        Pdata.setreleaseDate(objjobinfo.getString(getString(R.string.releaseDate)));
                        Pdata.setaddress(objjobinfo.getString(getString(R.string.address)));
                        Pdata.setcompany(objjobinfo.getString(getString(R.string.company)));
                        Pdata.setemploymentStatus(objjobinfo.getString(getString(R.string.employmentStatus)));
                        Pdata.setfrom(objjobinfo.getString(getString(R.string.from)));
                        String str = "";
                        if(objjobinfo.getString(getString(R.string.isPaid)).equals("1")){
                            str = getString(R.string.kyuubo)+ " ";
                        }
                        if(objjobinfo.has(getString(R.string.kinworktag))){
                            JSONArray JATag = objjobinfo.getJSONArray(getString(R.string.kinworktag));
                            for(int i = 0;i<JATag.length();i++){
                                str = str + JATag.getString(i) + " ";
                            }
                        }
                        Pdata.setkinworkTag(str);
                        Pdata.setcontentShorts(objjobinfo.getString(getString(R.string.contentShorts)));
                        Pdata.setcontentShortsHighlight(objjobinfo.getString(getString(R.string.contentShortsHighlight)));
                        param.put(getString(R.string.file),PARAM_likeJob);
                    } else if(likejobfl.equals("1")){
                        urlFlg = getString(R.string.deletelikejob);
                        listlikejobflg.add(iIndex,"0");
                        Pdata.setUserId(userid);
                        Pdata.setToken(token);
                        Pdata.seturl(objjobinfo.getString(getString(R.string.url)));
                        param.put(getString(R.string.file),PARAM_deletelikeJob);
                    }
                    String data = JsonChnge(AesKey,Pdata);
                    param.put(getString(R.string.data),data);
                    //数据通信处理（访问服务器，并取得访问结果）
                    new GithubQueryTask().execute(param);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    TextView msg = new TextView(this);
                    msg.setText(getString(R.string.checkloginstatus));
                    //msg.setPadding(10, 10, 10, 10);
                    msg.setGravity(Gravity.CENTER);
                    msg.setTextSize(15);
                    msg.setTextColor(Color.parseColor("#000000"));
                    builder.setTitle("　").setView(msg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //确定按钮的点击事件
                            PreferenceUtils.setsaveid(getString(R.string.SearchResults));
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.setClass(SearchResultsActivity.this, LoginActivity.class);
                            intent.putExtra(getString(R.string.Activity),getString(R.string.SearchResults));
                            startActivity(intent);

                        }
                    }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //取消按钮的点击事件
                        }
                    }).create().show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //翻页按钮设定
    private View.OnClickListener Click_buttom =new View.OnClickListener() {
        public void onClick(View View) {
            int PageNum = 0;
            int i = 0;
            String Textpage = "";
            urlFlg = "";
            switch (View.getId()) {
                case R.id.tl_tr_bu_Before:
                case R.id.tl_tr_bu_Before_Bottom:
                    PageNum = Integer.parseInt(page);
                    i = PageNum - 1;
                    if (i > 0) {
                        Textpage = String.valueOf(i);
                    }
                    break;
                case R.id.tl_tr_bu_leftsearch:
                case R.id.tl_tr_bu_leftsearch_Bottom:
                    Textpage = tltrbuleftsearch.getText().toString();
                    break;
                case R.id.tl_tr_bu_theleft:
                case R.id.tl_tr_bu_theleft_Bottom:
                    Textpage = tltrbutheleft.getText().toString();
                    break;
                case R.id.tl_tr_bu_left:
                case R.id.tl_tr_bu_left_Bottom:
                    Textpage = tltrbuleft.getText().toString();
                    break;
                case R.id.tl_tr_bu_centre:
                case R.id.tl_tr_bu_centre_Bottom:
                    Textpage = tltrbucentre.getText().toString();
                    break;
                case R.id.tl_tr_bu_right:
                case R.id.tl_tr_bu_right_Bottom:
                    Textpage = tltrburight.getText().toString();
                    break;
                case R.id.tl_tr_bu_theright:
                case R.id.tl_tr_bu_theright_Bottom:
                    Textpage = tltrbutheright.getText().toString();
                    break;
                case R.id.tl_tr_bu_rightsearch:
                case R.id.tl_tr_bu_rightsearch_Bottom:
                    Textpage = tltrburightsearch.getText().toString();
                    break;
                case R.id.tl_tr_bu_Next:
                case R.id.tl_tr_bu_Next_Bottom:
                    Log.d(TAG, "page: " + page);
                    Log.d(TAG, "OfPage: " + OfPage);
                    PageNum = Integer.parseInt(page);
                    i = PageNum + 1;
                    if (i <= OfPage) {
                        Textpage = String.valueOf(i);
                    }
                    break;
            }
            if (Textpage.length() > 0 && !page.equals(Textpage)) {
                page = Textpage;
                getSearchResults();
            }
        }
    };
    //翻页按钮内容设定
    public void Buttom_set(int numOfPage,int PageNum){
        String butNumflg = "";
        tltrbuleftsearch.setTextColor(Color.parseColor("#0196FF"));
        tltrbutheleft.setTextColor(Color.parseColor("#0196FF"));
        tltrbuleft.setTextColor(Color.parseColor("#0196FF"));
        tltrbucentre.setTextColor(Color.parseColor("#0196FF"));
        tltrburight.setTextColor(Color.parseColor("#0196FF"));
        tltrbutheright.setTextColor(Color.parseColor("#0196FF"));
        tltrburightsearch.setTextColor(Color.parseColor("#0196FF"));

        tltrbuleftsearchBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbutheleftBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbuleftBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbucentreBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrburightBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbutherightBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrburightsearchBottom.setTextColor(Color.parseColor("#0196FF"));

        tltrbuleftsearch.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutheleft.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbuleft.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbucentre.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburight.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutheright.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburightsearch.setBackgroundColor(Color.parseColor("#EEF9FF"));

        tltrbuleftsearchBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutheleftBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbuleftBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbucentreBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburightBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutherightBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburightsearchBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));

        tltrbuBefore.setVisibility(View.INVISIBLE);
        tltrbuleftsearch.setText("");
        tltrbutheleft.setText("");
        tltrbuleft.setText("");
        tltrbucentre.setText("");
        tltrburight.setText("");
        tltrbutheright.setText("");
        tltrburightsearch.setText("");
        tltrbuNext.setVisibility(View.INVISIBLE);

        tltrbuBeforeBottom.setVisibility(View.INVISIBLE);
        tltrbuleftsearchBottom.setText("");
        tltrbutheleftBottom.setText("");
        tltrbuleftBottom.setText("");
        tltrbucentreBottom.setText("");
        tltrburightBottom.setText("");
        tltrbutherightBottom.setText("");
        tltrburightsearchBottom.setText("");
        tltrbuNextBottom.setVisibility(View.INVISIBLE);

        if(PageNum > 1) {
            tltrbuBefore.setVisibility(View.VISIBLE);
            tltrbuBeforeBottom.setVisibility(View.VISIBLE);
        }
        if(PageNum < numOfPage) {
            tltrbuNext.setVisibility(View.VISIBLE);
            tltrbuNextBottom.setVisibility(View.VISIBLE);
        }

        if(PageNum == 1) {
            leftsearch = PageNum;
            theleft = PageNum + 1;
            left = PageNum + 2;
            centre = PageNum + 3;
            right = PageNum + 4;
            theright = PageNum + 5;
            rightsearch = PageNum + 6;
            butNumflg = getString(R.string.leftsearch);
        }else if(PageNum == 2) {
            leftsearch = PageNum - 1;
            theleft = PageNum;
            left = PageNum + 1;
            centre = PageNum + 2;
            right = PageNum + 3;
            theright = PageNum + 4;
            rightsearch = PageNum + 5;
            butNumflg = getString(R.string.theleft);
        }else if(PageNum == 3) {
            leftsearch = PageNum - 2;
            theleft = PageNum - 1;
            left = PageNum;
            centre = PageNum + 1;
            right = PageNum + 2;
            theright = PageNum + 3;
            rightsearch = PageNum + 4;
            butNumflg = getString(R.string.left);
        }else if(PageNum + 2 == numOfPage) {
            leftsearch = PageNum - 4;
            theleft = PageNum - 3;
            left = PageNum - 2;
            centre = PageNum - 1;
            right = PageNum;
            theright = PageNum + 1;
            rightsearch = PageNum + 2;
            butNumflg = getString(R.string.right);
        }else if(PageNum + 1 == numOfPage) {
            leftsearch = PageNum - 5;
            theleft = PageNum - 4;
            left = PageNum - 3;
            centre = PageNum - 2;
            right = PageNum - 1;
            theright = PageNum;
            rightsearch = PageNum + 1;
            butNumflg = getString(R.string.theright);
        }else if(PageNum == numOfPage) {
            leftsearch = PageNum - 6;
            theleft = PageNum - 5;
            left = PageNum - 4;
            centre = PageNum - 3;
            right = PageNum - 2;
            theright = PageNum - 1;
            rightsearch = PageNum;
            butNumflg = getString(R.string.rightsearch);
        }else {
            leftsearch = PageNum - 3;
            theleft = PageNum - 2;
            left = PageNum - 1;
            centre = PageNum;
            right = PageNum + 1;
            theright = PageNum + 2;
            rightsearch = PageNum + 3;
            butNumflg = getString(R.string.centre);
        }
        if(leftsearch <= numOfPage){
            tltrbuleftsearch.setText(String.valueOf(leftsearch));
            tltrbuleftsearchBottom.setText(String.valueOf(leftsearch));
            if(butNumflg.equals(getString(R.string.leftsearch))){
                tltrbuleftsearch.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbuleftsearch.setTextColor(Color.parseColor("#ffffffff"));
                tltrbuleftsearchBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbuleftsearchBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrbuleftsearch.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrbuleftsearchBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(theleft <= numOfPage){
            tltrbutheleft.setText(String.valueOf(theleft));
            tltrbutheleftBottom.setText(String.valueOf(theleft));
            if(butNumflg.equals(getString(R.string.theleft))){
                tltrbutheleft.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbutheleft.setTextColor(Color.parseColor("#ffffffff"));
                tltrbutheleftBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbutheleftBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrbutheleft.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrbutheleftBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(left <= numOfPage){
            tltrbuleft.setText(String.valueOf(left));
            tltrbuleftBottom.setText(String.valueOf(left));
            if(butNumflg.equals(getString(R.string.left))){
                tltrbuleft.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbuleft.setTextColor(Color.parseColor("#ffffffff"));
                tltrbuleftBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbuleftBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrbuleft.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrbuleftBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(centre <= numOfPage){
            tltrbucentre.setText(String.valueOf(centre));
            tltrbucentreBottom.setText(String.valueOf(centre));
            if(butNumflg.equals(getString(R.string.centre))){
                tltrbucentre.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbucentre.setTextColor(Color.parseColor("#ffffffff"));
                tltrbucentreBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbucentreBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrbucentre.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrbucentreBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(right <= numOfPage){
            tltrburight.setText(String.valueOf(right));
            tltrburightBottom.setText(String.valueOf(right));
            if(butNumflg.equals(getString(R.string.right))){
                tltrburight.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrburight.setTextColor(Color.parseColor("#ffffffff"));
                tltrburightBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrburightBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrburight.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrburightBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(theright <= numOfPage){
            tltrbutheright.setText(String.valueOf(theright));
            tltrbutherightBottom.setText(String.valueOf(theright));
            if(butNumflg.equals(getString(R.string.theright))){
                tltrbutheright.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbutheright.setTextColor(Color.parseColor("#ffffffff"));
                tltrbutherightBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrbutherightBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrbutheright.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrbutherightBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        if(rightsearch <= numOfPage){
            tltrburightsearch.setText(String.valueOf(rightsearch));
            tltrburightsearchBottom.setText(String.valueOf(rightsearch));
            if(butNumflg.equals(getString(R.string.rightsearch))){
                tltrburightsearch.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrburightsearch.setTextColor(Color.parseColor("#ffffffff"));
                tltrburightsearchBottom.setBackgroundResource(R.drawable.ic_shape_bule_all);
                tltrburightsearchBottom.setTextColor(Color.parseColor("#ffffffff"));
            } else {
                tltrburightsearch.setBackgroundResource(R.drawable.ic_shape_bule);
                tltrburightsearchBottom.setBackgroundResource(R.drawable.ic_shape_bule);
            }
        }
        svsearch.post(new Runnable() {
            @Override
            public void run() {
                svsearch.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
    //再检索
    private View.OnClickListener Listener =new View.OnClickListener() {
        public void onClick(View View) {
            etnamereset = "";
            switch (View.getId()) {
                case R.id.tl_advancset:
                    if (tlsearchreset.getVisibility() == android.view.View.GONE) {
                        tlsearchreset.setVisibility(android.view.View.VISIBLE);
                        getWH();
                    } else {
                        tlsearchreset.setVisibility(android.view.View.GONE);
                        lvgethintreset.setVisibility(android.view.View.GONE);
                    }
                    break;
                case R.id.tv_search_reset:
                    tlsearchreset.setVisibility(android.view.View.GONE);
                    lvgethintreset.setVisibility(android.view.View.GONE);
                    keyword = etkeywordreset.getText().toString();
                    address = etworklocationreset.getText().toString();
                    myApplication.setkeyword(keyword);
                    myApplication.setaddress(address);
                    page = "1";
                    getSearchResults();
                    break;
            }
        }
    };
    //菜单栏按钮触发事件
    public void ll_Click(View View){
        String ViewID = "";
        switch (View.getId()){
            //連絡画面に移動
            case R.id.ll_b_contact:
                ViewID = getString(R.string.ll_contact);
                break;
            //Myリスト画面に移動
            case R.id.ll_b_mylist:
                myApplication.setAct(getString(R.string.Search));
                ViewID = getString(R.string.ll_mylist);
                break;
            //個人設定画面に移動
            case R.id.ll_b_personalsettings:
                ViewID = getString(R.string.ll_personalsettings);
                break;
        }
        if(! ViewID.equals("")){
            PreferenceUtils.setsaveid(ViewID);
            if(LoginFlg.equals("1")){
                Click_intent(ViewID);
            } else {
                Click_intent(getString(R.string.UserLogin));
            }
        }
    }
    //画面移动
    private void Click_intent(String name){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (name){
            //  ログイン画面に移動
            case "UserLogin":
                intent.setClass(SearchResultsActivity.this, LoginActivity.class);
                intent.putExtra(getString(R.string.Activity),getString(R.string.SearchResults));
                break;
            //連絡画面に移動
            case "ll_contact":
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(SearchResultsActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(SearchResultsActivity.this, ContactDialogActivity.class);
                }
                break;
            //Myリスト画面に移動
            case "ll_mylist":
                myApplication.setAct(getString(R.string.Apply));
                if(myApplication.getMURL(0).equals("0")){
                    if(myApplication.getMApply(0).equals("0")){
                        intent.setClass(SearchResultsActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(SearchResultsActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(SearchResultsActivity.this, WebActivity.class);
                }
                break;
            //個人設定画面に移動
            case "ll_personalsettings":
                if(myApplication.getpersonalset(0).equals("0")){
                    intent.setClass(SearchResultsActivity.this, PersonalSetActivity.class);
                } else if(myApplication.getpersonalset(0).equals("1")){
                    intent.setClass(SearchResultsActivity.this, BasicinfoeditActivity.class);
                } else if(myApplication.getpersonalset(0).equals("2")){
                    intent.setClass(SearchResultsActivity.this, ChangepwActivity.class);
                } else if(myApplication.getpersonalset(0).equals("3")){
                    intent.setClass(SearchResultsActivity.this, ResumeActivity.class);
                }
                break;
        }
        myApplication.setSearchResults("0",0);
        startActivity(intent);
    }

    //监听事件
    public void initData(){
        //点击空白位置收起下来列表
        llsearcgresults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvgethintreset.setVisibility(View.GONE);
            }
        });
        //监听キーワード的内容
        etkeywordreset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivclearkeywordreset.setVisibility(View.VISIBLE);
                    ivclearworklocationreset.setTag(getString(R.string.clear));
                    if( blkeywordreset == false){
                        etnamereset = getString(R.string.keyword);
                        keyword = s.toString();
                        getSearchResults();
                    }
                } else {
                    lvgethintreset.setVisibility(View.GONE);
                    ivclearkeywordreset.setTag(getString(R.string.keyword));
                    ivclearkeywordreset.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                blkeywordreset = false;
            }
        });
        //监听地址栏的内容
        etworklocationreset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    ivclearworklocationreset.setImageResource(R.drawable.ic_location_on);
                    ivclearworklocationreset.setTag(getString(R.string.location));
                    lvgethintreset.setVisibility(View.GONE);
                } else {
                    ivclearworklocationreset.setImageResource(R.drawable.ic_cancel);
                    ivclearworklocationreset.setTag(getString(R.string.clear));
                    if(blworklocationreset == false){
                        etnamereset = getString(R.string.worklocation);
                        address = s.toString();
                        getSearchResults();

                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                blworklocationreset = false;
            }
        });

        lvgethintreset.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取当前选择的值
                String data=(String) adapterreset.getItem(position);
                Log.w("etname", etnamereset);

                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.yyyyMMddHHmmssSSS));
                Date curDate =  new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                Log.w("***lvgethintDate1:", strDate);

                curDate =  new Date(System.currentTimeMillis());
                strDate = formatter.format(curDate);
                Log.w("***lvgethintDate2:", strDate);
                if(etnamereset.equals("keyword")){
                    blkeywordreset = true;
                    etkeywordreset.setText(data);
                    etkeywordreset.setSelection(etkeywordreset.getText().length());

                } else if(etnamereset.equals(getString(R.string.worklocation))){
                    blworklocationreset = true;
                    etworklocationreset.setText(data);
                    etworklocationreset.setSelection(etworklocationreset.getText().length());
                }
                etnamereset = "";
                lvgethintreset.setVisibility(View.GONE);
            }
        });
    }
    //输入框的清空处理

    public void onClick(View View){
        switch (View.getId()){
            case R.id.iv_clear_keyword_reset:
                etkeywordreset.setText("");
                break;
            case R.id.iv_clear_worklocation_reset:
                //当前图片为清空的时候，清空输入框
                if (ivclearworklocationreset.getTag().equals(getString(R.string.clear))){
                    etworklocationreset.setText("");
                    ivclearworklocationreset.setTag(getString(R.string.location));
                    ivclearworklocationreset.setImageResource(R.drawable.ic_location_on);
                }else{
                    //当前图片不为清空的时候，重新获取当前所在地
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                                1000);
                        return;
                    } else {
                        locationStart();
                    }
                }
                break;
        }
    }

    //当前位置经纬度取得
    private void locationStart(){
        Log.d("debug","locationStart()");
        // LocationManager インスタンス生成
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d("debug", "checkSelfPermission false");
            return;
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        criteria.setAltitudeRequired(false);//不要求海拔信息
        criteria.setBearingRequired(false);//不要求方位信息
        criteria.setCostAllowed(false);//是否允许付费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
        Location Location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,true));
        getaddress_components(Location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("onRequestrequestCode", requestCode+"");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationStart();
        }

    }

    private void getaddress_components(Location location){
        // 緯度の表示
        String str1 = getString(R.string.Latitude)+location.getLatitude();
        Log.d("str1", str1);
        // 経度の表示
        String str2 = getString(R.string.Longtude)+location.getLongitude();
        Log.d("str2", str2);
        Map<String,String> param = new HashMap<String, String>();
        param.put(getString(R.string.url),"https://maps.google.com/maps/api/geocode/json?latlng=");
        param.put(getString(R.string.itude),location.getLatitude() + ","+location.getLongitude());
        param.put(getString(R.string.key),"&sensor=false&language=ja&key=AIzaSyBzSkvprYMmBmLWaon_uBWJEiJ9DH21B6g");
        new GithubQueryTask2().execute(param);
    }

    public class GithubQueryTask2 extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String url = map.get(getString(R.string.url));
            String itude = map.get(getString(R.string.itude));
            String key = map.get(getString(R.string.key));
            Log.d("***url:", url + itude + key);
            URL searchUrl = null;
            try {
                searchUrl = new URL(url + itude + key);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl,"","");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                Log.d("***Results:", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    JSONArray job = obj.getJSONArray(getString(R.string.results));
                    myApplication.setaddress_components(job.get(0).toString());
                    Log.d("***job:", job.get(0).toString());
                    SetAddress(job.get(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //设置当前位置
    private void SetAddress(String date){
        Log.d("date", date);
        try {
            JSONObject obj = new JSONObject(date);
            JSONArray results = obj.getJSONArray(getString(R.string.address_components));
            Log.d("results", results.toString());
            int iIndex = -1;
            String add_1 = "";
            String add_2 = "";
            String add_3 = "";
            for (int i = 0;i < results.length();i++){
                if(results.getJSONObject(i).getString(getString(R.string.long_name)).equals(getString(R.string.nihon))){
                    iIndex = i;
                    break;
                }
            }
            if(iIndex > -1){
                add_1 = results.getJSONObject(iIndex - 1).getString(getString(R.string.long_name));
                add_2 = results.getJSONObject(iIndex - 2).getString(getString(R.string.long_name));
                add_3 = results.getJSONObject(iIndex - 3).getString(getString(R.string.long_name));
            } else {
                ivclearworklocationreset.setVisibility(View.GONE);
            }
            etworklocationreset.setText(add_1 + " " + add_2 + " " + add_3);

            myApplication.setaddress(etworklocationreset.getText().toString());
            if(etworklocationreset.getText().length() > 0){
                ivclearworklocationreset.setImageResource(R.drawable.ic_cancel);
                ivclearworklocationreset.setTag(getString(R.string.clear));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //联想关键字设定悬浮窗
    public void getItem(String []Stringdata,String name){

        int top= 0;
        Log.d("***name:", name);
        Log.d("***Width:", Width+"");
        if(name.equals(getString(R.string.keyword))){
            top= keywordresetTop;
        } else {
            top= worklocationresetTop;
        }
        Log.d("***top:", top+"");
        int left= dp2px(SearchResultsActivity.this, 10) + Width;
        int right= dp2px(SearchResultsActivity.this, 20);
        Log.d("***left:", left+"");
        Log.d("***right:", right+"");
        FrameLayout.LayoutParams flparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flparams.setMargins(left,top,right,0);
        lvgethintreset.setLayoutParams(flparams);
        adapterreset = new ArrayAdapter<>(this, R.layout.list_item, Stringdata);
        lvgethintreset.setAdapter(adapterreset);
        lvgethintreset.setVisibility(View.VISIBLE);
        setListViewHeightBasedOnChildren(lvgethintreset);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i,null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
        listView.invalidate();
    }

}
