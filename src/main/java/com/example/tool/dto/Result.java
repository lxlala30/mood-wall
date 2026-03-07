package com.example.tool.dto;

import lombok.Data;

// 结果返回示例
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "success";
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(T data) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = "error";
        result.data = data;
        return result;
    }

    // getter/setter
}
