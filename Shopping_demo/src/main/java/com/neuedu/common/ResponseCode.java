package com.neuedu.common;
/**
 * 维护状态码
 * */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(100,"ERROR"),
    NEED_LOGIN(10,"NEED_login")
    ;
    private final  int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }

}
