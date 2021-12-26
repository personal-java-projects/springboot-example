package com.example.exception;

public class UnAuthorizationException extends RuntimeException {

    private int code;

    private String message;

    public UnAuthorizationException (int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UnAuthorizationException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
