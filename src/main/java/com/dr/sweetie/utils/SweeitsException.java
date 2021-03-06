package com.dr.sweetie.utils;

/**
 * 自定义异常
 *
 * @author qewli12
 */
public class SweeitsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String msg;

    private int code = 0;

    public SweeitsException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public SweeitsException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public SweeitsException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public SweeitsException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
