package com.devdroid.sleepassistant.base;

/**
 * Created by litingdong on 16/5/24.
 */
public interface NetRequestHandler {
    public void successHandle(CYUser user);
    public void failedHandle(String errorMessage);
}
