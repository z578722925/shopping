package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    //状态码
    private int status;
    //数据
    private T data;
    //提示信息
    private String msg;

    private ServerResponse(int status){
        this.status=status;

    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,T data,String msg){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }



    /**
     * 判断接口是否正确返回
     * status=0
     * */
    @JsonIgnore
    //不在json序列化对象中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    //调用接口成功时回调
    public static <T> ServerResponse<T> serverResponseBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());

    }
    public static <T>  ServerResponse<T> serverResponseBySuccess(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> serverResponseBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> serverResponseBySuccess(T data,String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data,msg);
    }
    /**
     * 接口调用失败时回调
     * */
    public static <T> ServerResponse<T> serverResponseByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }
    public static <T> ServerResponse<T> serverResponseByError(String msg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }
    public static <T> ServerResponse<T> serverResponseByError(int status){
        return new ServerResponse<T>(status);
    }
    public static <T> ServerResponse<T> serverResponseByError(int status,String msg){
        return new ServerResponse<T>(status,msg);
    }



    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
    public static void main(String[] args){
        ServerResponse serverResponse=new ServerResponse(0,new Object());
        ServerResponse serverResponse1=ServerResponse.serverResponseBySuccess("hello",null);
        System.out.println(serverResponse);
    }
}

