package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class JobInfo {

    /**
     * id : id
     * user_id : 案件所有者id
     * company_name : 会社名
     * company_tel : 会社電話番号
     * used_email : 求人連絡用メールアドレス
     * employ_type : 応募方法
     * is_in_japan : 勤務地日本かどうかフラグ
     * post_code : 郵便番号
     * nearest_station : 最寄駅
     * add_1 : 都道府県
     * add_2 : 市区町村
     * add_3 : それ以降の住所
     * add_4 : アパート、マンション名
     * phone_number : 電話番号
     * occupation_name : 提供する職種名
     * use_language : 求人広告の言語種別
     * employment_status : 雇用形態
     * salary_from : 希望の給与from
     * salary_to : 希望の給与to
     * salary_type : 希望の給与の種別
     * job_describe1 : 仕事内容
     * job_describe2 : 求める人材
     * job_describe3 : 勤務時間・曜日
     * job_describe4 : 交通アクセス
     * job_describe5 : 待遇、福祉、厚生
     * job_describe6 : その他の説明
     * status : 処理状態
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     * is_deleted : 削除済みフラグ
     * isPaid : キーワード購入フラグ
     */

    private String id;
    private String user_id;
    private String company_name;
    private String company_tel;
    private String used_email;
    private String employ_type;
    private String is_in_japan;
    private String post_code;
    private String nearest_station;
    private String add_1;
    private String add_2;
    private String add_3;
    private String add_4;
    private String phone_number;
    private String occupation_name;
    private String use_language;
    private String employment_status;
    private String salary_from;
    private String salary_to;
    private String salary_type;
    private String job_describe1;
    private String job_describe2;
    private String job_describe3;
    private String job_describe4;
    private String job_describe5;
    private String job_describe6;
    private String status;
    private String created_user;
    private String created_date;
    private String updated_user;
    private String updated_date;
    private String is_deleted;
    private String isPaid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public void setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
    }

    public String getUsed_email() {
        return used_email;
    }

    public void setUsed_email(String used_email) {
        this.used_email = used_email;
    }

    public String getEmploy_type() {
        return employ_type;
    }

    public void setEmploy_type(String employ_type) {
        this.employ_type = employ_type;
    }

    public String getIs_in_japan() {
        return is_in_japan;
    }

    public void setIs_in_japan(String is_in_japan) {
        this.is_in_japan = is_in_japan;
    }

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    public String getNearest_station() {
        return nearest_station;
    }

    public void setNearest_station(String nearest_station) {
        this.nearest_station = nearest_station;
    }

    public String getAdd_1() {
        return add_1;
    }

    public void setAdd_1(String add_1) {
        this.add_1 = add_1;
    }

    public String getAdd_2() {
        return add_2;
    }

    public void setAdd_2(String add_2) {
        this.add_2 = add_2;
    }

    public String getAdd_3() {
        return add_3;
    }

    public void setAdd_3(String add_3) {
        this.add_3 = add_3;
    }

    public String getAdd_4() {
        return add_4;
    }

    public void setAdd_4(String add_4) {
        this.add_4 = add_4;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOccupation_name() {
        return occupation_name;
    }

    public void setOccupation_name(String occupation_name) {
        this.occupation_name = occupation_name;
    }

    public String getUse_language() {
        return use_language;
    }

    public void setUse_language(String use_language) {
        this.use_language = use_language;
    }

    public String getEmployment_status() {
        return employment_status;
    }

    public void setEmployment_status(String employment_status) {
        this.employment_status = employment_status;
    }

    public String getSalary_from() {
        return salary_from;
    }

    public void setSalary_from(String salary_from) {
        this.salary_from = salary_from;
    }

    public String getSalary_to() {
        return salary_to;
    }

    public void setSalary_to(String salary_to) {
        this.salary_to = salary_to;
    }

    public String getSalary_type() {
        return salary_type;
    }

    public void setSalary_type(String salary_type) {
        this.salary_type = salary_type;
    }

    public String getJob_describe1() {
        return job_describe1;
    }

    public void setJob_describe1(String job_describe1) {
        this.job_describe1 = job_describe1;
    }

    public String getJob_describe2() {
        return job_describe2;
    }

    public void setJob_describe2(String job_describe2) {
        this.job_describe2 = job_describe2;
    }

    public String getJob_describe3() {
        return job_describe3;
    }

    public void setJob_describe3(String job_describe3) {
        this.job_describe3 = job_describe3;
    }

    public String getJob_describe4() {
        return job_describe4;
    }

    public void setJob_describe4(String job_describe4) {
        this.job_describe4 = job_describe4;
    }

    public String getJob_describe5() {
        return job_describe5;
    }

    public void setJob_describe5(String job_describe5) {
        this.job_describe5 = job_describe5;
    }

    public String getJob_describe6() {
        return job_describe6;
    }

    public void setJob_describe6(String job_describe6) {
        this.job_describe6 = job_describe6;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }
}
