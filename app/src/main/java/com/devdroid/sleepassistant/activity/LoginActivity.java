package com.devdroid.sleepassistant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;


/**
 * Created by p1p1us on 17/9/25.
 */
public class LoginActivity extends Activity {
    private Button loginBtn = null;
    private TextView registerBtn = null;
//    private TextView tourBtn = null;
    private EditText userName = null;
    private EditText password = null;




    //用来存储用户的信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        layoutUI();

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

    public void layoutUI()
    {
        loginBtn = (Button)findViewById(R.id.loginButton);
        registerBtn = (TextView)findViewById(R.id.register);
//        tourBtn = (TextView)findViewById(R.id.tourist);
        userName = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().length()==0||password.getText().length()==0)
                {
//                    new  AlertDialog.Builder(LoginActivity.this)
//                            .setTitle("提示" )
//                            .setMessage("用户名密码不能为空")
//                            .setPositiveButton("确定" ,  null )
//                            .show();
                    OnlyAlert(LoginActivity.this,"用户名密码不能为空","确定");

//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
//                    startActivity(intent);

                }else{
                    //登录
                    RequestParams params = new RequestParams();
                    params.put("username", CYTools.getMD5Str(userName.getText().toString()));
                    params.put("password", CYTools.getMD5Str(password.getText().toString()));


                    RestClient.get("user/login", params,new JsonHttpResponseHandler(){

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
                                    //此处获得的token里面包含了两个"，
                                    CYUserManager.getInstance(LoginActivity.this).setToken(CYTools.getRealParam(jsonObject.get("token").toString()));
                                    System.out.println("denglushidetoken1:" + jsonObject.get("token").toString());
                                    //获取个人信息
//                                getProfile();
                                    //将信息保存起来，下次登陆
                                    CYUserManager.getInstance(LoginActivity.this).setUserInfo("username", userName.getText().toString());
                                    CYUserManager.getInstance(LoginActivity.this).setUserInfo("password", password.getText().toString());
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("type", "1");
                                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                                    startActivity(intent);
                                }else {
//                                    new AlertDialog.Builder(LoginActivity.this)
//                                            .setTitle("提示")
//                                            .setMessage(response.getString("desc"))
//                                            .setPositiveButton("确定", null)
//                                            .show();
                                    OnlyAlert(LoginActivity.this,CYTools.getRealParam(response.getString("desc")),"确定");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            System.out.println(responseString);
//                            new  AlertDialog.Builder(LoginActivity.this)
//                                    .setTitle("提示")
//                                    .setMessage("账号密码错误")
//                                    .setPositiveButton("确定" ,  null )
//                                    .show();
                            OnlyAlert(LoginActivity.this,"账号密码错误","确定");
                        }
                    });

                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //跳转到注册页面
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                startActivity(intent);
            }
        });

    }
}
