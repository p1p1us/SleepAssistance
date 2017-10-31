package com.devdroid.sleepassistant.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Gravity;
import android.widget.Toast;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.AppUtils;
import java.util.Date;
import java.util.List;

public class ApplockManager {

    private static ApplockManager mInstance;
    private Context mContext;
    private static List<String> componentNames;
    private static int mStartTime;
    private static int mEndTime;
    private static int sTimeSpace = 1000;
    private String mUseLauncherPackageName = null;
    private final Runnable mCheckFrontApp = new Runnable() {
        @Override
        public void run() {
            ComponentName mTopComponentName = AppUtils.getTopComponentName(mContext);
            String currentPackageName = mTopComponentName.getPackageName();
            List<String> mLauncherList = AppUtils.getLauncherPackageNames(mContext);
            if(mLauncherList.contains(currentPackageName)){
                mUseLauncherPackageName = currentPackageName;
            }
            if (isApplyTime()) {
                if (componentNames != null && componentNames.contains(currentPackageName)) {
                    choiceCancel(currentPackageName);
                    Toast toast = Toast.makeText(TheApplication.getAppContext(), "此应用现在不可用，请换其他应用", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    };

    public static ApplockManager getInstance(){
        return mInstance;
    }
    public void checkApplock(){
        mStartTime = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.APP_LOCK_RESTRICTION_SRART_TIME, 0);
        mEndTime = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.APP_LOCK_RESTRICTION_END_TIME, 0);
        componentNames = LauncherModel.getInstance().getLockerDao().queryLockerInfo();
        scheduleCheck();
    }

    private ApplockManager(Context context) {
        mContext = context;
    }

    private void scheduleCheck() {
        HandlerThread mAsyncHandlerThread = new HandlerThread("monitor-thread");
        mAsyncHandlerThread.start();
        Handler mAsyncHandler = new Handler(mAsyncHandlerThread.getLooper());
        mAsyncHandler.removeCallbacks(mCheckFrontApp);
        mAsyncHandler.postDelayed(mCheckFrontApp, sTimeSpace);
    }
    /**
     * 初始化单例,在程序启动时调用<br>
     */
    public static void initSingleton(Context context) {
        mInstance = new ApplockManager(context);
    }
    /**
     * 选择退出
     */
    private void choiceCancel(String pkgName) {
        AppUtils.gotoLauncherWithoutChoice(mContext, mUseLauncherPackageName);
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (!pkgName.equals(mContext.getPackageName())) {
            activityManager.killBackgroundProcesses(pkgName);
        }
    }

    private boolean isApplyTime() {
        Date date = new Date();
        int currentMinue = date.getMinutes();
        int currentTime = Integer.parseInt(date.getHours() + "" +  (currentMinue > 9 ? currentMinue:("0" + currentMinue)));
        if ((mStartTime < mEndTime && currentTime >= mStartTime && currentTime < mEndTime) ||
            (mStartTime > mEndTime && currentTime >= mStartTime || currentTime < mEndTime)) {
            sTimeSpace = 300;
            return true;
        } else if ((mStartTime > currentTime && mStartTime - currentTime <= 1) || (mStartTime == 0 && currentMinue >= 2359)) {
            sTimeSpace = 300;
        } else {
            sTimeSpace = 1000 * 60;
        }
        return false;
    }
}
