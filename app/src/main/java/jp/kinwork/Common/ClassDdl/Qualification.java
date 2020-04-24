package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class Qualification {
    /**
     * id : id
     * id_resume : 関連履歴書のid番号
     * user_id : 基本ユーザー情報のid
     * qualification_name : 資格名
     * from_date : 資格開始日
     * to_date : 資格終了日
     * description : 説明
     * is_till_now : 現在、ここに勤務しているフラグ(0：現在にはない、1：現在にもある)
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String id_resume;
    private String user_id;
    private String qualification_name;
    private String from_date;
    private String to_date;
    private String description;
    private String is_till_now;
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

    public String getQualification_name() {
        return qualification_name;
    }

    public void setQualification_name(String qualification_name) {
        this.qualification_name = qualification_name;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIs_till_now() {
        return is_till_now;
    }

    public void setIs_till_now(String is_till_now) {
        this.is_till_now = is_till_now;
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
