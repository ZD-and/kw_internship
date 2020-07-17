package jp.kinwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.kinwork.Common.AES;
import jp.kinwork.Common.AESprocess;
import jp.kinwork.Common.MyApplication;
import jp.kinwork.Common.PostDate;
import jp.kinwork.Common.PreferenceUtils;

import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class BootanimationActivity extends AppCompatActivity {
    //初次通信
    final static String PARAM_A = "/Common/initialCommunication";
    //Mypage获取初始所有数据
    final static String PARAM_B = "/MypagesMobile/initMypageData";
    private final int SPLASH_DISPLAY_LENGHT = 3000; // 两秒后进入系统

    //    private boolean sProcessResult;
    private String savedeviceId;
    private String saveAesKey;
    private String loaddeviceId;
    private String loadAesKey;
    private String loadUserId;
    private String loadToken;
    private String AccessFineFocation = "";
    private String Initial_Name;
    private String Information_Name;
    private String flg = "";
    private MyApplication mMyApplication;
    private PreferenceUtils mPreferenceUtils;
    private LocationManager mLocationManager;

    private String TAG = "BootanimationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootanimation);
        Initial_Name = getString(R.string.Initial);
        Information_Name = getString(R.string.Information);

        mMyApplication = (MyApplication) getApplication();
        mPreferenceUtils = new PreferenceUtils(BootanimationActivity.this);
        load();
        SaveDate(Initial_Name,getString(R.string.Result),"1");
        mMyApplication.setSearchResults("0",0);
        mMyApplication.setContactDialog("0",0);
        mMyApplication.setSApply("0",0);
        mMyApplication.setMApply("0",0);
        mMyApplication.setSURL("0",0);
        mMyApplication.setMURL("0",0);
        mMyApplication.setpersonalset("0",0);
        mMyApplication.setkeyword("");
        mMyApplication.setaddress("");
        Log.d(TAG +"***onCreate***", "onCreate-1" );
        if (loaddeviceId.equals("A") || loadAesKey.equals("A")) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                        1000);
            } else {
                locationStart();
            }
        }else {
//            Check();
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                AccessFineFocation = "1";
            }
            locationStart();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG+"onRequestrequestCode", requestCode+"");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            AccessFineFocation = "1";
        }
        locationStart();
    }

    //判断是否是初次通信
    public void Check(){
        Log.d(TAG, "Check: ");
        load();
        Log.d(TAG+"loaddeviceId", loaddeviceId);
        Log.d(TAG+"loadAesKey", loadAesKey);
        SaveDate(Initial_Name,"Result","1");
        if (loaddeviceId.equals("A") || loadAesKey.equals("A"))
        {
            urllodad();
        }else {
            SaveDate(Information_Name,getString(R.string.Information_Name_aesKey),loadAesKey);
            SaveDate(Information_Name,getString(R.string.Information_Name_deviceId),loaddeviceId);
            urlMypageData();
        }
    }

    //登録内容を保存
    public void SaveDate(String file ,String name ,String value){
        //创建SharedPreferences对象
        SharedPreferences sp_LoginInformation = getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp_LoginInformation.edit();
        editor.putString(name,value);
        editor.commit();
    }

    //设备IDと対象Key取得
    public void load(){
        SharedPreferences Initial_object = getSharedPreferences(getString(R.string.Initial), Context.MODE_PRIVATE);
        loaddeviceId = Initial_object.getString("deviceId","A");
        loadAesKey = Initial_object.getString("aesKey","A");
        SharedPreferences Information_object = getSharedPreferences(getString(R.string.Information), Context.MODE_PRIVATE);
        loadUserId = Information_object.getString("userid","A");
        loadToken = Information_object.getString("token","A");
        mMyApplication.setuser_id(loadUserId);
        mMyApplication.setToken(loadToken);
    }

    //初次通信
    public void urllodad() {
        Map<String,String> param = new HashMap<String, String>();
        param.put(getString(R.string.file),PARAM_A);
        param.put(getString(R.string.data),"2");
        flg = "1";
        new GithubQueryTask().execute(param);
    }

    //初始所有数据
    public void urlMypageData(){
        load();
        Log.d(TAG+"loadUserId", loadUserId);
        Log.d(TAG+"loadToken", loadToken);
        if(! loadUserId.equals("A") && ! loadToken.equals("A")){
            //Json格式转换并且加密
            String data = JsonChnge(loadAesKey,loadUserId,loadToken);
            Map<String,String> param = new HashMap<String, String>();
            param.put(getString(R.string.file),PARAM_B);
            param.put(getString(R.string.data),data);
            flg = "2";
            //数据通信处理（访问服务器，并取得访问结果）
            new GithubQueryTask().execute(param);
        }
        else {
            Bootanimation();
        }
//        locationStart();
    }

    //转换为Json格式并且AES加密
    public static String JsonChnge(String AesKey,String Data_a,String Data_b) {
        PostDate Pdata = new PostDate();
        Pdata.setUserId(Data_a);
        Pdata.setToken(Data_b);
        Gson mGson = new Gson();
        String sdPdata = mGson.toJson(Pdata,PostDate.class);

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

    //解密，并且保存得到的数据
    public void decryptchange(String data){
        AESprocess AESprocess = new AESprocess();
        String datas = AESprocess.getdecrypt(data,loadAesKey);
        Log.d(TAG+"***+++解密datas+++***", datas);
        try {
            JSONObject obj = new JSONObject(datas);
            if(obj.has(getString(R.string.id))){
                mMyApplication.setMyApplicationFlg("1");
                mPreferenceUtils.setbasicInfoID(obj.getString(getString(R.string.id)));
            }
            if(obj.has(getString(R.string.user_id))){
                mMyApplication.setuser_id(obj.getString(getString(R.string.user_id)));
            }
            if(obj.has(getString(R.string.first_name))){
                mMyApplication.setfirst_name(obj.getString(getString(R.string.first_name)));
            }
            if(obj.has(getString(R.string.last_name))){
                mMyApplication.setlast_name(obj.getString(getString(R.string.last_name)));
            }
            if(obj.has(getString(R.string.first_name_kana))){
                mMyApplication.setfirst_name_kana(obj.getString(getString(R.string.first_name_kana)));
            }
            if(obj.has(getString(R.string.last_name_kana))){
                mMyApplication.setlast_name_kana(obj.getString(getString(R.string.last_name_kana)));
            }
            if(obj.has(getString(R.string.birthday))){
                mMyApplication.setbirthday(obj.getString(getString(R.string.birthday)));
            }
            if(obj.has(getString(R.string.sex_div))){
                mMyApplication.setsex_div(obj.getString(getString(R.string.sex_div)));
            }
            if(obj.has(getString(R.string.country))){
                mMyApplication.setcountry(obj.getString(getString(R.string.country)));
            }
            if(obj.has(getString(R.string.post_code))){
                mMyApplication.setpost_code(obj.getString(getString(R.string.post_code)));
            }
            if(obj.has(getString(R.string.add_1))){
                mMyApplication.setadd_1(obj.getString(getString(R.string.add_1)));
            }
            if(obj.has(getString(R.string.add_2))){
                mMyApplication.setadd_2(obj.getString(getString(R.string.add_2)));
            }
            if(obj.has(getString(R.string.add_3))){
                mMyApplication.setadd_3(obj.getString(getString(R.string.add_3)));
            }
            if(obj.has(getString(R.string.add_4))){
                mMyApplication.setadd_4(obj.getString(getString(R.string.add_4)));
            }
            if(obj.has(getString(R.string.phone_number))){
                mMyApplication.setphone_number(obj.getString(getString(R.string.phone_number)));
            }
            setResumeInfo(obj.getJSONArray(getString(R.string.Resumes)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //通信处理
    public class GithubQueryTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String file = map.get(getString(R.string.file));
            String data = map.get(getString(R.string.data));
            URL searchUrl = buildUrl(file);
            String githubSearchResults = null;
            try {
                if(flg.equals("1")){
                    githubSearchResults = getResponseFromHttpUrl(searchUrl,data,"");
                } else {
                    githubSearchResults = getResponseFromHttpUrl(searchUrl,data,loaddeviceId);
                }
            } catch (IOException e) {
                SaveDate(Initial_Name,getString(R.string.Result),"0");
                e.printStackTrace();
            }
            return githubSearchResults;
        }
        @Override
        protected void onPostExecute(String githubSearchResults) {
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                try {
                    Log.d(TAG+"***Results***", githubSearchResults);
                    JSONObject obj = new JSONObject(githubSearchResults);
                    Boolean processResult = obj.getBoolean(getString(R.string.processResult));
                    if(processResult == true)
                    {
                        String returnData = obj.getString(getString(R.string.returnData));
                        if(flg.equals("1")){
                            JSONObject objData = new JSONObject(returnData);
                            savedeviceId = objData.getString(getString(R.string.Information_Name_deviceId));
                            saveAesKey = objData.getString(getString(R.string.Information_Name_aesKey));
                            SaveDate(Initial_Name,getString(R.string.Information_Name_deviceId),savedeviceId);
                            SaveDate(Initial_Name,getString(R.string.Information_Name_aesKey),saveAesKey);
                            SaveDate(Information_Name,getString(R.string.Information_Name_deviceId),savedeviceId);
                            SaveDate(Information_Name,getString(R.string.Information_Name_aesKey),saveAesKey);
                            urlMypageData();
                        } else {
                            decryptchange(returnData);
                        }
                    } else{
                        JSONObject objError = new JSONObject(obj.getString(getString(R.string.showErrors)));
                        String errorCode = "";
                        if(objError.has(getString(R.string.errorCode))){
                            errorCode = objError.getString(getString(R.string.errorCode));
                            switch(errorCode){
                                case "999":
                                    clearSP();
                                    urllodad();
                                    break;
                            }
                        } else {
                            SaveDate(Initial_Name,getString(R.string.Result),"0");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            Bootanimation();
        }
    }

    //跳转至主画面
    private void Bootanimation(){
        mMyApplication.setAccessFineFocation(AccessFineFocation);
        if (loaddeviceId.equals("A") || loadAesKey.equals("A")) {
            Intent SearchIntent = new Intent(BootanimationActivity.this,SearchActivity.class);
            SearchIntent.putExtra(getString(R.string.act),"Boot");
            startActivity(SearchIntent);
        } else {
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent SearchIntent = new Intent(BootanimationActivity.this,SearchActivity.class);
                    SearchIntent.putExtra(getString(R.string.act),"Boot");
                    startActivity(SearchIntent);
                }
            }, SPLASH_DISPLAY_LENGHT);
        }
    }

    //已登录職歴信息取得
    public void setResumeInfo(JSONArray data){
        int num = 0;
        for(int i=0; i < data.length(); i++){
            try {
                JSONObject obj = data.getJSONObject(i);
                Log.d(TAG+"***ResumeInfo***", data.getString(i));
                num = num + 1;
                if(num == 1 && obj.getString(getString(R.string.id)) != null){
                    mPreferenceUtils.setresumeid_1(obj.getString(getString(R.string.id)));
                    mPreferenceUtils.setresume_number(num);
                    mMyApplication.setresume_name(obj.getString(getString(R.string.resume_name)),"1");
                } else if (num == 2 && obj.getString(getString(R.string.id)) != null){
                    mPreferenceUtils.setresumeid_2(obj.getString(getString(R.string.id)));
                    mPreferenceUtils.setresume_number(num);
                    mMyApplication.setresume_name(obj.getString(getString(R.string.resume_name)),"2");
                } else if (num == 3 && obj.getString(getString(R.string.id)) != null){
                    mPreferenceUtils.setresumeid_3(obj.getString(getString(R.string.id)));
                    mPreferenceUtils.setresume_number(num);
                    mMyApplication.setresume_name(obj.getString(getString(R.string.resume_name)),"3");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //本地保存信息清空
    private void clearSP(){
        SharedPreferences sp = getSharedPreferences("Information", 0);
        SharedPreferences.Editor editor = sp.edit();
        //用clear()方法清除数据，commit()保存清除结果
        editor.clear().commit();
    }

    private void locationStart() {
        Log.d(TAG+"debug", "locationStart()");
        // LocationManager インスタンス生成
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d(TAG+"debug", "locationStart() AccessFineFocation:" +AccessFineFocation);
        if (AccessFineFocation.equals("1")) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG+"debug", "checkSelfPermission false");
            }
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
            criteria.setAltitudeRequired(false);//不要求海拔信息
            criteria.setBearingRequired(false);//不要求方位信息
            criteria.setCostAllowed(false);//是否允许付费
            criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
            Location Location = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(criteria,true));
            Log.d(TAG+"debug", "locationStart() Location:" +Location);
            if (Location != null) {
                getaddress_components(Location);
            } else {
                Log.w("Location", "= null");
                Check();
            }
        } else {
            Check();
        }
    }

    public class GithubQueryTask2 extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            Map<String, String> map = params[0];
            String url = map.get(getString(R.string.url));
            String itude = map.get(getString(R.string.itude));
            String key = map.get(getString(R.string.key));
            Log.d(TAG+"***url***", url + itude + key);
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
                Log.d(TAG+"***Results***", githubSearchResults);
                try {
                    JSONObject obj = new JSONObject(githubSearchResults);
                    JSONArray job = obj.getJSONArray("results");
                    Log.d(TAG+"***job***", job.get(0).toString());
                    mMyApplication.setaddress_components(job.get(0).toString());
                    Log.d(TAG+"***job***", job.get(0).toString());
                    Check();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getaddress_components(Location location){
        // 緯度の表示
        Map<String,String> param = new HashMap<String, String>();
        param.put(getString(R.string.url),"https://maps.google.com/maps/api/geocode/json?latlng=");
        param.put(getString(R.string.itude),location.getLatitude() + ","+location.getLongitude());
        param.put(getString(R.string.key),"&sensor=false&language=ja&key=AIzaSyBzSkvprYMmBmLWaon_uBWJEiJ9DH21B6g");
        new GithubQueryTask2().execute(param);
//        Check();
    }
}
