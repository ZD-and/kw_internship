package jp.kinwork.Common;

import java.util.List;

/**
 * Created by zml98 on 2018/04/12.
 */

public class GetJsonData{


    /**
     * processResult : false
     * message : メールアドレスフォーマット不正
     * returnData :
     * fieldErrors : {"email":["メールのルールが正しくない"]}
     */

    private boolean processResult;
    private String message;
    private String returnData;
    private FieldErrorsBean fieldErrors;

    public boolean isProcessResult() {
        return processResult;
    }

    public void setProcessResult(boolean processResult) {
        this.processResult = processResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReturnData() {
        return returnData;
    }

    public void setReturnData(String returnData) {
        this.returnData = returnData;
    }

    public FieldErrorsBean getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(FieldErrorsBean fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public static class FieldErrorsBean {
        private List<String> email;

        public List<String> getEmail() {
            return email;
        }

        public void setEmail(List<String> email) {
            this.email = email;
        }
    }
}
