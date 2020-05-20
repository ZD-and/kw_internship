package jp.kinwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;

import jp.kinwork.Common.MyApplication;

public class AdvancSetActivity extends AppCompatActivity {
    private String keyword;
    private String address;
    private String yearlyIncome;
    private String employmentStatus;

    private Spinner tvshowannualincome;
    private Spinner tvshowemploymentstatus;
    private TextView tvback;
    private EditText etJobname;
    private EditText etaddress;
    private MyApplication myApplication;

    Button Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanc_set);
        Initialization();
    }

    //初期化
    public void Initialization(){
        tvback = (TextView) findViewById(R.id.tv_back);
        tvback.setText(getString(R.string.Search));
        etJobname = (EditText) findViewById(R.id.et_Jobname);
        etaddress = (EditText) findViewById(R.id.et_address);
        tvshowemploymentstatus = (Spinner) findViewById(R.id.tv_show_employmentstatus);
        tvshowannualincome = (Spinner) findViewById(R.id.tv_show_annualincome);
        Search.findViewById(R.id.button);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click_Search();
            }
        });
        myApplication = (MyApplication) getApplication();
    }

    //菜单栏按钮触发事件
    public void Click_back(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setClass(AdvancSetActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    //检索结果画面移动
    public void Click_Search(){
        keyword = etJobname.getText().toString();
        address = etaddress.getText().toString();
        employmentStatus = tvshowemploymentstatus.getSelectedItem().toString();
        yearlyIncome = tvshowannualincome.getSelectedItem().toString();
        myApplication.setkeyword(keyword);
        myApplication.setaddress(address);
        if(! employmentStatus.equals(getString(R.string.employmentStatuslist))){
            myApplication.setemploymentStatus(employmentStatus);
        }
        if(! yearlyIncome.equals(getString(R.string.yearlyIncome))){
            myApplication.setyearlyIncome(yearlyIncome);
        }
        myApplication.setpage("1");
        Intent intent_results = new Intent();
        intent_results.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent_results.setClass(AdvancSetActivity.this, SearchResultsActivity.class);
        intent_results.putExtra("Act","Set");
        startActivity(intent_results);
    }

//    //雇用形態選択 start
//    private void showemploymentstatus() {
//        //监听事件
//        tvshowemploymentstatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showemploymentstatusChooseDialog();
//            }
//        })
//        ;
//
//    }
//
//    private void showemploymentstatusChooseDialog() {
//        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
//        builder3.setSingleChoiceItems(employmentstatus, employmentstatusIndex, new DialogInterface.OnClickListener() {// 2默认的选中
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
//                // showToast(which+"");
//                employmentstatusIndex = which;
//                Log.d("selectedFruitIndex", ": " +which);
//                tvshowemploymentstatus.setText(employmentstatus[which]);
//                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
//            }
//        });
//        builder3.show();// 让弹出框显示
//    }
//    //雇用形態選択 end


    //推定年収選択 start
//    private void showannualincome() {
//        //监听事件
//        tvshowannualincome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showannualincomeChooseDialog();
//            }
//        })
//        ;
//
//    }
//
//    private void showannualincomeChooseDialog() {
//        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
//        builder3.setSingleChoiceItems(annualincome, annualincomeIndex, new DialogInterface.OnClickListener() {// 2默认的选中
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
//                // showToast(which+"");
//                annualincomeIndex = which;
//                Log.d("selectedFruitIndex", ": " +which);
//                tvshowannualincome.setText(annualincome[which]);
//                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
//            }
//        });
//        builder3.show();// 让弹出框显示
//    }
    //推定年収選択 end
}
