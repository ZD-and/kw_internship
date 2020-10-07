package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

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
import jp.kinwork.Common.CommonView.BadgeView;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.NetworkUtils;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

public class ContactActivity extends AppCompatActivity {
    final static String PARAM_File = "/MyMailMobile/getDialogList";

    private String deviceId;
    private String AesKey;
    private String userId;
    private String token;


    private ImageView ivbcontact;
    private TextView tvbcontact;
    private TextView tvtitle;
    private TextView tvname;
    private TextView tvemail;
    private TextView tvshow;

    private TableLayout tlcontact;
    private TableLayout tlcontact_son;


    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;

    private LinkedList<TableLayout> listTL_info;
    private LinkedList<String> list_employer_id;
    private LinkedList<String> list_company_name;
    private LinkedList<String> list_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Initialization();
    }

    public void Initialization() {
        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(ContactActivity.this);
        listTL_info = new LinkedList<TableLayout>();
        list_employer_id = new LinkedList<String>();
        list_company_name = new LinkedList<String>();
        list_address = new LinkedList<String>();
        tvname = (TextView) findViewById(R.id.tv_userinfo_name);
        tvemail = (TextView) findViewById(R.id.tv_userinfo_email);
        tvshow = (TextView) findViewById(R.id.tv_show);
        ivbcontact = (ImageView) findViewById(R.id.iv_b_contact);
        tvbcontact = (TextView) findViewById(R.id.tv_b_contact);
        ivbcontact.setImageResource(R.mipmap.blue_contact);
        tvbcontact.setTextColor(Color.parseColor("#5EACE2"));
        tvtitle      = (TextView) findViewById(R.id.tv_title_b_name);
        tvtitle.setText(getString(R.string.title_contact));
        tlcontact = (TableLayout) findViewById(R.id.tl_contact);
        //tlcontact.setOnClickListener(Listener);
        deviceId = mPreferenceUtils.getdeviceId();
        AesKey = mPreferenceUtils.getAesKey();
        userId = mPreferenceUtils.getuserId();
        token = mPreferenceUtils.gettoken();
        tvemail.setText(mPreferenceUtils.getEmail());
        mMyApplication.setContactDialog("0",0);
        if(mMyApplication.getlast_name().length() > 0){
            tvname.setText(mMyApplication.getlast_name() + mMyApplication.getfirst_name() + " 様");
            urllodad();
        }
    }

    public void ll_Click(View View) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()) {
            //检索画面に移動
            case R.id.ll_b_search:
                mMyApplication.setAct(getString(R.string.Search));
                if(mMyApplication.getSURL(0).equals("0")){
                    if(mMyApplication.getSApply(0).equals("0")){
                        if(mMyApplication.getSearchResults(0).equals("0")){
                            intent.setClass(ContactActivity.this, SearchActivity.class);
                            intent.putExtra(getString(R.string.act),"");
                        } else {
                            intent.setClass(ContactActivity.this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(ContactActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(ContactActivity.this, WebActivity.class);
                }
                break;
            case R.id.ll_b_contact:
                intent.setClass(ContactActivity.this, ContactActivity.class);
                break;
            //Myリスト画面に移動
            case R.id.ll_b_mylist:
                mMyApplication.setAct(getString(R.string.Apply));
                if(mMyApplication.getMURL(0).equals("0")){
                    if(mMyApplication.getMApply(0).equals("0")){
                        intent.setClass(ContactActivity.this, MylistActivity.class);
                    } else {
                        intent.setClass(ContactActivity.this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(ContactActivity.this, WebActivity.class);
                }
                break;
            //個人設定画面に移動
            case R.id.ll_b_personalsettings:
                if(mMyApplication.getpersonalset(0).equals("0")){
                    intent.setClass(ContactActivity.this, PersonalSetActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("1")){
                    intent.setClass(ContactActivity.this, BasicinfoeditActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("2")){
                    intent.setClass(ContactActivity.this, ChangepwActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("3")){
                    intent.setClass(ContactActivity.this, ResumeActivity.class);
                }
                break;
        }
        startActivity(intent);
    }

    private void urllodad() {
        PostDate Pdata = new PostDate();
        Pdata.setUserId(userId);
        Pdata.setToken(token);
        Gson mGson1 = new Gson();
        String sdPdata = mGson1.toJson(Pdata,PostDate.class);
        Log.d("***mailTitle***", sdPdata);
        AES mAes = new AES();
        byte[] mBytes = null;
        try {
            mBytes = sdPdata.getBytes("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String enString = mAes.encrypt(mBytes,AesKey);
        String data = enString.replace("\n", "").replace("+","%2B");

        Map<String,String> param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_File);
        param.put(getString(R.string.data),data);
        new GithubQueryTask().execute(param);
    }

    private class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            URL searchUrl = NetworkUtils.buildUrl(file);
            String githubSearchResults = null;
            try {
                githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl,data,deviceId);
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
                    Boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    String message = obj.getString(getString(R.string.message));
                    if(processResult == true) {
                        String returnData = obj.getString(getString(R.string.returnData));
                        decryptchange(returnData);
                    } else {
                        alertdialog(message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {

            }
        }
    }

    private void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data,AesKey);
        Log.d("***+++datas+++***", datas);
        try {
            JSONArray obj = new JSONArray(datas);
            getMessageList(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //気に入り数据取得
    public void getMessageList(JSONArray data){
        int x= -1;
        for(int i =0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d("***obj***", obj.toString());
                Log.d("***Dialog***", obj.getString(getString(R.string.Dialog)));
                Log.d("***other***", obj.getString(getString(R.string.other)));
                Log.d("***MyMail***", obj.getString(getString(R.string.MyMail)));
                JSONObject objDialog = obj.getJSONObject(getString(R.string.Dialog));
                JSONObject objother = obj.getJSONObject(getString(R.string.other));
                JSONObject objMyMail = obj.getJSONObject(getString(R.string.MyMail));
                if(objMyMail == null)
                {
                    continue;
                }
                Log.d("**objDialog**", objDialog.toString());
                Log.d("***objother***", objother.toString());
                Log.d("***objMyMail***", objMyMail.toString());
                int top= dp2px(this, 10);
                TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams();
                tlparams.setMargins(0,0,0,top);
                View contact = getLayoutInflater().inflate(R.layout.include_contact, null);
                TableLayout information = (TableLayout) contact.findViewById(R.id.tl_contact_son);
                information.setOnClickListener(Listener);
                TextView tvcompanyname = (TextView) contact.findViewById(R.id.tv_companyname);
                TextView tvlatesttime = (TextView) contact.findViewById(R.id.tv_latesttime);
                TextView tvmailtitle = (TextView) contact.findViewById(R.id.tv_mailtitle);
                TextView tvmailContent = (TextView) contact.findViewById(R.id.tv_mailContent);
                String SnotReadedCount = objother.getString(getString(R.string.SnotReadedCount));
                int InotReadedCount = Integer.parseInt(SnotReadedCount);
                if(InotReadedCount > 0){
                    BadgeView badgeView = new BadgeView(this);
                    badgeView.setTargetView(tvcompanyname);
                    badgeView.setBadgeCount(InotReadedCount);
                    badgeView.setBackground(12, Color.parseColor("#ff5040"));
                }

//                    String Title = objMyMail.getString("mail_title");
//                    String mailTitle = new String(android.util.Base64.decode(Title.getBytes(), android.util.Base64.DEFAULT));
//                    String Content = objMyMail.getString("mail_content");
//                    String mailContent = new String(android.util.Base64.decode(Content.getBytes(), android.util.Base64.DEFAULT));
//                    mailContent = mailContent.replace("\n", "");
//                    Log.d("***mailTitle***", Title);
//                    Log.d("***mailContent***", Content);
                tvcompanyname.setText(objother.getString(getString(R.string.employer_user_name)));
                tvmailtitle.setText(objMyMail.getString(getString(R.string.mail_title)));
                tvmailContent.setText(objMyMail.getString(getString(R.string.mail_content)));
                tvlatesttime.setText(objMyMail.getString(getString(R.string.send_time)));
                contact.setLayoutParams(tlparams);
                tlcontact.addView(contact,i);
                listTL_info.add(i,information);
                list_employer_id.add(i,objDialog.getString(getString(R.string.employer_user_id)));
                list_company_name.add(i,objother.getString(getString(R.string.employer_user_name)));
                list_address.add(i,objDialog.getString(getString(R.string.employer_email)));
                x += 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(x < 0){
            tvshow.setVisibility(View.VISIBLE);
        }
    }

    private int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("エラー").setMessage(meg).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }

    //気に入り職歴信息取得
    private View.OnClickListener Listener =new View.OnClickListener() {
        public void onClick(View View) {
            if (View == null) {
                return;
            }
            int iIndex = -1;
            String employer_id = "";
            String company_name = "";
            String mailaddress = "";
            for (int i = 0; i < listTL_info.size(); i++) {
                if (listTL_info.get(i) == View) {
                    iIndex = i;
                    employer_id = list_employer_id.get(i);
                    company_name = list_company_name.get(i);
                    mailaddress = list_address.get(i);
                    break;
                }
            }
            if (iIndex >= 0) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setClass(ContactActivity.this, ContactDialogActivity.class);
                intent.putExtra(getString(R.string.Act),getString(R.string.Contact));
                intent.putExtra(getString(R.string.ID),employer_id);
                intent.putExtra(getString(R.string.company_name),company_name);
                intent.putExtra(getString(R.string.mailaddress),mailaddress);
                startActivity(intent);
            }
        }
    };
}
