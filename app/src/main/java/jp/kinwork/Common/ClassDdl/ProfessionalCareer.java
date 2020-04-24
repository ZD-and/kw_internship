package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class ProfessionalCareer {

    /**
     * id : 職歴のid
     * id_resume : 関連履歴書のid番号
     * user_id : 基本ユーザー情報のid
     * job_name : 職業名
     * company_name : 会社名
     * country_of_professional : 国
     * from_year : 勤務開始年
     * from_month : 勤務開始月
     * to_year : 勤務終了年
     * to_month : 勤務終了月
     * description : 説明
     * is_working_till_now : 現在、ここに勤務しているフラグ
     * had_no_work_yet : まだ職務経験がないフラグ
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String id_resume;
    private String user_id;
    private String job_name;
    private String company_name;
    private String country_of_professional;
    private String from_year;
    private String from_month;
    private String to_year;
    private String to_month;
    private String description;
    private String is_working_till_now;
    private String had_no_work_yet;
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

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCountry_of_professional() {
        return country_of_professional;
    }

    public void setCountry_of_professional(String country_of_professional) {
        this.country_of_professional = country_of_professional;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIs_working_till_now() {
        return is_working_till_now;
    }

    public void setIs_working_till_now(String is_working_till_now) {
        this.is_working_till_now = is_working_till_now;
    }

    public String getHad_no_work_yet() {
        return had_no_work_yet;
    }

    public void setHad_no_work_yet(String had_no_work_yet) {
        this.had_no_work_yet = had_no_work_yet;
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
