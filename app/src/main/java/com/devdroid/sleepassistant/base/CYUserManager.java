package com.devdroid.sleepassistant.base;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

/**
 * Created by litingdong on 16/4/15.
 */

//管理用户信息的类
public class CYUserManager {

    private String USER_INFO = "userInfo";
    private String NOT_DELETE = "notDelete";
    private String SAVE_USER = "saveUser";
    private String SAVE_TEAM = "saveTeam";
    private String USER_TOKEN = "token";
    private String USER_NAME = "username";
    private String PASSWORD = "password";
    private String SAVE_OTHER_USER = "saveOtherUser";
    private String FIRST_USE = "firstUse";
    private String FIRST_USE_DATE = "firstUseDate";
    private String EQUIP_ID = "equipId";
    private Context context;

    private static CYUserManager instance = null;

    //单例模式
    public static CYUserManager getInstance(Context context){
        if (instance == null)
        {
            instance = new CYUserManager(context);
        }
        return instance;
    }

    private CYUserManager() {
    }

    private CYUserManager(Context context) {

        this.context = context;
    }

    public void setToken(String token) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(USER_TOKEN);
        editor.putString(USER_TOKEN, token);
        editor.commit();
    }

    public String getToken(){
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        String token = sp.getString(USER_TOKEN, "");
        return token;
    }

    public void setEQUIP_ID(String equip_id) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(EQUIP_ID);
        editor.putString(EQUIP_ID, equip_id);
        editor.commit();
    }

    public String getEQUIP_ID(){
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        String equip_id = sp.getString(EQUIP_ID, "");
        return equip_id;
    }

    //判断时候是否已经登录过，已经登录过直接到首页
    public  Boolean isLogin(){
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        String username = sp.getString(USER_NAME, "");
        if (username!=null && username.length()>0)
            return  true;

        return false;

    }
    // 存放字符串型的值
    public void setUserInfo(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.commit();
    }

    // 存放整形的值
    public void setUserInfo(String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putInt(key, value);
        editor.commit();
    }

    //将用户类存储
    public void saveCYUser(CYUser user) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(SAVE_USER);
        Gson gson = new Gson();
        String CYUser_json = gson.toJson(user);
        editor.putString(SAVE_USER,CYUser_json);
        editor.commit();
    }

    //设置第一次使用的值
    public void setFirstUse(String isFirstUse){
        SharedPreferences sp = context.getSharedPreferences(NOT_DELETE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(FIRST_USE);
        editor.putString(FIRST_USE, isFirstUse);
        editor.commit();
    }

    public Boolean isFirstUse(){
        SharedPreferences sp = context.getSharedPreferences(NOT_DELETE,
                Context.MODE_PRIVATE);
        String token = sp.getString(FIRST_USE, "");
        if (token!=null && token.equals("NO")){
            return false;
        }
        return true;
    }

    //设置第一次使用的时间
    public void saveFirstDate(String date) {
        SharedPreferences sp = context.getSharedPreferences(NOT_DELETE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(FIRST_USE_DATE);
        editor.putString(FIRST_USE_DATE,date);
        editor.commit();
    }

    //获取第一次使用的时间
    public String getFirstDate(){
        SharedPreferences sp = context.getSharedPreferences(NOT_DELETE,
                Context.MODE_PRIVATE);
        String date = sp.getString(FIRST_USE_DATE, "");
        return date;
    }



//    //将团队存储
//    public void saveCYTeam(CYTeam team) {
//        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.remove(SAVE_TEAM);
//        Gson gson = new Gson();
//        String CYTeam_json = gson.toJson(team);
//        editor.putString(SAVE_TEAM,CYTeam_json);
//        editor.commit();
//    }

    //将其他用户类存储
    public void saveOtherCYUser(CYUser user) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(SAVE_OTHER_USER);
        Gson gson = new Gson();
        String CYUser_json = gson.toJson(user);
        editor.putString(SAVE_OTHER_USER,CYUser_json);
        editor.commit();
    }

    //读取当前用户类
    public  CYUser getCurrentCYUser()
    {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        String CYUser_json = sp.getString(SAVE_USER, "");
        Gson gson = new Gson();
        CYUser user = gson.fromJson(CYUser_json,CYUser.class);
        return user;
    }

//    //读取当前浏览团队
//    public  CYTeam getCurrentCYTeam()
//    {
//        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
//                Context.MODE_PRIVATE);
//        String CYTeam_json = sp.getString(SAVE_TEAM, "");
//        Gson gson = new Gson();
//        CYTeam team = gson.fromJson(CYTeam_json,CYTeam.class);
//        return team;
//    }

    //读取当前选择的用户类
    public  CYUser getOtherCYUser()
    {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        String CYUser_json = sp.getString(SAVE_OTHER_USER, "");
        Gson gson = new Gson();
        CYUser user = gson.fromJson(CYUser_json,CYUser.class);
        return user;
    }

    // 存放长整形值
    public void setUserInfo(String key, Long value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putLong(key, value);
        editor.commit();
    }

    // 存放布尔型值
    public void setUserInfo(String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.putBoolean(key, value);
        editor.commit();
    }

    // 清空记录
    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /*
    // 注销用户时清空用户名和密码
     public void logOut() {
       SharedPreferences sp = context.getSharedPreferences(USER_INFO,
       Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sp.edit();
       editor.remove("username");
       editor.remove("password");
         //清除token
         editor.remove(USER_TOKEN);
         //清除存储的用户类
         editor.remove(SAVE_USER);
       editor.commit();
      }
    */

    // 获得用户信息中某项字符串型的值
    public String getStringInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    // 获得用户息中某项整形参数的值
    public int getIntInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getInt(key, -1);
    }

    // 获得用户信息中某项长整形参数的值
    public Long getLongInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getLong(key, -1);
    }

    // 获得用户信息中某项布尔型参数的值
    public boolean getBooleanInfo(String key) {
        SharedPreferences sp = context.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
}
