package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.devdroid.sleepassistant.R;

import java.util.ArrayList;
import java.util.List;

public class HelpQuestionDetailActivity extends AppCompatActivity {
    private TextView mQuestionTitle;
    private TextView mQuestionDetail;
    private List<String> mTitles = new ArrayList<String>(){{
        add("\t\t我设置的手机闹钟为什么没有响?");
        add("\t\t手机监测睡眠功能是怎么使用的?");
        add("\t\t睡眠监测功能一定要手动点击开始和结束睡觉吗?");
        add("\t\tAPP的助眠功能怎么使用?");
        add("\t\tAPP的智能闹钟怎么使用?");
        add("\t\t手机睡眠监测功能的原理?");
        add("\t\t我昨晚使用手机监测睡眠但是没有生成报告?");
        add("\t\t手机没有点击“起床”，睡眠监测功能会自动结束吗?");
    }};
    private List<String> mDetails = new ArrayList<String>(){{
        add("\t\t\t\t以下几种情况，可能会导致智能闹钟无法响铃：\nIOS平台：\n\t\t1.没有手动点击开始睡觉\n\t\t2.\"睡眠监测\" App没有在后台运行\n\t\t3.配合\"睡眠监测\"的产品使用，关闭了手机蓝牙\n\t\t4.在系统“设置>通知>\"睡眠监测\"”关闭了允许通知\n\t\t5.将闹钟设置在“设置>勿扰模式”中的设定时间之内\n\t\t6.使用耳机时无法外放响铃\n\t\t7.闹钟铃声被调至最小\nAndroid平台：\n\t\t1.没有手动点击开始睡觉\n\t\t2.\"睡眠监测\" App没有在后台运行\n\t\t3.配合\"睡眠监测\"的产品使用，关闭了手机蓝牙\n\t\t4.\"睡眠监测\" App被手机系统列入黑名单\n\t\t5.\"睡眠监测\" App的后台被安全类软件误清理\n\t\t6.\"睡眠监测\" APP安装在手机SD卡，手机重启后软件无法自启\n\t\t7.使用耳机时无法外放响铃\n\t\t8.闹钟铃声被调至最小");
        add("\t\t\t\t打开APP，点击“开始睡觉”按钮后，将手机正面朝下放置在枕头旁（不要将手机放在枕头或被子下以影响手机散热）；建议同时为手机充电,以防手机没电影响睡眠监测。第二天点击“起床”即可完成一晚的睡眠监测，并生成睡眠报告。");
        add("\t\t\t\t\"睡眠监测\" APP手机监测功能必须手动点击开始和结束，且保持整晚手机电量充足。若你拥有\"睡眠监测\"的其他睡眠产品（如RestOn睡眠监测器），你将享受全自动的睡眠监测服务。");
        add("\t\t\t\t将“我的>设备管理>智能模式”的助眠设备设置为手机后点击“开始睡觉”，手机将播放助眠音乐。若设置了智能结束助眠模式，配合手机睡眠监测功能或\"睡眠监测\" 的其他睡眠产品（如RestOn睡眠监测器，且不可关闭手机蓝牙），助眠音乐将会在你睡着后自动结束播放。若设置了定时结束，助眠音乐则在是设定的时间结束。");
        add("\t\t\t\t将“我的>设备管理>智能模式”的助眠设备设置为手机后点击“开始睡觉”，手机将会在设定的唤醒时间范围内的浅睡眠期轻轻唤醒你。为了保证闹钟的正常运作，请保持手机电量充足。若配合\"睡眠监测\" 的其他睡眠产品（如RestOn睡眠监测器），请不要退出手机APP、关闭手机蓝牙且保持手机电量充足。若未配合手机睡眠监测功能，APP的闹钟将会定时响起。");
        add("\t\t\t\t人在睡眠过程中，会经历从浅睡眠到深睡眠的不同阶段。\"睡眠监测\"睡眠模式利用手机传感器监测你在睡眠时产生的体动，并通过算法分析你的睡眠状态。\n\t\t\t\t若设置了智能助眠，助眠模式将会在你睡着后自动结束。\n\t\t\t\t若设置了智能闹钟，在设定的时间范围内，\"睡眠监测\"将在你睡眠最浅的时候唤醒你，此刻醒来可让身体感到轻松舒适，精力充沛。");
        add("\t\t\t\t开始睡眠监测后，请不要将APP关闭并保持整晚电量充足。另外部分安卓系统手机会在后台强制关闭APP，请将APP列入手机系统或安全软件的白名单内。");
        add("\t\t\t\t会。手机监测睡眠的最长时长为16小时，超过后将自动结束。为了不影响睡眠分析的结果，建议手动结束睡眠监测。\n\t\t\t\t若是配合Nox智能床头灯，关闭Nox的闹钟将会结束手机的监测功能。\"睡眠监测\" 享睡其它产品（如RestOn）可以在你起床一小时后自动结束监测睡眠。");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_question_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mQuestionTitle = (TextView)findViewById(R.id.question_title);
        mQuestionDetail = (TextView)findViewById(R.id.question_detail);
        mQuestionTitle.setText(intent.getStringExtra("number"));
        System.out.println("number:"+intent.getStringExtra("number"));
        mQuestionTitle.setText(mTitles.get(Integer.valueOf(intent.getStringExtra("number")).intValue()));
        mQuestionDetail.setText(mDetails.get(Integer.valueOf(intent.getStringExtra("number")).intValue()));
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
