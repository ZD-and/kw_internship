package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public  class User {
    /**
     * id : id
     * kinwork_email : 中間メールアドレス
     * user_type : ユーザー種類
     * company_id : 会社情報のid
     * permissions : ユーザー権限（企業ユーザー用）
     * created_program : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String kinwork_email;
    private String user_type;
    private String company_id;
    private String permissions;
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

    public String getKinwork_email() {
        return kinwork_email;
    }

    public void setKinwork_email(String kinwork_email) {
        this.kinwork_email = kinwork_email;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
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
