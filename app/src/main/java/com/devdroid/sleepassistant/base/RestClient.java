package com.devdroid.sleepassistant.base;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by litingdong on 16/4/15.
 */

// 用于网络请求

public class RestClient {
    //测试服务器
//    private static final String BASE_URL = "http://123.206.24.226:8000/";
    //生产服务器
    private static String  BASE_URL = "http://www.chuangyh.com:13217/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        client.removeHeader("X-User-Token");
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void getWithToken(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        String token = CYUserManager.getInstance(context).getToken();
        //params.put("token",token);
        client.addHeader("X-User-Token",token);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        client.removeHeader("X-User-Token");
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void postWithToken(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        String token = CYUserManager.getInstance(context).getToken();
        //params.put("token", token);
        client.addHeader("X-User-Token",token);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
    public static void postWithToken2(Context context,String token,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        //String token = CYUserManager.getInstance(context).getToken();
        //params.put("token", token);
        client.addHeader("X-User-Token",token);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        client.removeHeader("X-User-Token");
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void deleteWithToken(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(1, 5);
        String token = CYUserManager.getInstance(context).getToken();
        //params.put("token",token);
        client.addHeader("X-User-Token",token);
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }
    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }
}
