package com.msg.bean;

/**
 * Created by 3020mt on 2016/9/26.
 */
public class RequestInfo {


    public String upload0;
    public boolean state;
    public String msg;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUpload0() {
        return upload0;
    }

    public void setUpload0(String upload0) {
        this.upload0 = upload0;
    }


}
