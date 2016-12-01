package com.gmugu.dakaqi.view.activity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gmugu.dakaqi.R;
import com.gmugu.dakaqi.model.GameInfoResult;
import com.gmugu.dakaqi.model.PlayerModel;
import com.gmugu.dakaqi.presenter.ILogicPresenter;
import com.gmugu.dakaqi.presenter.impl.LogicPresenterImpl;
import com.gmugu.dakaqi.view.IView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by mugu on 16/11/30.
 */

public class RunActivity extends AppCompatActivity implements IView {


    public static final String GAME_INFO_P = "GAME_INFO_P";
    public static final String BLUETOOTH_MODE_P = "BLUETOOTH_MODE_P";
    public static final String NFC_MODE_P = "NFC_MODE_P";
    public static final String POINT_ID_P = "POINT_ID_P";
    private final String TAG = getClass().getName();

    private boolean isBluetoothMode = false;
    private boolean isNfcMode = false;
    private GameInfoResult gameInfo;
    private int pointId;

    private TextView uploadSizeTv;
    private TextView runnerIdTv;
    private TextView runnerNameTv;
    private TextView runnerGroupTv;


    private ILogicPresenter presenter = new LogicPresenterImpl(this);

    //nfc field
    private NfcAdapter nfcAdapter;
    private String[][] techList;
    private IntentFilter[] intentFilters;
    private PendingIntent pendingIntent;

    //bluetooth field
    private BluetoothSocket btSocket;
    private BufferedReader btReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        Intent intent = getIntent();
        isBluetoothMode = intent.getBooleanExtra(BLUETOOTH_MODE_P, false);
        isNfcMode = intent.getBooleanExtra(NFC_MODE_P, false);
        pointId = intent.getIntExtra(POINT_ID_P, 0);
        gameInfo = (GameInfoResult) intent.getSerializableExtra(GAME_INFO_P);


        uploadSizeTv = (TextView) findViewById(R.id.main_view_upload_size_tv);
        runnerIdTv = (TextView) findViewById(R.id.main_view_info_id_tv);
        runnerNameTv = (TextView) findViewById(R.id.main_view_info_name_tv);
        runnerGroupTv = (TextView) findViewById(R.id.main_view_group_tv);

        if (isBluetoothMode) {
            initBluetooth();
        }
        if (isNfcMode) {
            initNFC();
        }
    }


    private void initNFC() {
        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // 定义程序可以兼容的nfc协议，例子为nfca和nfcv
        // 在Intent filters里声明你想要处理的Intent，一个tag被检测到时先检查前台发布系统，
        // 如果前台Activity符合Intent filter的要求，那么前台的Activity的将处理此Intent。
        // 如果不符合，前台发布系统将Intent转到Intent发布系统。如果指定了null的Intent filters，
        // 当任意tag被检测到时，你将收到TAG_DISCOVERED intent。因此请注意你应该只处理你想要的Intent。
        techList = new String[][]{new String[]{android.nfc.tech.NfcA.class
                .getName()}};
        intentFilters = new IntentFilter[]{new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED)
                // ,
                // new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                // new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };
        // 创建一个 PendingIntent 对象, 这样Android系统就能在一个tag被检测到时定位到这个对象
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private void initBluetooth() {
        btSocket = BluetoothDeviceListActivity.getBtSocket();
        try {
            btReader = new BufferedReader(new InputStreamReader(
                    btSocket.getInputStream())); // 得到蓝牙数据输入流
        } catch (IOException e) {
            Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT)
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
    protected void onResume() {
        super.onResume();
//        if ()
        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            // 处理该intent
            processIntent(getIntent());
        }

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters,
                techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    // 字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 得到是否检测到ACTION_TECH_DISCOVERED触发
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            // 处理该intent
            processIntent(intent);
        }
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    private void processIntent(Intent intent) {
        // 取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcA nfcA = NfcA.get(tagFromIntent);
        onNfcRead(nfcA);
    }

    private void onNfcRead(NfcA nfcA) {
        String id = bytesToHexString(nfcA.getTag().getId());

        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        presenter.onReceive(id, pointId + "");
    }

    @Override
    public void showCurUserInfo(final String runnerName) {
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "run: " + gameInfo.toString());
                Map<String, PlayerModel> playerInfos = gameInfo.getPlayerInfos();
                PlayerModel playerModel = playerInfos.get(runnerName);
                runnerIdTv.setText(playerModel.getID());
                runnerNameTv.setText(playerModel.getName());
                runnerGroupTv.setText(playerModel.getGroup());
            }
        });
    }

    @Override
    public void showMakeErrorMsg(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(RunActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showUploadSize(final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadSizeTv.setText((size + 1) + "");
            }
        });
    }


    @Override
    public int getPointId() {
        return pointId;
    }
}
