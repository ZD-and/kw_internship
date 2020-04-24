package jp.kinwork.Common;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by zml98 on 2018/03/29.
 */

public class NetworkUtils {

    final static String MAIN_URL ="http://39.110.199.201:7775";
//    final static String MAIN_URL ="https://www.kinwork.jp";
    public static URL buildUrl(String Sub_url) {
        String surl = MAIN_URL + Sub_url;
        URL url = null;
        try {
            url = new URL(surl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL PARA_url, String postdata, String deviceId) throws IOException {
        String data = "";
        URL url = PARA_url;
        if(! deviceId.equals("")) {
            data = "datas=" + postdata + "&deviceId=" + deviceId;
            url = PARA_url;
        }
        Log.d("***data***", data);
        Log.d("***url***", url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(3000);
        urlConnection.setReadTimeout(3000);
        urlConnection.connect();
        //urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        urlConnection.getOutputStream().write(data.getBytes());
        int code = urlConnection.getResponseCode();
        Log.d("***Results_code***", "" + code);
        if(code == 200){
            try {
                InputStream getin = urlConnection.getInputStream();
                Scanner scanner = new Scanner(getin);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        } else {
            return null;
        }

    }

}
