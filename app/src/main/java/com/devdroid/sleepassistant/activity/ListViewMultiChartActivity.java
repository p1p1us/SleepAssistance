package com.devdroid.sleepassistant.activity;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.devdroid.sleepassistant.listviewitems.BarChartItem;
import com.devdroid.sleepassistant.listviewitems.ChartItem;
import com.devdroid.sleepassistant.listviewitems.LineChartItem;
import com.devdroid.sleepassistant.listviewitems.PieChartItem;
import com.devdroid.sleepassistant.base.DemoBase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 *
 * @author Philipp Jahoda
 */
public class ListViewMultiChartActivity extends DemoBase {

    public boolean tag_detail = false;
    public boolean is_month = false;
    private ListView listview;
    private ArrayList<String> theslist;
    public int clickPosition = -1;
    public Boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView lv = (ListView) findViewById(R.id.listView1);
        final TextView weekReport = (TextView) findViewById(R.id.hour_num);
//        final LinearLayout showscore = (LinearLayout) findViewById(R.id.the_score);
//        final LinearLayout showsleeptime = (LinearLayout) findViewById(R.id.the_third);

        final TextView monthReport = (TextView) findViewById(R.id.min_num);
        final TextView text1 = (TextView) findViewById(R.id.text1);
        final TextView text2 = (TextView) findViewById(R.id.text2);
        final TextView text3 = (TextView) findViewById(R.id.text3);
        final TextView the_time = (TextView) findViewById(R.id.the_time);
        final TextView text4 = (TextView) findViewById(R.id.text4);
        final TextView justAbout = (TextView) findViewById(R.id.about);
        final TextView justDetail = (TextView) findViewById(R.id.detail);
        final TextView avetimeorscore = (TextView) findViewById(R.id.avetimeorscore);
        ImageView suggestionsOne = (ImageView) findViewById(R.id.right_arrow2);
        ImageView suggestionsTwo = (ImageView) findViewById(R.id.right_arrow3);
        final LinearLayout toshoworhide1 = (LinearLayout) findViewById(R.id.the_forth);
        final LinearLayout toshoworhide2 = (LinearLayout) findViewById(R.id.toshoworhide2);

        justDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                justAbout.setTextColor(Color.GRAY);
                avetimeorscore.setText("平均分数");
                justDetail.setTextColor(Color.WHITE);
                toshoworhide1.setVisibility(View.GONE);
                toshoworhide2.setVisibility(View.VISIBLE);
                text1.setVisibility(View.GONE);
                text2.setVisibility(View.GONE);
                text3.setText("50");
                tag_detail = true;
                if (is_month){
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new LineChartItem(generateDataLine2(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }
                else {
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }
            }
        });
        justAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                justAbout.setTextColor(Color.WHITE);
                justDetail.setTextColor(Color.GRAY);
                avetimeorscore.setText("平均睡眠时长");
                toshoworhide1.setVisibility(View.VISIBLE);
                toshoworhide2.setVisibility(View.GONE);
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                text3.setText("50");
                tag_detail = false;
                if (is_month){
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new BarChartItem(generateDataBar2(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }else {
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new BarChartItem(generateDataBar(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }
            }
        });
        weekReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekReport.setTextColor(Color.WHITE);
                monthReport.setTextColor(Color.GRAY);

                the_time.setText("2017/9/11~9/17");

                is_month = false;
                if (tag_detail){
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }else {
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new BarChartItem(generateDataBar(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }

            }
        });
        monthReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekReport.setTextColor(Color.GRAY);
                monthReport.setTextColor(Color.WHITE);

                the_time.setText("2017/9/1~9/30");

                is_month = true;

                if (tag_detail){
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new LineChartItem(generateDataLine2(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }else {
                    ArrayList<ChartItem> list = new ArrayList<ChartItem>();
                    list.add(new BarChartItem(generateDataBar2(1), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
                    lv.setAdapter(cda);
                }

            }
        });

        suggestionsOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(ListViewMultiChartActivity.this)
//                        .setTitle("睡眠数据不足")
//                        .setMessage("你本周未进行连续的睡眠监测。长期连续的睡眠监测有助于更准确地分析你的睡眠健康情况，发现睡眠健康问题，改善睡眠质量。间断监测可能会造成睡眠分析的误差。建议你至少保证一周5天的睡眠监测。")
//                        .setPositiveButton("我知道了", null)
//                        .show();
                OnlyAlert(ListViewMultiChartActivity.this,"你本周未进行连续的睡眠监测。长期连续的睡眠监测有助于更准确地分析你的睡眠健康情况，发现睡眠健康问题，改善睡眠质量。间断监测可能会造成睡眠分析的误差。建议你至少保证一周5天的睡眠监测。","我知道了");
            }
        });
        suggestionsTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AlertDialog.Builder(ListViewMultiChartActivity.this)
//                        .setTitle("睡眠时间过短")
//                        .setMessage("睡眠不足会使人感到精力不济、反应迟钝、记忆力衰退、免疫力降低。甚至是机体的过早衰老。如果没有足够的睡眠时间，建议每天安排半小时午睡，以恢复体力。")
//                        .setPositiveButton("我知道了", null)
//                        .show();
                OnlyAlert(ListViewMultiChartActivity.this,"睡眠不足会使人感到精力不济、反应迟钝、记忆力衰退、免疫力降低。甚至是机体的过早衰老。如果没有足够的睡眠时间，建议每天安排半小时午睡，以恢复体力。","我知道了");

            }
        });

        listview = (ListView) findViewById(R.id.listview2);
        theslist = new ArrayList<>();
//        for (int i = 0; i < 9; i++) {
//            theslist.add("我是第" + i + "个条目");
//        }
        theslist.add("平均睡眠时长");
        theslist.add("平均深睡眠时长");
        theslist.add("上床时间");
        theslist.add("平均入睡时长");
        theslist.add("清醒时间");
        theslist.add("平均心率");
        theslist.add("平均呼吸率");
        theslist.add("清醒次数");
        theslist.add("离床次数");
        adapter = new MyAdapter2();
        listview.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listview);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items
//        for (int i = 0; i < 1; i++) {

//            if(i % 3 == 0) {
//                list.add(new LineChartItem(generateDataLine(i + 1), getApplicationContext()));
//            } else if(i % 3 == 1) {
//                list.add(new BarChartItem(generateDataBar(i + 1), getApplicationContext()));
//            } else if(i % 3 == 2) {
//                list.add(new PieChartItem(generateDataPie(i + 1), getApplicationContext()));
//            }
//        }
        list.add(new BarChartItem(generateDataBar(1), getApplicationContext()));
//        list.add(new LineChartItem(generateDataLine(2), getApplicationContext()));
//        list.add(new BarChartItem(generateDataBar2(3), getApplicationContext()));
//        list.add(new LineChartItem(generateDataLine2(4), getApplicationContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
//        setListViewHeightBasedOnChildren(lv);
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

    private MyAdapter2 adapter;

    class MyAdapter2 extends BaseAdapter implements View.OnClickListener {


        @Override
        public int getCount() {
            return theslist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final MyViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(ListViewMultiChartActivity.this, R.layout.listitem_reportdetail, null);
                vh = new MyViewHolder(convertView,theslist.get(position).toString());
                convertView.setTag(vh);
            } else {
                vh = (MyViewHolder) convertView.getTag();
            }
            switch (position){
                case 0:
                    vh.selectorImg.setImageResource(R.drawable.sleepinbed_new);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setText("5h50m");
                    break;
                case 1:
                    vh.selectorImg.setImageResource(R.drawable.sleepinbed_new);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setText("3h20m");
                    break;
                case 2:
                    vh.selectorImg.setImageResource(R.drawable.moon1);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    vh.selectorImg.setImageResource(R.drawable.sleepinbed_new);
                    vh.unit_type.setText("分");
                    vh.score.setText("18");
                    break;
                case 4:
                    vh.selectorImg.setImageResource(R.drawable.sun1);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setVisibility(View.INVISIBLE);
                    break;
                case 5:
                    vh.selectorImg.setImageResource(R.drawable.heartbeat_new);
                    vh.unit_type.setText("次/分");
                    vh.score.setText("88");
                    break;
                case 6:
                    vh.selectorImg.setImageResource(R.drawable.breath_new);
                    vh.unit_type.setText("次/分");
                    vh.score.setText("18");
                    break;
                case 7:
                    vh.selectorImg.setImageResource(R.drawable.sleepinbed_new);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setVisibility(View.INVISIBLE);
                    break;
                case 8:
                    vh.selectorImg.setImageResource(R.drawable.leave_bed);
                    vh.unit_type.setVisibility(View.INVISIBLE);
                    vh.score.setVisibility(View.INVISIBLE);
                break;
                default:
                    break;
            }
            vh.tv_test.setText(theslist.get(position));

            //刷新adapter的时候，getview重新执行，此时对在点击中标记的position做处理
            if (clickPosition == position) {//当条目为刚才点击的条目时
                if (vh.ll_toshoworhide.isSelected()) {//当条目状态图标为选中时，说明该条目处于展开状态，此时让它隐藏，并切换状态图标的状态。
                    vh.ll_toshoworhide.setSelected(false);
                    vh.ll_hide.setVisibility(View.GONE);
                    Log.e("listview", "if执行");
                    clickPosition = -1;//隐藏布局后需要把标记的position去除掉，否则，滑动listview让该条目划出屏幕范围，
                    // 当该条目重新进入屏幕后，会重新恢复原来的显示状态。经过打log可知每次else都执行一次 （条目第二次进入屏幕时会在getview中寻找他自己的状态，相当于重新执行一次getview）
                    //因为每次滑动的时候没标记得position填充会执行click
                } else {//当状态条目处于未选中时，说明条目处于未展开状态，此时让他展开。同时切换状态图标的状态。
                    vh.ll_toshoworhide.setSelected(true);
                    vh.ll_hide.setVisibility(View.VISIBLE);

                    Log.e("listview", "else执行");
                }
//                ObjectAnimator//
//                        .ofInt(vh.ll_hide, "rotationX", 0.0F, 360.0F)//
//                        .setDuration(500)//
//                        .start();
                // vh.ll_toshoworhide.setSelected(true);
            } else {//当填充的条目position不是刚才点击所标记的position时，让其隐藏，状态图标为false。

                //每次滑动的时候没标记得position填充会执行此处，把状态改变。所以如果在以上的if (vh.ll_toshoworhide.isSelected()) {}中不设置clickPosition=-1；则条目再次进入屏幕后，还是会进入clickposition==position的逻辑中
                //而之前的滑动（未标记条目的填充）时，执行此处逻辑，已经把状态图片的selected置为false。所以上面的else中的逻辑会在标记过的条目第二次进入屏幕时执行，如果之前的状态是显示，是没什么影响的，再显示一次而已，用户看不出来，但是如果是隐藏状态，就会被重新显示出来
                vh.ll_hide.setVisibility(View.GONE);
                vh.ll_toshoworhide.setSelected(false);

                Log.e("listview", "状态改变");
            }
//            vh.hide_1.setOnClickListener(this);
//            vh.hide_2.setOnClickListener(this);
//            vh.hide_3.setOnClickListener(this);
//            vh.hide_4.setOnClickListener(this);
//            vh.hide_5.setOnClickListener(this);
            vh.ll_toshoworhide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(ListViewMultiChartActivity.this, "被点了", Toast.LENGTH_SHORT).show();
                    clickPosition = position;//记录点击的position
                    notifyDataSetChanged();//刷新adapter重新填充条目。在重新填充的过程中，被记录的position会做展开或隐藏的动作，具体的判断看上面代码
                    //在此处需要明确的一点是，当adapter执行刷新操作时，整个getview方法会重新执行，也就是条目重新做一次初始化被填充数据。
                    //所以标记position，不会对条目产生影响，执行刷新后 ，条目重新填充当，填充至所标记的position时，我们对他处理，达到展开和隐藏的目的。
                    //明确这一点后，每次点击代码执行逻辑就是 onclick（）---》getview（）
                }
            });
            return convertView;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.hide_1:
//                    Toast.makeText(ListViewMultiChartActivity.this, "加密", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.hide_2:
//                    Toast.makeText(ListViewMultiChartActivity.this, "解密", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.hide_3:
//                    Toast.makeText(ListViewMultiChartActivity.this, "分享", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.hide_4:
//                    Toast.makeText(ListViewMultiChartActivity.this, "删除", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.hide_5:
//                    Toast.makeText(ListViewMultiChartActivity.this, "属性", Toast.LENGTH_SHORT).show();
//                    break;

            }
        }

        class MyViewHolder {
            View itemView;
            TextView tv_test;
            TextView unit_type;
            TextView score;
            ListView aChart;
//            TextView hide_1, hide_2, hide_3, hide_4, hide_5;
            ImageView selectorImg;
            LinearLayout ll_hide;
            LinearLayout ll_toshoworhide;

            public MyViewHolder(View itemView,String title) {
                this.itemView = itemView;
                tv_test = (TextView) itemView.findViewById(R.id.txt_type);
                unit_type = (TextView) itemView.findViewById(R.id.unit_type);
                score = (TextView) itemView.findViewById(R.id.score);
                selectorImg = (ImageView) itemView.findViewById(R.id.imageview);
                aChart = (ListView) itemView.findViewById(R.id.listview_achart);
//                hide_1 = (TextView) itemView.findViewById(R.id.hide_1);
//                hide_2 = (TextView) itemView.findViewById(R.id.hide_2);
//                hide_3 = (TextView) itemView.findViewById(R.id.hide_3);
//                hide_4 = (TextView) itemView.findViewById(R.id.hide_4);
//                hide_5 = (TextView) itemView.findViewById(R.id.hide_5);
                ll_hide = (LinearLayout) itemView.findViewById(R.id.ll_hide);
                ll_toshoworhide = (LinearLayout) itemView.findViewById(R.id.toshoworhide2);

                if (is_month) {
                    ArrayList<ChartItem> list1231 = new ArrayList<ChartItem>();
                    list1231.add(new LineChartItem(generateDataLine4(title), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list1231);
                    aChart.setAdapter(cda);
                }else {
                    ArrayList<ChartItem> list1231 = new ArrayList<ChartItem>();
                    list1231.add(new LineChartItem(generateDataLine3(title), getApplicationContext()));
                    ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list1231);
                    aChart.setAdapter(cda);
                }

            }
        }
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 1; i < 8; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 90) + 10));
        }

        LineDataSet d1 = new LineDataSet(e1, "睡眠得分");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        d1.setDrawCircles(false);
        d1.setValueTextColor(Color.WHITE);

//        ArrayList<Entry> e2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < 7; i++) {
//            e2.add(new Entry(i, (int) (Math.random() * 65) + 10));
//        }
//
//        LineDataSet d2 = new LineDataSet(e2, "睡觉时间");
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
        cd.setValueTextColor(Color.WHITE);
        return cd;
    }
    private LineData generateDataLine2(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 1; i < 31; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 90) + 10));
        }

        LineDataSet d1 = new LineDataSet(e1, "睡眠得分");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

//        ArrayList<Entry> e2 = new ArrayList<Entry>();
//
//        for (int i = 0; i < 7; i++) {
//            e2.add(new Entry(i, (int) (Math.random() * 65) + 10));
//        }
//
//        LineDataSet d2 = new LineDataSet(e2, "睡觉时间");
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

    private LineData generateDataLine3(String cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 1; i < 8; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 90) + 10));
        }

        LineDataSet d1 = new LineDataSet(e1, cnt);
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        LineData cd = new LineData(sets);
        return cd;
    }
    private LineData generateDataLine4(String cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 1; i < 31; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 90) + 10));
        }

        LineDataSet d1 = new LineDataSet(e1, cnt);
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        LineData cd = new LineData(sets);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 1; i < 8; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 5) + 7));
        }

        BarDataSet d = new BarDataSet(entries, "睡眠时长(小时)");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);
        d.setValueTextColor(Color.WHITE);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }
    private BarData generateDataBar2(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 1; i < 31; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 5) + 7));
        }

        BarDataSet d = new BarDataSet(entries, "睡眠时长(小时)");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setValueTextColor(Color.WHITE);
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

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(d);
        return cd;
    }
}
