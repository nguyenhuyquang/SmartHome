package com.example.smarthome.Model;

public class FirebaseModel {
    public String code;
    public String cmd;

    public FirebaseModel() {

    }

    public FirebaseModel(String code, String cmd) {
        this.code = code;
        this.cmd = cmd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
