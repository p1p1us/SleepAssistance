package com.devdroid.sleepassistant.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用引导页
 */
public class GuideActivity extends BaseActivity {
//    private Animation alphaAnimation = null;
    private CYUserManager cyUserManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        cyUserManager = CYUserManager.getInstance(this);
    }

    private void initView(){
        if (Build.VERSION.SDK_INT >= 23) {   //申请通用权限
            String[] permissions = requestPermissions();
            if (permissions != null) {
                requestPermissions(requestPermissions(), 1003);
                return;
            }
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());

        if (cyUserManager.isFirstUse()){
            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition( R.anim.move_right_in_activity, R.anim.move_left_out_activity);
            finish();
            cyUserManager.setFirstUse("NO");
            cyUserManager.saveFirstDate(date);
        }else{
            String firstUseDate = cyUserManager.getFirstDate();
            //如果和上次出现引导页的登录时间间隔超过7天的话,更新时间,显示引导页
            System.out.println("intervals: " + CYTools.stringDaysBetween(firstUseDate, date));
            if (CYTools.stringDaysBetween(firstUseDate, date) > 7){
                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition( R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                finish();
//                cyUserManager.setFirstUse("NO");
                cyUserManager.saveFirstDate(date);
            }else {
                if (!cyUserManager.isLogin()) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                            GuideActivity.this.startActivity(intent);
                            GuideActivity.this.finish();
                        }
                    }, 1000);
                } else {
                    //如果登录成功，直接进入主界面
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                            intent.putExtra("type", "2");
                            GuideActivity.this.startActivity(intent);
                            GuideActivity.this.finish();
                        }
                    }, 1000);
                }
            }
        }


        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    /**
     * 请求需要的权限
     */
    private String[] requestPermissions() {
        String[] permissions = null;
        List<String> permissionsList = new ArrayList<>();
        addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionsList.size() > 0) {
            permissions = new String[permissionsList.size()];
            for(int i = 0 ; i < permissionsList.size() ; i++){
                permissions[i] = permissionsList.get(i);
            }
        }
        return permissions;
    }
    private void addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1003) {
            for (int i = 0; i < grantResults.length;i++) {
                int grant = grantResults[i];
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.permissionnot), Toast.LENGTH_SHORT).show();
                } else if(i == grantResults.length -1){
                    initView();
                }
            }
        }
    }

    private void initData() {
        TextView tvDayLable = (TextView)this.findViewById(R.id.tv_activity_guide_time_lable);
        long appInstallTime = LauncherModel.getInstance().getSharedPreferencesManager().getLong(IPreferencesIds.KEY_FIRST_START_APP_TIME, (long)0);
        if (appInstallTime != 0) {
            int datepoor = (int)((System.currentTimeMillis() - appInstallTime)/1000/60/60/24 + 1);
            String date = getString(R.string.guard_date);
            tvDayLable.setText(date);
        } else {
            String date = getString(R.string.guard_date);
            tvDayLable.setText(date);
        }
    }


}
