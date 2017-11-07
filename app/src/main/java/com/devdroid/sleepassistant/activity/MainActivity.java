package com.devdroid.sleepassistant.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.adapter.CalendarViewAdapter;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUserManager;
import com.devdroid.sleepassistant.base.CustomConfirmDialog;
import com.devdroid.sleepassistant.base.ImageLoaderTool;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.bluetooth.AnyScanActivity;
import com.devdroid.sleepassistant.listener.NavigationItemSelectedListener;
import com.devdroid.sleepassistant.listviewitems.ChartItem;
import com.devdroid.sleepassistant.listviewitems.LineChartItem;
import com.devdroid.sleepassistant.listviewitems.PieChartItem;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.view.CalendarCard;
import com.devdroid.sleepassistant.view.CircleRelativeLayout;
import com.devdroid.sleepassistant.view.chart.GeneralSplineChartView;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.squareup.haha.perflib.Main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements CalendarCard.OnCellClickListener{
    private ViewPager mViewPager;//日历


    boolean doubleBackToExitPressedOnce = false;


    protected String[] mSleepStates = new String[] {
            "清醒", "浅睡", "中睡", "深睡"
    };

    private CombinedChart mChart;

    private ViewPager viewPager;//左右滑动控件
    private ImageView leftImg;
    private ImageView rightImg;
    private ImageView shareImg;
    private ImageView calendarImg;

    private LinearLayout llSleepEfficiency;
    private LinearLayout llBodyMove;
    private LinearLayout llSleepDuration;
    private LinearLayout theCalendar;

    private TextView theDate;
    private ArrayList<View> pageview;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;
    //导航栏的头像、昵称、邮箱、添加好友
    private ImageView mHeadImage;
    private ImageView mAddImage;
    private TextView nickName;
    private TextView userName;

    private TextView mText1;//开始睡觉 睡觉中
    private TextView mText2;//起床
    private TextView noReport;//当日无报告

    private TextView sleepScore;//日报告睡眠得分
    private TextView scoreJudge;//日报告得分评价
    private TextView sleepHourNum;//日报告睡眠时长小时数
    private TextView sleepMinNum;//日报告睡眠时长分钟数
    private TextView sleepEfficiency;//日报告睡眠效率
    private TextView bodyMove;//日报告体动频率
    private TextView sleepDuration;//日报告入睡时长

    private RelativeLayout dayReport;//当日报告
    private CircleRelativeLayout mCircle;
    private CircleRelativeLayout mCircleGetup;
    private CircleRelativeLayout mCircle2;
    private CalendarViewAdapter<CalendarCard> adapter;
    private int mCurrentIndex = 1000;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private TextView mTvDateLable;
    private GeneralSplineChartView mGSCWeek;
    private DrawerLayout mDrawerLayout;

    private LineChart mChart1;
    private LineChart mChart2;

    public ImageLoader imageLoader = ImageLoader.getInstance();

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        initView();
        Intent intent = getIntent();
        String intype = intent.getStringExtra("type");
        if (intype.equals("1")) {
//            UseBluetooth(MainActivity.this,"是否使用蓝牙接收实时数据？","确定","取消");
            final CustomConfirmDialog confirmDialog = new CustomConfirmDialog(MainActivity.this, "是否使用蓝牙接收实时数据？","确定","取消");
            confirmDialog.show();
            confirmDialog.setClicklistener(new CustomConfirmDialog.ClickListenerInterface() {
                @Override
                public void doConfirm() {
                    // TODO Auto-generated method stub
                    Intent intent1 = new Intent(MainActivity.this, AnyScanActivity.class);
                    startActivity(intent1);
                }

                @Override
                public void doCancel() {
                    // TODO Auto-generated method stub
                    confirmDialog.dismiss();
                }
            });
//            new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题
//
//                    .setMessage("是否使用蓝牙接收实时数据？")//设置显示的内容
//
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
//
//
//                        @Override
//
//                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//                            // TODO Auto-generated method stub
//
//                            Intent intent = new Intent(MainActivity.this, AnyScanActivity.class);
//                            startActivity(intent);
//
//                        }
//
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮
//
//
//                @Override
//
//                public void onClick(DialogInterface dialog, int which) {//响应事件
//
//                    // TODO Auto-generated method stub
//
////                Log.i("alertdialog"," 请保存数据！");
//
//                }
//
//            }).show();//在按键响应事件中显示此对话框
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
//        Intent intent = getIntent();
//        if(intent != null){
//            String action = intent.getStringExtra("action");
//            if("create_sleep_time_new".equals(action)){
//                createSleepTimeNew();
//                mViewPager.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 500);
//            }
//        }
    }

    private void createSleepTimeNew() {
        SleepDataMode date = new SleepDataMode();
        Calendar calendar = Calendar.getInstance();
        date.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        date.setMinute(calendar.get(Calendar.MINUTE));
        List<SleepDataMode> insertList = new LinkedList<>();
        if(date.getHour() < 6){
            date.setHour(24 + date.getHour());
        }
        insertList.add(date);
        LauncherModel.getInstance().getSnssdkTextDao().insertSleepDataItem(insertList);
        LauncherModel.getInstance().getSharedPreferencesManager().commitBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, true);
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        adapter.getItem(mCurrentIndex % adapter.getAllItems().length).update(false);
//    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        mHeadImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.head_image);
        mAddImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.add_image);
        nickName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nickname);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener(this, mDrawerLayout);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        leftImg = (ImageView) findViewById(R.id.left_img);
        rightImg = (ImageView) findViewById(R.id.right_img);
        shareImg = (ImageView) findViewById(R.id.share_img);
        calendarImg = (ImageView) findViewById(R.id.calendar_img);
        theDate = (TextView) findViewById(R.id.the_date);
        mViewPager = (ViewPager) findViewById(R.id.vp_calendar);
        theCalendar = (LinearLayout) findViewById(R.id.the_calendar);
        mTvDateLable = (TextView) findViewById(R.id.calendar_date_lable);
        checkIsSleep();//检查当前是否在睡觉
        //获取用户信息
        RequestParams params1 = new RequestParams();

        userName.setText(CYUserManager.getInstance(MainActivity.this).getStringInfo("username"));

        System.out.println("manager.gettoken:"+CYUserManager.getInstance(MainActivity.this).getToken());

        RestClient.getWithToken(MainActivity.this, "user/info", params1,new JsonHttpResponseHandler() {

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
                        nickName.setText(CYTools.getRealParam(jsonObject.get("nickname").toString()));
//                        System.out.println("imageurl:"+RestClient.getAbsoluteUrl(CYTools.getRealParam(jsonObject.get("icon").toString()).substring(1)));
                        ImageLoaderTool.loadCircleImage(RestClient.getAbsoluteUrl(CYTools.getRealParam(jsonObject.get("icon").toString()).substring(1)),mHeadImage);
                    } else {
//                        new android.app.AlertDialog.Builder(MainActivity.this)
//                                .setTitle("提示")
//                                .setMessage("登录已失效，请注销后重新登录")
//                                .setPositiveButton("确定", null)
//                                .show();
                        OnlyAlert(MainActivity.this,"登录已失效，请注销后重新登录","确定");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                System.out.println(responseString);
//                new android.app.AlertDialog.Builder(MainActivity.this)
//                        .setTitle("提示")
//                        .setMessage("未知网络错误"+responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(MainActivity.this,"未知网络错误","确定");
            }
        });



        shareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("睡眠监测")
//                        .setMessage("开始睡眠监测后，将手机正面朝下放置在枕头旁（不要将手机放在枕头或被子下以影响手机散热）；建议同时为手机充电，以防手机没电影响睡眠监测。")
//                        .setPositiveButton("我知道了", null)
//                        .show();
                OnlyAlert(MainActivity.this,"开始睡眠监测后，将手机正面朝下放置在枕头旁（不要将手机放在枕头或被子下以影响手机散热）；建议同时为手机充电，以防手机没电影响睡眠监测。","确定");
            }
        });

        calendarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theCalendar.setVisibility(View.VISIBLE);
            }
        });

        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(MainActivity.this, MainActivity.this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();

        // TODO Auto-generated method stub

        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.activity_home_before, null);
        View view2 = inflater.inflate(R.layout.activity_home_today, null);

        mCircle = (CircleRelativeLayout) view2.findViewById(R.id.circle_view);
        mCircleGetup = (CircleRelativeLayout) view2.findViewById(R.id.circle_view_getup);
        mText1 = (TextView) view2.findViewById(R.id.text1);
        mText2 = (TextView) view2.findViewById(R.id.text2);

        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(1);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());




        mCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mText1.setText("正在睡觉");
//                mCircleGetup.setVisibility(View.VISIBLE);
                if (CYUserManager.getInstance(MainActivity.this).getEQUIP_ID().length()>0) {
                    startSleep();
                }else{
//                    new AlertDialog.Builder(MainActivity.this)
//                            .setTitle("未检测到设备")
//                            .setMessage("请先在设置中选择连接设备方式")
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MainActivity.this,"请先在设置中选择连接设备方式","确定");
                }
            }
        });

        mCircleGetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mText1.setText("开始睡觉");
                mCircleGetup.setVisibility(View.GONE);
                stopSleep();
            }
        });



        mHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("提示")
//                        .setMessage("点击了头像")
//                        .setPositiveButton("确定", null)
//                        .show();
                Intent intent = new Intent(MainActivity.this, UserCenterActivity.class);
                startActivity(intent);
            }
        });

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("提示")
//                        .setMessage("点击了头像")
//                        .setPositiveButton("确定", null)
//                        .show();
                Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });
//        System.out.println(mHeadImage.getWidth());

        boolean isClick = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, false);
        if(!isClick){
//            addGuideImage(R.id.drawer_layout, R.drawable.main_guide);
        }
    }

    public void checkIsSleep() {
        if (CYUserManager.getInstance(MainActivity.this).getEQUIP_ID().length() > 0) {
            //获取设备是否睡眠
            RequestParams params2 = new RequestParams();
            RestClient.getWithToken(MainActivity.this, "equip/" + CYUserManager.getInstance(MainActivity.this).getEQUIP_ID() + "/sleep", params2, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (response.getString("code").equals("0")) {
                            System.out.println("shebeizaixian:" + response.getString("data"));
                            JsonObject jsonObject = CYTools.stringToJsonObject(response.getString("data"));
//                        System.out.println("denglushidetoken1:" + jsonObject.get("token").toString());
                            if (CYTools.getRealParam(jsonObject.get("isSleep").toString()).equals("true")) {
                                mText1.setText("正在睡觉");
                                mCircleGetup.setVisibility(View.VISIBLE);
                            } else {
                                mText1.setText("开始睡觉");
                                mCircleGetup.setVisibility(View.GONE);
                            }
                        } else {
//                            new android.app.AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("提示")
//                                    .setMessage(CYTools.getRealParam(response.getString("desc")))
//                                    .setPositiveButton("确定", null)
//                                    .show();
                            System.out.println("checkIsSleep:"+CYTools.getRealParam(response.getString("desc")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    System.out.println(responseString);
//                    new android.app.AlertDialog.Builder(MainActivity.this)
//                            .setTitle("提示")
//                            .setMessage("未知网络错误" + responseString)
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MainActivity.this,"未知网络错误","确定");
                }
            });
        }
    }

    public void startSleep() {
            //获取设备是否睡眠
            RequestParams params = new RequestParams();
            RestClient.postWithToken(MainActivity.this, "equip/" + CYUserManager.getInstance(MainActivity.this).getEQUIP_ID() + "/sleep", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (response.getString("code").equals("0")) {
                            System.out.println("开始睡觉");
                            Intent intent = new Intent(MainActivity.this, WaveActivity.class);
                            startActivity(intent);
                        } else {
                            if (response.getString("desc").trim().equals("设备正处于睡眠状态，无法开始新的睡眠")){//更改为设备正在睡眠中的语句
                                Intent intent = new Intent(MainActivity.this, WaveActivity.class);
                                startActivity(intent);
                            }else {
                                System.out.println(response.getString("desc").trim());
//                                new android.app.AlertDialog.Builder(MainActivity.this)
//                                        .setTitle("提示")
//                                        .setMessage(CYTools.getRealParam(response.getString("desc")))
//                                        .setPositiveButton("确定", null)
//                                        .show();
                                OnlyAlert(MainActivity.this,CYTools.getRealParam(response.getString("desc")),"确定");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    System.out.println(responseString);
//                    new android.app.AlertDialog.Builder(MainActivity.this)
//                            .setTitle("提示")
//                            .setMessage("未知网络错误" + responseString)
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MainActivity.this,"未知网络错误" + responseString,"确定");
                }
            });
    }

    public void stopSleep() {
        if (CYUserManager.getInstance(MainActivity.this).getEQUIP_ID().length() > 0) {
            //获取设备是否睡眠
            RequestParams params = new RequestParams();
            RestClient.deleteWithToken(MainActivity.this, "equip/" + CYUserManager.getInstance(MainActivity.this).getEQUIP_ID() + "/sleep", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (response.getString("code").equals("0")) {
                            System.out.println("停止睡觉" + response.getString("data"));
                        } else {
//                            new android.app.AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("提示")
//                                    .setMessage(CYTools.getRealParam(response.getString("desc")))
//                                    .setPositiveButton("确定", null)
//                                    .show();
                            OnlyAlert(MainActivity.this,CYTools.getRealParam(response.getString("desc")),"确定");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    System.out.println(responseString);
//                    new android.app.AlertDialog.Builder(MainActivity.this)
//                            .setTitle("提示")
//                            .setMessage("未知网络错误" + responseString)
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MainActivity.this,"未知网络错误" + responseString,"确定");
                }
            });
        }
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/

                    noReport = (TextView) findViewById(R.id.no_release1);
                    sleepScore = (TextView) findViewById(R.id.sleep_score);
                    scoreJudge = (TextView) findViewById(R.id.score_judge);
                    sleepHourNum = (TextView) findViewById(R.id.hour_num);
                    sleepMinNum = (TextView) findViewById(R.id.min_num);
                    sleepEfficiency = (TextView) findViewById(R.id.sleep_efficiency);
                    bodyMove = (TextView) findViewById(R.id.body_move);
                    sleepDuration = (TextView) findViewById(R.id.sleep_duration);

                    dayReport = (RelativeLayout) findViewById(R.id.rl_dayreport);
                    mCircle2 = (CircleRelativeLayout) findViewById(R.id.circle_view);
                    llBodyMove = (LinearLayout) findViewById(R.id.ll_body_move);
                    llSleepDuration = (LinearLayout) findViewById(R.id.ll_sleep_duration);
                    llSleepEfficiency = (LinearLayout) findViewById(R.id.ll_sleep_efficiency);
                    leftImg.setVisibility(View.INVISIBLE);
                    rightImg.setVisibility(View.VISIBLE);
//                    calendarImg.setVisibility(View.VISIBLE);
                    shareImg.setImageResource(R.drawable.share_2);

                    calendarImg.setVisibility(View.VISIBLE);
                    theDate.setText("2017/9/15");
//                    mGSCWeek = (GeneralSplineChartView) findViewById(R.id.gsc_activity_main_week);

//                    initGengralSplineChart();


                    shareImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareText(createShareFile());
                        }
                    });

                    ListView lv = (ListView) findViewById(R.id.listView_chart);
                    mChart1 = (LineChart) findViewById(R.id.chart1);
                    mChart2 = (LineChart) findViewById(R.id.chart2);
                    setupmChart1();
                    setupmChart2();





                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();

//                    // 3 items
//                    for (int i = 0; i < 3; i++) {
//
//                        if(i % 3 == 0) {
//                            list.add(new LineChartItem(generateDataLine1(i + 1), getApplicationContext()));
//                        } else if(i % 3 == 1) {
//                            list.add(new PieChartItem(generateDataPie(i + 1), getApplicationContext()));
//                        } else if(i % 3 == 2) {
//                            list.add(new LineChartItem(generateDataLine2(i + 1), getApplicationContext()));
//                        }
//                    }
                    list.add(new PieChartItem(generateDataPie(2), getApplicationContext()));

                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                    setListViewHeightBasedOnChildren(lv);

                    llBodyMove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("体动")
//                                    .setMessage("睡眠时体动情况是评价睡眠质量的重要因素。深度睡眠时人体肌肉松弛，肢体不会产生较大的运动，甚至不会动；而浅睡眠时人体会产生一定的轻微运动。大幅度的动作往往发生在浅睡眠和清醒过程中。体动可用来监测睡眠的安稳程度以及睡眠中出现的肌肉痉挛等症状。 睡眠时频繁的体动伴随觉醒事件的发生，体现人体的焦躁不安，会严重影响睡眠。")
//                                    .setPositiveButton("我知道了", null)
//                                    .show();
                            OnlyAlert(MainActivity.this,"睡眠时体动情况是评价睡眠质量的重要因素。深度睡眠时人体肌肉松弛，肢体不会产生较大的运动，甚至不会动；而浅睡眠时人体会产生一定的轻微运动。大幅度的动作往往发生在浅睡眠和清醒过程中。体动可用来监测睡眠的安稳程度以及睡眠中出现的肌肉痉挛等症状。 睡眠时频繁的体动伴随觉醒事件的发生，体现人体的焦躁不安，会严重影响睡眠。","我知道了");
                        }
                    });
                    llSleepDuration.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("入睡时长")
//                                    .setMessage("平均入睡时长较正常范围低入睡时长指人们开始睡眠到实际进入睡眠状态的时间长度。一般来说入睡时长在20分钟内为正常。因为各种原因引起入睡困难、长时间无法进入睡眠状态表现为失眠的一种症状。")
//                                    .setPositiveButton("我知道了", null)
//                                    .show();
                            OnlyAlert(MainActivity.this,"平均入睡时长较正常范围低入睡时长指人们开始睡眠到实际进入睡眠状态的时间长度。一般来说入睡时长在20分钟内为正常。因为各种原因引起入睡困难、长时间无法进入睡眠状态表现为失眠的一种症状。","我知道了");
                        }
                    });
                    llSleepEfficiency.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("睡眠效率")
//                                    .setMessage("睡眠效率是指除清醒外的睡眠时间占躺床上睡觉总时间的百分比。睡眠效率越高，表明睡眠时间的利用率越高，睡眠质量越好。 睡眠效率低于80%，则表示睡眠质量不高。建议尝试在两周内，每天都在规定的时间起床，每晚感到疲倦就上床休息（不要不困时逼迫自己休息，也不要在感到困意后还继续熬夜）。几周后你的身体会根据早上起床时间来调节你夜晚的疲劳度，以确保你得到充足的睡眠，从而提高你的睡眠效率。")
//                                    .setPositiveButton("我知道了", null)
//                                    .show();
                            OnlyAlert(MainActivity.this,"睡眠效率是指除清醒外的睡眠时间占躺床上睡觉总时间的百分比。睡眠效率越高，表明睡眠时间的利用率越高，睡眠质量越好。 睡眠效率低于80%，则表示睡眠质量不高。建议尝试在两周内，每天都在规定的时间起床，每晚感到疲倦就上床休息（不要不困时逼迫自己休息，也不要在感到困意后还继续熬夜）。几周后你的身体会根据早上起床时间来调节你夜晚的疲劳度，以确保你得到充足的睡眠，从而提高你的睡眠效率。","我知道了");
                        }
                    });
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    theCalendar.setVisibility(View.GONE);
                    leftImg.setVisibility(View.VISIBLE);
                    rightImg.setVisibility(View.INVISIBLE);
                    calendarImg.setVisibility(View.INVISIBLE);
                    shareImg.setImageResource(R.drawable.question_2);
                    shareImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("睡眠监测")
//                                    .setMessage("开始睡眠监测后，将手机正面朝下放置在枕头旁（不要将手机放在枕头或被子下以影响手机散热）；建议同时为手机充电，以防手机没电影响睡眠监测。")
//                                    .setPositiveButton("我知道了", null)
//                                    .show();
                            OnlyAlert(MainActivity.this,"开始睡眠监测后，将手机正面朝下放置在枕头旁（不要将手机放在枕头或被子下以影响手机散热）；建议同时为手机充电，以防手机没电影响睡眠监测。","我知道了");
                        }
                    });
                    calendarImg.setVisibility(View.INVISIBLE);
                    theDate.setText("今天");
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
//            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void setupmChart1(){
        // no description text
        mChart1.getDescription().setEnabled(false);

        // enable touch gestures
        mChart1.setTouchEnabled(true);

        mChart1.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(true);
        mChart1.setDrawGridBackground(false);
        mChart1.setHighlightPerDragEnabled(true);

        // set an alternative background color
//                    mChart1.setBackgroundColor(Color.WHITE);
//        mChart1.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        setData1(240, 4);
        mChart1.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart1.getLegend();
//        l.setEnabled(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = mChart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(6f); // 6 min
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.MINUTES.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = mChart1.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(3f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.WHITE);
//                    leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mSleepStates[(int) value % mSleepStates.length];
            }
        });

        YAxis rightAxis = mChart1.getAxisRight();
        rightAxis.setEnabled(false);
    }
    private void setupmChart2(){
        // no description text
        mChart2.getDescription().setEnabled(false);

        // enable touch gestures
        mChart2.setTouchEnabled(true);

        mChart2.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.setDrawGridBackground(false);
        mChart2.setHighlightPerDragEnabled(true);

        // set an alternative background color
//                    mChart1.setBackgroundColor(Color.WHITE);
//        mChart2.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        setData2(240, 4);
        mChart2.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart2.getLegend();
//        l.setEnabled(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xAxis = mChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(6f); // 6 min
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.MINUTES.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = mChart2.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = mChart2.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setData1(int count, float range) {

        // now in hours
        long now = 419580;//某天的17:00
//        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        for (float x = from; x < to; x++) {

//            float y = getRandom(range, 1);
            float y = new java.util.Random().nextInt(4);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "睡眠统计");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart1.setData(data);
    }

    private void setData2(int count, float range) {

        // now in hours
        long now = 419580;//某天的17:00
//        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        for (float x = from; x < to; x++) {

//            float y = getRandom(range, 1);
            float y = (float)(Math.random() * 65);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "体动");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart2.setData(data);
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    //为listview指定高度，为了避免scrollview中的listview只显示一行
    public void setListViewHeightBasedOnChildren(ListView listView) {
// 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
// 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
// listView.getDividerHeight()获取子项间分隔符占用的高度
// params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine1(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 50; i++) {
            e1.add(new Entry(i, new java.util.Random().nextInt(4)+1));
        }

        LineDataSet d1 = new LineDataSet(e1, "睡眠统计");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        d1.setDrawCircles(false);

//        ArrayList<Entry> e2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < 12; i++) {
//            e2.add(new Entry(i, e1.get(i).getY() - 30));
//        }
//
//        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
//        d2.setLineWidth(2.5f);
//        d2.setCircleRadius(4.5f);
//        d2.setHighLightColor(Color.rgb(244, 117, 117));
//        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
//        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
//        sets.add(d2);

        LineData cd = new LineData(sets);
        return cd;
    }

    private LineData generateDataLine2(int cnt) {
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 65)));
        }

        LineDataSet d1 = new LineDataSet(e1, "体动");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        d1.setDrawCircles(false);
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(sets);
        return cd;
    }


    /**
     * 分享应用
     */
    public void shareText(String imgPath) {
        Intent shareIntent = new Intent();
        File f = new File(imgPath);
        if (f != null && f.exists() && f.isFile()) {
            shareIntent.setType("image/jpg");
            Uri u = Uri.fromFile(f);
            shareIntent.putExtra(Intent.EXTRA_STREAM, u);
        }
        String shareAppText = "分享一款睡眠监测应用";
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TITLE,shareAppText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppText);
//        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 创建分享的图片文件
     */
    public String createShareFile() {
        Bitmap bitmap = createBitmap();
        //将生成的Bitmap插入到手机的图片库当中，获取到图片路径
        String filePath = MediaStore.Images.Media.insertImage(this.getContentResolver(),     bitmap, null, null);
        //及时回收Bitmap对象，防止OOM
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        //转uri之前必须判空，防止保存图片失败
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return getRealPathFromURI(this, Uri.parse(filePath));
    }

    /**
     * 创建分享Bitmap
     */
    private Bitmap createBitmap() {
        //自定义ViewGroup，一定要手动调用测量，布局的方法
//        measure(getLayoutParams().width, getLayoutParams().height);
//        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        //如果图片对透明度无要求，可以设置为RGB_565
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        draw(canvas);
        return bitmap;
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor == null) {
                return "";
            }
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private PieData generateDataPie(int cnt) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "深睡"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "中睡"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "浅睡"));
        entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "清醒"));


        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(d);
        return cd;
    }

//    // 测试按钮
//    private class WaveOnClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
////            startActivity(new Intent(MainActivity.this, WaveActivity.class));
//            Intent intent = new Intent(MainActivity.this, WaveActivity.class);
//            startActivity(intent);
////            break;
//        }
//    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            new android.app.AlertDialog.Builder(MainActivity.this)
//                    .setTitle("提示")
//                    .setMessage("确定退出程序？")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
//
//
//                        @Override
//
//                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//
//                            // TODO Auto-generated method stub
//                            System.exit(0);
//                        }
//
//                    })
//                    .setNegativeButton("取消",null)
//                    .show();
//            super.onBackPressed();

//
//            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//            alertDialog.show();
//            Window window = alertDialog.getWindow();
//            window.setContentView(R.layout.dialog_main_info);
//            TextView tv_title = (TextView) window.findViewById(R.id.tv_dialog_title);
//            tv_title.setText("提示");
//            TextView tv_message = (TextView) window.findViewById(R.id.tv_dialog_message);
//            tv_message.setText("确定退出程序？");
//            TextView tv_button1 = (TextView) window.findViewById(R.id.button1);
//            tv_button1.setText("确定");
//            tv_button1.setOnClickListener(new View.OnClickListener() {
//                                              @Override
//                                              public void onClick(View v) {
//                                                  //TODO Auto-generated method stub
//                                                  System.exit(0);
//                                              }
//            });
//            TextView tv_button2 = (TextView) window.findViewById(R.id.button2);
//            tv_button2.setText("取消");
//            tv_button2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //TODO Auto-generated method stub
//                    alertDialog.dismiss();
//                }
//            });
            Exit(this);
        }
    }

    public static void Exit(final Context context) {
        final CustomConfirmDialog confirmDialog = new CustomConfirmDialog(context, "确定要退出吗?", "退出", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new CustomConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                // TODO Auto-generated method stub
                System.exit(0);
            }

            @Override
            public void doCancel() {
                // TODO Auto-generated method stub
                confirmDialog.dismiss();
            }
        });
    }
    public static void UseBluetooth(final Context context,String message,String confirm ,String cancel) {
        final CustomConfirmDialog confirmDialog = new CustomConfirmDialog(context, message, confirm, cancel);
        confirmDialog.show();
        confirmDialog.setClicklistener(new CustomConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                // TODO Auto-generated method stub
//                Intent intent1 = new Intent(context, AnyScanActivity.class);
//                startActivity(intent1);
            }

            @Override
            public void doCancel() {
                // TODO Auto-generated method stub
                confirmDialog.dismiss();
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


    /**
     * 计算方向
     */
    private void measureDirection(int arg0) {
        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;
        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        CalendarCard[] mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    // 日历点击
    @Override
    public void clickDate(SleepDataMode date) {
//        if(date.getHour() > 24) {
//            Toast.makeText(this, "睡眠时间为：第二天凌晨" + (date.getHour() - 24) + ":" + date.getMinute(), Toast.LENGTH_SHORT).show();
//        } else if(date.getHour() > 0){
//            Toast.makeText(this, "睡眠时间为：" + date.getHour() + ":" + date.getMinute(), Toast.LENGTH_SHORT).show();
//        } else {
        Toast.makeText(this, "点击日期：" + date.getYear() + "-" + date.getMonth() + "-" + date.getDay(), Toast.LENGTH_SHORT).show();
        theCalendar.setVisibility(View.GONE);
        theDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
        String month = date.getMonth()<10?"0"+date.getMonth():""+date.getMonth();//将一位数的月份转换成两位数
        String day = date.getDay()<10?"0"+date.getDay():""+date.getDay();//将一位数的日期转换成两位数
        showDayData(date.getYear() + month + day);

//        }
    }

    public void showDayData(String str) {
        if (CYUserManager.getInstance(MainActivity.this).getEQUIP_ID().length() > 0) {
            //获取设备是否睡眠
            RequestParams params = new RequestParams();
            RestClient.getWithToken(MainActivity.this, "equip/" + CYUserManager.getInstance(MainActivity.this).getEQUIP_ID() + "/day/"+str+"/analyze", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    super.onSuccess(statusCode, headers, responseString);


                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        if (response.getString("code").equals("0")) {
                            System.out.println("日报告" + response.getString("data"));
                            JsonObject jsonObject = CYTools.stringToJsonObject(response.getString("data"));
                            noReport.setVisibility(View.GONE);
                            dayReport.setVisibility(View.VISIBLE);
                            sleepScore.setText(CYTools.getRealParam(jsonObject.get("score").toString()));
                            sleepDuration.setText(CYTools.getRealParam(jsonObject.get("fall").toString()));
                            bodyMove.setText(CYTools.getRealParam(jsonObject.get("action").toString()));
//                            sleepEfficiency.setText(CYTools.getRealParam(jsonObject.get("score").toString()));
                            judgeScore(CYTools.getRealParam(jsonObject.get("score").toString()));
                            sleepHourNum.setText(""+Integer.valueOf(CYTools.getRealParam(jsonObject.get("asleep").toString())).intValue()/60);
                            sleepMinNum.setText(""+Integer.valueOf(CYTools.getRealParam(jsonObject.get("asleep").toString())).intValue()%60);
                        } else {
//                            new android.app.AlertDialog.Builder(MainActivity.this)
//                                    .setTitle("提示")
//                                    .setMessage(CYTools.getRealParam(response.getString("desc")))
//                                    .setPositiveButton("确定", null)
//                                    .show();
                            noReport.setVisibility(View.VISIBLE);
                            dayReport.setVisibility(View.GONE);
                            System.out.println("获取日报告:"+CYTools.getRealParam(response.getString("desc")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    System.out.println(responseString);
//                    new android.app.AlertDialog.Builder(MainActivity.this)
//                            .setTitle("提示")
//                            .setMessage("未知网络错误" + responseString)
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MainActivity.this,"未知网络错误","确定");
                }
            });
        }
    }

    public void judgeScore(String score) {
        int intScore = Integer.valueOf(score).intValue();
        if (intScore<50){
            scoreJudge.setText("糟糕");
            scoreJudge.setTextColor(Color.RED);
        }else if (intScore < 60){
            scoreJudge.setText("中等");
            scoreJudge.setTextColor(Color.BLACK);
        }else if (intScore < 70){
            scoreJudge.setText("一般");
            scoreJudge.setTextColor(Color.BLACK);
        }else if (intScore < 80){
            scoreJudge.setText("合格");
            scoreJudge.setTextColor(Color.BLACK);
        }
        else if (intScore < 90){
            scoreJudge.setText("不错");
            scoreJudge.setTextColor(Color.YELLOW);
        }else{
        scoreJudge.setText("很棒");
        scoreJudge.setTextColor(Color.GREEN);
        }
    }
    /**
     * 长按日期
     */
    @Override
    public void longClickDate(final SleepDataMode date) {
//        Calendar calendar = Calendar.getInstance();
//        new TimePickerDialog(MainActivity.this,
//                new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        List<SleepDataMode> insertList = new LinkedList<>();
//                        SleepDataMode sleepDataMode = new SleepDataMode(date.getYear(), date.getMonth(), date.getDay(), hourOfDay, minute);
//                        if(hourOfDay < 6){  //认为,时间设置为 24 + 实际时间。如：凌晨1点，保存为：25
////                            sleepDataMode = DateUtil.getPreviousDate(sleepDataMode);
//                            sleepDataMode.setHour(24 + sleepDataMode.getHour());
//                        }
//                        insertList.add(sleepDataMode);
//                        LauncherModel.getInstance().getSnssdkTextDao().insertSleepDataItem(insertList);
//                        adapter.getItem(mCurrentIndex % adapter.getAllItems().length).update(false);
//                        LauncherModel.getInstance().getSharedPreferencesManager().commitBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, true);
//                        mViewPager.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                initGengralSplineChart();
//                            }
//                        }, 500);
//                    }
//                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
    }

    @Override
    public void changeDate(SleepDataMode date) {
        mTvDateLable.setText(date.getYear() + "年" + date.getMonth() + "月");
    }


    private void initGengralSplineChart(){
        SleepDataMode currentData = new SleepDataMode();
        LinkedList<SleepDataMode> sleepDataModes = new LinkedList<>();
        for(int i = 0; i < 7; i ++ ) {
            currentData = DateUtil.getPreviousDate(currentData);
            currentData.setHour(-1);
            currentData.setMinute(-1);
            SleepDataMode sleepDataMode = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(currentData.getYear(), currentData.getMonth(), currentData.getDay());
            if(sleepDataMode != null) {
                sleepDataMode.setWeek(currentData.getWeek());
                currentData = sleepDataMode;
            }
            sleepDataModes.add(0, currentData);
        }
        mGSCWeek.chartDataSet(sleepDataModes, true);
    }
//    //点击返回两次退出程序
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
//    }
}