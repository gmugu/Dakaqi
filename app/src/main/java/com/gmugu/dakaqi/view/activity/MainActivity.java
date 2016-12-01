package com.gmugu.dakaqi.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gmugu.dakaqi.R;
import com.gmugu.dakaqi.data.ApiService;
import com.gmugu.dakaqi.model.GameInfoResult;
import com.gmugu.dakaqi.model.PlayerModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getName();
    private final String GAME_INFO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data.obj";
    private ToggleButton bluetoothModeTB;
    private ToggleButton nfcModeTB;
    private Spinner pointSp;
    private TextView gameInfoStatus;

    private GameInfoResult gameInfo;
    private int pointId = 0;

    private final String pointList[] = new String[]{
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
    };

    private void test() {
        GameInfoResult result = new GameInfoResult();
        result.setGameName("heheda");
        result.setRSAPrivateKey("gdfgdfg");
        HashMap<String, PlayerModel> playerInfos = new HashMap<>();
        PlayerModel value = new PlayerModel();
        value.setCardMAC("0xdb20fb3a");
        value.setID("32");
        value.setName("hehe");
        value.setGroup("Sf");
        playerInfos.put("0xdb20fb3a", value);
        result.setPlayerInfos(playerInfos);

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(GAME_INFO_PATH));
            outputStream.writeObject(result);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        bluetoothModeTB = (ToggleButton) findViewById(R.id.bluetooth_mode_tb);
        nfcModeTB = (ToggleButton) findViewById(R.id.nfc_mode_tb);
        pointSp = (Spinner) findViewById(R.id.point_spinner);
        gameInfoStatus = (TextView) findViewById(R.id.status_of_init_tv);

        bluetoothModeTB.setOnCheckedChangeListener(this);
        nfcModeTB.setOnCheckedChangeListener(this);
        pointSp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pointList));
        pointSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                pointId = Integer.parseInt(tv.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(GAME_INFO_PATH));
            gameInfo = (GameInfoResult) in.readObject();
            in.close();
            gameInfoStatus.setText("初始化比赛数据!(" + gameInfo.getGameName() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BluetoothDeviceListActivity.getBtSocket() != null) {
            try {
                BluetoothDeviceListActivity.getBtSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothDeviceListActivity.REQUEST_CONNECT_DEVICE:
                try {
                    if (resultCode == RESULT_OK) {
                        boolean isSuc = data.getBooleanExtra(BluetoothDeviceListActivity.EXTRA_IS_BT_CONNECT_SUCCESS, false);
                        if (!isSuc) {
                            throw new Exception("设备未连接");
                        }
                    } else {
                        throw new Exception("设备连接失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    bluetoothModeTB.setChecked(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == bluetoothModeTB) {
            if (!isChecked) {
                BluetoothDeviceListActivity.closeBtSocket();
            } else {
                BluetoothDeviceListActivity.startDiscoveryBluetooth(this);
            }
        } else if (buttonView == nfcModeTB) {
            if (!isChecked) {
                // 获取默认的NFC控制器
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
                if (nfcAdapter == null) {
                    Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_SHORT).show();
                    nfcModeTB.setChecked(false);
                    return;
                }
                if (!nfcAdapter.isEnabled()) {
                    Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
                    nfcModeTB.setChecked(false);
                    return;
                }
            }
        }
    }

    public void onInitGameClick(View view) {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("输入当前比赛密码")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog waitDialog = new ProgressDialog(MainActivity.this);
                        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        waitDialog.setMessage("正在联网...");
                        Call<GameInfoResult> gameInfoResultCall = ApiService.getApiService().initGame(editText.getText().toString());
                        gameInfoResultCall.enqueue(new Callback<GameInfoResult>() {
                            @Override
                            public void onResponse(Call<GameInfoResult> call, Response<GameInfoResult> response) {
                                try {
                                    GameInfoResult body = response.body();
                                    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(GAME_INFO_PATH));
                                    outputStream.writeObject(body);
                                    outputStream.close();
                                    gameInfo = body;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
                                } finally {
                                    waitDialog.cancel();
                                }
                            }

                            @Override
                            public void onFailure(Call<GameInfoResult> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                                waitDialog.cancel();
                            }
                        });
                        waitDialog.show();
                    }
                })
                .show();
    }

    public void onStareClick(View view) {
        if (gameInfo == null) {
            Toast.makeText(this, "未初始化比赛信息", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean bluetoothMode = bluetoothModeTB.isChecked();
        boolean nfcMode = nfcModeTB.isChecked();
        if (!(bluetoothMode || nfcMode)) {
            Toast.makeText(this, "必须选择一种刷卡模式", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pointId == 0) {
            Toast.makeText(this, "必须设置打卡点", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, RunActivity.class);
        intent.putExtra(RunActivity.GAME_INFO_P, gameInfo);
        intent.putExtra(RunActivity.BLUETOOTH_MODE_P, bluetoothMode);
        intent.putExtra(RunActivity.NFC_MODE_P, nfcMode);
        intent.putExtra(RunActivity.POINT_ID_P, pointId);
        startActivity(intent);
    }
}
