package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
    private LinkedList<TableLayout> list_tlmeg;
    private LinkedList<ScrollView> list_slmeg;

    private Button bu_sendmeg_first;
    private Button bu_sendmeg_second;
    private Button bu_sendmeg_third;

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
    private String flg="";

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

    private LinkedList<EditText>list_ettitle;
    private LinkedList<EditText>list_etmeg;
    private LinkedList<TextView>list_tvToCompanyName;

    private Button nextfirst;
    private Button nextsecond;
    private Button nextthird;

    String TAG = "ContactDialogActivity";

    private ViewPager viewPager;
    private List<View> pages;
    private int index=0;
    private int nMaildisplaypage = 0;
    private boolean ismailpageend = false;
    private int currentpage=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_dialog);
        //初始化viewpager页面
        initPages();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new customViewPagerAdapter(pages);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        int nPageNum = viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Intent Intent = getIntent();
        Initialization();
        if(mMyApplication.getContactDialog(0).equals("0")){
            employer_id = Intent.getStringExtra(getString(R.string.ID));
            company_name = Intent.getStringExtra(getString(R.string.company_name));
            Log.d(TAG, "company_name:"+company_name);
            mMyApplication.setContactDialog("1",0);
            mMyApplication.setContactDialog(employer_id,1);
            mMyApplication.setContactDialog(company_name,2);
            getSearchResults("1");
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
        switch (position){
            case 0:
                educationInfo(0,"");
                break;
            case 1:
                educationInfo(1,"1");
                break;
            case 2:
                educationInfo(2,"0");
                break;
            default:break;
        }

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
        bu_sendmeg_first = pages.get(0).findViewById(R.id.bu_sendmeg_first);
        bu_sendmeg_second = pages.get(1).findViewById(R.id.bu_sendmeg_second);
        bu_sendmeg_third = pages.get(2).findViewById(R.id.bu_sendmeg_third);
        bu_sendmeg_first.setOnClickListener(Click_setSendMeg);
        bu_sendmeg_second.setOnClickListener(Click_setSendMeg);
        bu_sendmeg_third.setOnClickListener(Click_setSendMeg);

        list_tlnamedata = new LinkedList<TableLayout>();
        list_tlreply    = new LinkedList<TableLayout>();
        list_tvtitle    = new LinkedList<TextView>();
        list_tvsubtitle = new LinkedList<TextView>();
        list_tvmeg      = new LinkedList<TextView>();
        list_tvisReaded = new LinkedList<TextView>();
        list_ivread     = new LinkedList<ImageView>();
        list_String     = new LinkedList<String>();
        list_obj     = new LinkedList<JSONObject>();

        list_llmeg = new LinkedList<LinearLayout>();
        list_tlmeg = new LinkedList<TableLayout>();
        list_slmeg = new LinkedList<ScrollView>();

        list_ettitle=new LinkedList<EditText>();
        list_etmeg=new LinkedList<EditText>();
        list_tvToCompanyName=new LinkedList<TextView>();

        list_llmeg.clear();
        list_llmeg.add((LinearLayout) pages.get(0).findViewById(R.id.ll_meg_first));
        list_llmeg.add((LinearLayout) pages.get(1).findViewById(R.id.ll_meg_second));
        list_llmeg.add((LinearLayout) pages.get(2).findViewById(R.id.ll_meg_third));
        list_tlmeg.clear();
        list_tlmeg.add((TableLayout) pages.get(0).findViewById(R.id.tl_meg_first));
        list_tlmeg.add((TableLayout) pages.get(1).findViewById(R.id.tl_meg_second));
        list_tlmeg.add((TableLayout) pages.get(2).findViewById(R.id.tl_meg_third));
        list_slmeg.clear();
        list_slmeg.add((ScrollView) pages.get(0).findViewById(R.id.sl_meg_first));
        list_slmeg.add((ScrollView) pages.get(1).findViewById(R.id.sl_meg_second));
        list_slmeg.add((ScrollView) pages.get(2).findViewById(R.id.sl_meg_third));

//        nextfirst=pages.get(0).findViewById(R.id.next_first);
//        nextfirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentpage=currentpage+1;
//                getSearchResults(Integer.toString(currentpage));
//                list_slmeg.get(0).setBottom(nMaildisplaypage-1);
//                nextfirst.setVisibility(View.GONE);
//
//            }
//        });
//        nextsecond=pages.get(1).findViewById(R.id.next_second);
//        nextsecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentpage=currentpage+1;
//                getSearchResults(Integer.toString(currentpage));
//                list_slmeg.get(1).setBottom(nMaildisplaypage-1);
//                nextsecond.setVisibility(View.GONE);
//
//            }
//        });
//        nextthird=pages.get(2).findViewById(R.id.next_third);
//        nextthird.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentpage=currentpage+1;
//                getSearchResults(Integer.toString(currentpage));
//                list_slmeg.get(2).setBottom(nMaildisplaypage-1);
//                nextthird.setVisibility(View.GONE);
//
//            }
//        });

        list_slmeg.get(0).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        break;
                    case MotionEvent.ACTION_MOVE :
                        index++;
                        break;
                    default :
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP &&  index > 0) {
                    index = 0;
                    View view = (list_slmeg.get(0)).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
//                        nextfirst.setVisibility(VISIBLE);
                        currentpage=currentpage+1;
                        getSearchResults(Integer.toString(currentpage));
                        list_slmeg.get(0).setBottom(nMaildisplaypage-1);
                        flg="";
                    }

                }
                return false;
            }

        });
        list_slmeg.get(1).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        break;
                    case MotionEvent.ACTION_MOVE :
                        index++;
                        break;
                    default :
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP &&  index > 0) {
                    index = 0;
                    View view = (list_slmeg.get(1)).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
//                        nextsecond.setVisibility(VISIBLE);
                        currentpage=currentpage+1;
                        getSearchResults(Integer.toString(currentpage));
                        list_slmeg.get(1).setBottom(nMaildisplaypage-1);
                        flg="0";
                    }

                }
                return false;
            }

        });

        list_slmeg.get(2).setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        break;
                    case MotionEvent.ACTION_MOVE :
                        index++;
                        break;
                    default :
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP &&  index > 0) {
                    index = 0;
                    View view = (list_slmeg.get(2)).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
//                        nextthird.setVisibility(VISIBLE);
                        currentpage=currentpage+1;
                        getSearchResults(Integer.toString(currentpage));
                        list_slmeg.get(2).setBottom(nMaildisplaypage-1);
                        flg="1";
                    }

                }
                return false;
            }

        });



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

        list_ettitle.add((EditText) pages.get(0).findViewById(R.id.et_title_first));
        list_ettitle.add((EditText) pages.get(1).findViewById(R.id.et_title_second));
        list_ettitle.add((EditText) pages.get(2).findViewById(R.id.et_title_third));

        list_etmeg.add((EditText) pages.get(0).findViewById(R.id.et_meg_first));
        list_etmeg.add((EditText) pages.get(1).findViewById(R.id.et_meg_second));
        list_etmeg.add((EditText) pages.get(2).findViewById(R.id.et_meg_third));

        list_tvToCompanyName.add((TextView) pages.get(0).findViewById(R.id.tv_ToCompanyName_first));
        list_tvToCompanyName.add((TextView) pages.get(1).findViewById(R.id.tv_ToCompanyName_second));
        list_tvToCompanyName.add((TextView) pages.get(2).findViewById(R.id.tv_ToCompanyName_third));

        mPreferenceUtils = new PreferenceUtils(ContactDialogActivity.this);
        mMyApplication = (MyApplication) getApplication();
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userid = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        Email = mPreferenceUtils.getEmail();
    }


    //获取搜索结果
    public void getSearchResults(String number){
        PostDate Pdata = new PostDate();
        Map<String,String> param = new HashMap<String, String>();
        Pdata.setUserId(userid);
        Pdata.setToken(token);
        Pdata.setemployerUserId(employer_id);
        Pdata.setpage(number);
        Pdata.setType("all");//all:全部　receive:受信 send:送信
        String data = JsonChnge(AesKey,Pdata);
        param.put(getString(R.string.file),PARAM_File);
        param.put(getString(R.string.data),data);
        param.put(getString(R.string.name),"");
        nMaildisplaypage=Integer.parseInt(number)-1;
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
                Log.d(TAG,"Results:"+ githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String meg = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        Log.d("***returnData***", obj.getString(getString(R.string.returnData)));
                        if(!name.equals(getString(R.string.isReaded))){
                            decryptchange(obj.getString(getString(R.string.returnData)),name);
                        }
                    }else {
                        alertdialogone(meg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void alertdialogone(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage("メールはもうありません。").setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                educationInfo(0,"");
                educationInfo(1,"1");
                educationInfo(2,"0");

            }
        }).show();
    }
    //解密，并且保存得到的数据
    public void decryptchange(String data,String inputname){
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
            }else {
                JSONArray obj = new JSONArray(decryptdata);
                for(int x=(nMaildisplaypage*10)+0; x < (nMaildisplaypage*10)+obj.length(); x++){
                    try {
                        list_String.add(x,obj.getString(x-(nMaildisplaypage*10)));
//                        if(obj.getString(x-(nMaildisplaypage*10))==null){
//                            ismailpageend=true;
//                            break;
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mMyApplication.setContactDialog(decryptdata,3);
                mMyApplication.setContactDialog(DisplayEmailFlg,4);
                educationInfo(1,"1");
                educationInfo(0,"");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //通信信息取得
    public void educationInfo(int pages,String flg){
        list_slmeg.get(pages).scrollTo(0,0);
        list_llmeg.get(pages).removeAllViews();
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

                //收信的场合不显示送信邮件
                if(flg.equals("1") && objMyMail.getString(getString(R.string.direction)).equals("0")){
                    dialog.setVisibility(GONE);

                }
                //送信的场合不显示收信邮件
                else if(flg.equals("0") && objMyMail.getString(getString(R.string.direction)).equals("1")){
                    dialog.setVisibility(GONE);
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
        Log.d(TAG,"sendflg:"+ sendflg);
        int nPageNum = viewPager.getCurrentItem();
        if(sendflg.equals("1")){
            if(!list_ettitle.get(nPageNum).getText().toString().equals(setTitle) || ! list_etmeg.get(nPageNum).getText().toString().equals(setmeg)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("").setMessage(getString(R.string.setMessage)).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int nPageNum = viewPager.getCurrentItem();
                        //确定按钮的点击事件
                        sendflg = "0";
                        list_slmeg.get(nPageNum).setVisibility(VISIBLE);
                        list_tlmeg.get(nPageNum).setVisibility(GONE);
                        list_ettitle.get(nPageNum).setText("");
                        list_etmeg.get(nPageNum).setText("");
                    }
                }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消按钮的点击事件
                    }
                }).show();
            } else {
                sendflg = "0";
                list_slmeg.get(nPageNum).setVisibility(VISIBLE);
                list_tlmeg.get(nPageNum).setVisibility(GONE);
                list_ettitle.get(nPageNum).setText("");
                list_etmeg.get(nPageNum).setText("");
            }
        } else {
                    mMyApplication.setContactDialog("0",0);
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.setClass(ContactDialogActivity.this, ContactActivity.class);
                    startActivity(intent);
        }
    }
    //显示邮件的背景设定
    public void DisplayEmail(String name){
        switch (name){
            case "tv_allEmail":
                //显示全部的邮件
                DisplayEmailFlg = "";
                educationInfo(0,"");
                break;
            case "tv_sendEmail":
                //显示收到的邮件
                DisplayEmailFlg = "1";
                educationInfo(1,"1");
                break;
            case "tv_ReceptionEmail":
                //显示发送的邮件
                DisplayEmailFlg = "0";
                educationInfo(2,"0");
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
        int nPageNum = viewPager.getCurrentItem();
        sendflg = "1";
        list_tvToCompanyName.get(nPageNum).setText("To:" + company_name);
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
                        /*他のページに伝えるもの、findbyidもらえない、null値になる*/
                        list_ettitle.get(nPageNum).setText(setTitle);
                        list_etmeg.get(nPageNum).setText(setmeg);
                        break;
                    }
                }
                break;
        }
        //当前界面隐藏
        list_slmeg.get(nPageNum).setVisibility(GONE);
        //显示送信界面
        list_tlmeg.get(nPageNum).setVisibility(VISIBLE);
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
    //送信处理
    private View.OnClickListener Click_setSendMeg = new View.OnClickListener() {
        public void onClick(View View) {
            int nPageNum = viewPager.getCurrentItem();
            PostDate Pdata = new PostDate();
            Map<String, String> param = new HashMap<String, String>();
            Pdata.setUserId(userid);
            Pdata.setToken(token);
            Pdata.setemployerId(employer_id);
            Pdata.setmailTitle(list_ettitle.get(nPageNum).getText().toString());
            Pdata.setmailContent(list_etmeg.get(nPageNum).getText().toString());
            String data = JsonChnge(AesKey, Pdata);
            param.put(getString(R.string.file), PARAM_sendMessage);
            param.put(getString(R.string.data), data);
            param.put(getString(R.string.name), getString(R.string.SendMeg));
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
            alertdialog();

        }
    };
    private void alertdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage("送信しました。").setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
                finish();
            }
        }).show();
    }

}
