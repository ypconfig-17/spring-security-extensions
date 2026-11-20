package com.example.api;


/**
 * @author yangpan
 */


public enum ApiCode implements CommonCode {

    /**
     * 操作成功
     **/
    SUCCESS(2000, "操作成功"),
    /**
     * 登录异常
     **/
    LOGIN_EXCEPTION(1000, "登录异常"),
    /**
     * 业务异常
     **/
    BUSINESS_EXCEPTION(3000, "业务异常"),
    /**
     * 参数异常
     **/
    PARAMETER_EXCEPTION(4000, "参数异常"),
    /**
     * 系统异常
     **/
    FAIL(5000, "系统异常"),
    ;

    private final int code;
    private final String message;

    ApiCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = ApiCode.values();
        for (ApiCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
