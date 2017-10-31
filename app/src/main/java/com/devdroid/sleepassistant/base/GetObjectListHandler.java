package com.devdroid.sleepassistant.base;

/**
 * Created by litingdong on 16/5/26.
 */
public interface GetObjectListHandler {
    public void successHandle(String responseString);
    public void failedHandle(String errorMessage);
}
