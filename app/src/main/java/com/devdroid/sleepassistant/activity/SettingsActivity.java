package com.devdroid.sleepassistant.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.CustomConfirmDialog;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.bluetooth.AnyScanActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;
import com.devdroid.sleepassistant.view.SelectConnectWayPopup;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends BaseActivity {
    private DatabaseBackupTask mBackupTask;
    private Dialog mBackupDialog;
    private LinearLayout mAlllayout;
    private Object mEventSubscriber = new Object() {
        @SuppressWarnings("unused")
        public void onEventMainThread(OnUpdateProgressBackup event) {

            if(event.getTypeProgress() == 0){  //数据还原
                if(mBackupDialog != null){
                    mBackupDialog.dismiss();
                }
                if(event.getProgressNum() == 100){
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_restore_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.restore_data_data_bad), Toast.LENGTH_SHORT).show();
                }
            } else {   //数据备份
                if(event.getProgressNum() == 100){
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_backup_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_backup_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAlllayout = (LinearLayout) findViewById(R.id.all_layout);
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
            case R.id.ll_setting_data_export:
                new DatabaseBackupTask(this).execute(CustomConstant.COMMAND_BACKUP_INTERNAL_STORAGE);
                Toast.makeText(this, getString(R.string.ime_setting_backuping), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_setting_data_import:
                showFileChooser();
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_setting_about:
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_setting_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.bt_setting_logout:
//                finish();
//                new AlertDialog.Builder(SettingsActivity.this).setTitle("确认注销?")//设置对话框标题
//
//
//                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
//
//
//
//                            @Override
//
//                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//                                // TODO Auto-generated method stub
//
//                                Intent logoutIntent = new Intent(SettingsActivity.this, LoginActivity.class);
//                                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(logoutIntent);
//
//                            }
//
//                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//
//                    @Override
//
//                    public void onClick(DialogInterface dialog, int which) {//响应事件
//
//                        // TODO Auto-generated method stub
//
////                Log.i("alertdialog"," 请保存数据！");
//
//                    }
//
//                }).show();//在按键响应事件中显示此对话框
                Logout(this);
                break;
            case R.id.ll_setting_helpCenter:
                startActivity(new Intent(this, HelpCenterActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_setting_account:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                break;
            case R.id.ll_setting_connectType:
                changeConnectType();
                break;
        }
    }

    public void Logout(final Context context) {
        final CustomConfirmDialog confirmDialog = new CustomConfirmDialog(context, "确认注销?", "确定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new CustomConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                // TODO Auto-generated method stub
                Intent logoutIntent = new Intent(context, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
            }

            @Override
            public void doCancel() {
                // TODO Auto-generated method stub
                confirmDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case 1111:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    final String path = FileUtils.getPath(this, uri);
                    if(path != null && path.endsWith(".back")) {
                        showBackupDialog(path);
                    } else {
                        Toast.makeText(this, getString(R.string.restore_data_select_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * 注销账号时进行密码验证
     */
    private void showBackupDialog(final String path) {
        mBackupDialog = new Dialog(this, R.style.dialogCustom);
        mBackupDialog.setContentView(R.layout.dialog_add_to_file);
        final ProgressBar mPbProgressBar = (ProgressBar) mBackupDialog.findViewById(R.id.pb_addconstact_progress);
        final TextView mTvProgressBarNum = (TextView) mBackupDialog.findViewById(R.id.addconstact_progress);
        final TextView mContactPhone = (TextView) mBackupDialog.findViewById(R.id.contactsphone);
        Button addfile_cancle = (Button) mBackupDialog.findViewById(R.id.addfile_cancle);
        addfile_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackupDialog.dismiss();
                if(mBackupTask != null){
                    mBackupTask.cancel(true);
                }
            }
        });
        findViewById(R.id.bt_setting_logout).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackupTask = new DatabaseBackupTask(SettingsActivity.this, mPbProgressBar, mTvProgressBarNum, mContactPhone);
                mBackupTask.execute(CustomConstant.COMMAND_RESTORE_INTERNAL_STORAGE, path);
            }
        }, 500);
        mBackupDialog.show();
    }

    public void changeConnectType2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this); //定义一个AlertDialog
        String[] strarr = {"蓝牙连接","远程连接"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                // 自动生成的方法存根
                if (arg1 == 0) {//
                    Intent intent = new Intent(SettingsActivity.this, AnyScanActivity.class);
                    startActivity(intent);
                }else {//
//                    final EditText inputServer = new EditText(SettingsActivity.this);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//                    builder.setTitle("请输入设备号").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                            .setNegativeButton("取消", null);
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (inputServer.getText().toString().length()>0){
////                            Toast.makeText(UserCenterActivity.this,inputServer.getText().toString(),Toast.LENGTH_LONG).show();
//                                addEquipment(inputServer.getText().toString());
//                            }else {
//                                Toast.makeText(SettingsActivity.this,"设备号不能为空",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                    builder.show();
                    showAlertDialog();
                }
            }
        });
        builder.show();
    }

    public void changeConnectType() {
        SelectConnectWayPopup fp = new SelectConnectWayPopup(this);
        fp.showPopup(mAlllayout);
        fp.setOnSelectRemindWayPopupListener(new SelectConnectWayPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    //
                    case 0:
                        Intent intent = new Intent(SettingsActivity.this, AnyScanActivity.class);
                        startActivity(intent);
                        break;
                    //
                    case 1:
                        showAlertDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.activity_input_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.activity_input_dialog);
        TextView btnPositive = (TextView) dialog.findViewById(R.id.btn_save_pop);
//        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.text_input);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("设备号不能为空");
                } else {
                    dialog.dismiss();
                    addEquipment(etContent.getText().toString());
//                    Toast.makeText(MyEquipsActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

    public void addEquipment(String str) {
        CYUserManager.getInstance(SettingsActivity.this).setEQUIP_ID(str);
        RequestParams params = new RequestParams();
        params.put("equip_id", str);
        RestClient.postWithToken(SettingsActivity.this, "equip/"+str+"/connect", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new android.app.AlertDialog.Builder(SettingsActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(SettingsActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")) {
                    Toast.makeText(SettingsActivity.this, "选择成功", Toast.LENGTH_SHORT).show();
                } else {

//                    new android.app.AlertDialog.Builder(SettingsActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(SettingsActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
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
    /**
     * 打开文件选择器
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Import"), 1111);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
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
