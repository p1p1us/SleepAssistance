package com.devdroid.sleepassistant.bluetooth;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.devdroid.sleepassistant.R;
import com.clj.fastble.data.ScanResult;
import com.devdroid.sleepassistant.activity.MyEquipsActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AnyScanActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_start, btn_stop;
    private ImageView img_loading;
    private Animation operatingAnim;
    private ResultAdapter mResultAdapter;
    private ProgressDialog progressDialog;

    private BluetoothService mBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_any_scan);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null)
            unbindService();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("搜索设备");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);
        img_loading = (ImageView) findViewById(R.id.img_loading);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        operatingAnim.setInterpolator(new LinearInterpolator());
        progressDialog = new ProgressDialog(this);

        mResultAdapter = new ResultAdapter(this);
        ListView listView_device = (ListView) findViewById(R.id.list_device);
        listView_device.setAdapter(mResultAdapter);
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
                    mBluetoothService.connectDevice(mResultAdapter.getItem(position));
                    mResultAdapter.clear();
                    mResultAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                checkPermissions();
                break;

            case R.id.btn_stop:
                if (mBluetoothService != null) {
                    mBluetoothService.cancelScan();
                }
                break;
        }
    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private List<ScanResult> scanResultList;

        ResultAdapter(Context context) {
            this.context = context;
            scanResultList = new ArrayList<>();
        }

        void addResult(ScanResult result) {
            scanResultList.add(result);
        }

        void clear() {
            scanResultList.clear();
        }

        @Override
        public int getCount() {
            return scanResultList.size();
        }

        @Override
        public ScanResult getItem(int position) {
            if (position > scanResultList.size())
                return null;
            return scanResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ResultAdapter.ViewHolder holder;
            if (convertView != null) {
                holder = (ResultAdapter.ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_scan_result, null);
                holder = new ResultAdapter.ViewHolder();
                convertView.setTag(holder);
                holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
                holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
                holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            }

            ScanResult result = scanResultList.get(position);
            BluetoothDevice device = result.getDevice();
            String name = device.getName();
            String mac = device.getAddress();
            int rssi = result.getRssi();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(rssi));
            return convertView;
        }

        class ViewHolder {
            TextView txt_name;
            TextView txt_mac;
            TextView txt_rssi;
        }
    }

    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
            mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private BluetoothService.Callback callback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {
            mResultAdapter.clear();
            mResultAdapter.notifyDataSetChanged();
            img_loading.startAnimation(operatingAnim);
            btn_start.setEnabled(false);
            btn_stop.setVisibility(View.VISIBLE);
        }

        @Override
        public void onScanning(ScanResult result) {
            mResultAdapter.addResult(result);
            mResultAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScanComplete() {
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onConnecting() {
            progressDialog.show();
        }

        @Override
        public void onConnectFail() {
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            Toast.makeText(AnyScanActivity.this, "连接失败", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisConnected() {
            progressDialog.dismiss();
            mResultAdapter.clear();
            mResultAdapter.notifyDataSetChanged();
            img_loading.clearAnimation();
            btn_start.setEnabled(true);
            btn_stop.setVisibility(View.INVISIBLE);
            Toast.makeText(AnyScanActivity.this, "连接断开", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServicesDiscovered() {
            progressDialog.dismiss();
//            startActivity(new Intent(AnyScanActivity.this, OperationActivity.class));
            startOperate();
        }
    };

    private void startOperate(){
//        String str = "110";//接收到的消息中的设备号
        //开始接收消息
        startReadData();
//        //添加该设备
//        addEquipment(str);
//        //连接该设备
//        connectEquipment(str);
//        //设置设备号为该设备
//        CYUserManager.getInstance(AnyScanActivity.this).setEQUIP_ID(str);

        finish();
    }

    private void startReadData(){

        BluetoothGatt gatt = mBluetoothService.getGatt();
        BluetoothGattService service = gatt.getServices().get(0);
        mBluetoothService.setService(service);

        final BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(0);
        mBluetoothService.setCharaProp(1);
        mBluetoothService.setCharacteristic(characteristic);

        mBluetoothService.read(
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleCharacterCallback() {

                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        AnyScanActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String strhhh = "";
                                strhhh = strhhh+String.valueOf(HexUtil.encodeHex(characteristic.getValue()))+"\n";
                                System.out.println("收到的消息："+strhhh);
//                                String equip_id = CYTools.stringToJsonObject(strhhh).get("equip_id").toString();
                                String equip_id = "";
                                //添加该设备
                                addEquipment(equip_id);
                                //连接该设备
                                connectEquipment(equip_id);
                                //设置设备号为该设备
                                CYUserManager.getInstance(AnyScanActivity.this).setEQUIP_ID(equip_id);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final BleException exception) {
                        AnyScanActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String eceptions = "";
                                eceptions = eceptions+ exception.toString()+"\n";
                                System.out.println("收到的消息异常："+eceptions);
                            }
                        });
                    }

                    @Override
                    public void onInitiatedResult(boolean result) {

                    }
                });
    }

    public void addEquipment(String str) {
        RequestParams params = new RequestParams();
        params.put("equip_id", str);
        RestClient.postWithToken(AnyScanActivity.this, "user/equips", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new android.app.AlertDialog.Builder(MyEquipsActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(AnyScanActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")) {
//                    Toast.makeText(AnyScanActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                } else {

//                    new android.app.AlertDialog.Builder(MyEquipsActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(AnyScanActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }
    public void connectEquipment(String str) {
        RequestParams params = new RequestParams();
        params.put("equip_id", str);
        RestClient.postWithToken(AnyScanActivity.this, "equip/"+str+"/connect", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new android.app.AlertDialog.Builder(SettingsActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(AnyScanActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")) {
//                    Toast.makeText(AnyScanActivity.this, "选择成功", Toast.LENGTH_SHORT).show();
                    Toast.makeText(AnyScanActivity.this,"连接设备成功",Toast.LENGTH_SHORT).show();
                } else {

//                    new android.app.AlertDialog.Builder(SettingsActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(AnyScanActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }
    public static void OnlyAlert(final Context context, String message, String confirm) {
        final OnlyAlertDialog onlyAlertDialog = new OnlyAlertDialog(context, message, confirm);
        onlyAlertDialog.show();
        onlyAlertDialog.setClicklistener(new OnlyAlertDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                // TODO Auto-generated method stub
                onlyAlertDialog.dismiss();
            }
        });
    }



    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i]);
                        }
                    }
                }
                break;
        }
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (mBluetoothService == null) {
                    bindService();
                } else {
                    mBluetoothService.scanDevice();
                }
                break;
        }
    }

}
