package jp.kinwork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import jp.kinwork.Common.MyApplication;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout ll_b_search;
    private LinearLayout ll_b_contact;
    private LinearLayout ll_b_mylist;
    private LinearLayout ll_b_personalsettings;

    private MyApplication mMyApplication;

    private EditText etpasswordOld;
    private EditText etpasswordNew;
    private EditText etpasswordNewConform;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        setContentView(R.layout.title_bule);
        setContentView(R.layout.activity_changepw);
    }

    //初期化
    private void Initialization(){
        etpasswordOld = (EditText) findViewById(R.id.et_oldpw);
        etpasswordNew = (EditText) findViewById(R.id.et_newpw);
        etpasswordNewConform = (EditText) findViewById(R.id.et_newpwConform);
        if(!mMyApplication.getpersonalset(0).equals("0")){
            etpasswordOld.setText(mMyApplication.getpersonalset(1));
            etpasswordNew.setText(mMyApplication.getpersonalset(2));
            etpasswordNewConform.setText(mMyApplication.getpersonalset(3));
        }
    }
    //底部のメニュー
    public void init(){
        ll_b_search=findViewById(R.id.ll_b_search);
        ll_b_contact=findViewById(R.id.ll_b_contact);
        ll_b_mylist=findViewById(R.id.ll_b_mylist);
        ll_b_personalsettings=findViewById(R.id.ll_b_personalsettings);
        ll_b_search.setOnClickListener(this);
        ll_b_contact.setOnClickListener(this);
        ll_b_mylist.setOnClickListener(this);
        ll_b_personalsettings.setOnClickListener(this);

        mMyApplication = (MyApplication) getApplication();
    }
    //底部のメニューの処理
    public void onClick(View View){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        switch (View.getId()){
            //跳转检索画面
            case R.id.ll_b_search:
                mMyApplication.setAct(getString(R.string.Search));
                if(mMyApplication.getSApply(0).equals("0")){
                    if(mMyApplication.getSearchResults(0).equals("0")){
                        intent.setClass(this, SearchActivity.class);
                        intent.putExtra(getString(R.string.act),"");
                    } else {
                        intent.setClass(this, SearchResultsActivity.class);
                    }
                } else {
                    intent.setClass(this, ApplyActivity.class);
                }
                if(mMyApplication.getSURL(0).equals("0")){
                    if(mMyApplication.getSApply(0).equals("0")){
                        if(mMyApplication.getSearchResults(0).equals("0")){
                            intent.setClass(this, SearchActivity.class);
                            intent.putExtra("act","");
                        } else {
                            intent.setClass(this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(this, WebActivity.class);
                    Initialization();
                }
                if(mMyApplication.getSURL(0).equals("0")){
                    if(mMyApplication.getSApply(0).equals("0")){
                        if(mMyApplication.getSearchResults(0).equals("0")){
                            intent.setClass(this, SearchActivity.class);
                            intent.putExtra(getString(R.string.act),"");
                        } else {
                            intent.setClass(this, SearchResultsActivity.class);
                        }
                    } else {
                        intent.setClass(this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(this, WebActivity.class);
                }
                break;
            //跳转联络画面
            case R.id.ll_b_contact:
                intent.setClass(this, ContactActivity.class);
                if(mMyApplication.getContactDialog(0).equals("0")){
                    intent.setClass(this, ContactActivity.class);
                } else {
                    intent.setClass(this, ContactDialogActivity.class);
                }
                break;
            //跳转到Myリスト
            case R.id.ll_b_mylist:
                intent.setClass(this, MylistActivity.class);
                mMyApplication.setAct(getString(R.string.Apply));
                if(mMyApplication.getMURL(0).equals("0")){
                    if(mMyApplication.getMApply(0).equals("0")){
                        intent.setClass(this, MylistActivity.class);
                    } else {
                        intent.setClass(this, ApplyActivity.class);
                    }
                } else {
                    intent.setClass(this, WebActivity.class);
                }
                break;
            //跳转个人设定画面
            case R.id.ll_b_personalsettings:
                intent.setClass(this, ChangepwActivity.class);
                if(mMyApplication.getpersonalset(0).equals("0")){
                    intent.setClass(this, PersonalSetActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("1")){
                    intent.setClass(this, BasicinfoeditActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("2")){
                    intent.setClass(this, ChangepwActivity.class);
                } else if(mMyApplication.getpersonalset(0).equals("3")){
                    intent.setClass(this, ResumeActivity.class);
                }
                break;
        }
        startActivity(intent);
    }
}





