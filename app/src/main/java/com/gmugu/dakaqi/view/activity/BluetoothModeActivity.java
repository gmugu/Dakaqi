package com.gmugu.dakaqi.view.activity;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.gmugu.dakaqi.R;
import com.gmugu.dakaqi.presenter.ILogicPresenter;
import com.gmugu.dakaqi.presenter.impl.LogicPresenterImpl;
import com.gmugu.dakaqi.view.IView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mugu on 16/11/24.
 */

public class BluetoothModeActivity extends AppCompatActivity implements IView {
    private static final String TAG = BluetoothModeActivity.class.getSimpleName();
    private ILogicPresenter presenter = new LogicPresenterImpl(this);
    private BluetoothSocket btSocket;
    private int pointId;
    private TextView msgTv;
    private Context mContext;

    private BufferedReader btReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        mContext = this;
        init();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btReader != null) {
            try {
                btReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                btReader = null;
            }
        }
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                btSocket = null;
            }
        }

    }

    private void init() {
        pointId = getIntent().getIntExtra("pointId", 0);
        if (pointId < 1 || pointId > 100) {
            Toast.makeText(this, "打卡点错误!", Toast.LENGTH_SHORT).show();
            finish();
        }

        msgTv = (TextView) findViewById(R.id.main_view_upload_size_tv);
        msgTv.setText(pointId + "");

        btSocket = BluetoothDeviceListActivity.getBtSocket();
        try {
            btReader = new BufferedReader(new InputStreamReader(
                    btSocket.getInputStream())); // 得到蓝牙数据输入流
        } catch (IOException e) {
            Toast.makeText(mContext, "接收数据失败！", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        // 打开接收线程
        new Thread("get data") {

            @Override
            public void run() {
                String getStr;
                while (btReader != null) {
                    try {
                        getStr = btReader.readLine();
//                        Log.d(TAG, "run: btRecv:" + getStr);
                        presenter.onReceive(getStr, pointId + "");

                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        }.start();

    }


    @Override
    public void showCurUserInfo(String cardMAC) {


    }

    @Override
    public void showMakeErrorMsg(String msg) {

    }

    @Override
    public void showUploadSize(int size) {

    }


    @Override
    public int getPointId() {
        return pointId;
    }
}
