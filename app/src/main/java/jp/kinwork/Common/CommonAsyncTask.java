package jp.kinwork.Common;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.URL;
import java.util.Map;


import static jp.kinwork.Common.NetworkUtils.buildUrl;
import static jp.kinwork.Common.NetworkUtils.getResponseFromHttpUrl;

public class CommonAsyncTask extends AsyncTask<Map<String, String>, Void, String> {

    private Listener listener;
    private String deviceId;

    public void setParams(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSuccess(String results);
    }


    @Override
    protected String doInBackground(Map<String, String>... params) {
        Map<String, String> map = params[0];
        String file = map.get("file");
        String data = map.get("data");
        URL searchUrl = buildUrl(file);
        String results = null;
        try {
            results = getResponseFromHttpUrl(searchUrl,data,deviceId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
    @Override
    protected void onPostExecute(String results) {
        if (listener != null) {
            listener.onSuccess(results);
        }
    }
}
