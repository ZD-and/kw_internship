package jp.kinwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.login.LoginManager;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.api.LineApiClientBuilder;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.ClassDdl.Resume;
import jp.kinwork.Common.CommonAsyncTask;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import jp.kinwork.Common.AESprocess;

import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;
import static jp.kinwork.Common.NetworkUtils.buildUrl;

public class PersonalSetActivity extends AppCompatActivity {
    final static String PARAM_File = "/MypagesMobile/initMypageData";
    final static String PARAM_logout = "/usersMobile/logoutMobile";

    private String flg = "";

    private String mDeviceId;
    private String mAesKey;
    private String mUserId;
    private String mToken;

//    private String deviceId;
    private TextView tvname;
    private TextView tvemail;

    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;
    private LineApiClient mLineApiClient;
    private FirebaseAuth mAuth;
    private String TAG = "PersonalSetActivity";

    private ArrayList<Resume> resumeList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalset);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Initialization();
        load();
        urllodad();
        setViews();
    }
    //初始化
    public void Initialization(){
        mPreferenceUtils = new PreferenceUtils(PersonalSetActivity.this);
        mDeviceId = mPreferenceUtils.getdeviceId();
        mAesKey = mPreferenceUtils.getAesKey();
        mUserId = mPreferenceUtils.getuserId();
        mToken = mPreferenceUtils.gettoken();
        tvname = (TextView) findViewById(R.id.tv_userinfo_name);
        tvemail = (TextView) findViewById(R.id.tv_userinfo_email);
        findViewById(R.id.tr_basicinfoedit).setOnClickListener(Listener);
        findViewById(R.id.tr_changpw).setOnClickListener(Listener);
        findViewById(R.id.tr_LoginOut).setOnClickListener(Listener);
        findViewById(R.id.tr_licenses).setOnClickListener(Listener);
        findViewById(R.id.tr_termsofservice).setOnClickListener(Listener);
        findViewById(R.id.tr_privacypolicy).setOnClickListener(Listener);
        TextView tvtitle = (TextView) findViewById(R.id.tv_title_b_name);
        tvtitle.setText(getString(R.string.personalsettings));
        mDeviceId = mPreferenceUtils.getdeviceId();
        mAesKey = mPreferenceUtils.getAesKey();
        mUserId = mPreferenceUtils.getuserId();
        mToken = mPreferenceUtils.gettoken();
        ImageView ivpersonalsettings = (ImageView) findViewById(R.id.iv_b_personalsettings);
        TextView tvpersonalsettings = (TextView) findViewById(R.id.tv_b_personalsettings);
        ivpersonalsettings.setImageResource(R.mipmap.blue_personalsettings);
        tvpersonalsettings.setTextColor(Color.parseColor("#5EACE2"));
        resumeList = new ArrayList<>();
        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(PersonalSetActivity.this);
        mLineApiClient = new LineApiClientBuilder(getApplicationContext(), getString(R.string.line_client_id)).build();
        mAuth = FirebaseAuth.getInstance();
    }
    //菜单栏按钮
    public void ll_Click(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            case R.id.ll_b_search:
                mMyApplication.setAct(getString(R.string.Search));
                if(mMyApplication.getSURL(0).equals("0")){
                    if(mMyApplication.getSApply(0).equals("0")){
                        if(mMyApplication.getSearchResults(0).equals("0")){
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
                if(mMyApplication.getContactDialog(0).equals("0")){
                    intent.setClass(PersonalSetActivity.this, ContactActivity.class);
                } else {
                    intent.setClass(PersonalSetActivity.this, ContactDialogActivity.class);
                }
                break;
            case R.id.ll_b_mylist:
                mMyApplication.setAct(getString(R.string.Apply));
                if(mMyApplication.getMURL(0).equals("0")){
                    if(mMyApplication.getMApply(0).equals("0")){
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

    //内容取得、通信
    private void urllodad() {
        String data = JsonChnge(mAesKey, mUserId, mToken);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_File);
        param.put("data",data);
        flg = "0";
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    public void goToBasicinfoedit(){
        String data = JsonChnge(mAesKey, mUserId, mToken);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_File);
        param.put("data",data);
        flg= "1";
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);

    }
    public void goToChangPW(){
        String data = JsonChnge(mAesKey, mUserId, mToken);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_File);
        param.put("data",data);
        flg ="2";
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    public void Click_Resume(){
        String data = JsonChnge(mAesKey, mUserId, mToken);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_File);
        param.put("data",data);
        flg ="3";
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,String data_a,String data_b) {
        PostDate Pdata = new PostDate();
        Pdata.setUserId(data_a);
        Pdata.setToken(data_b);
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Pdata,PostDate.class);
        Log.d("***+++sdPdata+++***", sdPdata);
        AESprocess AESprocess = new AESprocess();
        String encrypt = AESprocess.getencrypt(sdPdata,AesKey);
        Log.d("***+++encrypt+++***", encrypt);
        return encrypt;
    }

    //访问服务器，并取得访问结果
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            Log.d("****file****", file);
            Log.d("****data****", data);
            URL searchUrl = buildUrl(file);
            Log.d("****URL****", searchUrl.toString());
            String githubSearchResults = null;
            try {
                githubSearchResults = getResponseFromHttpUrl(searchUrl,data, mDeviceId);
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
                    String message = obj.getString(getString(R.string.message));
                    Log.d("***+++msg+++***", message);
                    if(processResult) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        switch (flg){
                            case "0":
                                setData(returnData);
                                break;
                            case "1":
                                Intent intentBasicinfoedit = new Intent();
                                intentBasicinfoedit.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intentBasicinfoedit.setClass(PersonalSetActivity.this, BasicinfoeditActivity.class);
                                intentBasicinfoedit.putExtra("Act", "person");
                                intentBasicinfoedit.putExtra("resume_status", "");
                                intentBasicinfoedit.putExtra("resume_Num", "");
                                startActivity(intentBasicinfoedit);
                                break;
                            case "2":
                                Intent intentChangepw = new Intent();
                                intentChangepw.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intentChangepw.setClass(PersonalSetActivity.this, ChangepwActivity.class);
                                startActivity(intentChangepw);
                                break;
                            case "3":
                                Intent intentResume = new Intent();
                                intentResume.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intentResume.setClass(PersonalSetActivity.this, ResumeActivity.class);
                                startActivity(intentResume);
                                break;
                        }
                    }
                    else {
                        alertdialog(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    //設定の基本情報をセット
    private void setData(String data){
        String datas = AESprocess.getdecrypt(data, mAesKey);
        Log.d(TAG,"datas:"+ datas);
        try {
            JSONObject obj = new JSONObject(datas);
            if(obj.has(getString(R.string.id))){
                mMyApplication.setMyApplicationFlg("1");
                mMyApplication.setid(obj.getString(getString(R.string.id)));
                mMyApplication.setuser_id(obj.getString(getString(R.string.user_id)));
                mMyApplication.setfirst_name(obj.getString(getString(R.string.first_name)));
                mMyApplication.setlast_name(obj.getString(getString(R.string.last_name)));
                mMyApplication.setfirst_name_kana(obj.getString(getString(R.string.first_name_kana)));
                mMyApplication.setlast_name_kana(obj.getString(getString(R.string.last_name_kana)));
                mMyApplication.setbirthday(obj.getString(getString(R.string.birthday)));
                mMyApplication.setsex_div(obj.getString(getString(R.string.sex_div)));
                mMyApplication.setcountry(obj.getString(getString(R.string.country)));
                mMyApplication.setpost_code(obj.getString(getString(R.string.post_code)));
                mMyApplication.setadd_1(obj.getString(getString(R.string.add_1)));
                mMyApplication.setadd_2(obj.getString(getString(R.string.add_2)));
                mMyApplication.setadd_3(obj.getString(getString(R.string.add_3)));
                mMyApplication.setadd_4(obj.getString(getString(R.string.add_4)));
                mMyApplication.setphone_number(obj.getString(getString(R.string.phone_number)));
                JSONArray Resumes = obj.getJSONArray(getString(R.string.Resumes));
                Log.d(TAG,"datas:"+ Resumes);
                int ResumeNum = 0;
                for(int i = 0; i < Resumes.length(); i++){
                    Gson mGson = new Gson();
                    Resume resumedata = mGson.fromJson(Resumes.getString(i),Resume.class);
                    resumeList.add(i,resumedata);
                    if(i == 0 && ! resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_1(resumedata.getId());
                        mMyApplication.setresume_name(resumedata.getresume_name(),"1");
                        ResumeNum += 1;
                    } else if(i == 1 && ! resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_2(resumedata.getId());
                        mMyApplication.setresume_name(resumedata.getresume_name(),"2");
                        ResumeNum += 1;
                    } else if(i == 2 && ! resumedata.getId().equals("")){
                        mPreferenceUtils.setresumeid_3(resumedata.getId());
                        mMyApplication.setresume_name(resumedata.getresume_name(),"3");
                        ResumeNum += 1;
                    }
                }
                mPreferenceUtils.setresume_number(ResumeNum);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setViews();
    }

    //通信结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage("他の端末から既にログインしています。もう一度ログインしてください。").setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                mPreferenceUtils.clear();
                mMyApplication.clear();
                Intent intentClose = new Intent();
                intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mMyApplication.setAct(getString(R.string.Search));
                intentClose.setClass(PersonalSetActivity.this, SearchActivity.class);
                intentClose.putExtra("act", "");
                startActivity(intentClose);
            }
        }).show();
    }


    //子功能画面按钮
    private View.OnClickListener Listener =new View.OnClickListener() {
        public void onClick(View View) {
            switch (View.getId()) {
                //退出登录
                case R.id.tr_LoginOut:
                    logout();
                    break;
                case R.id.tr_basicinfoedit:
                    goToBasicinfoedit();
                    break;
                case R.id.tr_changpw:
                    goToChangPW();
                    break;
                case R.id.tr_licenses:
                    Intent intent = new Intent();
                    intent.setClass(PersonalSetActivity.this, OssLicensesMenuActivity.class);
                    startActivity(intent);
                    break;
                case R.id.tr_termsofservice:
                    goToAgreement(getString(R.string.termsofservice));
                    break;
                case R.id.tr_privacypolicy:
                    goToAgreement(getString(R.string.privacypolicy));
                    break;
            }

        }
    };

    //履歴書画面按钮
    private View.OnClickListener resumeListener =new View.OnClickListener() {
        public void onClick(View View) {
            String ResumeIdNum = "1";
            String ResumeStatus = View.getTag().toString();
            mPreferenceUtils.setsaveid(getString(R.string.PreferenceUtils));
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setClass(PersonalSetActivity.this, ResumeActivity.class);
            switch (View.getId()) {
                //検索画面に
                case 0:
                    ResumeIdNum = "1";
                    break;
                case 1:
                    ResumeIdNum = "2";
                    break;
                case 2:
                    ResumeIdNum = "3";
                    break;
            }
            mMyApplication.setResumeId(ResumeIdNum);
            mMyApplication.setresume_status(ResumeStatus);
            startActivity(intent);
        }
    };

    //履歴書设定
    public void load(){
        int resumeNumber = mPreferenceUtils.getresume_number();
        String Email = mPreferenceUtils.getEmail();
//        deviceId = PreferenceUtils.getdeviceId();
        if(mMyApplication.getlast_name().length() > 0){
            String name = mMyApplication.getlast_name() + mMyApplication.getfirst_name() + " 様";
            Log.d("PersonalSetActivity", "name:" + name);
            tvname.setText(name);
        }
        tvemail.setText(Email);
        Log.d("***resumeNumber***", "" + resumeNumber);

    }
    private void logout(){
        PostDate Pdata = new PostDate();
        Pdata.setUserId(mPreferenceUtils.getuserId());
        Pdata.setToken(mPreferenceUtils.gettoken());
        Pdata.setDeviceType("2");
        Gson mGson1 = new Gson();
        String sdPdata = mGson1.toJson(Pdata,PostDate.class);
        AES mAes = new AES();
        byte[] mBytes = null;
        try {
            mBytes = sdPdata.getBytes("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String enString = mAes.encrypt(mBytes, mPreferenceUtils.getAesKey());
        String data = enString.replace("\n", "").replace("+","%2B");
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_logout);
        param.put(getString(R.string.data),data);
        //数据通信处理（访问服务器，并取得访问结果）
        CommonAsyncTask commonAsyncTask = new CommonAsyncTask();
        commonAsyncTask.setParams(mPreferenceUtils.getdeviceId());
        commonAsyncTask.setListener(new CommonAsyncTask.Listener() {
            @Override
            public void onSuccess(String results) {
                Log.d("PersonalSetActivity", "onSuccess: " + results);
                switch (mPreferenceUtils.getLoginFlag()){
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
                mPreferenceUtils.clear();
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(PersonalSetActivity.this, SearchActivity.class);
                intent.putExtra("act", "");
                startActivity(intent);
            }
        });
        commonAsyncTask.execute(param);
    }
    private void setViews() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        CustomPagerAdapter adapter = new CustomPagerAdapter(this);
        adapter.setItemList(resumeList);
        viewPager.setAdapter(adapter);
    }

    public class CustomPagerAdapter extends PagerAdapter {

        /** コンテキスト. */
        private Context mContext;

        /** リスト. */
        private ArrayList<Resume> mList;

        /**
         * コンストラクタ.
         */
        public CustomPagerAdapter(Context context) {
            mContext = context;
            mList = new ArrayList<Resume>();
        }

        /**
         * リストにアイテムを追加する.
         */
        public void setItemList(ArrayList<Resume> itemList) {
            mList = itemList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem position: " + position);
            Log.d(TAG, "instantiateItem mList.size(): " + mList.size());
            View view = getLayoutInflater().inflate(R.layout.include_resumeset, null);
            LinearLayout includeResumesetLinearlayout = (LinearLayout) view.findViewById(R.id.include_resumeset_linearlayout);
            LinearLayout linearLayoutResume = (LinearLayout) view.findViewById(R.id.linearLayout_resume);
            ImageView addImageView = (ImageView) view.findViewById(R.id.imageview_add);
            TextView resumeName = (TextView) view.findViewById(R.id.resume_name);
            includeResumesetLinearlayout.setId(position);
            includeResumesetLinearlayout.setOnClickListener(resumeListener);
            if(mList.size() ==0 || mList.size() == position){
                addImageView.setVisibility(View.VISIBLE);
                linearLayoutResume.setVisibility(View.GONE);
                includeResumesetLinearlayout.setTag("add");
            } else {
                addImageView.setVisibility(View.GONE);
                linearLayoutResume.setVisibility(View.VISIBLE);
//                Resume item = mList.get(position);
//                resumeName.setText(item.getresume_name());
                includeResumesetLinearlayout.setTag("upd");
            }
            // コンテナに追加
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // コンテナから View を削除
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            int resumeNumber = mList.size();
            int totalPageNumber = 1;
            switch (resumeNumber) {
                case 0:
                    totalPageNumber = 1;
                    break;
                case 1:
                    totalPageNumber = 2;
                    break;
                case 2:
                    totalPageNumber = 3;
                    break;
                case 3:
                    totalPageNumber = 3;
                    break;
            }
            return totalPageNumber;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // Object 内に View が存在するか判定する
            return view == (View) object;
        }
    }

    private void goToAgreement(String touchBtn){
        mMyApplication.setActivity("PersonalSetActivity");
        mMyApplication.setAgreement(touchBtn);
        Intent intent = new Intent();
        intent.setClass(PersonalSetActivity.this, AgreementActivity.class);
        startActivity(intent);
    }
}


