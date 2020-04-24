package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class UserBasic {

    /**
     * id : id
     * user_id : 基本ユーザー情報のid
     * first_name : 名
     * last_name : 姓
     * first_name_kana : 名発音
     * last_name_kana : 姓発音
     * birthday : 生日
     * sex_div : 性別
     * country : 国籍
     * post_code : 郵便番号
     * add_1 : 都道府県
     * add_2 : 市区町村
     * add_3 : それ以降の住所
     * add_4 : アパート、マンション名
     * phone_number : 電話番号
     * is_deleted : 删除标志
     * created_program : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String user_id;
    private String first_name;
    private String last_name;
    private String first_name_kana;
    private String last_name_kana;
    private String birthday;
    private String sex_div;
    private String country;
    private String post_code;
    private String add_1;
    private String add_2;
    private String add_3;
    private String add_4;
    private String phone_number;
    private String is_deleted;
    private String created_program;
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

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name_kana() {
        return first_name_kana;
    }

    public void setFirst_name_kana(String first_name_kana) {
        this.first_name_kana = first_name_kana;
    }

    public String getLast_name_kana() {
        return last_name_kana;
    }

    public void setLast_name_kana(String last_name_kana) {
        this.last_name_kana = last_name_kana;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex_div() {
        return sex_div;
    }

    public void setSex_div(String sex_div) {
        this.sex_div = sex_div;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getCreated_program() {
        return created_program;
    }

    public void setCreated_program(String created_program) {
        this.created_program = created_program;
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
