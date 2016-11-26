package com.gmugu.dakaqi.view.activity;


import com.gmugu.dakaqi.R;
import com.gmugu.dakaqi.presenter.ILogicPresenter;
import com.gmugu.dakaqi.presenter.impl.LogicPresenterImpl;
import com.gmugu.dakaqi.view.IView;

/**
 * Created by mugu on 16/11/24.
 */


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class NFCModeActivity extends Activity implements IView {

    private ILogicPresenter presenter = new LogicPresenterImpl(this);

    private NfcAdapter nfcAdapter;
    private TextView msgTv;

    private PendingIntent pendingIntent;

    private String[][] techList;

    private IntentFilter[] intentFilters;
    private int pointId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        msgTv = (TextView) findViewById(R.id.main_view_info_tv);
        pointId = getIntent().getIntExtra("pointId", 0);
        // 获取默认的NFC控制器
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            msgTv.setText("设备不支持NFC！");
            finish();
            return;
        }
        if (!nfcAdapter.isEnabled()) {
            msgTv.setText("请在系统设置中先启用NFC功能！");
            finish();
            return;
        }

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

    @Override
    protected void onResume() {
        super.onResume();
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
    public void showCurUserInfo(String runnerName) {
        msgTv.setText(runnerName);
    }

    @Override
    public void showMakeErrorMsg(String msg) {
        msgTv.setText("出错了:" + msg);
    }

    @Override
    public void showMakeSuccessMsg(String msg) {
        msgTv.setText(msg);
    }

    @Override
    public int getPointId() {
        return pointId;
    }
}
