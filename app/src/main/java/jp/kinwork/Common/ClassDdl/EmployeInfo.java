package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class EmployeInfo {
    /**
     * id : id
     * user_id : 基本ユーザー情報のid
     * company_name : 会社名
     * post_code : 郵便番号
     * add_1 : 都道府県
     * add_2 : 市区町村
     * add_3 : それ以降の住所
     * add_4 : アパート、マンション名
     * email : メールアドレス
     * your_position : 会社内の役職
     * company_tel : 会社電話番号
     * company_website : 会社ウエブサイト
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String user_id;
    private String company_name;
    private String post_code;
    private String add_1;
    private String add_2;
    private String add_3;
    private String add_4;
    private String email;
    private String your_position;
    private String company_tel;
    private String company_website;
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

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYour_position() {
        return your_position;
    }

    public void setYour_position(String your_position) {
        this.your_position = your_position;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public void setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
    }

    public String getCompany_website() {
        return company_website;
    }

    public void setCompany_website(String company_website) {
        this.company_website = company_website;
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
