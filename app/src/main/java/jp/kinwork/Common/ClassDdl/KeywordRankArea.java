package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class KeywordRankArea {
    /**
     * id : id
     * prefecture : 县级
     * city : 市
     * district : 区
     * magnification : 热度倍率
     * created_program : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String prefecture;
    private String city;
    private String district;
    private String magnification;
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

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMagnification() {
        return magnification;
    }

    public void setMagnification(String magnification) {
        this.magnification = magnification;
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
