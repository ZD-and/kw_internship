package jp.kinwork.Common;

/**
 * Created by zml98 on 2018/04/11.
 */

public class AESprocess {

    public String getencrypt(String endata, String Key){

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

    public String getdecrypt(String dedata, String Key){

        AES mAes = new AES();
        String decryp = mAes.decrypt(dedata,Key);
        return decryp;
    }
}
