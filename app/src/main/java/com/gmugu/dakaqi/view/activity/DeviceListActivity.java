/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmugu.dakaqi.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmugu.dakaqi.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class DeviceListActivity extends Activity {
    // 调试用
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // 返回时数据标签
    public static final String EXTRA_BT_DEVICE_ADDRESS = "EXTRA_BT_DEVICE_ADDRESS";
    public static final String EXTRA_IS_BT_CONNECT_SUCCESS = "EXTRA_IS_BT_CONNECT_SUCCESS";
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号

    // 成员域
    private BluetoothAdapter mBtAdapter;
    private BluetoothDevice mBtDevice;

    private static BluetoothSocket mBtSocket;
    private Context mContext;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayAdapter<String> mConnectedDevicesArrayAdapter;
    private View disconnectBn;
    private TextView titleConnectDevicesTv;
    private ListView connectedDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // 创建并显示窗口
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // 设置窗口显示模式为窗口方式
        setContentView(R.layout.device_list);

        // 设定默认返回值为取消
        setResult(Activity.RESULT_CANCELED);

        // 设定扫描按键响应
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // 初使化设备存储数组
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        mConnectedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);

        // 设置已配对设备列表

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 设置新查找设备列表
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 设置已连接设备列表
        connectedDevicesListView = (ListView) findViewById(R.id.connected_devices);
        connectedDevicesListView.setAdapter(mConnectedDevicesArrayAdapter);
        connectedDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 注册查找结束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // 得到本地蓝牙句柄
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(mContext, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
            finish();
        }
        if (!mBtAdapter.isEnabled()) {
            if (!mBtAdapter.enable()) {
                Toast.makeText(mContext, "无法打开手机蓝牙！", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        // 得到已连接蓝牙设备列表
        if (mBtSocket != null) {
            mConnectedDevicesArrayAdapter.add(mBtSocket.getRemoteDevice().getName() + "\n" + mBtSocket.getRemoteDevice().getAddress());
            titleConnectDevicesTv = (TextView) findViewById(R.id.title_connected_devices);
            titleConnectDevicesTv.setVisibility(View.VISIBLE);
            connectedDevicesListView.setVisibility(View.VISIBLE);
            disconnectBn = findViewById(R.id.button_disconnect);
            disconnectBn.setVisibility(View.VISIBLE);
            disconnectBn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mBtSocket.close();
                        Toast.makeText(mContext, "成功断开", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mBtSocket = null;
                        disconnectBn.setVisibility(View.GONE);
                        titleConnectDevicesTv.setVisibility(View.GONE);
                        connectedDevicesListView.setVisibility(View.GONE);
                        mConnectedDevicesArrayAdapter.clear();
                    }
                }
            });
        }

        // 得到已配对蓝牙设备列表
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // 添加已配对设备到列表并显示
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = "No devices have been paired";
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭服务查找
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // 注销action接收器
        this.unregisterReceiver(mReceiver);
    }

    public void OnCancel(View v) {
        finish();
    }

    /**
     * 开始服务和设备查找
     */
    private void doDiscovery() {
        if (D) {
            Log.d(TAG, "doDiscovery()");
        }

        // 在窗口显示查找中信息
        setProgressBarIndeterminateVisibility(true);
        setTitle("查找设备中...");

        // 显示其它设备（未配对设备）列表
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 关闭再进行的服务查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        // 并重新开始
        mBtAdapter.startDiscovery();
    }

    // 选择设备响应函数
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 准备连接设备，关闭服务查找
            mBtAdapter.cancelDiscovery();

            // 得到mac地址
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 设置返回数据
            Intent intent = new Intent();
            intent.putExtra(EXTRA_BT_DEVICE_ADDRESS, address);

            if (av != connectedDevicesListView) {

                // 得到蓝牙设备句柄
                mBtDevice = mBtAdapter.getRemoteDevice(address);
                // 用服务号得到socket
                try {
                    if (mBtSocket != null) {
                        mBtSocket.close();
                        mBtSocket = null;
                        connectedDevicesListView.setVisibility(View.GONE);
                        titleConnectDevicesTv.setVisibility(View.GONE);
                        disconnectBn.setVisibility(View.GONE);
                    }
                    mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(UUID
                            .fromString(MY_UUID));
                } catch (IOException e) {
                    Toast.makeText(mContext, "连接失败！", Toast.LENGTH_SHORT).show();
                    mBtSocket = null;
                    return;
                }
                // 连接socket
                try {
                    mBtSocket.connect();
                    intent.putExtra(EXTRA_IS_BT_CONNECT_SUCCESS, true);
                    Toast.makeText(mContext, "连接" + mBtDevice.getName() + "成功！", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    try {
                        Toast.makeText(mContext, "连接失败！", Toast.LENGTH_SHORT).show();
                        mBtSocket.close();
                    } catch (IOException ee) {
                    } finally {
                        mBtSocket = null;
                    }
                    return;
                }
            } else {
                intent.putExtra(EXTRA_IS_BT_CONNECT_SUCCESS, true);
            }
            // 设置返回值并结束程序
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // 查找到设备和搜索完成action监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果是已配对的则略过，已得到显示，其余的在添加到列表中进行显示
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                } else { // 添加到已配对设备列表
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
                // 搜索完成action
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("选择要连接的设备");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "没有找到新设备";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                if (mPairedDevicesArrayAdapter.getCount() > 0) {
                    findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
                }
            }
        }
    };


    public static BluetoothSocket getBtSocket() {
        return mBtSocket;
    }

}
