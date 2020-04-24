package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class ContractPlan {
    /**
     * month_amount : 時間（月）
     * price_magnification : 金額倍率(%)
     * created_program : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String month_amount;
    private String price_magnification;
    private String created_program;
    private String created_date;
    private String updated_user;
    private String updated_date;

    public String getMonth_amount() {
        return month_amount;
    }

    public void setMonth_amount(String month_amount) {
        this.month_amount = month_amount;
    }

    public String getPrice_magnification() {
        return price_magnification;
    }

    public void setPrice_magnification(String price_magnification) {
        this.price_magnification = price_magnification;
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
