package com.gmugu.dakaqi.view;

/**
 * Created by mugu on 16/11/24.
 */

public interface IView {
    void showCurUserInfo(String runnerName);

    void showMakeErrorMsg(String msg);

    void showMakeSuccessMsg(String msg);

    int getPointId();
}
