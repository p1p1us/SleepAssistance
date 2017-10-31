package com.devdroid.sleepassistant.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.Base64Coder;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.BitmapBase64Transfer;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.GetObjectListHandler;
import com.devdroid.sleepassistant.base.ImageLoaderTool;
import com.devdroid.sleepassistant.base.MarshMallowPermission;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.base.TagGroup;
import com.devdroid.sleepassistant.base.TagsManager;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;
import com.devdroid.sleepassistant.view.SelectSexPopup;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.addapp.pickers.picker.NumberPicker;
import cz.msebera.android.httpclient.Header;


public class UserCenterActivity extends BaseActivity {
    private ImageView mHeadImg;
    private TextView mName;
    private TextView mSex;
    private TextView mAge;
    private TextView mHeight;
    private TextView mWeight;
    private LinearLayout mAlllayout;


    private Button btGallery, btCamera, btCancel, btSave;
    private Dialog dialogHead;
    private boolean isRequireCheck; // 是否需要系统权限检测
    private PermissionsChecker mPermissionsChecker;//检查权限
    //危险权限（运行时权限）
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    public static final int PERMISSION_REQUEST_CODE1 = 5;
    public String imageName = "headImage.jpg";
    private final static int CODE_TAG = 4;
    private final static int CODE_AREA = 8;
    private TagGroup mTagGroup;
    private TagsManager mTagsManager;
    private ArrayList<String> arr = new ArrayList<String>();
    File temp = null;
    private String tp = null;



    private Bitmap head;// 头像Bitmap
    private static String path = "/sdcard/myHead/";// sd路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        mPermissionsChecker = new PermissionsChecker(this);
        mHeadImg = (ImageView) findViewById(R.id.head);
        mName = (TextView) findViewById(R.id.txt_name);
        mSex = (TextView) findViewById(R.id.txt_sex);
        mAge = (TextView) findViewById(R.id.txt_age);
        mHeight = (TextView) findViewById(R.id.txt_height);
        mWeight = (TextView) findViewById(R.id.txt_weight);
        btSave = (Button) findViewById(R.id.bt_save);
        mAlllayout = (LinearLayout) findViewById(R.id.all_layout);
        initView();
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(UserCenterActivity.this,"点击了保存",Toast.LENGTH_LONG).show();
                save_to_net();
            }
        });
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void save_to_net() {
        RequestParams params = new RequestParams();
        params.put("nickname", mName.getText().toString());
        params.put("age", mAge.getText().toString());
        params.put("sex", mSex.getText().toString().equals("男")?"true":"false");
        params.put("height", mHeight.getText().toString());
        params.put("weight", mWeight.getText().toString());
//        params.put("validation_code", validationCodeText.getText().toString());
//        params.put("invitation_code", invitationCode.getText().toString());
        RestClient.postWithToken(UserCenterActivity.this, "user/info", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new android.app.AlertDialog.Builder(UserCenterActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(UserCenterActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("register response:" + responseString);
//                Toast.makeText(UserCenterActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")){
                    Toast.makeText(UserCenterActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {

//                    new android.app.AlertDialog.Builder(UserCenterActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(UserCenterActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }
    public static void OnlyAlert(final Context context,String message,String confirm) {
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

    public void initView(){
        //获取用户信息
        RequestParams params1 = new RequestParams();

//        mName.setText(CYUserManager.getInstance(UserCenterActivity.this).getStringInfo("nickname"));

//        System.out.println("manager.gettoken:"+CYUserManager.getInstance(MainActivity.this).getToken());

        RestClient.getWithToken(UserCenterActivity.this, "user/info", params1,new JsonHttpResponseHandler() {

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
//                        System.out.println("denglushidetoken1:" + jsonObject.get("token").toString());
                        mName.setText(CYTools.getRealParam(jsonObject.get("nickname").toString()));
                        mSex.setText(CYTools.getRealParam(jsonObject.get("sex").toString()).equals("false")?"女":"男");
                        System.out.println("性别："+CYTools.getRealParam(jsonObject.get("sex").toString()));
                        mAge.setText(CYTools.getRealParam(jsonObject.get("age").toString()).equals("null")?"20":CYTools.getRealParam(jsonObject.get("age").toString()));
                        mHeight.setText(CYTools.getRealParam(jsonObject.get("height").toString()).equals("null")?"170":CYTools.getRealParam(jsonObject.get("height").toString()));
                        mWeight.setText(CYTools.getRealParam(jsonObject.get("weight").toString()).equals("null")?"70":CYTools.getRealParam(jsonObject.get("weight").toString()));
//                        System.out.println("imageurl:"+RestClient.getAbsoluteUrl(CYTools.getRealParam(jsonObject.get("icon").toString()).substring(1)));
                        ImageLoaderTool.loadCircleImage(RestClient.getAbsoluteUrl(CYTools.getRealParam(jsonObject.get("icon").toString()).substring(1)),mHeadImg);
                    } else {
//                        new android.app.AlertDialog.Builder(UserCenterActivity.this)
//                                .setTitle("提示")
//                                .setMessage("网络错误")
//                                .setPositiveButton("确定", null)
//                                .show();
                        OnlyAlert(UserCenterActivity.this,"网络错误","确定");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
//                new android.app.AlertDialog.Builder(UserCenterActivity.this)
//                        .setTitle("提示")
//                        .setMessage("未知网络错误"+responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(UserCenterActivity.this,"未知网络错误"+responseString,"确定");
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_head:
                if (isRequireCheck) {
                    //权限没有授权，进入授权界面
                    if(mPermissionsChecker.judgePermissions(PERMISSIONS)){
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE1);
                        //Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                        //startActivityForResult(intent, REQUEST_CODE);
                    }else {
                        chooseHeadDialog();
                    }
                }else {
                    isRequireCheck = true;
                }
                break;
            case R.id.rl_name:
//                final EditText inputServer = new EditText(UserCenterActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(UserCenterActivity.this);
//                builder.setTitle("请输入昵称").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                        .setNegativeButton("取消", null);
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (inputServer.getText().toString().length()>0){
////                            Toast.makeText(UserCenterActivity.this,inputServer.getText().toString(),Toast.LENGTH_LONG).show();
//                            mName.setText(inputServer.getText().toString());
//                        }else {
//                            Toast.makeText(UserCenterActivity.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                builder.show();
                inputNickname();
                break;
            case R.id.rl_sex:
                change_sex();
                break;
            case R.id.rl_age:
                NumberPicker picker1 = new NumberPicker(this);
                picker1.setAnimationStyle(R.style.Animation_CustomPopup);
                picker1.setCanLoop(false);
                picker1.setOffset(2);//偏移量
                picker1.setRange(0, 100, 1);//数字范围
                picker1.setSelectedItem(30);
                picker1.setLabel("岁");
                picker1.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
//                        String sss = "index=" + index + ", item=" + item.intValue();
//                        Toast.makeText(UserCenterActivity.this, sss ,Toast.LENGTH_LONG).show();
                        mAge.setText(""+item.intValue());
                    }
                });
                picker1.show();
                break;
            case R.id.rl_height:
                NumberPicker picker2 = new NumberPicker(this);
                picker2.setAnimationStyle(R.style.Animation_CustomPopup);
                picker2.setCanLoop(false);
                picker2.setOffset(2);//偏移量
                picker2.setRange(30, 250, 1);//数字范围
                picker2.setSelectedItem(170);
                picker2.setLabel("cm");
                picker2.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
//                        String sss = "index=" + index + ", item=" + item.intValue();
//                        Toast.makeText(UserCenterActivity.this, sss ,Toast.LENGTH_LONG).show();
                        mHeight.setText(item.intValue()+"");
                    }
                });
                picker2.show();
                break;
            case R.id.rl_weight:
                NumberPicker picker3 = new NumberPicker(this);
                picker3.setAnimationStyle(R.style.Animation_CustomPopup);
                picker3.setCanLoop(false);
                picker3.setOffset(2);//偏移量
                picker3.setRange(10, 200, 1);//数字范围
                picker3.setSelectedItem(60);
                picker3.setLabel("kg");
                picker3.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
//                        String sss = "index=" + index + ", item=" + item.intValue();
//                        Toast.makeText(UserCenterActivity.this, sss ,Toast.LENGTH_LONG).show();
                        mWeight.setText(item.intValue()+"");
                    }
                });
                picker3.show();
                break;
        }
    }

    private void inputNickname() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.activity_input_dialog2, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.activity_input_dialog2);
        TextView btnPositive = (TextView) dialog.findViewById(R.id.btn_save_pop);
//        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.text_input);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("昵称不能为空");
                } else {
                    dialog.dismiss();
                    mName.setText(etContent.getText().toString());
//                    Toast.makeText(MyEquipsActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

    /**
     * 检查权限的工具类
     * Created by holen on 2016/5/13.
     */
    public class PermissionsChecker {
        private Context mContext;

        public PermissionsChecker(Context context){
            mContext = context.getApplicationContext();
        }

        /**
         * 判断权限
         */
        public boolean judgePermissions(String...permissions){
            for(String permission:permissions){
                if(deniedPermission(permission)){
                    return true;
                }
            }
            return false;
        }

        /**
         * 判断是否缺少权限
         * PackageManager.PERMISSION_GRANTED 授予权限
         * PackageManager.PERMISSION_DENIED 缺少权限
         *
         */
        private boolean deniedPermission(String permission){
            return    ContextCompat.checkSelfPermission(mContext,permission)==                                                    PackageManager.PERMISSION_DENIED;
        }
    }

    /*头像选择窗口*/
    public void chooseHeadDialog() {

        View view = this.getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
        dialogHead = new Dialog(this, R.style.transparentFrameWindowStyle);

        dialogHead.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialogHead.getWindow();

        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.8f;
        wl.x = 0;
        wl.y = this.getWindowManager().getDefaultDisplay().getHeight();

        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        dialogHead.onWindowAttributesChanged(wl);
        dialogHead.setCanceledOnTouchOutside(true);
        dialogHead.show();

        btCamera = (Button) dialogHead.findViewById(R.id.camera);
        btGallery = (Button) dialogHead.findViewById(R.id.gallery);
        btCancel = (Button) dialogHead.findViewById(R.id.cancel_selector);

        btCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogHead.dismiss();
                /**
                 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
                 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
                 * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
                 * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
                 */
                MarshMallowPermission marshMallowPermission = new MarshMallowPermission(UserCenterActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!marshMallowPermission.checkPermissionForCamera()) {
                        marshMallowPermission.requestPermissionForCamera();
                    } else {
                        if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                            marshMallowPermission.requestPermissionForExternalStorage();
                        } else {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            File mediaStorageDir = new File(
//                                    Environment.getExternalStorageDirectory()+ "");
//
//
//                            try {
//                                File mediaFile = File.createTempFile(
//                                        "xiaoma",  /* prefix */
//                                        ".jpg",         /* suffix */
//                                        mediaStorageDir      /* directory */
//                                );
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                    .fromFile(new File(Environment
                                            .getExternalStorageDirectory(),
                                            imageName)));
                            startActivityForResult(takePictureIntent, 2);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }

                } else {
                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    imageName)));
                    startActivityForResult(intent, 2);
                }


            }
        });
        btGallery.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogHead.dismiss();
                /**
                 * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
                 * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
                 */

                MarshMallowPermission marshMallowPermission = new MarshMallowPermission(UserCenterActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                        marshMallowPermission.requestPermissionForExternalStorage();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);

                        /**
                         * 下面这句话，与其它方式写是一样的效果，如果：
                         * intent.DataReceiver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                         * intent.setType(""image/*");设置数据类型
                         * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                         * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
                         */
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, 1);
                    }
                }else {

                    Intent intent = new Intent(Intent.ACTION_PICK, null);

                    /**
                     * 下面这句话，与其它方式写是一样的效果，如果：
                     * intent.DataReceiver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                     * intent.setType(""image/*");设置数据类型
                     * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                     * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
                     */
                    intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dialogHead.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_TAG && resultCode == Activity.RESULT_OK) {
            String[] tags = mTagsManager.getTags();
            mTagGroup.setTags(tags);
            arr = new ArrayList<>();
            for (int i = 0; i < tags.length; i++)
            {
                arr.add(tags[i]);
                System.out.println("tags :" + tags[i]);
            }
            Log.i("qwe", "qwe");
        } else if (requestCode == CODE_AREA && resultCode == Activity.RESULT_OK && null != data) {
//            etArea.setText(data.getStringExtra("province")+"-"+data.getStringExtra("city")+"-"+data.getStringExtra("district"));
        }else {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                    break;
                // 如果是调用相机拍照时
                case 2:
                    if (resultCode == Activity.RESULT_OK) {
                        temp = new File(Environment.getExternalStorageDirectory()
                                + "/" + imageName);
                        if (temp != null){
                            System.out.println("not null");
                        }else{
                            System.out.println("null");
                        }

                        startPhotoZoom(Uri.fromFile(temp));
                    }
                    break;
                // 取得裁剪后的图片
                case 3:
                    /**
                     * 非空判断大家一定要验证，如果不验证的话，
                     * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                     * 当前功能时，会报NullException，小马只
                     * 在这个地方加下，大家可以根据不同情况在合适的
                     * 地方做判断处理类似情况
                     *
                     */
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        try {
                            setPicToView(data);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;

            }
        }
    }

    public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
		 * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
		 * 制做的了...吼吼
		 */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("circleCrop", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */

    private void setPicToView(Intent picdata) throws FileNotFoundException {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            // headImg.setBackgroundDrawable(drawable);
            mHeadImg.setImageDrawable(drawable);

            /**
             * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
             * 传到服务器，QQ头像上传采用的方法跟这个类似
             */

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] b = stream.toByteArray();
            // 将图片流以字符串形式存储下来

            tp = new String(Base64Coder.encodeLines(b));
            Bitmap dBitmap = BitmapFactory.decodeFile(tp);
            Bitmap bit = BitmapBase64Transfer.base64ToBitmap(tp);

            File f = new File(Environment.getExternalStorageDirectory(),imageName);
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //网络传输
            try {
                CYTools.setIcon(this, "user/info", f, new GetObjectListHandler() {
                    @Override
                    public void successHandle(String responseString) {
//                        new  android.app.AlertDialog.Builder(UserCenterActivity.this)
//                                .setTitle("提示")
//                                .setMessage("修改头像成功")
//                                .setPositiveButton("确定" ,  null)
//                                .show();
                        OnlyAlert(UserCenterActivity.this,"修改头像成功","确定");
                    }
                    @Override
                    public void failedHandle(String errorMessage) {
//                            new  android.app.AlertDialog.Builder(UserCenterActivity.this)
//                                    .setTitle("提示")
//                                    .setMessage("修改头像失败")
//                                    .setPositiveButton("确定" ,  null)
//                                    .show();
                        OnlyAlert(UserCenterActivity.this,"修改头像失败","确定");
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
			/*
			这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
			服务器处理的方法是服务器那边的事了，吼吼

			如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
			为我们可以用的图片类型就OK啦...吼吼
			Bitmap dBitmap = BitmapFactory.decodeFile(tp);
			Drawable drawable = new BitmapDrawable(dBitmap);
			*/
            //ib.setBackgroundDrawable(drawable);
            //iv.setBackgroundDrawable(drawable);

        }
    }

    public void change_sex2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(UserCenterActivity.this); //定义一个AlertDialog
        String[] strarr = {"男","女"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                // 自动生成的方法存根
                if (arg1 == 0) {//男
                    mSex.setText("男");
                }else {//女
                    mSex.setText("女");
                }
            }
        });
        builder.show();
    }
    public void change_sex() {
        SelectSexPopup fp = new SelectSexPopup(this);
        fp.showPopup(mAlllayout);
        fp.setOnSelectRemindWayPopupListener(new SelectSexPopup
                .SelectRemindWayPopupOnClickListener() {

            @Override
            public void obtainMessage(int flag) {
                switch (flag) {
                    //
                    case 0:
                        mSex.setText("男");
                        break;
                    //
                    case 1:
                        mSex.setText("女");
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isRequireCheck) {
            //权限没有授权，进入授权界面
            if (mPermissionsChecker.judgePermissions(PERMISSIONS)) {
                ActivityCompat.requestPermissions(UserCenterActivity.this, PERMISSIONS, PERMISSION_REQUEST_CODE1);
            }
        } else {
            isRequireCheck = true;
        }
    }

}
