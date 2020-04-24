package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class SavedLikedJob {

    /**
     * id : id
     * job_info_id : 仕事id
     * user_id : 基本ユーザー情報のid
     * is_deleted : 削除フラグ
     * url : 仕事のURL
     * title : 仕事のタイトル
     * releaseDate : 仕事の開始時間
     * isFromKinwork : 自社判定フラグ
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String job_info_id;
    private String user_id;
    private String is_deleted;
    private String url;
    private String title;
    private String releaseDate;
    private String isFromKinwork;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getIsFromKinwork() {
        return isFromKinwork;
    }

    public void setIsFromKinwork(String isFromKinwork) {
        this.isFromKinwork = isFromKinwork;
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
