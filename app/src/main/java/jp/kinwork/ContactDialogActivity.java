package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class ContactDialogActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    final static String PARAM_File = "/MyMailMobile/MyMailList";
    final static String PARAM_sendMessage = "/MyMailMobile/pendingMyMail";
    final static String PARAM_Readed = "/MyMailMobile/setReaded";

    private LinkedList<LinearLayout> list_llmeg;
    private LinkedList<ScrollView> list_slmeg;

    private ImageView imvivread;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private TextView tltvcompany;

    private String deviceId;
    private String AesKey;
    private String userid;
    private String token;
    private String employer_id;
    private String company_name;
    private String Email;
    private String setTitle = "";
    private String setmeg = "";
    private String DisplayEmailFlg = "0";
    private String sendflg = "0";


    private PreferenceUtils mPreferenceUtils;
    private MyApplication mMyApplication;

    private LinkedList<TableLayout> list_tlnamedata;
    private LinkedList<TableLayout> list_tlreply;
    private LinkedList<TextView> list_tvtitle;
    private LinkedList<TextView> list_tvsubtitle;
    private LinkedList<TextView> list_tvmeg;
    private LinkedList<TextView> list_tvisReaded;
    private LinkedList<ImageView> list_ivread;
    private LinkedList<String> list_String;
    private LinkedList<String> list_StringAll;
    private LinkedList<String> list_StringReceive;
    private LinkedList<String> list_StringSend;
    private LinkedList<JSONObject> list_obj;
//    private LinkedList<EditText>list_ettitle;
//    private LinkedList<EditText>list_etmeg;
//    private LinkedList<TextView>list_tvToCompanyName;

    String TAG = "ContactDialogActivity";

    private ViewPager viewPager;
    private List<View> pages;
    private int index=0;
    private int nMaildisplaypage = 0;
    private int currentpageAll=1;
    private int currentpageReceive=1;
    private int currentpageSend=1;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        dialog = new ProgressDialog(this);
        dialog.setMessage("通信中");
        //初始化viewpager页面
        initPages();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new customViewPagerAdapter(pages);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        Intent intent = getIntent();
        Initialization();
        Log.d(TAG, "onStart mMyApplication.getContactDialog(0): " + mMyApplication.getContactDialog(0));
        Log.d(TAG, "onStart mMyApplication.getcompany_name(): " + mMyApplication.getcompany_name());
        Log.d(TAG, "onStart mMyApplication.getemployerID(): " + mMyApplication.getemployerID());

        if(mMyApplication.getContactDialog(0).equals("0")){
//            employer_id = intent.getStringExtra(getString(R.string.ID));
//            company_name = intent.getStringExtra(getString(R.string.company_name));
            employer_id = mMyApplication.getemployerID();
            company_name = mMyApplication.getcompany_name();
            Log.d(TAG, "onStart employer_id: " + employer_id);
            Log.d(TAG, "company_name:"+company_name);
            mMyApplication.setContactDialog("1",0);
            mMyApplication.setContactDialog(employer_id,1);
            mMyApplication.setContactDialog(company_name,2);
            getSearchResults("1","all");
            getSearchResults("1","receive");
            getSearchResults("1","send");
        } else {
            employer_id = mMyApplication.getContactDialog(1);
            company_name = mMyApplication.getContactDialog(2);
            try {
                JSONArray obj = new JSONArray(mMyApplication.getContactDialog(3));
                for(int x=0; x < obj.length(); x++){
                    list_String.add(x,obj.getString(x));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            DisplayEmailFlg = mMyApplication.getContactDialog(4);
//            if(DisplayEmailFlg.equals("")){
//                //flg为空的时候，显示全部的邮件
//                DisplayEmail(getString(R.string.tv_allEmail));
//            }else if(DisplayEmailFlg.equals("1")){
//                //flg为1的时候，显示收到的邮件
//                DisplayEmail(getString(R.string.tv_sendEmail));
//            }else if(DisplayEmailFlg.equals("0")){
//                //flg为1的时候，显示发送的邮件
//                DisplayEmail(getString(R.string.tv_ReceptionEmail));
//            }
            getSearchResults("1","all");
            getSearchResults("1","receive");
            getSearchResults("1","send");
        }
        tvbacktitle.setText(getString(R.string.tvbacktitle));
        tltvcompany.setText(company_name);
    }

    private void initPages() {
        pages = new ArrayList<View>();
        View page01 = View.inflate(ContactDialogActivity.this,R.layout.fragment_first,null);
        View page02 = View.inflate(ContactDialogActivity.this,R.layout.fragment_second,null);
        View page03 = View.inflate(ContactDialogActivity.this,R.layout.fragment_third,null);
        pages.add(page01);
        pages.add(page02);
        pages.add(page03);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public class customViewPagerAdapter extends PagerAdapter {
        private String[] mTitles = new String[]{"すべて", "受信トレイ", "送信済み"};
        List<View> pages;
        public customViewPagerAdapter(List<View> pages){
            this.pages = pages;
        };

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object==view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pages.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pages.get(position));
        }

        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    //初期化
    private void Initialization(){
        list_tlnamedata = new LinkedList<TableLayout>();
        list_tlreply    = new LinkedList<TableLayout>();
        list_tvtitle    = new LinkedList<TextView>();
        list_tvsubtitle = new LinkedList<TextView>();
        list_tvmeg      = new LinkedList<TextView>();
        list_tvisReaded = new LinkedList<TextView>();
        list_ivread     = new LinkedList<ImageView>();
        list_String     = new LinkedList<String>();
        list_StringAll     = new LinkedList<String>();
        list_StringReceive     = new LinkedList<String>();
        list_StringSend     = new LinkedList<String>();
        list_obj     = new LinkedList<JSONObject>();
        list_llmeg = new LinkedList<LinearLayout>();
        list_slmeg = new LinkedList<ScrollView>();

        list_llmeg.clear();
        list_llmeg.add(0,(LinearLayout) pages.get(0).findViewById(R.id.ll_meg_first));
        list_llmeg.add(1,(LinearLayout) pages.get(1).findViewById(R.id.ll_meg_second));
        list_llmeg.add(2,(LinearLayout) pages.get(2).findViewById(R.id.ll_meg_third));
        list_slmeg.clear();
        list_slmeg.add(0,(ScrollView) pages.get(0).findViewById(R.id.sl_meg_first));
        list_slmeg.add(1,(ScrollView) pages.get(1).findViewById(R.id.sl_meg_second));
        list_slmeg.add(2,(ScrollView) pages.get(2).findViewById(R.id.sl_meg_third));

        list_slmeg.get(0).setOnTouchListener(onTouchListener);
        list_slmeg.get(0).setTag("0");
        list_slmeg.get(1).setOnTouchListener(onTouchListener);
        list_slmeg.get(1).setTag("1");
        list_slmeg.get(2).setOnTouchListener(onTouchListener);
        list_slmeg.get(2).setTag("2");

        tvback          = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle               = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.title_contact));
        tvbackdummy.setText(getString(R.string.title_contact));
        tltvcompany     = (TextView) findViewById(R.id.tl_tv_company);

        mPreferenceUtils = new PreferenceUtils(ContactDialogActivity.this);
        mMyApplication = (MyApplication) getApplication();
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userid = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        Email = mPreferenceUtils.getEmail();
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN :
                    break;
                case MotionEvent.ACTION_MOVE :
                    index++;
                    break;
                default :
                    break;
            }
            Log.d(TAG, "onTouch v.getTag(): " + v.getTag());
            if (motionEvent.getAction() == MotionEvent.ACTION_UP &&  index > 0) {
                index = 0;
                int i = Integer.parseInt(v.getTag().toString());
                View view = (list_slmeg.get(i)).getChildAt(0);
                Log.d(TAG, "onTouch view: " + view);
                Log.d(TAG, "onTouch v: " + v);
                if (view != null && v != null && view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                    String type = "";
                    String page = "";
                    switch (i){
                        case 0:
                            currentpageAll=currentpageAll+1;
                            page = Integer.toString(currentpageAll);
                            type = "all";
                            break;
                        case 1:
                            currentpageReceive=currentpageReceive+1;
                            page = Integer.toString(currentpageReceive);
                            type = "receive";
                            break;
                        case 2:
                            currentpageSend=currentpageSend+1;
                            page = Integer.toString(currentpageSend);
                            type = "send";
                            break;

                    }
                    getSearchResults(page,type);
                }
            }
            return false;
        }
    };

    //获取搜索结果
    public void getSearchResults(String page,String Type){
        dialog.show();
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        Pdata.setemployerUserId(employer_id);
        Pdata.setpage(page);
        Pdata.setType(Type);//all:全部　receive:受信 send:送信
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.file),PARAM_File);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        param.put("Type",Type);
        nMaildisplaypage=Integer.parseInt(page)-1;
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }

    //转换为Json格式并且AES加密
    public String JsonChnge(String AesKey,PostDate Data) {
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Data,PostDate.class);
        Log.d(TAG,"sdPdata:"+ sdPdata);
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
        String name = "";
        String type="";
        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            name = map.get(getString(R.string.name));
            type=map.get("Type");
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
            Log.d(TAG,"Results:"+ githubSearchResults);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = obj.getString(getString(R.string.message));
                    String errorCode = obj.getString(getString(R.string.errorCode));
                    if(processResult == true) {
                        Log.d("***returnData***", obj.getString(getString(R.string.returnData)));
                        if(!name.equals(getString(R.string.isReaded))){
                            decryptchange(obj.getString(getString(R.string.returnData)),name,type);
                        }
                    }else {
                        if(errorCode.equals("101")){
                            if(type.equals("all")) {
                                currentpageAll = currentpageAll - 1;
                            }else if(type.equals("receive")){
                                currentpageReceive = currentpageReceive - 1;
                            }else{
                                currentpageSend = currentpageSend - 1;
                            }
                        }
                        if(errorCode.equals("100")){
                            message = "他の端末から既にログインしています。もう一度ログインしてください。";
                            alertdialog(message,errorCode);
                        } else if(!errorCode.equals("101")){
                            alertdialog(message,errorCode);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data,String inputname,String type){
        AESprocess AESprocess = new AESprocess();
        String decryptdata = AESprocess.getdecrypt(data,AesKey);
        Log.d(TAG,"decryptdata:"+ decryptdata);
        try {
            if (inputname.equals(getString(R.string.SendMeg))) {
                JSONObject obj = new JSONObject(decryptdata);
                JSONObject objPendingMail = obj.getJSONObject(getString(R.string.PendingMail));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                Log.d(TAG,"strDate:"+ strDate);
                Setsendmeg(list_slmeg.get(0),list_llmeg.get(0),objPendingMail.getString(getString(R.string.mail_title)), objPendingMail.getString(getString(R.string.mail_content)), strDate);
            }else{
                Log.d(TAG, "decryptchange type: " +type);
                JSONArray obj = new JSONArray(decryptdata);
                Log.d(TAG, "decryptchange obj.length(): " +obj.length());
                if(type.equals("all")) {
                    for (int x = (nMaildisplaypage * 10) + 0; x < (nMaildisplaypage * 10) + obj.length(); x++) {
                        try {
                            list_StringAll.add(x, obj.getString(x - (nMaildisplaypage * 10)));
                            list_String = list_StringAll;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "decryptchange list_StringAll.size(): " +list_StringAll.size());
                    Log.d(TAG, "decryptchange list_String.size(): " +list_String.size());
                    mMyApplication.setContactDialog(decryptdata, 3);
                    mMyApplication.setContactDialog(DisplayEmailFlg, 4);
                    educationInfo(0);
                }else if(type.equals("receive")){
                    for (int x = (nMaildisplaypage * 10) + 0; x < (nMaildisplaypage * 10) + obj.length(); x++) {
                        try {
                            list_StringReceive.add(x, obj.getString(x - (nMaildisplaypage * 10)));
                            list_String = list_StringReceive;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "decryptchange list_StringReceive.size(): " +list_StringReceive.size());
                    Log.d(TAG, "decryptchange list_String.size(): " +list_String.size());
                    mMyApplication.setContactDialog(decryptdata, 3);
                    mMyApplication.setContactDialog(DisplayEmailFlg, 4);
                    educationInfo(1);
                }else{
                    for (int x = (nMaildisplaypage * 10) + 0; x < (nMaildisplaypage * 10) + obj.length(); x++) {
                        try {
                            list_StringSend.add(x, obj.getString(x - (nMaildisplaypage * 10)));
                            list_String = list_StringSend;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "decryptchange list_StringSend.size(): " +list_StringSend.size());
                    Log.d(TAG, "decryptchange list_String.size(): " +list_String.size());
                    mMyApplication.setContactDialog(decryptdata, 3);
                    mMyApplication.setContactDialog(DisplayEmailFlg, 4);
                    educationInfo(2);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //通信信息取得
    public void educationInfo(int pages){
        list_llmeg.get(pages).removeAllViews();
        Log.d(TAG, "educationInfo pages: " + pages);
        Log.d(TAG, "educationInfo list_StringAll.size(): " +list_StringAll.size());
        Log.d(TAG, "educationInfo list_StringReceive.size(): " +list_StringReceive.size());
        Log.d(TAG, "educationInfo list_StringSend.size(): " +list_StringSend.size());
        Log.d(TAG, "educationInfo list_String.size(): " +list_String.size());
        for(int i=0; i < list_String.size(); i++){
            try {
                JSONObject obj = new JSONObject(list_String.get(i));
                JSONObject objMyMail = obj.getJSONObject(getString(R.string.MyMail));
                Log.d(TAG,"obj:"+ objMyMail.toString());
                View dialog = getLayoutInflater().inflate(R.layout.include_dialog, null);
                TableLayout tltlTableLayout = (TableLayout) dialog.findViewById(R.id.tl_tl_TableLayout);
                TableLayout tlnamedata = (TableLayout) dialog.findViewById(R.id.tl_name_data);
                tlnamedata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_meg_visibility(v);
                    }
                });
                TextView tvcompany_name = (TextView) dialog.findViewById(R.id.tl_tv_company_name);
                TextView tvisReaded = (TextView) dialog.findViewById(R.id.tv_isReaded);
                ImageView ivread = (ImageView) dialog.findViewById(R.id.iv_read);
                TextView tvdata = (TextView) dialog.findViewById(R.id.tl_tv_data);
                TextView tvtitle = (TextView) dialog.findViewById(R.id.tl_tv_title);
                TextView tvsubtitle = (TextView) dialog.findViewById(R.id.tl_tv_sub_title);
                TextView tvmeg = (TextView) dialog.findViewById(R.id.tl_tv_meg);
                TableLayout tlreply = (TableLayout) dialog.findViewById(R.id.tl_reply);
                tlreply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_setmeg(v);
                    }
                });
                TableLayout tlnew = (TableLayout) dialog.findViewById(R.id.tl_new);
                tlnew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Click_setmeg(v);
                    }
                });
                tvdata.setText(objMyMail.getString(getString(R.string.send_time)));
                String mailContent = objMyMail.getString(getString(R.string.mail_content)).replace("<br />","");
                tvtitle.setText(objMyMail.getString(getString(R.string.mail_title)));
                tvsubtitle.setText(objMyMail.getString(getString(R.string.mail_title)));
                tvmeg.setText(mailContent);
                String isReaded = objMyMail.getString(getString(R.string.is_readed));
                if(objMyMail.getString(getString(R.string.direction)).equals("1")){
                    tltlTableLayout.setBackgroundResource(R.drawable.ic_shape_w_bule);
                    tvcompany_name.setText(company_name + " から");
                    tvcompany_name.setGravity(Gravity.LEFT);
                    tvdata.setGravity(Gravity.LEFT);
                    if(isReaded.equals("1")){
                        tvisReaded.setVisibility(GONE);
                    }
                } else {
                    tltlTableLayout.setBackgroundResource(R.drawable.ic_shape_w_bule_green);
                    tvcompany_name.setText(company_name + " へ");
                    tvisReaded.setVisibility(GONE);
                }
                list_llmeg.get(pages).addView(dialog,i);
                list_tlnamedata.add(i,tlnamedata);
                list_tvtitle.add(i,tvtitle);
                list_tvsubtitle.add(i,tvsubtitle);
                list_tvmeg.add(i,tvmeg);
                list_tlreply.add(i,tlreply);
                list_tvisReaded.add(i,tvisReaded);
                list_ivread.add(i,ivread);
                list_obj.add(i,objMyMail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        list_slmeg.get(pages).setVisibility(VISIBLE);
    }
    //返回联络画面
    public void Click_back(){
        mMyApplication.setContactDialog("0",0);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(ContactDialogActivity.this, ContactActivity.class);
        startActivity(intent);
    }
    //显示邮件的背景设定
    public void DisplayEmail(String name){
        switch (name){
            case "tv_allEmail":
                //显示全部的邮件
                DisplayEmailFlg = "";
                educationInfo(0);
                break;
            case "tv_sendEmail":
                //显示收到的邮件
                DisplayEmailFlg = "1";
                educationInfo(1);
                break;
            case "tv_ReceptionEmail":
                //显示发送的邮件
                DisplayEmailFlg = "0";
                educationInfo(2);
                break;
        }
        //flg保存到全局变量里
        mMyApplication.setContactDialog(DisplayEmailFlg,4);
    }
    //点击回复或者新规的按钮触发事件
    public void Click_setmeg(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (View == null) {
            return;
        }
//        int nPageNum = viewPager.getCurrentItem();
        sendflg = "1";
//        list_tvToCompanyName.get(nPageNum).setText("To:" + company_name);
        setTitle = "";
        setmeg = "";
        // 判断第几个按钮触发了事件
        int iIndex = -1;
        TextView Click_tvsubtitle = new TextView(this);
        TextView Click_tvmeg = new TextView(this);
        switch (View.getId()){
            case R.id.tl_reply:
                //点击回复的时候设定内容
                for (int i = 0; i < list_tlreply.size(); i++) {
                    if (list_tlreply.get(i) == View) {
                        iIndex = i;
                        Click_tvsubtitle = list_tvsubtitle.get(iIndex);
                        Click_tvmeg = list_tvmeg.get(iIndex);
                        String ti = Click_tvsubtitle.getText().toString();
                        String tm = Click_tvmeg.getText().toString();
                        ti = ti.replace("\n","\n>");
                        tm = tm.replace("\n","\n>");
                        setTitle = "Re:" + ti;
                        setmeg = "\n\n\n\n\n\n>" + tm;
                        break;
                    }
                }
                break;
        }
        intent.setClass(this, SelectResumeActivity.class);
        intent.putExtra("companyname",company_name);
        intent.putExtra("mailtitle", setTitle);
        intent.putExtra("mailmeg", setmeg);
        intent.putExtra("emploerID",employer_id);
        startActivity(intent);
    }
    //将发送的信息添加到列表的最上方
    public void Setsendmeg(ScrollView xslmeg,LinearLayout xtlmeg,String title,String meg,String date){
        xslmeg.setVisibility(VISIBLE);
        xtlmeg.setVisibility(GONE);
        sendflg = "0";
        JSONObject obj = new JSONObject();
        try {
            obj.put("isNeedDecode",true);
            obj.put("isSended",true);
            obj.put("isReaded",true);
            obj.put("mailTitle",title);
            obj.put("mailContent",meg);
            obj.put("direction","0");
            obj.put("sendTime",date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View dialog = getLayoutInflater().inflate(R.layout.include_dialog, null);
        TableLayout tltlTableLayout = (TableLayout) dialog.findViewById(R.id.tl_tl_TableLayout);
        TableLayout tlnamedata = (TableLayout) dialog.findViewById(R.id.tl_name_data);
        TextView tvcompany_name = (TextView) dialog.findViewById(R.id.tl_tv_company_name);
        TextView tvisReaded = (TextView) dialog.findViewById(R.id.tv_isReaded);
        ImageView ivread = (ImageView) dialog.findViewById(R.id.iv_read);
        TextView tvdata = (TextView) dialog.findViewById(R.id.tl_tv_data);
        TextView tvtitle = (TextView) dialog.findViewById(R.id.tl_tv_title);
        TextView tvsubtitle = (TextView) dialog.findViewById(R.id.tl_tv_sub_title);
        TextView tvmeg = (TextView) dialog.findViewById(R.id.tl_tv_meg);
        TableLayout tlreply = (TableLayout) dialog.findViewById(R.id.tl_reply);
        TableLayout tlnew = (TableLayout) dialog.findViewById(R.id.tl_new);
        tltlTableLayout.setBackgroundResource(R.drawable.ic_shape_w_bule_green);
        tvcompany_name.setText(Email);
        tvcompany_name.setGravity(Gravity.RIGHT);
        tvdata.setGravity(Gravity.RIGHT);
        tvisReaded.setVisibility(GONE);
        ivread.setVisibility(GONE);
        tvdata.setText(date);
        tvtitle.setText(title);
        tvsubtitle.setText(title);
        tvmeg.setText(meg);
        list_llmeg.get(0).addView(dialog,0);
        Log.d("obj.toString()", obj.toString());
        list_String.addFirst(obj.toString());
        list_tlnamedata.addFirst(tlnamedata);
        list_tvtitle.addFirst(tvtitle);
        list_tvsubtitle.addFirst(tvsubtitle);
        list_tvmeg.addFirst(tvmeg);
        list_tlreply.addFirst(tlreply);
    }
    //显示具体的邮件信息，如果当前邮件为企业发来的未读信息，则设定为已读
    public void Click_meg_visibility(View View){
        if (View == null) {
            return;
        }
        int nPageNum = viewPager.getCurrentItem();
        // 判断第几个“-”按钮触发了事件
        int iIndex = -1;
        TextView Click_tvtitle = new TextView(this);
        TextView Click_tvsubtitle = new TextView(this);
        TextView Click_tvmeg = new TextView(this);
        TextView Click_tvisReaded = new TextView(this);
        for (int i = 0; i < list_tlnamedata.size(); i++) {
            if (list_tlnamedata.get(i) == View) {
                iIndex = i;
                Click_tvtitle = list_tvtitle.get(iIndex);
                Click_tvsubtitle = list_tvsubtitle.get(iIndex);
                Click_tvmeg = list_tvmeg.get(iIndex);
                Click_tvisReaded = list_tvisReaded.get(iIndex);
                imvivread = list_ivread.get(iIndex);
                break;
            }
        }
        if (iIndex >= 0) {
            String direction = "";
            String mymailid = "";
            String isReaded = "";
            JSONObject object = new JSONObject();
            object = list_obj.get(iIndex);
            try {
                direction = object.getString(getString(R.string.direction));
                mymailid =  object.getString(getString(R.string.my_mail_id));
                isReaded = object.getString(getString(R.string.is_readed));
                if(direction.equals("1") && isReaded.equals("0")){
                    Click_tvisReaded.setVisibility(GONE);
                    object.put(getString(R.string.is_readed),"1");
                    LinkedList<String> list_back = new LinkedList<String>(list_String);
                    list_String.clear();
                    for(int i = 0; i < list_back.size(); i++){
                        if(i == iIndex){
                            list_String.add(i,object.toString());
                        } else {
                            list_String.add(i,list_back.get(i));
                        }
                    }
                    PostDate Pdata = new PostDate();
                    Map<String,String> param = new HashMap<String, String>();
                    Pdata.setUserId(userid);
                    Pdata.setToken(token);
                    Pdata.setemployerId(employer_id);
                    Pdata.setMyMailId(mymailid);
                    String data = JsonChnge(AesKey,Pdata);
                    param.put(getString(R.string.file),PARAM_Readed);
                    param.put(getString(R.string.data),data);
                    param.put(getString(R.string.name),getString(R.string.isReaded));
                    //数据通信处理（访问服务器，并取得访问结果）
                    new GithubQueryTask().execute(param);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(Click_tvtitle.getVisibility() == GONE){
                Click_tvtitle.setVisibility(VISIBLE);
                Click_tvsubtitle.setVisibility(GONE);
                Click_tvmeg.setVisibility(GONE);
                imvivread.setImageResource(R.mipmap.isreaded_f);
            } else {
                Click_tvtitle.setVisibility(GONE);
                Click_tvsubtitle.setVisibility(VISIBLE);
                Click_tvmeg.setVisibility(VISIBLE);
                imvivread.setImageResource(R.mipmap.isreaded_t);
            }
        }
    }

    private void alertdialog(String meg,String errorCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                if(errorCode.equals("100")){
                    mPreferenceUtils.clear();
                    mMyApplication.clear();
                    Intent intentClose = new Intent();
                    intentClose.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    mMyApplication.setAct(getString(R.string.Search));
                    intentClose.setClass(ContactDialogActivity.this, SearchActivity.class);
                    intentClose.putExtra("act", "");
                    startActivity(intentClose);
                } else {
                    finish();
                }
            }
        }).show();
    }
}
