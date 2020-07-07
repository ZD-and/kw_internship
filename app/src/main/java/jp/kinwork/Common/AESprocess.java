package jp.kinwork.Common;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import jp.kinwork.R;

/**
 * Created by zml98 on 2018/04/11.
 */

public class AESprocess {

    public static String getencrypt(String endata, String Key){
        Log.d("AESprocess", "getencrypt endata: " + endata);
        AES mAes = new AES();
        byte[] mBytes = null;
            try {
            mBytes = endata.getBytes("UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String enString = mAes.encrypt(mBytes,Key);
        String encrypt = enString.replace("\n", "").replace("+","%2B");
        return encrypt;
    }

    public static String getdecrypt(String dedata, String Key){

        AES mAes = new AES();
        String decryp = mAes.decrypt(dedata,Key);
        return decryp;
    }
}
