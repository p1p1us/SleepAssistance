package com.devdroid.sleepassistant.base;


/**
 * Created by litingdong on 16/6/12.
 */
public interface NetResultHandler {

    public void successHandle(String responseString, int statusCode);
    public void failedHandle(String responseString, int statusCode);
}
