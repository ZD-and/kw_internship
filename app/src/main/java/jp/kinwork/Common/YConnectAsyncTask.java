package jp.kinwork.Common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import jp.co.yahoo.yconnect.YConnectImplicit;
import jp.co.yahoo.yconnect.core.api.ApiClientException;
import jp.co.yahoo.yconnect.core.oidc.OIDCDisplay;
import jp.co.yahoo.yconnect.core.oidc.OIDCPrompt;
import jp.co.yahoo.yconnect.core.oidc.OIDCScope;

public class YConnectAsyncTask extends AsyncTask<String, Integer, YConnectImplicit> {
    private final static String TAG = "YConnectAsyncTask";

    private Activity activity;

    private String clientId;
    private String idTokenString;
    private String accessTokenString;
    private String nonce;
    // 同意キャンセル時の挙動を指定
    private String bail;
    // 最大認証経過時間
    private String maxAge;

    private String androidAppScheme;

    private Listener listener;
//    private String mParams;

    private String YCONNECT_PREFERENCE_NAME = "yconnect";

    public void setParams(Activity activity, String clientId, String idTokenString, String accessTokenString, String nonce, String androidAppScheme, String bail, String maxAge) {
        this.activity = activity;
        this.clientId = clientId;
        this.idTokenString = idTokenString;
        this.accessTokenString = accessTokenString;
        this.nonce = nonce;
        this.androidAppScheme = androidAppScheme;
        this.bail = bail;
        this.maxAge = maxAge;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSuccess(YConnectImplicit yconnect);
    }

    @Override
    protected YConnectImplicit doInBackground(String... params) {

        Log.d(TAG, params[0]);

        SharedPreferences sharedPreferences
                = activity.getSharedPreferences(YCONNECT_PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // YConnectインスタンス取得
        YConnectImplicit yconnect = YConnectImplicit.getInstance();

        try {
            /***************
             Verify ID Token.
             ***************/
            Log.i(TAG, "Verify ID Token.");
            // ID Tokenの検証
            yconnect.verifyIdToken(idTokenString, clientId, nonce, accessTokenString, maxAge);
            /*****************
             Request UserInfo.
             *****************/
            yconnect.requestUserInfo(accessTokenString);

            return yconnect;
        } catch (ApiClientException e) {

            // エラーレスポンスが"Invalid_Token"であるかチェック
            if ("invalid_token".equals(e.getError())) {

                /*********************
                 Refresh Access Token.
                 *********************/

                Log.i(TAG, "Refresh Access Token.");

                // ImplicitフローでAccess Tokenの有効期限がきれた場合は
                // 初回と同様に新たにAccess Tokenを取得してください

                String state = "44GC44Ga44GrWeOCk+ODmuODreODmuODrShez4leKQ==";
                String nonce = "KOOAjeODu8+J44O7KeOAjVlhaG9vISAo77yP44O7z4njg7sp77yPSkFQQU4=";
                String display = OIDCDisplay.TOUCH;
                String[] prompt = {OIDCPrompt.DEFAULT};
                String[] scope = {OIDCScope.OPENID, OIDCScope.PROFILE, OIDCScope.EMAIL, OIDCScope.ADDRESS};

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("state", state);
                editor.putString("nonce", nonce);
                editor.commit();

                // androidAppSchemeを設定する
                yconnect.init(clientId,
                        androidAppScheme, state, display, prompt, scope, nonce, bail, maxAge);
                yconnect.requestAuthorization(activity);

            }
            Log.e(TAG, "error=" + e.getError() + ", error_description=" + e.getErrorDescription());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "error=" + e.getMessage());
            return null;
        }
    }

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute(YConnectImplicit result) {
        if (listener != null) {
            listener.onSuccess(result);
        }
    }
}
