package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class SavedResume {

    /**
     * id : id
     * job_info_id : 関連求人広告のid番号
     * applicant_user_id : 応募者id
     * status_personal : 面接状況（個人側）
     * employer_id : 雇用者id
     * status_company : 面接状況（企業側）
     * personal_is_deleted : 個人ユーザ削除フラグ
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String job_info_id;
    private String applicant_user_id;
    private String status_personal;
    private String employer_id;
    private String status_company;
    private String personal_is_deleted;
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

    public String getJob_info_id() {
        return job_info_id;
    }

    public void setJob_info_id(String job_info_id) {
        this.job_info_id = job_info_id;
    }

    public String getApplicant_user_id() {
        return applicant_user_id;
    }

    public void setApplicant_user_id(String applicant_user_id) {
        this.applicant_user_id = applicant_user_id;
    }

    public String getStatus_personal() {
        return status_personal;
    }

    public void setStatus_personal(String status_personal) {
        this.status_personal = status_personal;
    }

    public String getEmployer_id() {
        return employer_id;
    }

    public void setEmployer_id(String employer_id) {
        this.employer_id = employer_id;
    }

    public String getStatus_company() {
        return status_company;
    }

    public void setStatus_company(String status_company) {
        this.status_company = status_company;
    }

    public String getPersonal_is_deleted() {
        return personal_is_deleted;
    }

    public void setPersonal_is_deleted(String personal_is_deleted) {
        this.personal_is_deleted = personal_is_deleted;
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
