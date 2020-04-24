package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class KeywordsLevel {
    /**
     * level : レベル
     * click_volumes_min : 点击量（最小）
     * click_volumes_max : 点击量（最大）
     * created_program : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String level;
    private String click_volumes_min;
    private String click_volumes_max;
    private String created_program;
    private String created_date;
    private String updated_user;
    private String updated_date;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getClick_volumes_min() {
        return click_volumes_min;
    }

    public void setClick_volumes_min(String click_volumes_min) {
        this.click_volumes_min = click_volumes_min;
    }

    public String getClick_volumes_max() {
        return click_volumes_max;
    }

    public void setClick_volumes_max(String click_volumes_max) {
        this.click_volumes_max = click_volumes_max;
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
