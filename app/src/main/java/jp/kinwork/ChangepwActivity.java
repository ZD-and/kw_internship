package jp.kinwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class ChangepwActivity extends BaseActivity {
    final static String PARAM_File = "/usersMobile/passwordChange";
    private EditText etpasswordOld;
    private EditText etpasswordNew;
    private EditText etpasswordNewConform;
    private ImageView ivpersonalsettings;
    private TextView tvpersonalsettings;
    private TextView tvback;
    private TextView tvbacktitle;
    private TextView tvbackdummy;
    private String passwordOld;
    private String passwordNew;
    private String passwordNewConform;
    private String UserId;
    private String deviceId;
    private String AesKey;
    private String token;
    private MyApplication mMyApplication;
    private Button changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepw);
        mMyApplication = (MyApplication) getApplication();
        Initialization();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

    public void Initialization(){
        load();
        tvback          = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_back();
            }
        });
        tvbacktitle               = (TextView) findViewById(R.id.tv_back_title);
        tvbackdummy               = (TextView) findViewById(R.id.tv_back_dummy);
        tvback.setText(getString(R.string.personalsettings));
        tvbackdummy.setText(getString(R.string.personalsettings));

        changePassword=findViewById(R.id.bu_pwc);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_changepw();
            }
        });

        ivpersonalsettings = (ImageView) findViewById(R.id.iv_b_personalsettings);
        tvpersonalsettings = (TextView) findViewById(R.id.tv_b_personalsettings);
        ivpersonalsettings.setImageResource(R.mipmap.blue_personalsettings);
        tvpersonalsettings.setTextColor(Color.parseColor("#5EACE2"));
        etpasswordOld = (EditText) findViewById(R.id.et_oldpw);
        etpasswordNew = (EditText) findViewById(R.id.et_newpw);
        etpasswordNewConform = (EditText) findViewById(R.id.et_newpwConform);
        if(!mMyApplication.getpersonalset(0).equals("0")){
            etpasswordOld.setText(mMyApplication.getpersonalset(1));
            etpasswordNew.setText(mMyApplication.getpersonalset(2));
            etpasswordNewConform.setText(mMyApplication.getpersonalset(3));
        }

        // 新建一个可以添加文本的对象
        SpannableString ee = new SpannableString(getString(R.string.password));
        // 设置文本字体大小
        AbsoluteSizeSpan aee = new AbsoluteSizeSpan(12, true);
        // 将字体大小附加到文本的属性
        ee.setSpan(aee, 0, ee.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint属性
        etpasswordNew.setHint(new SpannedString(ee));//转码
        // 新建一个可以添加文本的对象
        SpannableString eec = new SpannableString(getString(R.string.Spannable));
        // 设置文本字体大小
        AbsoluteSizeSpan aeec = new AbsoluteSizeSpan(12, true);
        // 将字体大小附加到文本的属性
        eec.setSpan(aeec, 0, eec.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint属性
        etpasswordNewConform.setHint(new SpannedString(eec));//转码

        init();

    }


    //菜单栏按钮
//    public void ll_Click(View View){
//        mMyApplication.setpersonalset("2",0);
//        mMyApplication.setpersonalset(etpasswordOld.getText().toString(),1);
//        mMyApplication.setpersonalset(etpasswordNew.getText().toString(),2);
//        mMyApplication.setpersonalset(etpasswordNewConform.getText().toString(),3);
//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        switch (View.getId()){
//            case R.id.ll_b_search:
//                mMyApplication.setAct(getString(R.string.Search));
//                if(mMyApplication.getSURL(0).equals("0")){
//                    if(mMyApplication.getSApply(0).equals("0")){
//                        if(mMyApplication.getSearchResults(0).equals("0")){
//                            intent.setClass(ChangepwActivity.this, SearchActivity.class);
//                            intent.putExtra("act","");
//                        } else {
//                            intent.setClass(ChangepwActivity.this, SearchResultsActivity.class);
//                        }
//                    } else {
//                        intent.setClass(ChangepwActivity.this, ApplyActivity.class);
//                    }
//                } else {
//                    intent.setClass(ChangepwActivity.this, WebActivity.class);
//                    Initialization();
//                }
//                break;
//            //Myリスト画面に移動
//            case R.id.ll_b_contact:
//                if(mMyApplication.getContactDialog(0).equals("0")){
//                    intent.setClass(ChangepwActivity.this, ContactActivity.class);
//                } else {
//                    intent.setClass(ChangepwActivity.this, ContactDialogActivity.class);
//                }
//                break;
//            case R.id.ll_b_mylist:
//                mMyApplication.setAct(getString(R.string.Apply));
//                if(mMyApplication.getMURL(0).equals("0")){
//                    if(mMyApplication.getMApply(0).equals("0")){
//                        intent.setClass(ChangepwActivity.this, MylistActivity.class);
//                    } else {
//                        intent.setClass(ChangepwActivity.this, ApplyActivity.class);
//                    }
//                } else {
//                    intent.setClass(ChangepwActivity.this, WebActivity.class);
//                }
//                break;
//            //跳转个人设定画面
//            case R.id.ll_b_personalsettings:
//                intent.setClass(ChangepwActivity.this, ChangepwActivity.class);
//                break;
//        }
//        startActivity(intent);
//    }

    public void Click_back(){
        mMyApplication.setpersonalset("0",0);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(ChangepwActivity.this, PersonalSetActivity.class);
        startActivity(intent);
    }

    public void Click_changepw(){
        passwordOld = etpasswordOld.getText().toString();
        passwordNew = etpasswordNew.getText().toString();
        passwordNewConform = etpasswordNewConform.getText().toString();
        //Json格式转换并且加密
        String data = JsonChnge(AesKey,UserId, token,passwordOld,passwordNew,passwordNewConform);
        Map<String,String> param = new HashMap<String, String>();
        param.put("file",PARAM_File);
        param.put("data",data);
        //数据通信处理（访问服务器，并取得访问结果）
        new GithubQueryTask().execute(param);
    }



    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,String data_a,String data_b,String data_c,String data_d,String data_e) {
        PostDate Pdata = new PostDate();
        Pdata.setUserId(data_a);
        Pdata.setToken(data_b);
        Pdata.setPasswordOld(data_c);
        Pdata.setPassword(data_d);
        Pdata.setPasswordConform(data_e);
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Pdata,PostDate.class);
        Log.d("***+++sdPdata+++***", sdPdata);
        AESprocess AESprocess = new AESprocess();
        String encrypt = AESprocess.getencrypt(sdPdata,AesKey);
        Log.d("***+++encrypt+++***", encrypt);
        return encrypt;
    }

    //结果提示
    private void alertdialog(String meg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("エラー").setMessage(meg).setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确定按钮的点击事件
            }
        }).show();
    }


    //登録内容を保存
    public void SaveDate(String name ,String value){
        //创建SharedPreferences对象
        SharedPreferences sp_LoginInformation = getSharedPreferences("Information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_LoginInformation.edit();
        editor.putString(name,value);
        editor.commit();
    }

    //删除保存内容
    public void removeDate(String name){
        //创建SharedPreferences对象
        SharedPreferences sp_LoginInformation = getSharedPreferences("Information", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_LoginInformation.edit();
        editor.remove(name);
        editor.commit();
    }

    //本地情报取得
    public void load(){
        SharedPreferences object = getSharedPreferences("Information", Context.MODE_PRIVATE);
        deviceId = object.getString(getString(R.string.Information_Name_deviceId),"A");
        AesKey = object.getString(getString(R.string.Information_Name_aesKey),"A");
        UserId = object.getString(getString(R.string.userid),"A");
        token = object.getString(getString(R.string.token),"A");
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
                    String message = obj.getString(getString(R.string.message));
                    Log.d("***+++msg+++***", message);
                    if(processResult == true) {
                        Intent intent_personalsettings = new Intent();
                        intent_personalsettings.setClass(ChangepwActivity.this, PersonalSetActivity.class);
                        startActivity(intent_personalsettings);
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
}
