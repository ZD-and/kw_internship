package jp.kinwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import jp.kinwork.Common.MyApplication;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvagreementname;
    private TextView tvagreementcontents;
    private String termsofservice = getString(R.string.termsofservice);
    private String contents_Termsofservice = getString(R.string.contentsTermsofservice);
    private String privacypolicy = getString(R.string.privacypolicy);
    private String contents_privacypolicy = getString(R.string.contentsprivacypolicy);
    private String Agreement = "";
    private String[] Termsofservice_title = new String[]{getString(R.string.Termsofservice_title)};
    private String[] privacypolicy_title = new String[]{getString(R.string.privacypolicy_title)};

    private Button bu_back;
    private Button bu_ok;
    private MyApplication mMyApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        mMyApplication = (MyApplication) getApplication();
        Agreement = mMyApplication.getAgreement();
        tvagreementname = (TextView) findViewById(R.id.tv_agreement_name);
        tvagreementcontents = (TextView) findViewById(R.id.tv_agreement_contents);
        bu_back=findViewById(R.id.bu_back);
        bu_ok=findViewById(R.id.bu_ok);
        bu_back.setOnClickListener(this);
        bu_ok.setOnClickListener(this);
        String text = "";
        if(Agreement.equals(getString(R.string.termsofservice))){
            tvagreementname.setText(termsofservice);
//            text = contents_Termsofservice.replace("\\n", "\n");
            text = ChangeString(contents_Termsofservice,Termsofservice_title);
        } else {
            tvagreementname.setText(privacypolicy);
//            text = contents_privacypolicy.replace("\\n", "\n");
            text = ChangeString(contents_privacypolicy,privacypolicy_title);
        }
        tvagreementcontents.setText(Html.fromHtml(text));
    }

    public void onClick(View View){
        mMyApplication.setAgreement(Agreement);
        switch (View.getId()){
            case R.id.bu_back:
                if(Agreement.equals(getString(R.string.termsofservice))){
                    mMyApplication.settermsofserviceflg("0");
                } else {
                    mMyApplication.setprivacypolicyflg("0");
                }
                break;
            case R.id.bu_ok:
                if(Agreement.equals(getString(R.string.termsofservice))){
                    mMyApplication.settermsofserviceflg("1");
                } else {
                    mMyApplication.setprivacypolicyflg("1");
                }
                break;
        }
        Intent intent = new Intent();
        intent.setClass(AgreementActivity.this,MakeUserActivity.class);
        startActivity(intent);
    }

    private String ChangeString(String data_A,String[] data_B){
        String Stringreturn = data_A;
        String data = "";
        for(int i = 0;i<data_B.length;i++){
            if(data_B[i].equals(getString(R.string.welcometoKinwork))){
                data = "<font color=#000000><big><big><b>" + data_B[i] +"</b></big></big></font> <br />";
            } else {
                data = "<br /><br /> <font color=#000000><big><b>" + data_B[i] +"</b></big></font> <br />";
            }
            Stringreturn = Stringreturn.replace(data_B[i],data);
        }
        Stringreturn = Stringreturn.replace("\\n", "<br />");
        Log.w("Stringreturn", Stringreturn);
        return Stringreturn;
    }
}
