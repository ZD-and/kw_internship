package jp.kinwork.Common;

import android.app.Application;

import java.util.LinkedList;

/**
 * Created by zml98 on 2018/04/14.
 */

public class MyApplication extends Application {

    private String myapplicationFlg = "";
    private String email = "";
    private String token = "";
    private String id = "";
//    private String Basicinfoid = "";
//    private String basicinfo = "";
    private String user_id = "";
    private String first_name = "";
    private String last_name = "";
    private String first_name_kana = "";
    private String last_name_kana = "";
    private String birthday = "";
    private String sex_div = "";
    private String country = "";
    private String post_code = "";
    private String add_1 = "";
    private String add_2 = "";
    private String add_3 = "";
    private String add_4 = "";
    private String phone_number = "";
    private String ResumeId = "";
    private String keyword = "";
    private String address = "";
    private String employmentStatus = "";
    private String yearlyIncome = "";
    private String page = "";
//    private String returnData = "";
    private String jobinfo = "";
    private String URL = "";
    private String JobId = "";
    private String Act = "";
    private String Myjob = "";
    private String Agreement = "";
    private String inputA = "";
    private String inputB = "";
    private String screenflg = "";
    private String termsofserviceflg = "";
    private String privacypolicyflg = "";
    private String resume_name_1 = "";
    private String resume_name_2 = "";
    private String resume_name_3 = "";
    private String resume_status = "";
    private String company_name = "";
    private String Jobname = "";
    private String ActCation = "";
    private String employerID = "";
    private String address_components = "";
    private String AccessFineFocation = "";


    private int WindowWidth = 0;
    private int WindowWidthreset = 0;
//    private int num = 0;
//    private int myIndex = -1;
//    private LinkedList<String> enteredID = new LinkedList<String>();
    private LinkedList<String> list_SearchResults = new LinkedList<String>();
    private LinkedList<String> list_ContactDialog = new LinkedList<String>();
    private LinkedList<String> list_S_Apply = new LinkedList<String>();
    private LinkedList<String> list_M_Apply = new LinkedList<String>();
    private LinkedList<String> list_S_URL = new LinkedList<String>();
    private LinkedList<String> list_M_URL = new LinkedList<String>();
    private LinkedList<String> list_P_personalset = new LinkedList<String>();


    public String getpersonalset(int Num) {
        String data = list_P_personalset.get(Num);
        return data;
    }
    public void setpersonalset(String data,int i) {
        this.list_P_personalset.add(i,data);
    }

    public String getSURL(int Num) {
        String data = list_S_URL.get(Num);
        return data;
    }
    public void setSURL(String data,int i) {
        this.list_S_URL.add(i,data);
    }


    public String getMURL(int Num) {
        String data = list_M_URL.get(Num);
        return data;
    }
    public void setMURL(String data,int i) {
        this.list_M_URL.add(i,data);
    }


    public String getAccessFineFocation() {
        return AccessFineFocation;
    }
    public void setAccessFineFocation(String AccessFineFocation) {
        this.AccessFineFocation = AccessFineFocation;
    }

    public int getWindowWidthreset() {
        return WindowWidthreset;
    }
    public void setWindowWidthreset(int WindowWidthreset) {
        this.WindowWidthreset = WindowWidthreset;
    }
    public int getWindowWidth() {
        return WindowWidth;
    }
    public void setWindowWidth(int WindowWidth) {
        this.WindowWidth = WindowWidth;
    }

    public String getaddress_components() {
        return address_components;
    }
    public void setaddress_components(String address_components) {
        this.address_components = address_components;
    }

    public String getSearchResults(int Num) {
        String data = list_SearchResults.get(Num);
        return data;
    }
    public void setSearchResults(String data,int i) {
        this.list_SearchResults.add(i,data);
    }

    public String getContactDialog(int Num) {
        String data = list_ContactDialog.get(Num);
        return data;
    }
    public void setContactDialog(String data,int i) {
        this.list_ContactDialog.add(i,data);
    }

    public String getSApply(int Num) {
        String data = list_S_Apply.get(Num);
        return data;
    }
    public void setSApply(String data,int i) {
        this.list_S_Apply.add(i,data);
    }

    public String getMApply(int Num) {
        String data = list_M_Apply.get(Num);
        return data;
    }
    public void setMApply(String data,int i) {
        this.list_M_Apply.add(i,data);
    }

    public String getemployerID() {
        return employerID;
    }
    public void setemployerID(String employerID) {
        this.employerID = employerID;
    }
//    public int getnum() {
//        return num;
//    }
//    public void setnum(int num) {
//        this.num = num;
//    }
//    public int getmyIndex() {
//        return myIndex;
//    }
//    public void setmyIndex(int myIndex) {
//        this.myIndex = myIndex;
//    }
//    public boolean getenteredID(String data) {
//        for(int i = 0; i < enteredID.size(); i++){
//            if(data.equals(enteredID.get(i))){
//                return true;
//            }
//        }
//        return false;
//    }
    public String getresume_name(String Num) {
        if(Num.equals("1")){
            return resume_name_1;
        }
        if(Num.equals("2")){
            return resume_name_2;
        }
        if(Num.equals("3")){
            return resume_name_3;
        }
        return "";
    }
    public String getActCation() {
        return ActCation;
    }
    public String getcompany_name() {
        return company_name;
    }
    public String getJobname() {
        return Jobname;
    }
    public String getresume_status() {
        return resume_status;
    }
    public String getAgreement() {
        return Agreement;
    }
    public String getinputA() {
        return inputA;
    }
    public String getinputB() {
        return inputB;
    }
    public String getscreenflg() {
        return screenflg;
    }
    public String gettermsofserviceflg() {
        return termsofserviceflg;
    }
    public String getprivacypolicyflg() {
        return privacypolicyflg;
    }
//    public String getBasicinfoid() {
//        return Basicinfoid;
//    }
//    public String getbasicinfo() {
//        return basicinfo;
//    }
    public String getMyjob() {
        return Myjob;
    }
    public String getAct() {
        return Act;
    }
    public String getMyApplicationFlg() {
        return myapplicationFlg;
    }
    public String getEmail() {
        return email;
    }
    public String getToken() {
        return token;
    }
    public String getid() { return id;}
    public String getuser_id() { return user_id;}
    public String getfirst_name() { return first_name;}
    public String getlast_name() { return last_name;}
    public String getfirst_name_kana() { return first_name_kana;}
    public String getlast_name_kana() { return last_name_kana;}
    public String getbirthday() { return birthday;}
    public String getsex_div() { return sex_div;}
    public String getcountry() { return country;}
    public String getpost_code() { return post_code;}
    public String getadd_1() { return add_1;}
    public String getadd_2() { return add_2;}
    public String getadd_3() { return add_3;}
    public String getadd_4() { return add_4;}
    public String getphone_number() { return phone_number;}
    public String getResumeId() { return ResumeId;}
    public String getkeyword() { return keyword;}
    public String getaddress() { return address;}
    public String getemploymentStatus() { return employmentStatus;}
    public String getyearlyIncome() { return yearlyIncome;}
    public String getpage() { return page;}
//    public String getreturnData() { return returnData;}
    public String getjobinfo() { return jobinfo;}
    public String getURL() {
        return URL;
    }
    public String getJobId() {
        return JobId;
    }

//    public void setenteredID(String data,int i) {
//        enteredID.add(i,data);
//    }
    public void setresume_name(String resume_name,String Num) {
        if(Num.equals("1")){
            this.resume_name_1 = resume_name;
        }
        if(Num.equals("2")){
            this.resume_name_2 = resume_name;
        }
        if(Num.equals("3")){
            this.resume_name_3 = resume_name;
        }
    }
    public void setActCation(String ActCation) {
        this.ActCation = ActCation;
    }
    public void setcompany_name(String company_name) {
        this.company_name = company_name;
    }
    public void setJobname(String Jobname) {
        this.Jobname = Jobname;
    }
    public void setresume_status(String resume_status) {
        this.resume_status = resume_status;
    }
//    public void setBasicinfoid(String Basicinfoid) {
//        this.Basicinfoid = Basicinfoid;
//    }
//    public void setbasicinfo(String basicinfo) {
//        this.basicinfo = basicinfo;
//    }
    public void setMyjob(String Myjob) {
        this.Myjob = Myjob;
    }
    public void setAct(String Act) {
        this.Act = Act;
    }
    public void setMyApplicationFlg(String myapplicationFlg) {
        this.myapplicationFlg = myapplicationFlg;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setid(String id) { this.id = id;}
    public void setuser_id(String user_id) { this.user_id = user_id;}
    public void setfirst_name(String first_name) { this.first_name = first_name;}
    public void setlast_name(String last_name) { this.last_name = last_name;}
    public void setfirst_name_kana(String first_name_kana) { this.first_name_kana = first_name_kana;}
    public void setlast_name_kana(String last_name_kana) { this.last_name_kana = last_name_kana;}
    public void setbirthday(String birthday) { this.birthday = birthday;}
    public void setsex_div(String sex_div) { this.sex_div = sex_div;}
    public void setcountry(String country) { this.country = country;}
    public void setpost_code(String post_code) { this.post_code = post_code;}
    public void setadd_1(String add_1) { this.add_1 = add_1;}
    public void setadd_2(String add_2) { this.add_2 = add_2;}
    public void setadd_3(String add_3) { this.add_3 = add_3;}
    public void setadd_4(String add_4) { this.add_4 = add_4;}
    public void setphone_number(String phone_number) { this.phone_number = phone_number;}
    public void setResumeId(String ResumeId) { this.ResumeId = ResumeId;}
    public void setkeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setaddress(String address) {
        this.address = address;
    }
    public void setemploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    public void setyearlyIncome(String yearlyIncome) {
        this.yearlyIncome = yearlyIncome;
    }
    public void setpage(String page) {
        this.page = page;
    }
//    public void setreturnData(String returnData) {
//        this.returnData = returnData;
//    }
    public void setjobinfo(String jobinfo) {
        this.jobinfo = jobinfo;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
    public void setJobId(String JobId) {
        this.JobId = JobId;
    }
    public void setAgreement(String Agreement) { this.Agreement = Agreement;}
    public void setinputA(String inputA) { this.inputA = inputA;}
    public void setinputB(String inputB) { this.inputB = inputB;}
    public void setscreenflg(String screenflg) { this.screenflg = screenflg;}
    public void settermsofserviceflg(String termsofserviceflg) { this.termsofserviceflg = termsofserviceflg;}
    public void setprivacypolicyflg(String privacypolicyflg) { this.privacypolicyflg = privacypolicyflg;}

}
