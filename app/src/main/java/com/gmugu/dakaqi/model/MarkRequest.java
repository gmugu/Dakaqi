package com.gmugu.dakaqi.model;

/**
 * Created by mugu on 16/11/24.
 */

/**
 * 打卡请求model
 */
public class MarkRequest {
    //运动员卡号
    private String runnerNo;
    //当前打卡时间
    private long curTime;
    //打卡点编号
    private int pointId;

    public MarkRequest() {
    }

    public String getRunnerNo() {
        return runnerNo;
    }

    public void setRunnerNo(String runnerNo) {
        this.runnerNo = runnerNo;
    }

    public long getCurTime() {
        return curTime;
    }

    public void setCurTime(long curTime) {
        this.curTime = curTime;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
}
