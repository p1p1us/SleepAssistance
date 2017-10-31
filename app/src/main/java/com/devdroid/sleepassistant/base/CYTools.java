package com.devdroid.sleepassistant.base;

import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.activity.ChangePasswordActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by p1p1us on 17/10/18.
 */

//一些公用的方法
public class CYTools {


    //获取安卓sdk的版本
    public static int getAndroidOSVersion()
    {
        int osVersion;
        try
        {
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        }
        catch (NumberFormatException e)
        {
            osVersion = 0;
        }

        return osVersion;
    }
    //判断电话是否有效
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    //判断邮箱的合法性
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    public static <T> T stringToClass(String s, Class<T> clazz) {
        T a = new Gson().fromJson(s, clazz);
        return a; //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }



    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    public static JsonObject stringToJsonObject(String s){
        Gson gson = new Gson();
        JsonElement element = gson.fromJson (s, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        return  jsonObj;
    }

    public static String encryptPhone(String phone){
        Random random = new Random();
        int xxx = random.nextInt(1000) % 900  + 100;
        int yyy = random.nextInt(1000) % 900 + 100;
        String WXWXWXW = "" + phone.charAt(phone.length()- 4) + (xxx / 100) % 10 + phone.charAt(phone.length()- 3) + (xxx /10) % 10 + phone.charAt(phone.length()- 2) + xxx % 10 + phone.charAt(phone.length()- 1);
        System.out.println(WXWXWXW);
        int WXWXWXWInt = Integer.parseInt(WXWXWXW);
        int zzz = WXWXWXWInt % yyy;
        String newPhoneStr;
        if (zzz < 100){
            newPhoneStr = xxx + phone.substring(0,3) + yyy + phone.substring(3,11) + "0" + zzz;
        }else {
            newPhoneStr = xxx + phone.substring(0,3) + yyy + phone.substring(3,11)  + zzz;
        }
        String timeStamp = "2487100320";
        Long timeStampInt = Long.parseLong(timeStamp);
        Long d2 = timeStampInt / 2;
        String key = timeStamp + String.valueOf(d2);
        BigInteger newPhoneInt = new BigInteger(newPhoneStr);
        BigInteger keyInt = new BigInteger(key);
        BigInteger xorInt = newPhoneInt.xor(keyInt);
        return xorInt.toString();
    }

    //发送好友请求
    public static void sendFriendRequest(final Context context, String user_id, String description, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        params.put("description",description);
        String url = "/users/" + user_id + "/friend_requests/";
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //测试两个用户是否为好友
    public static void CheckFriends(Context context, String user_id_1, String user_id_2, final CYCheckFriendHandler handler){
     ///users/1/friends/4/
        RequestParams params = new RequestParams();
        String url = "/users/" + user_id_1 + "/friends/" + user_id_2 + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.isNotFriendHandle();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.isFriendHandle();
            }
        });
    }

    public static void refreshCurrentUserProfile(final Context context) {
        //获取用户的资料
        RequestParams params = new RequestParams();
        String url = "users/current/profile/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                CYUser user =  gson.fromJson(responseString, CYUser.class);
                //更新数据
                CYUserManager.getInstance(context).saveCYUser(user);
            }
        });
    }

    public static void getUserProfile(final Context context , String id, final NetRequestHandler handler) {
        //获取用户的资料
        RequestParams params = new RequestParams();
        String url = "users/" + id + "/profile/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("datachange" + responseString);
                Gson gson = new Gson();
                CYUser user =  gson.fromJson(responseString, CYUser.class);
                handler.successHandle(user);
            }
        });
    }
    //设置头像，包括用户和团队
    public static void setIcon(Context context , String url , File iconFile, final GetObjectListHandler handler) throws FileNotFoundException {

        RequestParams params = new RequestParams();
        params.put("icon", iconFile);
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("upload image" + responseString);
                handler.successHandle(responseString);
            }
        });
    }
    //获取md5加密后的32位字符串
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    //去除前后两个引号
    public static String getRealParam(String str) {
        if (str.startsWith("\"")&&str.endsWith("\"")){
            return str.substring(1,str.length()-1);
        }else
            return str;
    }

    //获取关注，点赞，评价，好友的列表
    public static void getObjectList(final Context context, String url, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")){
                    handler.successHandle(responseString);
                } else {

                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
                            .setPositiveButton("确定", null)
                            .show();
                }
            }
        });
    }

    //评价，包括给自己评价
    public static void toComment(Context context, String url, String content, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        params.put("content", content);
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对用户点赞
    public static void checkLikeOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/users/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对用户标签点赞
    public static void checkLikeUserTagOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/user_tags/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }
    //判断是否对团队标签点赞
    public static void checkLikeTeamTagOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/team_tags/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对活动点赞
    public static void checkLikeActivityOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/activities/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }


    //判断是否对活动收藏
    public static void checkFavorActivityOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/favored/activities/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对竞赛点赞
    public static void checkLikeCompetitionOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/competitions/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对竞赛收藏
    public static void checkFavorCompetitionOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/favored/competitions/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对团队点赞
    public static void checkLikeTeamOrNot(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/teams/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对动态点赞
    public static void checkLikeActionOrNot(Context context, String id, String type, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/" +  type + "_actions/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //判断是否对动态收藏
    public static void checkFavorActionOrNot(Context context, String id, String type, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/favored/" +  type + "_actions/" + id + "/";
        RestClient.getWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                handler.successHandle(responseString);
            }
        });
    }

    //点赞
    public static void toLike(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/users/" + id + "/";
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //点赞成功
                handler.successHandle(responseString);
            }
        });
    }
    //取消对某个人的点赞
    public static void toUnLike(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/users/" + id + "/";
        RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //取消点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //取消点赞成功
                handler.successHandle(responseString);
            }
        });
    }

    //点赞用户标签
    public static void toLikeUserTag(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/user_tags/" + id + "/";
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //点赞成功
                handler.successHandle(responseString);
            }
        });
    }

    public static void toLikeTeamTag(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/team_tags/" + id + "/";
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //点赞成功
                handler.successHandle(responseString);
            }
        });
    }

    //取消对某个用户标签的点赞
    public static void toUnLikeUserTag(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/user_tags/" + id + "/";
        RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //取消点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //取消点赞成功
                handler.successHandle(responseString);
            }
        });
    }

    //取消对某个团队标签的点赞
    public static void toUnLikeTeamTag(Context context, String id, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //users/current/liked/users/9/
        String url = "users/current/liked/team_tags/" + id + "/";
        RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //取消点赞失败
                handler.failedHandle(responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //取消点赞成功
                handler.successHandle(responseString);
            }
        });
    }

    //取消对某个人的点赞
    //id  动态的id
    //type team 团队的动态  user 用户的动态
    //method post点赞  delete取消点赞
    public static void toLikeActionOrNot(Context context, String id, String type, String method, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //liked/user_actions/(?P<action_id>[0-9]+)/
        String url = "users/current/liked/" + type + "_actions/" + id + "/";
        if (method.equals("post")){
            RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //取消点赞失败
                    handler.failedHandle(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //取消点赞成功
                    handler.successHandle(responseString);
                }
            });
        }else {
            RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //取消点赞失败
                    handler.failedHandle(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //取消点赞成功
                    handler.successHandle(responseString);
                }
            });
        }
    }

    //取消对某个人的收藏
    //id  动态的id
    //type team 团队的动态  user 用户的动态
    //method post收藏  delete取消收藏
    public static void toFavorActionOrNot(Context context, String id, String type, String method, final GetObjectListHandler handler){
        RequestParams params = new RequestParams();
        //liked/user_actions/(?P<action_id>[0-9]+)/
        String url = "users/current/favored/" + type + "_actions/" + id + "/";
        if (method.equals("post")){
            RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //取消点赞失败
                    handler.failedHandle(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //取消点赞成功
                    handler.successHandle(responseString);
                }
            });
        }else {
            RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //取消点赞失败
                    handler.failedHandle(responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //取消点赞成功
                    handler.successHandle(responseString);
                }
            });
        }
    }

    //组合字符串
    public static String join(String join , ArrayList<String> list){
        if (list.size() == 0)
            return "";
        else if(list.size() == 1){
            return list.get(0);
        }else{
            String str = list.get(0);
            for ( int i = 1; i< list.size(); i++){
                str = str + join + list.get(i);
            }
            return  str;
        }
    }

    //将字符串分割为ArrayList

    public static ArrayList<String> split(String str){

        String[] arr = str.split("-");
        ArrayList<String> list =  new ArrayList<String>(Arrays.asList(arr));
        return list;
    }

    public static ArrayList<String> split(String str, String join){

        String[] arr = str.split(join);
        ArrayList<String> list =  new ArrayList<String>(Arrays.asList(arr));
        return list;
    }

    //同意某个请求,包括加入团队，邀请，好友请求等
    public static void acceptSomeRequest(Context context, String url, final NetResultHandler handler){
        RequestParams params = new RequestParams();
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //同意请求失败
                handler.failedHandle(responseString, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //同意请求成功
                handler.successHandle(responseString, statusCode);
            }
        });
    }

    //同意好友请求
    public static void acceptSomeRequest2(Context context, String url,String applicationid, final NetResultHandler handler){
        RequestParams params = new RequestParams();
        params.put("application_id",applicationid);
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //同意请求失败
                handler.failedHandle(responseString, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //同意请求成功
                handler.successHandle(responseString, statusCode);
            }
        });
    }

    //拒绝某个请求,包括加入团队，邀请，好友请求等
    public static void declineSomeRequest(Context context, String url, final NetResultHandler handler){
        RequestParams params = new RequestParams();
        RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //同意请求失败
                handler.failedHandle(responseString, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //同意请求成功
                handler.successHandle(responseString, statusCode);
            }
        });
    }

    //拒绝某个好友请求等
    public static void declineSomeRequest2(Context context, String url,String application_id, final NetResultHandler handler){
        RequestParams params = new RequestParams();
        params.put("application_id",application_id);
        RestClient.deleteWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //同意请求失败
                handler.failedHandle(responseString, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //同意请求成功
                handler.successHandle(responseString, statusCode);
            }
        });
    }

    //举报
    //type 举报的类型
    //content 举报的内容
    public static void report(Context context, String type, String content, String objectId,  final NetResultHandler handler){
        RequestParams params = new RequestParams();
        params.put("content", content);
        params.put("type", type);
        params.put("object_id", objectId);

        String url = "users/current/report/";
        RestClient.postWithToken(context, url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //提交举报失败
                handler.failedHandle(responseString, statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //提交举报成功
                handler.successHandle(responseString, statusCode);
            }
        });
    }


    public static String[] getTaskStatus(){
        return  new String[]{"等待接受", "再派任务", "等待完成", "等待验收", "再次提交", "按时结束", "超时结束", "终止"};
    }


    //    status: 任务状态 - ('等待接受', 0), ('再派任务', 1),
    //            ('等待完成', 2), ('等待验收', 3),
    //            ('再次提交', 4), ('等待支付', 6),
    //            ('再次支付', 7), ('等待确认', 8),
    //            ('按时结束', 9),('超时结束', 10)

    public static String[] getExternalTaskStatus(){
        return  new String[]{"等待接受", "再派任务", "等待完成", "等待验收", "再次提交", "", "等待支付", "再次支付", "等待确认", "按时结束", "超时结束"};
    }


    public static String getExternalRate(int status){
        if (status == 0||status == 1) {
            return "0%";
        }else if(status == 2 || status == 4){
            return "20%";
        }else if(status == 3 ){
            return "40%";
        }else if(status == 6 || status == 7){
            return "60%";
        }else if(status == 8) {
            return "80%";
        }else if(status == 10 || status == 9){
            return "100%";
        }else{
            return "已终止";
        }
    }


    public static String getRate(int status){
        if (status == 0||status == 1) {
            return "0%";
        }else if(status == 2 || status == 4){
            return "33%";
        }else if(status == 3 ){
            return "66%";
        }else if(status == 5 || status == 6){
            return "100%";
        }else{
            return "已终止";
        }
    }

    //身份证号码验证：start
    /**
     * 功能：身份证的有效验证
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    public static String IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围。";
            return errorInfo;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

//        if (IDStr.length() == 18) {
//            if (Ai.equals(IDStr) == false) {
//                errorInfo = "身份证无效，不是合法的身份证号码";
//                return errorInfo;
//            }
//        } else {
//            return "";
//        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：判断字符串是否为数字
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：设置地区编码
     * @return Hashtable 对象
     */
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**验证日期字符串是否是YYYY-MM-DD格式
     * @param str
     * @return
     */
    public static boolean isDataFormat(String str){
        boolean flag=false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1=Pattern.compile(regxStr);
        Matcher isNo=pattern1.matcher(str);
        if(isNo.matches()){
            flag=true;
        }
        return flag;
    }



    public static String combineTheWord(String subject, String verb, String object){
        Map<String, String> map = new HashMap<String, String>();

        map.put("join", "加入");
        map.put("create", "创建");
        map.put("leave", "离开");
        map.put("send", "发布");

        if (map.containsKey(verb))
            return subject + " " + "<font color='#FF0000'><small>"+map.get(verb) +"</small></font>"+ " " + object;
        else
            return subject + " <font color='#FF0000'><small>***</small></font> " + object;
    }


    //将团队动态的英文转化为中文
    public static String combineTeamWord(String subject, String verb, String object){
//        Map<String, String> map = new HashMap<String, String>();
//
//        map.put("join", "加入");
//        map.put("create", "创建");
//        map.put("leave", "离开");
//        map.put("send", "发布");
//
//        if (map.containsKey(verb))
//            return subject + map.get(verb) + object;
//        else
//            return subject + "***" + object;

        if (verb.equals("join")){
            return subject + " " + "<font color='#FF0000'><small>"+"同意了"+"</small></font>"+ " " + object + " " + "<font color='#FF0000'><small>"+"的加入申请"+"</small></font>";
        }else if(verb.equals("create_team")){
            return object  + " " + "<font color='#FF0000'><small>"+ "创建了"+"</small></font>"+ " " + subject  + "<font color='#FF0000'><small>"+ " "+"团队"+"</small></font>";
        }else if(verb.equals("leave")){
            return subject+ " " + "<font color='#FF0000'><small>" + "踢出了成员" +"</small></font>"+" "+ object;
        }else if(verb.equals("send")){
            return subject+ " " + "<font color='#FF0000'><small>"  + "发布了"+"</small></font>"+" " + object;
        }else{
            return subject + " " + "<font color='#FF0000'><small>"  + "***"+"</small></font>"+" " + object;
        }
    }


    public static int daysBetween(Date startDate, Date endDate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startDate = sdf.parse(sdf.format(startDate));
        endDate = sdf.parse(sdf.format(endDate));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        long time1 = calendar.getTimeInMillis();


        calendar.setTime(endDate);
        long time2 = calendar.getTimeInMillis();


        long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(betweenDays));


    }


    public static int stringDaysBetween(String smdate, String bdate)
    {
        SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(smdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time1 = cal.getTimeInMillis();
        try {
            cal.setTime(sdf.parse(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);


        return Integer.parseInt(String.valueOf(between_days));
    }



}
