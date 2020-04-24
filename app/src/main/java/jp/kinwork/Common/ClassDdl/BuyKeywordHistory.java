package jp.kinwork.Common.ClassDdl;
/**
 * Created by zml98 on 2018/04/04.
 */

public class BuyKeywordHistory {
    /**
     * id : id
     * user_id : 案件所有者id
     * job_id : 仕事id
     * OrderID : オーダーid
     * keyword_id : キーワードid
     * keyword_name : キーワード名前
     * address_id : 場所id
     * address_name : 場所名前
     * begin_date : 開始時間
     * contract_length : 契約時間
     * end_date : 終了時間
     * Amount : 金額
     * tax : 税金
     * created_user : 作成者
     * created_date : 作成時間
     * updated_user : 更新者
     * updated_date : 更新日時
     */

    private String id;
    private String user_id;
    private String job_id;
    private String OrderID;
    private String keyword_id;
    private String keyword_name;
    private String address_id;
    private String address_name;
    private String begin_date;
    private String contract_length;
    private String end_date;
    private String Amount;
    private String tax;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String OrderID) {
        this.OrderID = OrderID;
    }

    public String getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(String keyword_id) {
        this.keyword_id = keyword_id;
    }

    public String getKeyword_name() {
        return keyword_name;
    }

    public void setKeyword_name(String keyword_name) {
        this.keyword_name = keyword_name;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getContract_length() {
        return contract_length;
    }

    public void setContract_length(String contract_length) {
        this.contract_length = contract_length;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
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
