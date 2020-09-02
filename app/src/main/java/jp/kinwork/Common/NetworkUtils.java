package jp.kinwork.Common;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
//import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zml98 on 2018/03/29.
 */

public class NetworkUtils {
    String TAG = "NetworkUtils";

    //    final static String MAIN_URL ="https://www.kinwork.jp:1443";
    final static String MAIN_URL ="https://www.kinwork.jp:1444";
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
        } else {
            data = "deviceType=" + postdata;
        }

        Log.d("NetworkUtils***data***", data);
        Log.d("NetworkUtils***url***", url.toString());
        //オレオレ証明書によるSSLサーバー接続でもエラーをスルーできるようにする
//        SSLContext sslcontext = closeSSL();
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {

//            urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);

            urlConnection.connect();
            //urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            urlConnection.getOutputStream().write(data.getBytes());
            int code = urlConnection.getResponseCode();
            Log.d("NetworkUtils-code:", "" + code);
            if(code == 200){

                InputStream getin = urlConnection.getInputStream();
                Scanner scanner = new Scanner(getin);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }

    }


    private static SSLContext closeSSL(){
        //オレオレ証明書によるSSLサーバー接続でもエラーをスルーできるようにする
        SSLContext sslcontext = null;

        try {
            //証明書情報 全て空を返す
            //証明書情報　全て空を返す
            TrustManager[] tm = {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }//function
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }//function
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }//function
                    }//class
            };
            sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, tm, null);
            //ホスト名の検証ルール　何が来てもtrueを返す
            HttpsURLConnection.setDefaultHostnameVerifier(
                    new HostnameVerifier(){
                        @Override
                        public boolean verify(String hostname,
                                              SSLSession session) {
                            return true;
                        }//function
                    }//class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }//try
        return sslcontext;
    }

}
