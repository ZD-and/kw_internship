package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class SchoolCareer {

    /**
     * id : id
     * id_resume : 履歴書のid番号
     * user_id : 基本ユーザー情報のid
     * school_name : 学校名
     * degree : 学位
     * major_field : 専攻分野
     * country_of_school : 学校所在国
     * from_year : 学歴開始年
     * from_month : 学歴開始月
     * to_year : 学歴終了年
     * to_month : 学歴終了月
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String id_resume;
    private String user_id;
    private String school_name;
    private String degree;
    private String major_field;
    private String country_of_school;
    private String from_year;
    private String from_month;
    private String to_year;
    private String to_month;
    private String created_user;
    private String created_date;
    private String updated_user;
    private String updated_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_resume() {
        return id_resume;
    }

    public void setId_resume(String id_resume) {
        this.id_resume = id_resume;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getMajor_field() {
        return major_field;
    }

    public void setMajor_field(String major_field) {
        this.major_field = major_field;
    }

    public String getCountry_of_school() {
        return country_of_school;
    }

    public void setCountry_of_school(String country_of_school) {
        this.country_of_school = country_of_school;
    }

    public String getFrom_year() {
        return from_year;
    }

    public void setFrom_year(String from_year) {
        this.from_year = from_year;
    }

    public String getFrom_month() {
        return from_month;
    }

    public void setFrom_month(String from_month) {
        this.from_month = from_month;
    }

    public String getTo_year() {
        return to_year;
    }

    public void setTo_year(String to_year) {
        this.to_year = to_year;
    }

    public String getTo_month() {
        return to_month;
    }

    public void setTo_month(String to_month) {
        this.to_month = to_month;
    }

    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_user() {
        return updated_user;
    }

    public void setUpdated_user(String updated_user) {
        this.updated_user = updated_user;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }
}
