package com.example.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yangpan
 */


@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 8004487252556526569L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;



    public ApiResult() {

    }

    public static <T> ApiResult<T> result(boolean flag) {
        if (flag) {
            return ok();
        }
        return fail();
    }

    public static <T> ApiResult<T> result(CommonCode apiCode) {
        return result(apiCode, null);
    }

    public static <T> ApiResult<T> result(CommonCode apiCode, T data) {
        return result(apiCode, null, data);
    }

    @SuppressWarnings("unchecked")
    public static <T> ApiResult<T> result(CommonCode apiCode, String message, T data) {
        boolean success = false;
        if (apiCode.getCode() == ApiCode.SUCCESS.getCode()) {
            success = true;
        }
        if (StringUtils.isBlank(message)) {
            message = apiCode.getMessage();
        }
        return (ApiResult<T>) ApiResult.builder()
                .code(apiCode.getCode())
                .message(message)
                .data(data)
                .success(success)
                .build();
    }

    public static <T> ApiResult<T> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return result(ApiCode.SUCCESS, data);
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return result(ApiCode.SUCCESS, message, data);
    }

    public static ApiResult<Map<String, Object>> okMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);
        return ok(map);
    }

    public static <T> ApiResult<T> fail(CommonCode apiCode) {
        return result(apiCode, null);
    }

    public static <T> ApiResult<T> fail(String message) {
        return result(ApiCode.FAIL, message, null);

    }

    public static <T> ApiResult<T> fail(CommonCode apiCode, T data) {
        if (ApiCode.SUCCESS.getCode() == apiCode.getCode()) {
            throw new RuntimeException("失败结果状态码不能为" + ApiCode.SUCCESS.getCode());
        }
        return result(apiCode, data);

    }

    public static <T>  ApiResult<T> fail(Integer errorCode, String message) {
        return new ApiResult<T>()
                .setSuccess(false)
                .setCode(errorCode)
                .setMessage(message);
    }
    public static <T>  ApiResult<T> fail(Integer errorCode, String message,T data) {
        return new ApiResult<T>()
                .setSuccess(false)
                .setCode(errorCode)
                .setMessage(message)
                .setData(data);
    }
    public static ApiResult<Map<String, Object>> fail(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);
        return result(ApiCode.FAIL, map);
    }

    public static <T> ApiResult<T> fail() {
        return fail(ApiCode.FAIL);
    }

    public static <T> ApiResult<T> fail(T t) {
        return new ApiResult<T>().setCode(ApiCode.FAIL.getCode()).setMessage(ApiCode.FAIL.getMessage());
    }
}