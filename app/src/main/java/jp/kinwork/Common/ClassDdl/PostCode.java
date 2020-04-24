package jp.kinwork.Common.ClassDdl;

/**
 * Created by zml98 on 2018/04/04.
 */

public class PostCode {

    /**
     * post_code : 郵便番号
     * add_1 : 都道府県名
     * add_2 : 市区町村名
     * add_3 : 町域名
     */

    private String post_code;
    private String add_1;
    private String add_2;
    private String add_3;

    public String getPost_code() {
        return post_code;
    }

    public void setPost_code(String post_code) {
        this.post_code = post_code;
    }

    public String getAdd_1() {
        return add_1;
    }

    public void setAdd_1(String add_1) {
        this.add_1 = add_1;
    }

    public String getAdd_2() {
        return add_2;
    }

    public void setAdd_2(String add_2) {
        this.add_2 = add_2;
    }

    public String getAdd_3() {
        return add_3;
    }

    public void setAdd_3(String add_3) {
        this.add_3 = add_3;
    }
}
