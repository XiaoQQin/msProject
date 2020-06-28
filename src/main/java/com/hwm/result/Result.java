package com.hwm.result;

public class Result<T> {
    private int code;
    private String msg;
    private T data;


    public static <T>Result<T> success(T data){
        return new Result<>(data);
    }
    private Result(T data){
        this.data=data;
    }
    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 成功调用
     */
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
