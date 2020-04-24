package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class SessionMessage {
    /**
     * id : id
     * submitted_resume_id : 提出済み履歴id
     * tmp_user_email : 中間個人メールアドレス
     * tmp_job_email : 中間企業メールアドレス
     * user_email : 真実個人メールアドレス
     * job_email : 真実企業メールアドレス
     * message : 通信内容詳細
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String submitted_resume_id;
    private String tmp_user_email;
    private String tmp_job_email;
    private String user_email;
    private String job_email;
    private String message;
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

    public String getSubmitted_resume_id() {
        return submitted_resume_id;
    }

    public void setSubmitted_resume_id(String submitted_resume_id) {
        this.submitted_resume_id = submitted_resume_id;
    }

    public String getTmp_user_email() {
        return tmp_user_email;
    }

    public void setTmp_user_email(String tmp_user_email) {
        this.tmp_user_email = tmp_user_email;
    }

    public String getTmp_job_email() {
        return tmp_job_email;
    }

    public void setTmp_job_email(String tmp_job_email) {
        this.tmp_job_email = tmp_job_email;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getJob_email() {
        return job_email;
    }

    public void setJob_email(String job_email) {
        this.job_email = job_email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
