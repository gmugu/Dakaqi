package com.gmugu.dakaqi.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.gmugu.dakaqi.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CONNECT_DEVICE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DeviceListActivity.getBtSocket() != null) {
            try {
                DeviceListActivity.getBtSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                try {
                    if (resultCode == RESULT_OK) {
                        boolean isSuc = data.getBooleanExtra(DeviceListActivity.EXTRA_IS_BT_CONNECT_SUCCESS, false);
                        if (!isSuc) {
                            throw new Exception("设备未连接");
                        }
                        askPoint(BluetoothModeActivity.class);
                    } else {
                        throw new Exception("设备连接失败");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void onBluetoothModeBnClick(View view) {
        Intent serverIntent = new Intent(this,
                DeviceListActivity.class); // 跳转程序设置
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
    }

    public void onNFCModeBnClick(View view) {
        askPoint(NFCModeActivity.class);
    }

    private void askPoint(final Class whichClass) {
        final EditText editText = new EditText(this);
        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("请选择当前打卡点")
                .setItems(new String[]{
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
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, whichClass);
                        intent.putExtra("pointId", which + 1);
                        startActivity(intent);
                    }
                }).show();
    }

}
