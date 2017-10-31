package com.devdroid.sleepassistant.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.utils.DevicesUtils;
import com.devdroid.sleepassistant.utils.NetworkUtil;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * FeedBack界面
 */

public class AddFriendsActivity extends BaseActivity {
    private EditText mContainer; //正文
    private EditText mUsername; //要添加的用户名
//    private ImageView mImageView; //下拉框箭头
//    private LinearLayout mMenuCommon; //下拉框
//    private TextView mProblem; //下拉框文本
//    private TextView mForceInstall; //下拉框文本
//    private TextView mSuggestion; //下拉框文本
//    private TextView mSelect; //下拉框文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }
    private void init(){
        mContainer = (EditText)findViewById(R.id.container_addreason);
        mUsername = (EditText)findViewById(R.id.container_username);
//        mSelect = (TextView)findViewById(R.id.setting_feedback_menu_select);
//        TextView mNotice = (TextView) findViewById(R.id.notice_setting_feedback);

        mContainer.setHint("请输入您的好友申请理由");
        mContainer.setFocusable(true);
        mContainer.setFocusableInTouchMode(true);
        mContainer.requestFocus();

        mUsername.setHint("请输入用户名");
        mUsername.setFocusable(true);
        mUsername.setFocusableInTouchMode(true);
        mUsername.requestFocus();
//        mNotice.setText(R.string.notice_setting_feedback);
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

    private void sendAddRequest(final String username, final String detail) {
        RequestParams params = new RequestParams();
        params.put("friend_username", CYTools.getMD5Str(username));
//        System.out.println("getMD5Str:"+CYTools.getMD5Str(phone.getText().toString())+"+password:"+CYTools.getMD5Str(password.getText().toString()));
        params.put("message", detail);
//        params.put("validation_code", validationCodeText.getText().toString());
//        params.put("invitation_code", invitationCode.getText().toString());
        RestClient.postWithToken(AddFriendsActivity.this, "user/friends_apply", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new AlertDialog.Builder(AddFriendsActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(AddFriendsActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")){
                    Toast.makeText(AddFriendsActivity.this, "发送好友申请成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {

//                    new AlertDialog.Builder(AddFriendsActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(AddFriendsActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }
    private void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.item_feedback_send:
                String detailString = mContainer.getText().toString().trim();
                String usernameString = mUsername.getText().toString().trim();
//                String selectItem = mSelect.getText().toString();
                if (detailString.equals("")) {
                    Toast.makeText(AddFriendsActivity.this, getString(R.string.no_contain_setting_feedback), Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (usernameString.equals("")) {
                    Toast.makeText(AddFriendsActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return true;
                }
                sendAddRequest(usernameString, detailString);
                break;
        }
        return true;
    }
}
