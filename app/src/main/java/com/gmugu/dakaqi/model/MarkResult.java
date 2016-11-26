package com.gmugu.dakaqi.model;

/**
 * Created by mugu on 16/11/24.
 */

public class MarkResult {

    //执行结果,没有错返回0
    private int resultCode;
    //MarkRequest md5值
    private String checkSum;
    //运动员姓名
    private String runnerName;
    //其他信息
    private String msg;

    public MarkResult() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MarkResult{" +
                "resultCode=" + resultCode +
                ", checkSum='" + checkSum + '\'' +
                ", runnerName='" + runnerName + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
