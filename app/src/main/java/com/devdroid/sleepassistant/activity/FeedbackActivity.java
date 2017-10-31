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

public class FeedbackActivity extends BaseActivity {
    private EditText mContainer; //正文
    private ImageView mImageView; //下拉框箭头
    private LinearLayout mMenuCommon; //下拉框
    private TextView mProblem; //下拉框文本
    private TextView mForceInstall; //下拉框文本
    private TextView mSuggestion; //下拉框文本
    private TextView mSelect; //下拉框文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }
    private void init(){
        mContainer = (EditText)findViewById(R.id.container_setting_feedback);
        mSelect = (TextView)findViewById(R.id.setting_feedback_menu_select);
        TextView mNotice = (TextView) findViewById(R.id.notice_setting_feedback);
        mImageView = (ImageView)findViewById(R.id.menu_imageview);
        mMenuCommon = (LinearLayout)findViewById(R.id.setting_feedback_menu);
        mMenuCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect.setText(getResources().getString(R.string.activity_setting_feedback_common));
                mImageView.setDrawingCacheEnabled(true);
                Bitmap bMap = Bitmap.createBitmap(mImageView.getDrawingCache());
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                int newWidth = bMap.getWidth();
                int newHeight = bMap.getHeight();
                Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, newWidth, newHeight, matrix, true);
                mImageView.setImageBitmap(bMapRotate);
                mImageView.setDrawingCacheEnabled(false);
                showPopupWindow(v);
            }
        });
        mContainer.setHint(R.string.container_hint_setting_feedback);
        mContainer.setFocusable(true);
        mContainer.setFocusableInTouchMode(true);
        mContainer.requestFocus();
        mNotice.setText(R.string.notice_setting_feedback);
    }

    /**
     * 获得popwindow
     *
     * */
    private void showPopupWindow(View view) {
        View contentView = getLayoutInflater().inflate(R.layout.activity_setting_feedback_menu, null);
        mSuggestion = (TextView)contentView.findViewById(R.id.setting_feedback_suggestion);
        mProblem = (TextView)contentView.findViewById(R.id.setting_feedback_problem);
        mForceInstall = (TextView)contentView.findViewById(R.id.setting_feedback_forceinstall);
        final PopupWindow popupWindow = new PopupWindow(contentView, mMenuCommon.getWidth()-2, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mImageView.setImageDrawable(ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.arrow_07));
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(FeedbackActivity.this, R.color.white));
        popupWindow.showAsDropDown(view,1,-5);
        mSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect.setText(mSuggestion.getText().toString());
                mImageView.setImageDrawable(ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.arrow_07));
                popupWindow.dismiss();
            }
        });
        mProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect.setText(mProblem.getText().toString());
                mImageView.setImageDrawable(ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.arrow_07));
                popupWindow.dismiss();
            }
        });
        mForceInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelect.setText(mForceInstall.getText().toString());
                mImageView.setImageDrawable(ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.arrow_07));
                popupWindow.dismiss();
            }
        });
    }

    private void sendFeedBack(final String detail, final String title) {
        RequestParams params = new RequestParams();
        params.put("content", title+"-"+detail);
        RestClient.postWithToken(FeedbackActivity.this, "user/feedback", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new AlertDialog.Builder(FeedbackActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(FeedbackActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")){
                    Toast.makeText(FeedbackActivity.this, "我们已收到您的反馈，并将尽快处理，谢谢！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
//                    new AlertDialog.Builder(FeedbackActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(FeedbackActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });

//        if (!NetworkUtil.isNetworkOK(getApplicationContext())) {
//            showToast(R.string.checknet_setting_feedback);
//            return;
//        }
//        String mDevinfo = DevicesUtils.getFeedbackDeviceInfo(FeedbackActivity.this, title);
//        String notice = this.getString(R.string.feedback_content);
//        String text = detail + "\n\n" + notice + "\n" + mDevinfo;
//        String titleContent = "Feedback, " + title;
//        String tos = "p1p1us@bupt.edu.cn";
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.setType("message/rfc822");
//        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, titleContent);
//        Uri uri = Uri.parse("mailto:" + tos);
//        emailIntent.setAction(Intent.ACTION_SENDTO);
//        emailIntent.setData(uri);
//        try {
//            startActivity(emailIntent);
//        } catch (Exception ex) {
//            Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.activity_setting_feedback_no_email), Toast.LENGTH_SHORT).show();
//        }
//        mContainer.setText(null);
//        finish();
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
                String selectItem = mSelect.getText().toString();
                if (detailString.equals("")) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.no_contain_setting_feedback), Toast.LENGTH_SHORT).show();
                    return true;
                }
                sendFeedBack(detailString, selectItem);
                break;
        }
        return true;
    }
}
