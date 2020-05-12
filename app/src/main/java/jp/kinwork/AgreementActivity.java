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
    private String termsofservice = "利用規約";
    private String contents_Termsofservice = "KinWork へようこそ\\nKinWork のWeb サイトまたはモバイルアプリケーションにて提供するサービス（以下「本サービス」）をご利用いただきありがとうございます。\\n第1条（本サービスのご利用）\\n本サービス内で入手できるすべてのポリシーを遵守してください。\\nユーザーは、本利用規約にご同意いただくことによって、当社のサービスをご利用いただくことができます。\\nユーザーが KinWork の規約やポリシーを遵守しない場合、または KinWork が不正行為と疑う行為について調査を行う場合に、KinWork はユーザーに対する本サービスの提供を一時停止または停止することができます。\\n当社は提供するサービスの内容について、瑕疵（かし）やバグがないことは保証しておりません。\\nまた当社は、お客様にあらかじめ通知することなくサービスの内容や仕様を変更したり、提供を停止したり中止したりすることができるものとします。\\n本サービスで表示されるコンテンツの一部は、KinWork の所有物ではありません。こうしたコンテンツについては、そのコンテンツを提供する当事者が単独で責任を負います。\\n本サービスの利用に関して、KinWork はユーザーに対してサービスの告知、管理上のメッセージ、およびその他の情報を送信することができます。ユーザーは、これらの通知について、受け取らないことを選択できる場合があります。\\n本サービスの一部は携帯端末でご利用いただけます。注意散漫になり交通や安全に関する法律を遵守できなくなる状態で本サービスを利用しないでください。\\n第2条（ユーザーの KinWork アカウント）\\n本サービスを利用するために、KinWork アカウントが必要になる場合があります。\\nKinWork アカウントを保護するため、パスワードは他人に知らせないでください。KinWork アカウント上や KinWork アカウントを通じての行動に対する責任はユーザー自身にあります。KinWork アカウントのパスワードを第三者のアプリケーションで再利用することは避けるようにしてください。\\nお客様を特定する所定の認証方法によりログインされた場合には、当社は、当該お客様ご自身によるご利用であるとみなします。サービスの利用などによって料金が発生した場合には、当該お客様に課金いたします。\\n第3条（プライバシー ポリシー）\\nKinWork の（プライバシーポリシー）では、本サービスの利用におけるユーザーの個人データの取り扱いとプライバシーの保護について記載しています。ユーザーは、本サービスを利用することにより、KinWork の（プライバシーポリシー）に従って、KinWork がユーザーの個人データを利用できることに同意することになります。\\n第4条（本サービス内のユーザーのコンテンツ）\\n本サービスの一部では、ユーザーがコンテンツをアップロード、提供、保存、送信、または受信することができます。ユーザーは、そのコンテンツに対して保有する知的財産権を引き続き保持します。つまり、ユーザーのものは、そのままユーザーが所有します。\\n第5条（本サービスの変更または終了）\\nKinWork は、常に本サービスの変更および改善を行っています。KinWork は、機能性や機能の追加や削除を行うことができ、本サービス全体を一時停止または終了することができます。 それによりお客様や第三者が損害を被った場合でも当社は一切の責任を負いかねます。\\nユーザーはいつでも本サービスの利用を終了することができます。KinWork もいつでも、ユーザーに対する本サービスの提供を停止し、または、本サービスに対する制限を追加または新規に設定することができます。\\nKinWork は、ユーザーが自身のデータを所有し、そのデータにユーザーが常にアクセスできるようにすることが重要であると考えています。KinWork が本サービスを中断する場合、合理的に可能なときには、KinWork はユーザーに対して、合理的な事前の通知を行い、ユーザーが本サービスから情報を取得する機会を提供します。\\n第6条（ソーシャルボタンのご利用について）\\nサイトの一部には外部サイトと連携するソーシャルボタンを設置しております。ご利用にあたっては、サービス提供元の規定が適用されます。\\n第7条（保証）\\nKinWork は、商業上合理的な水準の技術および注意のもとに本サービスを提供し、ユーザーに本サービスの利用を楽しんでいただくことを望んでいますが、本サービスについて約束できないことがあります。\\n本規約または追加規定に明示的に規定されている場合を除き、KinWork またはそのサプライヤーもしくはディストリビューターのいずれも、本サービスについて具体的な保証を行いません。たとえば KinWork は、本サービス内のコンテンツ、本サービスの特定の機能、その信頼性、利用可能性、またはユーザーのニーズに応える能力について、何らの約束もしません。本サービスは「現状有姿で」提供されます。\\n一部の法域においては、商品性、特定の目的への適合性、および権利の侵害がないことに関する黙示保証などの保証が認められることがあります。法律で許されている範囲内で、KinWork はすべての保証を排除します。\\n第8条（お客様へのご連絡手段）\\n当社からのご連絡、通知は、当社所定のサイトへの掲載又はメールを送信することをもって行うことします。但し、当社が必要と判断した場合は、郵便や電話など他の手段も使用する場合があります。\\n第9条（免責事項）\\n本サービスのご利用は、お客様ご自身の判断と責任において、下記の項目を承諾した上でご利用いただくものとします。\\n1.当社は、情報の取り扱いを充分に注意し、確認した上で掲載しておりますが、いかなる状況においても、またいかなる人物または法人に対しても、以下の各号について一切の責任を負いかねます。\\n　I.本サービスの提供する一切の情報の入手及び利用の結果または利用不可能により生じた損失または損害（全部または一部を問いません）。\\n　II.本サービスに含まれる一切の情報の誤り（当社の過失の有無を問いません）により生じた損失または損害（全部または一部を問いません）。\\n　III.本サービスの提供する情報がリンク切れ、または時間の経過に伴う情報の差異、またはその他の状況により生じた損失または損害（全部または一部を問いません）。\\n2.KinWorkおよびそのアフィリエイト、またそれらの第三者ライセンサーは、ユーザーによる本サイトの利用または誤用、または信頼に起因する請求について、いかなるユーザーに対しても一切責任を負いません。\\n3.本サービス中に掲載される広告（その形態は問いません）や本サービス中にリンクされた外部サイトについて、当社は、その内容の正確性、速報性、完全性、または合目的性等について、いかなる保証（明示的、黙示的を問いません）もいたしません。また、お客様や第三者に損害や不利益が発生した場合でも、当社は一切の責任を負いかねます。お客様は、広告内容及びリンクされた外部サイトに関してもご自身の判断と責任でご利用ください。\\n4.上記を制限することなく、いかなる状況下においても、KinWorkおよびそのアフィリエイト、またそれらの第三者ライセンサーは、天災、外圧、またはその合理的な支配を超えるものに直接的または間接的に起因する遅延や不履行に対して一切責任を負いません。\\n第10条（本規約について）\\nKinWork は、たとえば、法律の改正または本サービスの変更を反映するために、本サービスに適用する本規約または特定の本サービスについての追加規定を修正することがあります。ユーザーは定期的に本規約をご確認ください。KinWork は、本規約の修正に関する通知をこのページに表示します。本サービスに関する修正された規定に同意しないユーザーは、本サービスの利用を停止してください。\\n本規約と追加規定との間に矛盾が存在する場合には、追加規定が本規約に優先します。\\nユーザーが本規約を遵守しない場合に、KinWork が直ちに法的措置を講じないことがあったとしても、そのことによって、KinWork が有している権利（たとえば、将来において、法的措置を講じる権利）を放棄しようとしていることを意味するものではありません。\\n第11条（準拠法、裁判管轄）\\n本規約は、日本法に基づき解釈されるものとします。本規約に関して訴訟の必要が生じた場合には、訴額に応じて東京簡易裁判所または東京地方裁判所を第一審の専属的合意管轄裁判所とします。";
    private String privacypolicy = "プライバシー ポリシー";
    private String contents_privacypolicy = "KinWork では、個人情報へのアクセスを提供された際に、ユーザーおよびお客様から寄せられた信頼を重視しています。本プライバシー規約では、以下の個人情報保護方針を定め、当社で勤務するすべての役職員に周知し、この方針にしたがった個人情報の適切な保護に努めます。\\n1. パーソナルデータの取得\\nKinWorkは、以下の場合にパーソナルデータを取得させていただきます。\\n（1）端末操作を通じてお客様にご入力いただく場合\\n（2）お客様から直接または書面等の媒体を通じてご提供いただく場合\\n（3）お客様によるサービス、商品、広告、コンテンツ（以下これらをまとめて「サービス等」（*）といいます）の利用・閲覧に伴って自動的に送信される場合\\n（4）上記の他、お客様の同意を得た第三者から提供を受ける場合など、適法に取得する場合\\n* サービス等は、個人のお客様向けのサービス、商品、広告、コンテンツに限りません。\\n2. パーソナルデータの利用目的\\n当社は、以下のことを行うためパーソナルデータを利用させていただきます。\\n（1）お客様に適したサービス等をご提供するため\\n（2）お客様からのお問い合わせに対応するため\\n（3）代金請求、ポイント付与等をするため\\n（4）お客様にサービス等に関するお知らせをするため\\n（5）サービス等を安全にご提供するため。これには、利用規約に違反しているお客様を発見して当該お客様に通知をしたり、サービス等を悪用した詐欺や不正アクセスなどの不正行為を調査・検出・予防したり、これらに対応することが含まれます\\n（6）サービス等の改善および新たなサービス等を検討するため\\n（7）サービス等のご利用状況等を調査、分析するため\\n上記にかかわらず、KinWorkが第三者からパーソナルデータの提供を受ける際に、当該パーソナルデータの利用目的について別途定めがある場合は、その定めに従い当該パーソナルデータを利用します。\\n3. 個人情報の提供の同意\\n法令で認められた場合のほか、お客様の同意をいただいた場合は、KinWorkは採用企業等の第三者に対して個人情報を提供させていただきます。\\n4. 安全管理の実施\\nKinWorkは、個人情報の正確性及び安全性を確保するため、想定されるリスクに見合う合理的な対策として、個人情報へのアクセス管理、個人情報の持ち出し手段の制限、外部からの不正アクセス防止策等を実施し、個人情報の漏洩、滅失又は毀損の防止に努めます。これらの安全策は定期的に見直すとともに、是正を行います。\\n5. パーソナルデータに関する開示等、苦情および相談への対応\\nKinWorkは、個人情報に関して本人から自己の情報の開示、訂正・追加・削除、または利用若しくは提供の拒否、並びに苦情及び相談の申し出を受けた場合、適切な本人確認を実施した上、すみやかに対応します。";
    private String Agreement = "";
    private String[] Termsofservice_title = new String[]{"KinWork へようこそ","第1条（本サービスのご利用）","第2条（ユーザーの KinWork アカウント）","第3条（プライバシー ポリシー）","第4条（本サービス内のユーザーのコンテンツ）","第5条（本サービスの変更または終了）","第6条（ソーシャルボタンのご利用について）","第7条（保証）","第8条（お客様へのご連絡手段）","第9条（免責事項）","第10条（本規約について）","第11条（準拠法、裁判管轄）"};
    private String[] privacypolicy_title = new String[]{"1. パーソナルデータの取得","2. パーソナルデータの利用目的","3. 個人情報の提供の同意","4. 安全管理の実施","5. パーソナルデータに関する開示等、苦情および相談への対応"};

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
        if(Agreement.equals("termsofservice")){
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
                if(Agreement.equals("termsofservice")){
                    mMyApplication.settermsofserviceflg("0");
                } else {
                    mMyApplication.setprivacypolicyflg("0");
                }
                break;
            case R.id.bu_ok:
                if(Agreement.equals("termsofservice")){
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
            if(data_B[i].equals("KinWork へようこそ")){
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
