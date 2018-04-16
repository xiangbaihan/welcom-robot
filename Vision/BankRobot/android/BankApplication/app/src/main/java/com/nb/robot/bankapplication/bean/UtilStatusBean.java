package com.nb.robot.bankapplication.bean;

public class UtilStatusBean {
    public int errorCode;
    public String errorMessage;
    public boolean okay;

    public boolean isOkay(){
        return okay;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String toString() {
        return errorCode + ": " + errorMessage;
    }
}
