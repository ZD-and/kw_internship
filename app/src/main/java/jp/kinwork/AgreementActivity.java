package jp.kinwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import jp.kinwork.Common.MyApplication;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "AgreementActivity";
    private String mAgreement = "";
    private String mActivity = "";
    private String[] Termsofservice_title = new String[]{"KinWork へようこそ","第1条（本サービスのご利用）","第2条（ユーザーの KinWork アカウント）","第3条（プライバシー ポリシー）","第4条（本サービス内のユーザーのコンテンツ）","第5条（本サービスの変更または終了）","第6条（ソーシャルボタンのご利用について）","第7条（保証）","第8条（お客様へのご連絡手段）","第9条（免責事項）","第10条（本規約について）","第11条（準拠法、裁判管轄）"};
    private String[] privacypolicy_title = new String[]{"1. パーソナルデータの取得","2. パーソナルデータの利用目的","3. 個人情報の提供の同意","4. 安全管理の実施","5. パーソナルデータに関する開示等、苦情および相談への対応"};


    private MyApplication mMyApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        tvBack.setText("戻る");
        TextView tvBackDummy = findViewById(R.id.tv_back_dummy);
        tvBackDummy.setOnClickListener(this);
        tvBackDummy.setText("同意");
        tvBackDummy.setTextColor(Color.parseColor("#0196FF"));

        mMyApplication = (MyApplication) getApplication();
        mAgreement = mMyApplication.getAgreement();
        TextView tvagreementname = (TextView) findViewById(R.id.tv_back_title);
        TextView tvagreementcontents = (TextView) findViewById(R.id.tv_agreement_contents);

//        findViewById(R.id.bu_ok).setOnClickListener(this);
        String text = "";
        if(mAgreement.equals(getString(R.string.termsofservice))){
            tvagreementname.setText(getString(R.string.termsofservice));
            text = ChangeString(getString(R.string.contentsTermsofservice),Termsofservice_title);
        } else {
            tvagreementname.setText(getString(R.string.privacypolicy));
            text = ChangeString(getString(R.string.contentsprivacypolicy),privacypolicy_title);
        }
        tvagreementcontents.setText(HtmlCompat.fromHtml(text,HtmlCompat.FROM_HTML_MODE_COMPACT));

    }

    public void onClick(View View){
        mMyApplication.setAgreement(mAgreement);
        switch (View.getId()){
            case R.id.tv_back:
                if(mAgreement.equals(getString(R.string.termsofservice))){
                    mMyApplication.settermsofserviceflg("0");
                } else {
                    mMyApplication.setprivacypolicyflg("0");
                }
                break;
            case R.id.tv_back_dummy:
                if(mAgreement.equals(getString(R.string.termsofservice))){
                    mMyApplication.settermsofserviceflg("1");
                } else {
                    mMyApplication.setprivacypolicyflg("1");
                }
                break;
        }
        Intent intent = new Intent();
        if(mMyApplication.getActivity().equals("MakeUserActivity")){
            intent.setClass(AgreementActivity.this,MakeUserActivity.class);
        } else {
            intent.setClass(AgreementActivity.this,PersonalSetActivity.class);
        }
        startActivity(intent);
    }

    private String ChangeString(String data_A,String[] data_B){
        String Stringreturn = data_A;
        String data = "";
        for(int i = 0;i<data_B.length;i++){
            Log.w(TAG,"data_B["+i+"]"+ data_B[i]);
            if(data_B[i].equals(getString(R.string.welcometoKinwork))){
                data = "<font color=#000000><big><big><b>" + data_B[i] +"</b></big></big></font> <br />";
            } else {
                data = "<br /><br /> <font color=#000000><big><b>" + data_B[i] +"</b></big></font> <br />";
            }
            Stringreturn = Stringreturn.replace(data_B[i],data);
        }
        Stringreturn = Stringreturn.replace("\\n", "<br />");
        Log.w(TAG,"Stringreturn"+ Stringreturn);
        return Stringreturn;
    }
}
