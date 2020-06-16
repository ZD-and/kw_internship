package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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

public class ContactDialogActivity extends AppCompatActivity {

//    final static String PARAM_File = "/SessionMessageMobile/selectByEmployerIdAndApplicantId";
//    final static String PARAM_sendMessage = "/SessionMessageMobile/sendMessage";
//    final static String PARAM_Readed = "/SessionMessageMobile/personalSetReaded";

    final static String PARAM_File = "/MyMailMobile/MyMailList";
    final static String PARAM_sendMessage = "/MyMailMobile/pendingMyMail";
    final static String PARAM_Readed = "/MyMailMobile/setReaded";

    private LinearLayout llmeg;
    private TableLayout tlinformation;
    private TableLayout tlmeg;
    private TableLayout tlnamedate;
    private ScrollView slmeg;
    private ImageView imvivread;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private TextView tltvcompany;
    //    private TextView tltvmailaddress;
    private EditText ettitle;
    private EditText etmeg;
    private TextView tvallEmail;
    private TextView tvsendEmail;
    private TextView tvReceptionEmail;
    private TextView tvToCompanyName;

    private Button bu_sendmeg;

    private String deviceId;
    private String AesKey;
    private String userid;
    private String token;
    private String employer_id;
    private String company_name;
    //    private String mailaddress;
    private String Email;
    //private String name = "";
    private String setTitle = "";
    private String setmeg = "";
    private String DisplayEmailFlg = "0";
//    private String Act = "";

    private String sendflg = "0";

    private int iIndex = -1;

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
    private LinkedList<JSONObject> list_obj;

//    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_dialog);
        Intent Intent = getIntent();
//        Act = Intent.getStringExtra("Act");
        Initialization();
        if(mMyApplication.getContactDialog(0).equals("0")){
            employer_id = Intent.getStringExtra(getString(R.string.ID));
            company_name = Intent.getStringExtra(getString(R.string.company_name));
//            mailaddress = Intent.getStringExtra("mailaddress");
            Log.d("company_name", company_name);
            mMyApplication.setContactDialog("1",0);
            mMyApplication.setContactDialog(employer_id,1);
            mMyApplication.setContactDialog(company_name,2);
//            mMyApplication.setContactDialog(mailaddress,3);
            getSearchResults();
        } else {
            employer_id = mMyApplication.getContactDialog(1);
            company_name = mMyApplication.getContactDialog(2);
//            mailaddress = mMyApplication.getContactDialog(3);
            try {
                JSONArray obj = new JSONArray(mMyApplication.getContactDialog(3));
                for(int x=0; x < obj.length(); x++){
                    list_String.add(x,obj.getString(x));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Collections.reverse(list_String);
            DisplayEmailFlg = mMyApplication.getContactDialog(4);
            if(DisplayEmailFlg.equals("")){
                //flg为空的时候，显示全部的邮件
                DisplayEmail(getString(R.string.tv_allEmail));
            }else if(DisplayEmailFlg.equals("1")){
                //flg为1的时候，显示收到的邮件
                DisplayEmail(getString(R.string.tv_sendEmail));
            }else if(DisplayEmailFlg.equals("0")){
                //flg为1的时候，显示发送的邮件
                DisplayEmail(getString(R.string.tv_ReceptionEmail));
            }
        }
        tvbacktitle.setText(getString(R.string.tvbacktitle));
        tltvcompany.setText(company_name);
//        tltvmailaddress.setText(mailaddress);
        setViews();
    }
    private void setViews() {
        FragmentManager manager = getSupportFragmentManager();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        ExampleFragmentPagerAdapter adapter = new ExampleFragmentPagerAdapter(manager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class ExampleFragmentPagerAdapter extends FragmentPagerAdapter {
        public ExampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    educationInfo("");
                    return ExampleFragment.newInstance(position);
                case 1:
                    educationInfo("1");
                    return ExampleFragment.newInstance(position);
                case 2:
                    educationInfo("0");
                    return ExampleFragment.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String name = "";
            switch (position) {
                case 0:
                    return "すべて";
                case 1:
                    return "受信トレイ";
                case 2:
                    return "送信済み";
            }
            return null;
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        return super.onTouchEvent(event);
//    }

    //初期化
    private void Initialization(){
        bu_sendmeg=findViewById(R.id.bu_sendmeg);
        bu_sendmeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_setSendMeg();
            }
        });
        list_tlnamedata = new LinkedList<TableLayout>();
        list_tlreply    = new LinkedList<TableLayout>();
        list_tvtitle    = new LinkedList<TextView>();
        list_tvsubtitle = new LinkedList<TextView>();
        list_tvmeg      = new LinkedList<TextView>();
        list_tvisReaded = new LinkedList<TextView>();
        list_ivread     = new LinkedList<ImageView>();
        list_String     = new LinkedList<String>();
        list_obj     = new LinkedList<JSONObject>();
        llmeg           = (LinearLayout) findViewById(R.id.ll_meg);
        tlinformation   = (TableLayout) findViewById(R.id.tl_information);
        tlmeg           = (TableLayout) findViewById(R.id.tl_meg);
        slmeg           =  (ScrollView) findViewById(R.id.sl_meg);
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
//        tltvmailaddress     = (TextView) findViewById(R.id.tl_tv_mailaddress);
        ettitle        = (EditText) findViewById(R.id.et_title);
        etmeg          = (EditText) findViewById(R.id.et_meg);

//        tvallEmail = (TextView) findViewById(R.id.tv_allEmail);
//        tvsendEmail = (TextView) findViewById(R.id.tv_sendEmail);
//        tvReceptionEmail = (TextView) findViewById(R.id.tv_ReceptionEmail);
//        tvallEmail.setOnClickListener(this);
//        tvsendEmail.setOnClickListener(this);
//        tvReceptionEmail.setOnClickListener(this);

        tvToCompanyName  = (TextView) findViewById(R.id.tv_ToCompanyName);
        mPreferenceUtils = new PreferenceUtils(ContactDialogActivity.this);
        mMyApplication = (MyApplication) getApplication();
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userid = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        Email = mPreferenceUtils.getEmail();
    }


    //获取搜索结果
    public void getSearchResults(){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        Pdata.setemployerUserId(employer_id);
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.file),PARAM_File);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
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

        String name = "";
        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            name = map.get(getString(R.string.name));
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
                        if(!name.equals(getString(R.string.isReaded))){
                            decryptchange(obj.getString(getString(R.string.returnData)),name);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
//            dialog.dismiss();
        }
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data,String inputname){
        AESprocess AESprocess = new AESprocess();
        String decryptdata = AESprocess.getdecrypt(data,AesKey);
        Log.d("***decryptdata***", decryptdata);
        try {
            if (inputname.equals(getString(R.string.SendMeg))) {
                JSONObject obj = new JSONObject(decryptdata);
                JSONObject objPendingMail = obj.getJSONObject(getString(R.string.PendingMail));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                Log.d("***strDate***", strDate);
                Setsendmeg(objPendingMail.getString(getString(R.string.mail_title)), objPendingMail.getString(getString(R.string.mail_content)), strDate);
            }else {
                JSONArray obj = new JSONArray(decryptdata);
                for(int x=0; x < obj.length(); x++){
                    try {
                        list_String.add(x,obj.getString(x));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mMyApplication.setContactDialog(decryptdata,3);
                mMyApplication.setContactDialog(DisplayEmailFlg,4);
//                Collections.reverse(list_String);
                educationInfo("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //通信信息取得
    public void educationInfo(String flg){
        slmeg.scrollTo(0,0);
        llmeg.removeAllViews();
        list_tlnamedata.clear();
        list_tvtitle.clear();
        list_tvsubtitle.clear();
        list_tvmeg.clear();
        list_tlreply.clear();
        list_ivread.clear();
        list_obj.clear();
        for(int i=0; i < list_String.size(); i++){
            try {
                JSONObject obj = new JSONObject(list_String.get(i));
                JSONObject objMyMail = obj.getJSONObject(getString(R.string.MyMail));
                Log.d("***obj***", objMyMail.toString());
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
                tvdata.setText(objMyMail.getString(getString(R.string.send_time)));
//                String Title = obj.getString("mailTitle");
//                String mailTitle = new String(android.util.Base64.decode(Title.getBytes(), android.util.Base64.DEFAULT));
//                String Content = obj.getString("mailContent");
//                String mailContent = new String(android.util.Base64.decode(Content.getBytes(), android.util.Base64.DEFAULT));
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
//                    tvcompany_name.setGravity(Gravity.RIGHT);
//                    tvdata.setGravity(Gravity.RIGHT);
//                    ivread.setVisibility(GONE);
                    tvisReaded.setVisibility(GONE);
                }

                //收信的场合不显示送信邮件
                if(flg.equals("1") && objMyMail.getString(getString(R.string.direction)).equals("0")){
                    dialog.setVisibility(GONE);

                }
                //送信的场合不显示收信邮件
                else if(flg.equals("0") && objMyMail.getString(getString(R.string.direction)).equals("1")){
                    dialog.setVisibility(GONE);
                }
                llmeg.addView(dialog,i);
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
        slmeg.setVisibility(VISIBLE);
    }
    //返回联络画面
    public void Click_back(){
        Log.d("sendflg", sendflg);
        if(sendflg.equals("1")){
            if(! ettitle.getText().toString().equals(setTitle) || ! etmeg.getText().toString().equals(setmeg)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("").setMessage(getString(R.string.setMessage)).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确定按钮的点击事件
                        sendflg = "0";
                        slmeg.setVisibility(VISIBLE);
                        tlmeg.setVisibility(GONE);
                        ettitle.setText("");
                        etmeg.setText("");
                    }
                }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消按钮的点击事件
                    }
                }).show();
            } else {
                sendflg = "0";
                slmeg.setVisibility(VISIBLE);
                tlmeg.setVisibility(GONE);
                ettitle.setText("");
                etmeg.setText("");
            }
        } else {
//            switch (View.getId()){
//                case R.id.tv_back:
                    mMyApplication.setContactDialog("0",0);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(ContactDialogActivity.this, ContactActivity.class);
                    startActivity(intent);
//                    break;
//            }
        }
    }
    //显示邮件
//    public void onClick(View View){
//        String name = "";
//        switch (View.getId()){
//            case R.id.tv_allEmail:
//                name = getString(R.string.tv_allEmail);
//                break;
//            case R.id.tv_sendEmail:
//                name = getString(R.string.tv_sendEmail);
//                break;
//            case R.id.tv_ReceptionEmail:
//                name = getString(R.string.tv_ReceptionEmail);
//                break;
//        }
//        DisplayEmail(name);
//    }
    //显示邮件的背景设定
    public void DisplayEmail(String name){
        switch (name){
            case "tv_allEmail":
                //显示全部的邮件
//                tvallEmail.setBackgroundResource(R.drawable.frame_left_bule);
//                tvsendEmail.setBackgroundResource(R.drawable.frame_center_null);
//                tvReceptionEmail.setBackgroundResource(R.drawable.frame_right_null);
//                tvallEmail.setTextColor(Color.parseColor("#ffffffff"));
//                tvsendEmail.setTextColor(Color.parseColor("#0196FF"));
//                tvReceptionEmail.setTextColor(Color.parseColor("#0196FF"));
                DisplayEmailFlg = "";
                educationInfo("");
                break;
            case "tv_sendEmail":
                //显示收到的邮件
//                tvallEmail.setBackgroundResource(R.drawable.frame_left_null);
//                tvsendEmail.setBackgroundResource(R.drawable.frame_center_bule);
//                tvReceptionEmail.setBackgroundResource(R.drawable.frame_right_null);
//                tvallEmail.setTextColor(Color.parseColor("#0196FF"));
//                tvsendEmail.setTextColor(Color.parseColor("#ffffffff"));
//                tvReceptionEmail.setTextColor(Color.parseColor("#0196FF"));
                DisplayEmailFlg = "1";
                educationInfo("1");
                break;
            case "tv_ReceptionEmail":
                //显示发送的邮件
//                tvallEmail.setBackgroundResource(R.drawable.frame_left_null);
//                tvsendEmail.setBackgroundResource(R.drawable.frame_center_null);
//                tvReceptionEmail.setBackgroundResource(R.drawable.frame_right_bule);
//                tvallEmail.setTextColor(Color.parseColor("#0196FF"));
//                tvsendEmail.setTextColor(Color.parseColor("#0196FF"));
//                tvReceptionEmail.setTextColor(Color.parseColor("#ffffffff"));
                DisplayEmailFlg = "0";
                educationInfo("0");
                break;
        }
        //flg保存到全局变量里
        mMyApplication.setContactDialog(DisplayEmailFlg,4);
    }
    //点击回复或者新规的按钮触发事件
    public void Click_setmeg(View View){
        if (View == null) {
            return;
        }
        sendflg = "1";
        tvToCompanyName.setText("To:" + company_name);
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
                        ettitle.setText(setTitle);
                        etmeg.setText(setmeg);
                        break;
                    }
                }
                break;
        }
        //当前界面隐藏
        slmeg.setVisibility(GONE);
        //显示送信界面
        tlmeg.setVisibility(VISIBLE);
    }
    //将发送的信息添加到列表的最上方
    public void Setsendmeg(String title,String meg,String date){
        slmeg.setVisibility(VISIBLE);
        tlmeg.setVisibility(GONE);
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
        llmeg.addView(dialog,0);
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
    //送信处理
    public void Click_setSendMeg(){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        Pdata.setemployerId(employer_id);
        Pdata.setmailTitle(ettitle.getText().toString());
        Pdata.setmailContent(etmeg.getText().toString());
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.file),PARAM_sendMessage);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),getString(R.string.SendMeg));
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }
}
