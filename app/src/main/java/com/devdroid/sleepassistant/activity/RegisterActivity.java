package com.devdroid.sleepassistant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUser;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devdroid.sleepassistant.R;

import cz.msebera.android.httpclient.Header;

/**
 * Created by p1p1us on 17/10/15.
 */
public class RegisterActivity extends Activity{

    private EditText phone = null;
    private EditText code = null;
    private Button sendBtn = null;
    private EditText password = null;
    private EditText password2 = null;
    private Button registerBtn = null;
//    private Button sendSMSButton = null;
//    private EditText validationCodeText = null;
//    private EditText invitationCode = null;
//    private CheckBox cbAgreement = null;
//    private TextView tvAgreement = null;
//    private String validation_code = "";
    private boolean running = false;
    private List<CYUser> list;
    private List<String> urlList1 = new ArrayList<String>();
    private List<String> urlList2 = new ArrayList<String>();
    private List<CYUser> applyList = new ArrayList<CYUser>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regsiter);
        layoutUI();
    }

    public void layoutUI(){
        phone = (EditText)findViewById(R.id.username);
//        code =  (EditText)findViewById(R.id.code_text);
        //sendBtn = (Button)findViewById(R.id.send_code_btn);
        password = (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password2);
        registerBtn = (Button)findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkInput())
                {
                    //注册
                    register();
                }
            }
        });

    }

    public Boolean checkInput()
    {
        Boolean checked = true;
        if (phone.getText().length() == 0){
//            new  AlertDialog.Builder(RegisterActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("邮箱不能为空")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(RegisterActivity.this,"邮箱不能为空","确定");
            checked = false;
        }else if(!isEmail(phone.getText().toString())){
//            new  AlertDialog.Builder(RegisterActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("邮箱不合法")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(RegisterActivity.this,"邮箱不合法","确定");
            checked = false;
        }else if(password.getText().length() == 0){
//            new  AlertDialog.Builder(RegisterActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("密码不能为空")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(RegisterActivity.this,"密码不能为空","确定");
            checked = false;
        }else if (!password.getText().toString().equals(password2.getText().toString())){
//            new  AlertDialog.Builder(RegisterActivity.this)
//                    .setTitle("提示" )
//                    .setMessage("两次密码输入不一致")
//                    .setPositiveButton("确定" ,  null )
//                    .show();
            OnlyAlert(RegisterActivity.this,"两次密码输入不一致","确定");
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
    //判断邮箱的合法性
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }



    public void register() {
        RequestParams params = new RequestParams();
        params.put("username", CYTools.getMD5Str(phone.getText().toString()));
        System.out.println("getMD5Str:"+CYTools.getMD5Str(phone.getText().toString())+"+password:"+CYTools.getMD5Str(password.getText().toString()));
        params.put("password", CYTools.getMD5Str(password.getText().toString()));
//        params.put("validation_code", validationCodeText.getText().toString());
//        params.put("invitation_code", invitationCode.getText().toString());
        RestClient.post("user/signin", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new AlertDialog.Builder(RegisterActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(RegisterActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("register response:" + responseString);
                JsonObject jsonObj = stringToJsonObject(responseString);
                System.out.println("register response:" + jsonObj.get("code").toString());
//                CYUserManager.getInstance(RegisterActivity.this).setToken(jsonObj.get("token").getAsString());

//                System.out.println("token " + jsonObj.get("token").getAsString());

                System.out.println("zhuceshideresponse:"+responseString+"+statuscode:"+statusCode);
                if (jsonObj.get("code").toString().equals("0")){

                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    //获取个人信息
//                    getProfile();
                    //将信息保存起来，下次登陆
                    CYUserManager.getInstance(RegisterActivity.this).setUserInfo("username", phone.getText().toString());
                    CYUserManager.getInstance(RegisterActivity.this).setUserInfo("password", password.getText().toString());
                    //注册成功之后需要将用户信息存入缓存UserInfo
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
                    finish();
                }
                else {
//                    new AlertDialog.Builder(RegisterActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(RegisterActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }

    public static JsonObject stringToJsonObject(String s){
        Gson gson = new Gson();
        JsonElement element = gson.fromJson (s, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        return  jsonObj;
    }


//    public void getProfile(){
//
//        //获取个人资料
//        RequestParams params = new RequestParams();
//        //
//        //BaseJsonHttpResponseHandler
//        //TextHttpResponseHandler
//        RestClient.getWithToken(RegisterActivity.this,"get/user/info/", params,new JsonHttpResponseHandler(){
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                System.out.println("test1");
//                System.out.println(responseString);
//
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                System.out.println(response);
//                Gson gson = new Gson();
//                CYUser user =  gson.fromJson(response.toString(),CYUser.class);
//                CYUserManager.getInstance(RegisterActivity.this).saveCYUser(user);
//                CYUser user1 = CYUserManager.getInstance(RegisterActivity.this).getCurrentCYUser();
//
//                String uriString = "";
//                if (user.icon_url.length() == 0){
//                    uriString = "http://chuangyh.com:8000/uploaded/default_icon.jpg";
//                }else{
//                    uriString = user.getIcon_url();
//                }
//
//                initUserInfo();
//            }
//
//
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                System.out.println(responseString);
//                new  AlertDialog.Builder(RegisterActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定" , null)
//                        .show();
//            }
//        });
//    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

//    private void initUserInfo() {
//        list = new ArrayList<CYUser>();
//
//        RequestParams params = new RequestParams();
//        params.put("limit", "1000");
//        RestClient.getWithToken(this, "users/", params, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.e("MainActivity", "UserId is : ");
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//
//                JsonObject jsonObj = stringToJsonObject(responseString);
//                list = stringToArray(jsonObj.getAsJsonArray("list").toString(), CYUser[].class);
//                //System.out.println(list+"CYFriendsFragment的");
//                urlList1.clear();
//                for (final CYUser user : list) {
//                    String url;
//                    if (user.icon_url == null) {
//                        urlList1.add(null);
//                    } else {
//                        if (user.icon_url.length() > 0) {
//                            url = user.icon_url;
//                            urlList1.add(RestClient.getAbsoluteUrl(url));
//                        } else {
//                            urlList1.add("");
//                        }
//                    }
//                }
//            }
//        });
//    }

}
