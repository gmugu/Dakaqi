package com.gmugu.dakaqi.presenter.impl;

import android.util.Log;

import com.gmugu.dakaqi.data.ApiService;
import com.gmugu.dakaqi.data.IApiService;
import com.gmugu.dakaqi.model.MarkRequest;
import com.gmugu.dakaqi.model.MarkResult;
import com.gmugu.dakaqi.presenter.ILogicPresenter;
import com.gmugu.dakaqi.util.MD5Util;
import com.gmugu.dakaqi.util.QueueBuffer;
import com.gmugu.dakaqi.view.IView;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mugu on 16/11/24.
 */

public class LogicPresenterImpl implements ILogicPresenter {

    private static final String TAG = LogicPresenterImpl.class.getSimpleName();
    private IView view;
    private IApiService apiService = ApiService.getApiService();
    private HashMap<String, Long> macMap = new HashMap<>();

    private QueueBuffer<String> queueOfWaitForUpload = new QueueBuffer();

    public LogicPresenterImpl(final IView view) {
        this.view = view;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    String rawJson = queueOfWaitForUpload.pop();
                    Log.d(TAG, "正在重发");
                    String enJson = rawJson;
                    Call<MarkResult> mark = apiService.mark(enJson);
                    mark.enqueue(new MyCallback(rawJson, enJson));
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public synchronized void onReceive(String MAC, String tag) {
        if (macMap.containsKey(MAC)) {
            if (System.currentTimeMillis() - macMap.get(MAC) <= 30 * 1000) {
                Log.d(TAG, "onReceive: ignore:" + MAC + "  tag:" + tag);
                return;
            }
        }
        Log.d(TAG, "onReceive: " + MAC + "  tag:" + tag);
        macMap.put(MAC, System.currentTimeMillis());

        view.showCurUserInfo(MAC);
        view.showUploadSize(queueOfWaitForUpload.getBufferSize());
        MarkRequest request = new MarkRequest();
        request.setRunnerNo(MAC);
        request.setCurTime(System.currentTimeMillis());
        request.setPointId(Integer.parseInt(tag));
        String rawJson = new Gson().toJson(request);
        // TODO: 16/11/25
        String enJson = rawJson;
        Call<MarkResult> mark = apiService.mark(enJson);
        mark.enqueue(new MyCallback(rawJson, enJson));
    }

    private class MyCallback implements Callback<MarkResult> {
        private String rawJson;
        private String enJson;

        public MyCallback(String rawJson, String enJson) {
            this.rawJson = rawJson;
            this.enJson = enJson;
        }

        @Override
        public void onResponse(Call<MarkResult> call, Response<MarkResult> response) {
            try {
                MarkResult result = response.body();
                Log.d(TAG, result.toString());
                if (result.getResultCode() != 0) {
                    throw new Exception("服务器错误");
                }
                if (!result.getCheckSum().equals(MD5Util.md5ToHexStr(rawJson))) {
                    throw new Exception("校验码错误");
                }

            } catch (Exception e) {
                queueOfWaitForUpload.put(rawJson);
            } finally {
                view.showUploadSize(queueOfWaitForUpload.getBufferSize());
            }
        }

        @Override
        public void onFailure(Call<MarkResult> call, Throwable t) {
            t.printStackTrace();
            queueOfWaitForUpload.put(rawJson);
            view.showUploadSize(queueOfWaitForUpload.getBufferSize());
        }
    }

}
