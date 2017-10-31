package com.devdroid.sleepassistant.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.mode.MyRandom;
import com.devdroid.sleepassistant.view.SineWave;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class WaveActivity extends AppCompatActivity {
    private Button ceshi; // 波形
    private SineWave sw1;
    private SineWave sw2;
    private TextView heartbeat;
    private TextView breath;
    private boolean isCS = true;
    private PowerManager.WakeLock wakeLock = null;//通过powermanager保持应用在此界面下不熄灭屏幕
//	private Axes as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initView();
        //保持此页面常亮
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
        wakeLock.acquire();
//		as = new Axes(this);
    }

    private void initView() {
        sw1 = (SineWave) findViewById(R.id.sw1);
        sw2 = (SineWave) findViewById(R.id.sw2);
        heartbeat = (TextView) findViewById(R.id.heartbeat_num);
        breath = (TextView) findViewById(R.id.breath_num);
//        ceshi = (Button) findViewById(R.id.ceshi);
        MyRandom.isRandom = true;
        new MyRandom(handler1).start();
        new MyRandom(handler2).start();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                Message message=new Message();
                message.what=10;
                mHandler.sendMessage(message);
            }
        }, 500, 1000);

//        ceshi.setOnClickListener((View.OnClickListener) new WaveOnClickListener());
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            if(msg.what == 10){
//                getNowData();
                int a = (int)(Math.random()*20+60);
                int b = (int)(Math.random()*10+30);
                heartbeat.setText(a+"次/min");
                breath.setText(b+"次/min");
                sw1.Set(a);
                sw1.Set(-a);
                sw1.reFresh();
//                sw2.Set(b);
//                sw2.Set(-b);
//                sw2.reFresh();
            }
        }
    };

    public static void OnlyAlert(final Context context,String message,String confirm) {
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

    public void getNowData(){
        //获取实时信息
        RequestParams params1 = new RequestParams();
        RestClient.getWithToken(WaveActivity.this, "equip/110/now", params1,new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (response.getString("code").equals("0")) {
                        System.out.println("denglushidetoken:" + response.getString("data"));
                        JsonObject jsonObject = CYTools.stringToJsonObject(response.getString("data"));
                    } else {
//                        new android.app.AlertDialog.Builder(WaveActivity.this)
//                                .setTitle("提示")
//                                .setMessage("网络错误")
//                                .setPositiveButton("确定", null)
//                                .show();
                        OnlyAlert(WaveActivity.this,"网络错误","确定");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
//                new android.app.AlertDialog.Builder(WaveActivity.this)
//                        .setTitle("提示")
//                        .setMessage("未知网络错误"+responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(WaveActivity.this,"未知网络错误","确定");
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    // 刷新折线图
    public Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            sw1.Set(msg.what-30);
            sw1.reFresh();
        }
    };
    // 刷新折线图
    public Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            sw2.Set(msg.what-30);
            sw2.reFresh();
        }
    };

    @Override
    protected void onDestroy() {
        handler1.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
        handler1=null;
        handler2=null;
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();
    }

}
