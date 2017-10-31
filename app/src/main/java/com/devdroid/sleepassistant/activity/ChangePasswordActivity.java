package com.devdroid.sleepassistant.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


/**
 * 关于界面
 */
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    TextView oldPwd;
    TextView newPwd;
    TextView repeatPwa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        oldPwd = (TextView) findViewById(R.id.oldPassword);
        newPwd = (TextView) findViewById(R.id.newPassword);
        repeatPwa = (TextView) findViewById(R.id.repeatPassword);
//        LinearLayout llAppRight = (LinearLayout)findViewById(R.id.ll_app_right);
        Button button1 = (Button) findViewById(R.id.enterButton);
        button1.setOnClickListener(this);
//        PackageManager pm=getPackageManager();
//        PackageInfo info;
//        try {
//            info = pm.getPackageInfo(getPackageName(), 0);
//            tvVersion.setText(info.versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public Boolean checkInput()
    {
        Boolean checked = true;
        if (oldPwd.getText().length() == 0||newPwd.getText().length() == 0||repeatPwa.getText().length() == 0){
//            new  AlertDialog.Builder(ChangePasswordActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("密码不能为空")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(ChangePasswordActivity.this,"密码不能为空","确定");
            checked = false;
        }else if (!newPwd.getText().toString().equals(repeatPwa.getText().toString())){
//            new  AlertDialog.Builder(ChangePasswordActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("两次密码输入不一致")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(ChangePasswordActivity.this,"两次密码输入不一致","确定");
            checked = false;
        }
        return checked;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enterButton :
//                Toast.makeText(ChangePasswordActivity.this,"密码修改成功",Toast.LENGTH_LONG);
                if (checkInput()){
                    changePassword();
                }
                break;
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
    public void changePassword() {
        RequestParams params = new RequestParams();
        params.put("old_password", CYTools.getMD5Str(oldPwd.getText().toString()));
//        System.out.println("getMD5Str:"+CYTools.getMD5Str(phone.getText().toString())+"+password:"+CYTools.getMD5Str(password.getText().toString()));
        params.put("new_password", CYTools.getMD5Str(newPwd.getText().toString()));
//        params.put("validation_code", validationCodeText.getText().toString());
//        params.put("invitation_code", invitationCode.getText().toString());
        RestClient.postWithToken(ChangePasswordActivity.this, "user/modify_password", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new AlertDialog.Builder(ChangePasswordActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(ChangePasswordActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")){
                    Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
//
//                    new AlertDialog.Builder(ChangePasswordActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(ChangePasswordActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }
}
