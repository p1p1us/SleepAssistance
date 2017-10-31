package com.devdroid.sleepassistant.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.bluetooth.AnyScanActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class FriendsActivity extends BaseActivity {
    private DatabaseBackupTask mBackupTask;
    private Dialog mBackupDialog;
    private Object mEventSubscriber = new Object() {
        @SuppressWarnings("unused")
        public void onEventMainThread(OnUpdateProgressBackup event) {

            if(event.getTypeProgress() == 0){  //数据还原
                if(mBackupDialog != null){
                    mBackupDialog.dismiss();
                }
                if(event.getProgressNum() == 100){
                    Toast.makeText(FriendsActivity.this, getString(R.string.ime_setting_restore_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendsActivity.this, getString(R.string.restore_data_data_bad), Toast.LENGTH_SHORT).show();
                }
            } else {   //数据备份
                if(event.getProgressNum() == 100){
                    Toast.makeText(FriendsActivity.this, getString(R.string.ime_setting_backup_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FriendsActivity.this, getString(R.string.ime_setting_backup_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!LauncherModel.getInstance().getGlobalEventBus().isRegistered(mEventSubscriber)){
            LauncherModel.getInstance().getGlobalEventBus().register(mEventSubscriber);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_add_friends:
                startActivity(new Intent(this, AddFriendsActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_my_friends:
                startActivity(new Intent(this, MyFriendsActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_friends_request:
                startActivity(new Intent(this, FriendsRequestActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(LauncherModel.getInstance().getGlobalEventBus().isRegistered(mEventSubscriber)){
            LauncherModel.getInstance().getGlobalEventBus().unregister(mEventSubscriber);
        }
    }
}
