package jp.kinwork.Common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zml98 on 2018/05/10.
 */

public class PreferenceUtils {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public PreferenceUtils(Context context) {
        sp = context.getSharedPreferences("Information", context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public String getEmail() {
        return sp.getString("email", "A");
    }
    public int getresume_number() {
        return sp.getInt("resume_number", 0);
    }
    public String getresumeid_1() {
        return sp.getString("resumeid_1", "A");
    }
    public String getresumeid_2() {
        return sp.getString("resumeid_2", "A");
    }
    public String getresumeid_3() {
        return sp.getString("resumeid_3", "A");
    }
    public String getdeviceId() {
        return sp.getString("deviceId", "A");
    }
    public String getAesKey() {
        return sp.getString("aesKey", "A");
    }
    public String getuserId() {
        return sp.getString("userid", "A");
    }
    public String gettoken() {
        return sp.getString("token", "A");
    }
    public String getUserFlg() {
        return sp.getString("userflg", "0");
    }
    public String getsaveid() {
        return sp.getString("save_id", "A");
    }
    public String getbasicInfoID() {
        return sp.getString("basicInfoID", "A");
    }

    public void setbasicInfoID(String basicInfoID) {
        editor.putString("basicInfoID", basicInfoID);
        editor.commit();
    }
    public void setresume_number(int resume_number) {
        editor.putInt("resume_number", resume_number);
        editor.commit();
    }
    public void setresumeid_1(String resumeid_1) {
        editor.putString("resumeid_1", resumeid_1);
        editor.commit();
    }
    public void setresumeid_2(String resumeid_2) {
        editor.putString("resumeid_2", resumeid_2);
        editor.commit();
    }
    public void setresumeid_3(String resumeid_3) {
        editor.putString("resumeid_3", resumeid_3);
        editor.commit();
    }
    public void setdeviceId(String deviceId) {
        editor.putString("deviceId", deviceId);
        editor.commit();
    }
    public void setAesKey(String aesKey) {
        editor.putString("aesKey", aesKey);
        editor.commit();
    }
    public void setuserId(String userid) {
        editor.putString("userid", userid);
        editor.commit();
    }
    public void settoken(String token) {
        editor.putString("token", token);
        editor.commit();
    }
    public void setUserFlg(String userflg) {
        editor.putString("userflg", userflg);
        editor.commit();
    }
    public void setsaveid(String save_id) {
        editor.putString("save_id", save_id);
        editor.commit();
    }
    public void setemail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

    public void delresumeid() {
        editor.remove("resumeid_1");
        editor.remove("resumeid_2");
        editor.remove("resumeid_3");
        editor.commit();
    }

    public void del(String name) {
        editor.remove(name);
        editor.commit();
    }

    public void clear() {
        editor.clear().commit();
    }

    public void deluserinfo() {
        editor.remove("userid");
        editor.remove("token");
        editor.remove("email");
        editor.commit();
    }
}
