package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class Category {
    /**
     * category_1 : カテゴリー1
     * category_2 : カテゴリー2
     * keyword : keyword
     * keyword_weights : keyword_weights
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String category_1;
    private String category_2;
    private String keyword;
    private String keyword_weights;
    private String created_user;
    private String created_date;
    private String updated_user;
    private String updated_date;

    public String getCategory_1() {
        return category_1;
    }

    public void setCategory_1(String category_1) {
        this.category_1 = category_1;
    }

    public String getCategory_2() {
        return category_2;
    }

    public void setCategory_2(String category_2) {
        this.category_2 = category_2;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword_weights() {
        return keyword_weights;
    }

    public void setKeyword_weights(String keyword_weights) {
        this.keyword_weights = keyword_weights;
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
