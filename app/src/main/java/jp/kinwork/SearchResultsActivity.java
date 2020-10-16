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

    private int theleft = 0;
    private int left = 0;
    private int centre = 0;
    private int right = 0;
    private int theright = 0;
    private int OfPage = 0;
    private int buPage = 0;
    private int statusIndex = 0;

    private Button tltrbuBefore;
    private Button tltrbuNext;
    private Button tltrbutheleft;
    private Button tltrbuleft;
    private Button tltrbucentre;
    private Button tltrburight;
    private Button tltrbutheright;

    private Button tltrbuBeforeBottom;
    private Button tltrbuNextBottom;
    private Button tltrbutheleftBottom;
    private Button tltrbuleftBottom;
    private Button tltrbucentreBottom;
    private Button tltrburightBottom;
    private Button tltrbutherightBottom;

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
    private ImageView Ivsearch;
    private TextView tvsearch;
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
        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        return super.onTouchEvent(event);
    }

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
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {

                return true;
            }
        }
        return false;
    }

    public void CreateNew(){
        listIBTN_info = new LinkedList<TableLayout>();
        listIBTN_job = new LinkedList<ImageView>();
        listjobinfo =  new LinkedList<JSONObject>();
        listlikejobflg =  new LinkedList<String>();
        jobList = new JSONArray();
        objjobinfo = new JSONObject();
    }

    public void Initialization(){
        llsearcgresults = (LinearLayout)findViewById(R.id.ll_searcg_results);
        lvgethintreset = (ListView)findViewById(R.id.lv_gethint_reset);
        Ivsearch = (ImageView) findViewById(R.id.iv_b_search);
        Ivsearch.setImageResource(R.mipmap.blue_search);
        tvsearch = (TextView) findViewById(R.id.tv_b_search);
        tvsearch.setTextColor(Color.parseColor("#5EACE2"));
        svsearch = (ScrollView) findViewById(R.id.sv_search);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.kensakuchu));
        tvback          = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle     = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy     = (TextView) findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.kensaku));
        tvbacktitle.setText(getString(R.string.kensakukekka));
        tvbackdummy.setText(getString(R.string.kensaku));
        tltrtvtitle = (TextView) findViewById(R.id.tl_tr_tv_title);
        tladvancset = (TableLayout) findViewById(R.id.tl_advancset);
        tvsearchreset=findViewById(R.id.tv_search_reset);
        tladvancset.setOnClickListener(Listener);
        tvsearchreset.setOnClickListener(Listener);

        trkeywordreset = (TableRow) findViewById(R.id.tr_keyword_reset);
        trworklocationreset = (TableRow) findViewById(R.id.tr_worklocation_reset);
        tvkeywordreset = (TextView) findViewById(R.id.tv_keyword_reset);

        etkeywordreset = (EditText) findViewById(R.id.et_keyword_reset);
        etworklocationreset = (EditText) findViewById(R.id.et_worklocation_reset);
        ivclearkeywordreset = (ImageView) findViewById(R.id.iv_clear_keyword_reset);
        ivclearworklocationreset = (ImageView) findViewById(R.id.iv_clear_worklocation_reset);
        ivclearkeywordreset.setOnClickListener(this);
        ivclearworklocationreset.setOnClickListener(this);


        tlSearchcontents = (TableLayout) findViewById(R.id.tl_Search_contents);
        tlSearchcontentsBottom = (TableLayout) findViewById(R.id.tl_Search_contents_Bottom);
        tlresults = (TableLayout) findViewById(R.id.tl_results);
        tlsearchreset = (TableLayout) findViewById(R.id.tl_search_reset);

        tltrbuBefore = (Button) findViewById(R.id.tl_tr_bu_Before);
        tltrbuNext = (Button) findViewById(R.id.tl_tr_bu_Next);
         tltrbutheleft = (Button) findViewById(R.id.tl_tr_bu_theleft);
        tltrbuleft = (Button) findViewById(R.id.tl_tr_bu_left);
        tltrbucentre = (Button) findViewById(R.id.tl_tr_bu_centre);
        tltrburight = (Button) findViewById(R.id.tl_tr_bu_right);
        tltrbutheright = (Button) findViewById(R.id.tl_tr_bu_theright);
        tltrbuBeforeBottom = (Button) findViewById(R.id.tl_tr_bu_Before_Bottom);
        tltrbuNextBottom = (Button) findViewById(R.id.tl_tr_bu_Next_Bottom);
        tltrbutheleftBottom = (Button) findViewById(R.id.tl_tr_bu_theleft_Bottom);
        tltrbuleftBottom = (Button) findViewById(R.id.tl_tr_bu_left_Bottom);
        tltrbucentreBottom = (Button) findViewById(R.id.tl_tr_bu_centre_Bottom);
        tltrburightBottom = (Button) findViewById(R.id.tl_tr_bu_right_Bottom);
        tltrbutherightBottom = (Button) findViewById(R.id.tl_tr_bu_theright_Bottom);

        tltrbuBefore.setOnClickListener(Click_buttom);
        tltrbuNext.setOnClickListener(Click_buttom);
        tltrbutheleft.setOnClickListener(Click_buttom);
        tltrbuleft.setOnClickListener(Click_buttom);
        tltrbucentre.setOnClickListener(Click_buttom);
        tltrburight.setOnClickListener(Click_buttom);
        tltrbutheright.setOnClickListener(Click_buttom);
        tltrbuBeforeBottom.setOnClickListener(Click_buttom);
        tltrbuNextBottom.setOnClickListener(Click_buttom);
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
        Log.d("***deviceId***", deviceId);
        Log.d("***AesKey***", AesKey);
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
                Log.w( "runWidth: ",Width+"" );
            }
        });
    }
    public void Click_back(){
        myApplication.setSearchResults("0",0);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(SearchResultsActivity.this, SearchActivity.class);
        intent.putExtra(getString(R.string.act),"");
        startActivity(intent);
    }
    private void alertdialog(String title,String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
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
        new GithubQueryTask().execute(param);
    }
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
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
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
                Log.d("***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String meg = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        Log.d("***returnData***", obj.getString(getString(R.string.returnData)));
                        Log.d("***urlFlg***", urlFlg);
                        Log.d("***url_name***", etnamereset);
                        if(etnamereset.equals("")) {
                            if (urlFlg.equals(getString(R.string.job))) {
                                decryptchange(obj.getString(getString(R.string.returnData)));
                            } else if (urlFlg.equals(getString(R.string.likejob))) {
                                alertdialog(getString(R.string.alertdialog13), getString(R.string.alertdialog14));
                                IBNlikejob.setImageResource(R.mipmap.app_like);
                            } else if (urlFlg.equals(getString(R.string.deletelikejob))) {
                                IBNlikejob.setImageResource(R.mipmap.app_no_like);
                                alertdialog(getString(R.string.alertdialog14), getString(R.string.alertdialog15));
                            } else {
                                JSONObject Data = new JSONObject(obj.getString(getString(R.string.returnData)));
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
                            Log.d("***returnData***", obj.getString(getString(R.string.returnData)));
                            JSONArray returnData = obj.getJSONArray(getString(R.string.returnData));
                            ArrayList<String> stringArrayList = new ArrayList<String>();
                            String[] Stringdata = {};
                            int length = returnData.length();
                            if(!obj.getString(getString(R.string.returnData)).equals("[]")){
                                for (int i=0; i< length; i++) {
                                    Log.d("***i***", i+"");
                                    if( i < 6){
                                        stringArrayList.add(returnData.getString(i)); //add to arraylist
                                    } else {
                                        break;
                                    }
                                }
                            }
                            Stringdata = stringArrayList.toArray(new String[stringArrayList.size()]);
                            Log.d("***etnamereset***", etnamereset);

                            Stringdata = stringArrayList.toArray(new String[stringArrayList.size()]);
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
                        } else {
                            alertdialog(getString(R.string.error), getString(R.string.tsushinshierror));
                        }
                    }
                    dialog.dismiss();
                }catch (Exception e){
                    if(etnamereset.equals("")){
                        alertdialog(getString(R.string.error),getString(R.string.tsushinshierror));
                        dialog.dismiss();
                    }
                    e.printStackTrace();
                }
            }
        }
    }
    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String decryptdata = AESprocess.getdecrypt(data,AesKey);
        Log.d("***decryptdata***", decryptdata);
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
    public void displayresults(String numFound,String currentPage,String numOfPage){
        OfPage = Integer.parseInt(numOfPage);
        buPage = Integer.parseInt(currentPage);
        tltrtvtitle.setText(getString(R.string.kensakukekka) + numFound + getString(R.string.ken)  + currentPage + getString(R.string.pagemei) + numOfPage + getString(R.string.page));
        Buttom_set(OfPage,Integer.parseInt(currentPage));
    }
    public void addresults(JSONArray data){
        int top= dp2px(this, 10);
        TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
        tlparams.setMargins(0,0,0,top);
        tlresults.removeAllViews();
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***addresults***", data.getString(i));
                JSONArray jobName = obj.getJSONArray(getString(R.string.jobName));
                View searchresults;
                String likejobflg = "0";
                if(obj.getString(getString(R.string.isPaid)).equals("1")){
                    searchresults = getLayoutInflater().inflate(R.layout.include_searchresults_iskinwork, null);
                } else {
                    searchresults = getLayoutInflater().inflate(R.layout.include_searchresults_iskinwork, null);
                }

                TableLayout information = (TableLayout) searchresults.findViewById(R.id.tl_information);
                information.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_job(v);
                    }
                });
                TextView tvtitle = (TextView) searchresults.findViewById(R.id.tv_title);
                TextView tvcompanyname = (TextView) searchresults.findViewById(R.id.tv_company_name);
                TextView tvJobname = (TextView) searchresults.findViewById(R.id.tv_Jobname);
                TextView tvdate = (TextView) searchresults.findViewById(R.id.tv_date);
                TextView tvPublishedcompany = (TextView) searchresults.findViewById(R.id.tv_Published_company);
                TextView tvRecruitmentsite = (TextView) searchresults.findViewById(R.id.tv_Recruitment_site);
                ImageView ibucontact = (ImageView) searchresults.findViewById(R.id.ibu_contact);
                ibucontact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_likejob(v);
                    }
                });
                HorizontalScrollView hsvtag = (HorizontalScrollView) searchresults.findViewById(R.id.hsv_tag);
                TableRow trtag = (TableRow) searchresults.findViewById(R.id.tr_tag);

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
                        Log.d("***tvtag.setText***", tvtag.getText().toString());
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
    public void Click_job(View View){
        if (View == null) {
            return;
        }
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
    public void Click_likejob(View View){
        if (View == null) {
            return;
        }
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
                            intent.setClass(SearchResultsActivity.this, MainKinWork.class);
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
    private View.OnClickListener Click_buttom =new View.OnClickListener() {
        public void onClick(View View) {
            int PageNum = 0;
            int i = 0;
            String Textpage = "";
            switch (View.getId()) {
                case R.id.tl_tr_bu_Before:
                case R.id.tl_tr_bu_Before_Bottom:
                    PageNum = Integer.parseInt(page);
                    i = PageNum - 1;
                    if (i > 0) {
                        Textpage = String.valueOf(i);
                    }
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
                case R.id.tl_tr_bu_Next:
                case R.id.tl_tr_bu_Next_Bottom:
                    PageNum = Integer.parseInt(page);
                    i = PageNum + 1;
                    if (i < OfPage) {
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
    public void Buttom_set(int numOfPage,int PageNum){
        String butNumflg = "";
        tltrbutheleft.setTextColor(Color.parseColor("#0196FF"));
        tltrbuleft.setTextColor(Color.parseColor("#0196FF"));
        tltrbucentre.setTextColor(Color.parseColor("#0196FF"));
        tltrburight.setTextColor(Color.parseColor("#0196FF"));
        tltrbutheright.setTextColor(Color.parseColor("#0196FF"));

        tltrbutheleftBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbuleftBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbucentreBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrburightBottom.setTextColor(Color.parseColor("#0196FF"));
        tltrbutherightBottom.setTextColor(Color.parseColor("#0196FF"));

        tltrbutheleft.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbuleft.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbucentre.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburight.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutheright.setBackgroundColor(Color.parseColor("#EEF9FF"));

        tltrbutheleftBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbuleftBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbucentreBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrburightBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));
        tltrbutherightBottom.setBackgroundColor(Color.parseColor("#EEF9FF"));

        tltrbuBefore.setText("");
        tltrbutheleft.setText("");
        tltrbuleft.setText("");
        tltrbucentre.setText("");
        tltrburight.setText("");
        tltrbutheright.setText("");
        tltrbuNext.setText("");

        tltrbuBeforeBottom.setText("");
        tltrbutheleftBottom.setText("");
        tltrbuleftBottom.setText("");
        tltrbucentreBottom.setText("");
        tltrburightBottom.setText("");
        tltrbutherightBottom.setText("");
        tltrbuNextBottom.setText("");

        Log.d("PageNum",String.valueOf(PageNum));
        if(PageNum > 1) {
            tltrbuBefore.setVisibility(View.VISIBLE);
            tltrbuBeforeBottom.setVisibility(View.VISIBLE);
            tltrbuBefore.setText(getString(R.string.maehe));
            tltrbuBeforeBottom.setText(getString(R.string.maehe));
        }else{
            tltrbuBefore.setVisibility(View.GONE);
            tltrbuBeforeBottom.setVisibility(View.GONE);
        }
        if(PageNum < numOfPage) {
            tltrbuNext.setText(getString(R.string.tsugihe));
            tltrbuNextBottom.setText(getString(R.string.tsugihe));
        }


        if(PageNum == 1) {
            theleft = PageNum;
            left = PageNum + 1;
            centre = PageNum + 2;
            right = PageNum + 3;
            theright = PageNum + 4;
            butNumflg = getString(R.string.theleft);
        }else if(PageNum == 2) {
            theleft = PageNum - 1;
            left = PageNum;
            centre = PageNum + 1;
            right = PageNum + 2;
            theright = PageNum + 3;
            butNumflg = getString(R.string.left);
        }else if(PageNum + 1 == numOfPage) {
            theleft = PageNum - 3;
            left = PageNum - 2;
            centre = PageNum - 1;
            right = PageNum;
            theright = PageNum + 1;
            butNumflg = getString(R.string.right);
        }else if(PageNum == numOfPage) {
            theleft = PageNum - 4;
            left = PageNum - 3;
            centre = PageNum - 2;
            right = PageNum - 1;
            theright = PageNum;
            butNumflg = getString(R.string.theright);
        }else {
            theleft = PageNum - 2;
            left = PageNum - 1;
            centre = PageNum;
            right = PageNum + 1;
            theright = PageNum + 2;
            butNumflg = getString(R.string.centre);
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

        svsearch.post(new Runnable() {
            @Override
            public void run() {
                svsearch.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
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
    private void Click_intent(String name){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (name){
            //  ログイン画面に移動
            case "UserLogin":
                intent.setClass(SearchResultsActivity.this, MainKinWork.class);
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
        startActivity(intent);
    }

    public void initData(){
        llsearcgresults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvgethintreset.setVisibility(View.GONE);
            }
        });
        etkeywordreset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivclearkeywordreset.setImageResource(R.drawable.ic_cancel);
                    ivclearworklocationreset.setTag(getString(R.string.clear));
                    if( blkeywordreset == false){
                        etnamereset = getString(R.string.keyword);
                        keyword = s.toString();
                        getSearchResults();
                    }
                } else {
                    lvgethintreset.setVisibility(View.GONE);
                    ivclearkeywordreset.setImageResource(R.drawable.ic_null);
                    ivclearkeywordreset.setTag(getString(R.string.keyword));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                blkeywordreset = false;
            }
        });
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
                String data=(String) adapterreset.getItem(position);
                Log.w("etname", etnamereset);

                SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.yyyyMMddHHmmssSSS));
                Date curDate =  new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                Log.w("***lvgethintDate1***", strDate);

                curDate =  new Date(System.currentTimeMillis());
                strDate = formatter.format(curDate);
                Log.w("***lvgethintDate2***", strDate);
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

    public void onClick(View View){
        switch (View.getId()){
            case R.id.iv_clear_keyword_reset:
                if(ivclearkeywordreset.getTag().equals(getString(R.string.clear))){
                    etkeywordreset.setText("");
                    ivclearkeywordreset.setImageResource(R.drawable.ic_null);
                    ivclearkeywordreset.setTag(getString(R.string.keyword));
                }
                break;
            case R.id.iv_clear_worklocation_reset:
                if (ivclearworklocationreset.getTag().equals(getString(R.string.clear))){
                    etworklocationreset.setText("");
                    ivclearworklocationreset.setTag(getString(R.string.location));
                    ivclearworklocationreset.setImageResource(R.drawable.ic_location_on);
                }else{
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
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
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
        String str1 = getString(R.string.Latitude)+location.getLatitude();
        Log.d("str1", str1);
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
            Log.d("***url***", url + itude + key);
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
                Log.d("***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    JSONArray job = obj.getJSONArray(getString(R.string.results));
                    myApplication.setaddress_components(job.get(0).toString());
                    Log.d("***job***", job.get(0).toString());
                    SetAddress(job.get(0).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
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
    public void getItem(String []Stringdata,String name){

        int top= 0;
        Log.d("***name***", name);
        Log.d("***Width***", Width+"");
        if(name.equals(getString(R.string.keyword))){
            top= keywordresetTop;
        } else {
            top= worklocationresetTop;
        }
        Log.d("***top***", top+"");
        int left= dp2px(SearchResultsActivity.this, 10) + Width;
        int right= dp2px(SearchResultsActivity.this, 20);
        Log.d("***left***", left+"");
        Log.d("***right***", right+"");
        FrameLayout.LayoutParams flparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        flparams.setMargins(left,top,right,0);
        lvgethintreset.setLayoutParams(flparams);
        adapterreset = new ArrayAdapter<>(this, R.layout.list_item, Stringdata);
        lvgethintreset.setAdapter(adapterreset);
        lvgethintreset.setVisibility(View.VISIBLE);
        setListViewHeightBasedOnChildren(lvgethintreset);

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i,null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.invalidate();
    }

}
