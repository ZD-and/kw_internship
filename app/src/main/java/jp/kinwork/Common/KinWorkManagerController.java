package jp.kinwork.Common;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import jp.kinwork.LoginActivity;
import jp.kinwork.R;

public class KinWorkManagerController {
    private String TAG = KinWorkManagerController.class.getCanonicalName();
    private Context context;
    private MyApplication mMyApplication = new MyApplication();
    private PreferenceUtils mPreferenceUtils;
    final static String PARAM_setAndroidToken = "/usersMobile/setAndroidToken";

    public void setContext(Context context) {
        this.context = context;
        mPreferenceUtils = new PreferenceUtils(context);
    }
    public Context getContext() {
        return context;
    }

    public void getDeviceTokenToServer(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String deviceToken = task.getResult().getToken();
                        if(deviceToken != null && !deviceToken.equals("")){
                            sendDeviceTokenToServer(deviceToken);
                        }
                    }
                });
    }

    //内容取得、通信
    private void sendDeviceTokenToServer(String deviceToken) {
        //Json格式转换并且加密
        PostDate Pdata = new PostDate();
        Pdata.setUserId(mPreferenceUtils.getuserId());
        Pdata.setToken(mPreferenceUtils.gettoken());
        Pdata.setAndroidToken(deviceToken);
        Gson mGson1 = new Gson();
        String sdPdata = AESprocess.getencrypt(mGson1.toJson(Pdata,PostDate.class),mPreferenceUtils.getAesKey());;
        Map<String,String> param = new HashMap<String, String>();
        param.put(context.getString(R.string.file), PARAM_setAndroidToken);
        param.put(context.getString(R.string.data),sdPdata);
        //数据通信处理（访问服务器，并取得访问结果）
        CommonAsyncTask commonAsyncTask = new CommonAsyncTask();
        commonAsyncTask.setParams(mPreferenceUtils.getdeviceId());
        commonAsyncTask.setListener(new CommonAsyncTask.Listener() {
            @Override
            public void onSuccess(String results) {
                if(results != null && !results.equals("")){
                    Log.d(TAG, "onSuccess results: " + results);
                    try {
                        JSONObject obj = new JSONObject(results);
                        Boolean processResult = obj.getBoolean(context.getString(R.string.processResult));
                        mPreferenceUtils.setSendAndroidTokenProcessResult(processResult);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        commonAsyncTask.execute(param);

    }
}
