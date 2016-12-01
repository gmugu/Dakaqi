package com.gmugu.dakaqi.view;

/**
 * Created by mugu on 16/11/24.
 */

public interface IView {
    void showCurUserInfo(String cardMac);

    void showMakeErrorMsg(String msg);

    void showUploadSize(int size);

    int getPointId();
}
