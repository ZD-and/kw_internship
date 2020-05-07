package jp.kinwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.CommonView.MyScrollView;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class MylistActivity extends AppCompatActivity implements MyScrollView.OnScrollListener {

    final static String PARAM_likelist = "/MypagesMobile/personalSavedJobInfo";
    final static String PARAM_personalApplyJobList = "/MypagesMobile/personalApplyJobList";
    final static String PARAM_jobDetail = "/JobInfosMobile/jobDetail";
    final static String PARAM_deletelikeJob = "/JobInfosMobile/deleteLikeJobByUrl";
    final static String PARAM_deleteApplyJob = "/MypagesMobile/personalApplyJobListDelete";


    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;
    private String Act = "";
    private String url= "";

    private ImageView ivmylist;
    private TextView tvmylist;
    private TextView tvtitle;
    private TextView tvlikecont;
//    private TextView tvApplycont;
    private TextView tvTopApplycont;
    private TextView tvname;
    private TextView tvemail;
    private MyApplication myApplication;
    private PreferenceUtils PreferenceUtils;

    private TableLayout tllike;
    private TableLayout tltllike;
    private TableLayout tlEntered;
    private TableLayout tlEnteredtitle;
    private TableLayout tltlEntered;
    private TableLayout tlliketitle;

    private TableRow tltrlike;
    private TableRow tltrEntered;

    private LinkedList<JSONObject> listlikejobinfo;
    private LinkedList<JSONObject> listEnteredjobinfo;
    private LinkedList<TableLayout> listTL_likejobinfo;
    private LinkedList<ImageView> listIBTN_likejob;
    private LinkedList<String> listSTRURL_likejob;

    private LinkedList<TableLayout> listTL_Enteredjobinfo;
    private LinkedList<ImageView> listIBTN_Enteredjob;
    private LinkedList<ImageView> listIBTN_DelEnteredjob;
    private LinkedList<String> listEnteredjobId;
    private LinkedList<String> listDelEnteredID;

    private JSONArray likejobList;
    private JSONObject objjobinfo;

    private int DeleteIndex = -1;
    private int likejobIndex = -1;
    private int ApplyjobIndex = -1;
    private int likejobpage = 1;
    private int likejobpageCount = 0;
    private int likejobCount = 0;
    private int Applyjobpage = 1;
    private int ApplyjobpageCount = 0;
    private int ApplyjobCount = 0;
    private int ApplyDeleteIndex = -1;

    private String[] salary_type = new String[]{" ","月給","年給","周給","日給","時給"};

    private MyScrollView myScrollView;
    private LinearLayout mEnteredLayout;
    private LinearLayout mTopEnteredLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        myScrollView  = (MyScrollView) findViewById(R.id.SV_ScrollView);
        mEnteredLayout = (LinearLayout) findViewById(R.id.layout);
        mTopEnteredLayout = (LinearLayout) findViewById(R.id.top_layout);
        tvTopApplycont = (TextView) findViewById(R.id.tv_apply_cont_top);
        myScrollView.setOnScrollListener(this);
        //当布局的状态或者控件的可见性发生改变回调的接口
        findViewById(R.id.mylist_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //这一步很重要，使得上面的布局和下面的布局重合
                onScroll(myScrollView.getScrollY());
            }
        });
        CreateNew();
        Initialization();
        Log.w("tvTopApplycont", tvTopApplycont.getText().toString());
    }
    /**
     * 滚动的回调方法，当滚动的Y距离大于或者等于 购买布局距离父类布局顶部的位置，就显示购买的悬浮框
     * 当滚动的Y的距离小于 购买布局距离父类布局顶部的位置加上购买布局的高度就移除购买的悬浮框
     *
     */
    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mEnteredLayout.getTop());
        mTopEnteredLayout.layout(0, mBuyLayout2ParentTop, mTopEnteredLayout.getWidth(), mBuyLayout2ParentTop + mTopEnteredLayout.getHeight());
    }

    //数组初始化
    public void CreateNew(){
        listTL_likejobinfo = new LinkedList<TableLayout>();
        listIBTN_likejob = new LinkedList<ImageView>();
        listlikejobinfo =  new LinkedList<JSONObject>();
        listEnteredjobinfo =  new LinkedList<JSONObject>();
        listSTRURL_likejob =  new LinkedList<String>();
        listTL_Enteredjobinfo = new LinkedList<TableLayout>();
        listIBTN_Enteredjob = new LinkedList<ImageView>();
        listIBTN_DelEnteredjob = new LinkedList<ImageView>();
        listEnteredjobId = new LinkedList<String>();
        listDelEnteredID = new LinkedList<String>();
        likejobList = new JSONArray();
        objjobinfo = new JSONObject();
    }
    //初始化
    public void Initialization(){
        tlEnteredtitle = (TableLayout) findViewById(R.id.tl_Entered_title);
        tlliketitle=findViewById(R.id.tl_like_title);
        ivmylist = (ImageView) findViewById(R.id.iv_b_mylist);
        tvmylist = (TextView) findViewById(R.id.tv_b_mylist);
        ivmylist.setImageResource(R.mipmap.blue_mylist);
        tvmylist.setTextColor(Color.parseColor("#5EACE2"));
        tllike = (TableLayout) findViewById(R.id.tl_like);
        tltllike = (TableLayout) findViewById(R.id.tl_tl_like);
        tlEntered = (TableLayout) findViewById(R.id.tl_Entered);
        tltlEntered = (TableLayout) findViewById(R.id.tl_tl_Entered);
        tltrlike = (TableRow) findViewById(R.id.tl_tr_like);
        tltrEntered = (TableRow) findViewById(R.id.tl_tr_Entered);
        tvlikecont = (TextView) findViewById(R.id.tv_like_cont);
        tvtitle      = (TextView) findViewById(R.id.tv_title_b_name);
        tvtitle.setText("マイリスト");
        tvname = (TextView) findViewById(R.id.tv_userinfo_name);
        tvemail = (TextView) findViewById(R.id.tv_userinfo_email);
        myApplication = (MyApplication) getApplication();
        PreferenceUtils = new PreferenceUtils(MylistActivity.this);
        Act = myApplication.getAct();
        deviceId = PreferenceUtils.getdeviceId();
        AesKey = PreferenceUtils.getAesKey();
        userId = PreferenceUtils.getuserId();
        token = PreferenceUtils.gettoken();
        if(myApplication.getlast_name().length() > 0){
            tvname.setText(myApplication.getlast_name() + myApplication.getfirst_name() + " 様");
        }
        tvemail.setText(PreferenceUtils.getEmail());
        if(Act.equals("SelectResume")){
            tllike.setVisibility(View.GONE);
            tlEntered.setVisibility(View.VISIBLE);
        }
        getSearchResults();
    }
    //获取搜索结果菜单栏按钮
    public void ll_Click(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_b_mylist:
                intent.setClass(MylistActivity.this, MylistActivity.class);
                break;
            //跳转检索画面
            case R.id.ll_b_search:
                myApplication.setAct("Search");
                if(myApplication.getSApply(0).equals("0")){
                    if(myApplication.getSearchResults(0).equals("0")){
                        intent.setClass(MylistActivity.this, SearchActivity.class);
                        intent.putExtra("act","");
                    } else {
                        intent.setClass(MylistActivity.this, SearchResultsActivity.class);
                    }
                } else {
                    intent.setClass(MylistActivity.this, ApplyActivity.class);
                }
                break;
            //跳转联络画面
            case R.id.ll_b_contact:
                if(myApplication.getContactDialog(0).equals("0")){
                    intent.setClass(MylistActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(MylistActivity.this, ContactDialogActivity.class);
                }
                break;
            //跳转个人设定画面
            case R.id.ll_b_personalsettings:
                if(myApplication.getpersonalset(0).equals("0")){
                    intent.setClass(MylistActivity.this, PersonalSetActivity.class);
                } else if(myApplication.getpersonalset(0).equals("1")){
                    intent.setClass(MylistActivity.this, BasicinfoeditActivity.class);
                } else if(myApplication.getpersonalset(0).equals("2")){
                    intent.setClass(MylistActivity.this, ChangepwActivity.class);
                } else if(myApplication.getpersonalset(0).equals("3")){
                    intent.setClass(MylistActivity.this, ResumeActivity.class);
                }
                break;
        }
        startActivity(intent);

    }
    //获取搜索结果
    public void getSearchResults(){
        PostDate Pdata1 = new PostDate();
        Map<String,String> param1 = new HashMap<String, String>();
        Pdata1.setUserId(userId);
        Pdata1.setToken(token);
        Pdata1.setpage(String.valueOf(1));
        param1.put("file",PARAM_likelist);
        String data1 = JsonChnge(AesKey,Pdata1);
        param1.put("data",data1);
        param1.put("name","");
        PostDate Pdata2 = new PostDate();
        Map<String,String> param2 = new HashMap<String, String>();
        Pdata2.setUserId(userId);
        Pdata2.setToken(token);
        Pdata2.setcurrentPage("1");
        param2.put("file",PARAM_personalApplyJobList);
        String data2 = JsonChnge(AesKey,Pdata2);
        param2.put("data",data2);
        param2.put("name","");
        //数据通信处理（気に入り取得）
        new GithubQueryTask().execute(param1);
        //数据通信处理（応募状況取得）
        new GithubQueryTask().execute(param2);
    }
    //转换为Json格式并且AES加密
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
    //访问服务器，并取得访问结果
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        String name= "";

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get("file");
            String data = map.get("data");
            name = map.get("name");
            Log.d("***file***", file);
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
                    String meg = obj.getString("message");
                    if(processResult == true) {
                        if(obj.getString("returnData").equals("[]")){
                            alertdialog("対象データ存在しません。");
                        } else {
                            if(name.equals("")){
                                decryptchange(obj.getString("returnData"));
                            } else if(name.equals("likejob") || name.equals("Enteredjob")){
                                MoveApply(obj.getString("returnData"));
                            } else if(name.equals("deletelikejob")){
                                likejobCount = likejobCount - 1;
                                likejobIndex = likejobIndex - 1;
                                if(likejobCount == 0){
                                    tllike.setVisibility(View.GONE);
                                }
                                tvlikecont.setText(likejobCount + "件");
                                listIBTN_likejob.remove(DeleteIndex);
                                tltllike.removeViewAt(DeleteIndex);
                            } else if(name.equals("deleteApplyjob")){
                                ApplyjobCount = ApplyjobCount - 1;
                                ApplyjobIndex = ApplyjobIndex - 1;
                                if(ApplyjobCount == 0){
                                    tltlEntered.setVisibility(View.GONE);
                                }
                                //tvApplycont.setText(ApplyjobCount + "件");
                                tvTopApplycont.setText(ApplyjobCount + "件");
                                listIBTN_Enteredjob.remove(ApplyDeleteIndex);
                                listIBTN_DelEnteredjob.remove(ApplyDeleteIndex);
                                tltlEntered.removeViewAt(ApplyDeleteIndex);
                            }
                        }
                    } else {
                        alertdialog(meg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data){
        if(! data.equals("null")){
            AESprocess AESprocess = new AESprocess();
            String datas = AESprocess.getdecrypt(data,AesKey);
            Log.d("**datas**", datas);
            try {
                JSONObject obj = new JSONObject(datas);
                JSONArray job = obj.getJSONArray("dataList");
                if(obj.getJSONArray("dataList").length() > 0 && obj.getJSONArray("dataList").getJSONObject(0).has("SavedLikedJob")){
                    Log.d("**likejob**", job.toString());
                    Log.d("**likejobpageCount**", obj.getString("pageCount"));
                    Log.d("**likejobcount**", obj.getString("count"));
                    likejobpageCount = Integer.parseInt(obj.getString("pageCount"));
                    likejobCount = Integer.parseInt(obj.getString("count"));
                    tvlikecont.setText(likejobCount + "件");
                    if(likejobpage >= likejobpageCount){
                        tltrlike.setVisibility(View.GONE);
                    } else {
                        tltrlike.setVisibility(View.VISIBLE);
                    }
                    addlikejob(job);
                } else {
                    Log.d("**Applyjob**", job.toString());
                    ApplyjobpageCount = Integer.parseInt(obj.getString("pageCount"));
                    ApplyjobCount = Integer.parseInt(obj.getString("count"));
                    Log.d("**ApplyjobpageCount**", ApplyjobpageCount+"");
                    Log.d("**ApplyjobCount**", ApplyjobCount+"");
                    //tvApplycont.setText(ApplyjobCount + "件");
                    tvTopApplycont.setText(ApplyjobCount + "件");
                    if(Applyjobpage >= ApplyjobpageCount){
                        tltrEntered.setVisibility(View.GONE);
                    } else {
                        tltrEntered.setVisibility(View.VISIBLE);
                    }
                    addApplyjob(job);
                }

                //Log.w("tvApplycont", tvApplycont.getText().toString());
                Log.w("tvTopApplycont", tvTopApplycont.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myScrollView.setVisibility(View.VISIBLE);
        }
    }
    //解密数据,移动到下一个画面
    public void MoveApply(String data){
        AESprocess AESprocess = new AESprocess();
        String decryptdata = AESprocess.getdecrypt(data,AesKey);
        Log.d("***decryptdata***", decryptdata);
        try {
            JSONObject obj = new JSONObject(decryptdata);
            myApplication.setjobinfo(obj.getString("JobInfo"));
            myApplication.setAct("Apply");
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(MylistActivity.this, ApplyActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //気に入り数据取得
//    public void addlikejob(JSONArray data){
//        for(int i=0; i < data.length(); i++){
//            try {
//                likejobIndex = likejobIndex + 1;
//                Log.d("***addlikejobdata***", data.getJSONObject(i).toString());
//                JSONObject obj = data.getJSONObject(i).getJSONObject("SavedLikedJob");
//                JSONObject JobInfo = new JSONObject();
//                Log.d("***joblength***", JobInfo.length() + "");
//                if(data.getJSONObject(i).has("JobInfo")){
//                    JobInfo = data.getJSONObject(i).getJSONObject("JobInfo");
//                    Log.d("***addlikejJobInfo***", JobInfo.toString());
//                }
//                Log.d("***addlikejob***", obj.toString());
//                int top= dp2px(this, 10);
//                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
//                tlparams.setMargins(0,0,0,top);
//                View mylist = getLayoutInflater().inflate(R.layout.include_mylist_likejob, null);
//                TableLayout information = (TableLayout) mylist.findViewById(R.id.tl_l_basicinformation);
//                TextView tvlJobname = (TextView) mylist.findViewById(R.id.tv_l_Jobname);
//                TextView tvladdress = (TextView) mylist.findViewById(R.id.tv_l_address);
//                TextView tvldate = (TextView) mylist.findViewById(R.id.tv_l_date);
//                TextView tvlshortContent = (TextView) mylist.findViewById(R.id.tv_l_shortContent);
//                ImageView ibucontact = (ImageView) mylist.findViewById(R.id.ibu_l_contact);
//                JSONArray details = new JSONArray(obj.getString("details"));
//
//                if(JobInfo.length() > 0 && JobInfo.getString("status").equals("1")){
//                    tvlJobname.setTextColor(Color.GRAY);
//                    tvladdress.setTextColor(Color.GRAY);
//                    tvlshortContent.setTextColor(Color.GRAY);
//                    tvldate.setTextColor(Color.GRAY);
//                    String Jobname = "【期限切れ】" + details.get(0).toString();
//                    int length1 = Jobname.indexOf("【期限切れ】");
//                    int length2 = length1 + "【期限切れ】".length();
//
//                    SpannableStringBuilder style_name=new SpannableStringBuilder(Jobname);
//                    style_name.setSpan(new ForegroundColorSpan(Color.BLACK),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    tvlJobname.setText(style_name);
//                } else {
//                    tvlJobname.setText(details.get(0).toString());
//                }
//
//                tvladdress.setText("勤務先：" + details.get(1).toString());
//                tvlshortContent.setText(details.get(2).toString());
//                tvldate.setText("掲載日：" + obj.getString("releaseDate"));
//
//                mylist.setLayoutParams(tlparams);
//                tltllike.addView(mylist);
//                listTL_likejobinfo.add(likejobIndex,information);
//                listIBTN_likejob.add(likejobIndex,ibucontact);
//                listSTRURL_likejob.add(likejobIndex,obj.getString("url"));
//                listlikejobinfo.add(likejobIndex,data.getJSONObject(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    //気に入り数据取得
    public void addlikejob(JSONArray data){
        int top= dp2px(this, 10);
        TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
        tlparams.setMargins(0,0,0,top);
        for(int i=0; i < data.length(); i++){
            try {
                likejobIndex = likejobIndex + 1;
                JSONObject obj = data.getJSONObject(i);
                Log.d("***addlikejob***", data.getString(i));
                JSONObject SavedLikedJob = obj.getJSONObject("SavedLikedJob");
                Log.d("***SavedLikedJob***", SavedLikedJob.toString());
                View likejob;
                String likejobflg = "0";
                likejob = getLayoutInflater().inflate(R.layout.include_mylist_likejob, null);
                TableLayout information = (TableLayout) likejob.findViewById(R.id.tl_information);
                TextView tvtitle = (TextView) likejob.findViewById(R.id.tv_title);
                TextView tvcompanyname = (TextView) likejob.findViewById(R.id.tv_company_name);
                TextView tvJobname = (TextView) likejob.findViewById(R.id.tv_Jobname);
                TextView tvdate = (TextView) likejob.findViewById(R.id.tv_date);
                TextView tvPublishedcompany = (TextView) likejob.findViewById(R.id.tv_Published_company);
                TextView tvRecruitmentsite = (TextView) likejob.findViewById(R.id.tv_Recruitment_site);
                ImageView ibucontact = (ImageView) likejob.findViewById(R.id.ibu_contact);
                HorizontalScrollView hsvtag = (HorizontalScrollView) likejob.findViewById(R.id.hsv_tag);
                TableRow trtag = (TableRow) likejob.findViewById(R.id.tr_tag);

                Log.d("***SavedLikedJob+tag***", "["+SavedLikedJob.get("kinworkTag")+"]");
                if(!SavedLikedJob.getString("kinworkTag").equals("")){
                    String[] strArray = null;
                    strArray = SavedLikedJob.getString("kinworkTag").split(" ");
                    for(int y=0; y < strArray.length; y++){
                        TextView tvtag = new TextView(this);
                        TableRow.LayoutParams tvlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                        tvlp.setMargins(0,0,dp2px(this, 20),0);
                        tvlp.width = dp2px(this, 100);
                        tvlp.height = dp2px(this, 20);
                        tvtag.setLayoutParams(tvlp);
                        tvtag.setTextSize(10);
                        tvtag.setGravity(Gravity.CENTER);
                        TextPaint tp = tvtag.getPaint();
                        tp.setFakeBoldText(true);
                        if(i == 0 && strArray[i].equals("急募")){
                            tvtag.setBackgroundResource(R.drawable.ic_background_red);
                            tvtag.setTextColor(Color.parseColor("#ffff4444"));
                        } else {
                            tvtag.setBackgroundResource(R.drawable.ic_background_orange);
                            tvtag.setTextColor(Color.parseColor("#ffff8800"));
                        }
                        tvtag.setText(strArray[y]);
                        Log.d("***tvtag.setText***", tvtag.getText().toString());
                        trtag.addView(tvtag);
                    }
                } else {
                    hsvtag.setVisibility(View.GONE);
                }

                JSONObject JobInfo = new JSONObject();
                if(obj.has("JobInfo")){
                    JobInfo = obj.getJSONObject("JobInfo");
                }
                if(JobInfo.length() > 0 && JobInfo.getString("status").equals("1")){
                    tvtitle.setTextColor(Color.GRAY);
                    tvcompanyname.setTextColor(Color.GRAY);
                    tvPublishedcompany.setTextColor(Color.GRAY);
                    tvRecruitmentsite.setTextColor(Color.GRAY);
                    tvdate.setTextColor(Color.GRAY);
                    String Jobtitle = "【期限切れ】" + SavedLikedJob.getString("title");
                    int length1 = Jobtitle.indexOf("【期限切れ】");
                    int length2 = length1 + "【期限切れ】".length();

                    SpannableStringBuilder style_name=new SpannableStringBuilder(Jobtitle);
                    style_name.setSpan(new ForegroundColorSpan(Color.BLACK),length1,length2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    tvtitle.setText(style_name);
                } else {
                    tvtitle.setText(SavedLikedJob.getString("title"));
                }

                tvcompanyname.setText("勤務先：" + SavedLikedJob.getString("address"));
//                String contentShorts = SavedLikedJob.getString("contentShortsHighlight");
//                contentShorts = contentShorts.replace("<span class='keyword'>","<font color=#000000><b>").replace("</span>","</b></font>");
//                tvJobname.setText(Html.fromHtml(contentShorts));
                tvJobname.setText(SavedLikedJob.getString("contentShorts"));
                tvdate.setText("掲載日：" + SavedLikedJob.getString("releaseDate").substring(0,8));
                tvPublishedcompany.setText("掲載会社：" + SavedLikedJob.getString("company"));
                if(SavedLikedJob.has("from")){
                    tvRecruitmentsite.setText("求人サイト：" + SavedLikedJob.getString("from"));
                }

                likejob.setLayoutParams(tlparams);
                tltllike.addView(likejob);
                listTL_likejobinfo.add(likejobIndex,information);
                listIBTN_likejob.add(likejobIndex,ibucontact);
                listSTRURL_likejob.add(likejobIndex,SavedLikedJob.getString("url"));
                listlikejobinfo.add(likejobIndex,data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //应聘一览数据取得
    public void addApplyjob(JSONArray data){
        for(int i=0; i < data.length(); i++){
            try {
                ApplyjobIndex = ApplyjobIndex + 1;
                JSONObject objJobInfo = data.getJSONObject(i).getJSONObject("JobInfo");
                JSONObject objSavedResume = data.getJSONObject(i).getJSONObject("SavedResume");
                Log.d("***objJobInfo***", objJobInfo.toString());
                Log.d("***objSavedResume***", objSavedResume.toString());
                int top= dp2px(this, 10);
                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
                tlparams.setMargins(0,0,0,top);
                View mylist = getLayoutInflater().inflate(R.layout.include_mylist_enteredjob, null);
                TableLayout Enteredjobinfo = mylist.findViewById(R.id.tl_e_basicinformation);
                TextView tvecompanyname = (TextView) mylist.findViewById(R.id.tv_e_company_name);
                TextView tveJobname = (TextView) mylist.findViewById(R.id.tv_e_Jobname);
                TextView tveaddress = (TextView) mylist.findViewById(R.id.tv_e_address);
                TextView tvedate = (TextView) mylist.findViewById(R.id.tv_e_date);
                TextView tvesalary = (TextView) mylist.findViewById(R.id.tv_e_salary);
                ImageView ibucontact = (ImageView) mylist.findViewById(R.id.ibu_e_contact);
                ImageView ibuedel = (ImageView) mylist.findViewById(R.id.ibu_e_del);
                tvecompanyname.setText(objJobInfo.getString("company_name"));
                tveJobname.setText(objJobInfo.getString("occupation_name"));
                tveaddress.setText(objJobInfo.getString("add_1") +
                        objJobInfo.getString("add_2") +
                        objJobInfo.getString("add_3") +
                        objJobInfo.getString("add_4"));
                int typeNum = Integer.parseInt(objJobInfo.getString("salary_type"));
                tvesalary.setText("[" + salary_type[typeNum] + "] " + objJobInfo.getString("salary_from") + " ~ " + objJobInfo.getString("salary_to"));
                tvedate.setText("応募日：" + objSavedResume.getString("created_date").substring(0,10));
                mylist.setLayoutParams(tlparams);
                listTL_Enteredjobinfo.add(ApplyjobIndex,Enteredjobinfo);
                listIBTN_Enteredjob.add(ApplyjobIndex,ibucontact);
                listIBTN_DelEnteredjob.add(ApplyjobIndex,ibuedel);
                listEnteredjobId.add(ApplyjobIndex,objJobInfo.getString("id"));
                listDelEnteredID.add(ApplyjobIndex,objSavedResume.getString("id"));
                listEnteredjobinfo.add(ApplyjobIndex,objJobInfo);
                tltlEntered.addView(mylist,ApplyjobIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //dp转换为px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
    //通信结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage(meg).setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }
    //項目の表示
    public void Click_visibility(View ClikcView){
        switch (ClikcView.getId()){
            case R.id.tl_like_title:
                if(likejobCount == 0){
                    tllike.setVisibility(View.GONE);
                }else {
                    if(tllike.getVisibility() == View.GONE){
                        tllike.setVisibility(View.VISIBLE);
                    }
                    else {
                        tllike.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tl_Entered_title:
                if(tlEntered.getVisibility() == View.GONE){
                    tlEntered.setVisibility(View.VISIBLE);
                }
                else {
                    tlEntered.setVisibility(View.GONE);
                }
                break;
        }
    }
    //気に入り職歴信息取得
    public void Click_likejob(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listTL_likejobinfo.size(); i++) {
            if (listTL_likejobinfo.get(i) == View) {
                iIndex = i;
                break;
            }
        }
        try {
            Log.w("Click_likejob_info", listlikejobinfo.get(iIndex).toString());
//            JSONObject obj = listlikejobinfo.get(iIndex).getJSONObject("SavedLikedJob");
//            objjobinfo = obj.getJSONObject("SavedLikedJob");
            objjobinfo = listlikejobinfo.get(iIndex).getJSONObject("SavedLikedJob");
            JSONObject JobInfo = new JSONObject();
            if(listlikejobinfo.get(iIndex).has("JobInfo")){
                JobInfo = listlikejobinfo.get(iIndex).getJSONObject("JobInfo");
            }
            if (iIndex >= 0) {
                myApplication.setMyjob("likejob");
                if(objjobinfo.getString("isFromKinwork").equals("1")){
                    if( JobInfo == null || ! JobInfo.getString("status").equals("1")) {
                        PostDate Pdata = new PostDate();
                        Map<String,String> param = new HashMap<String, String>();
                        Pdata.setUserId(userId);
                        Pdata.setjobId(objjobinfo.getString("job_info_id"));
                        String data = JsonChnge(AesKey,Pdata);
                        param.put("file",PARAM_jobDetail);
                        param.put("data",data);
                        param.put("name","likejob");
                        //数据通信处理（访问服务器，并取得访问结果）
                        new GithubQueryTask().execute(param);
                    }
                } else {
                    myApplication.setURL(objjobinfo.getString("url"));
                    myApplication.setAct("mylist");
                    Intent intent = new Intent();
                    intent.setClass(MylistActivity.this, WebActivity.class);
                    startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //気に入り取消
    public void Click_Dellikejob(View View){
        if (View == null) {
            return;
        }

        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTN_likejob.size(); i++) {
            if (listIBTN_likejob.get(i) == View) {
                iIndex = i;
                DeleteIndex = i;
                url = listSTRURL_likejob.get(i);
                break;
            }
        }
        if (iIndex >= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MylistActivity.this);
            builder.setTitle("").setMessage("お気に入りを取消しますか？").setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //确定按钮的点击事件
                    PostDate Pdata = new PostDate();
                    Map<String,String> param = new HashMap<String, String>();
                    Pdata.setUserId(userId);
                    Pdata.setToken(token);
                    Pdata.seturl(url);
                    param.put("file",PARAM_deletelikeJob);
                    String data = JsonChnge(AesKey,Pdata);
                    param.put("data",data);
                    param.put("name","deletelikejob");
                    //数据通信处理（访问服务器，并取得访问结果）
                    new GithubQueryTask().execute(param);
                }
            }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //取消按钮的点击事件
                }
            }).show();
        }
    }
    //応募済み職歴信息取得
    public void Click_enteredjob(View View){
        String E_jobid = "";
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listTL_Enteredjobinfo.size(); i++) {
            if (listTL_Enteredjobinfo.get(i) == View) {
                iIndex = i;
                E_jobid = listEnteredjobId.get(i);
                break;
            }
        }
        if (iIndex >= 0) {
            PostDate Pdata = new PostDate();
            Map<String,String> param = new HashMap<String, String>();
            myApplication.setMyjob("Enteredjob");
            Pdata.setUserId(userId);
            Pdata.setToken(token);
            Pdata.setjobId(E_jobid);
            //Pdata.setlikeJobId();
            param.put("file",PARAM_jobDetail);
            String data = JsonChnge(AesKey,Pdata);
            param.put("data",data);
            param.put("name","Enteredjob");
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
        }
    }
    //显示更多内容按钮
    public void Click_getmore(View View){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        Pdata.setUserId(userId);
        Pdata.setToken(token);
        switch (View.getId()){
            case R.id.tl_tv_like:
                likejobpage = likejobpage + 1;
                param.put("file",PARAM_likelist);
                Pdata.setpage(String.valueOf(likejobpage));
                break;
            case R.id.tl_tv_Entered:
                Applyjobpage = Applyjobpage + 1;
                param.put("file",PARAM_personalApplyJobList);
                Pdata.setcurrentPage(String.valueOf(Applyjobpage));
                break;
        }
        String data = JsonChnge(AesKey,Pdata);
        param.put("data",data);
        param.put("name","");
        //数据通信处理（気に入り取得）
        new GithubQueryTask().execute(param);
    }
    //応募済み削除
    public void Click_DelApplyjob(View View){
        if (View == null) {
            return;
        }
        // 判断第几个按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTN_DelEnteredjob.size(); i++) {
            if (listIBTN_DelEnteredjob.get(i) == View) {
                iIndex = i;
                ApplyDeleteIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MylistActivity.this);
            builder.setTitle("").setMessage("この仕事を取消しますか？").setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //确定按钮的点击事件
                    PostDate Pdata = new PostDate();
                    Map<String,String> param = new HashMap<String, String>();
                    Pdata.setUserId(userId);
                    Pdata.setToken(token);
                    Pdata.setsavedResumeId(listDelEnteredID.get(ApplyDeleteIndex));
                    param.put("file",PARAM_deleteApplyJob);
                    String data = JsonChnge(AesKey,Pdata);
                    param.put("data",data);
                    param.put("name","deleteApplyjob");
                    //数据通信处理（访问服务器，并取得访问结果）
                    new GithubQueryTask().execute(param);
                }
            }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //取消按钮的点击事件
                }
            }).show();
        }
    }
    //通信一览画面跳转
    public void Click_ContactDialog(View View){
        if (View == null) {
            return;
        }
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        for (int i = 0; i < listIBTN_Enteredjob.size(); i++) {
            if (listIBTN_Enteredjob.get(i) == View) {
                iIndex = i;
                break;
            }
        }
        if (iIndex >= 0) {
            try {
                JSONObject obj = listEnteredjobinfo.get(iIndex);
                Log.d("***Dialog-obj***", obj.toString());
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(MylistActivity.this, ContactDialogActivity.class);
                intent.putExtra("company_name",obj.getString("company_name"));
                intent.putExtra("address",obj.getString("add_1") + obj.getString("add_2") + obj.getString("add_3") + obj.getString("add_4"));
                intent.putExtra("ID",obj.getString("employer_id"));
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
