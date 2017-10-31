package com.devdroid.sleepassistant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYEquip;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.GetObjectListHandler;
import com.devdroid.sleepassistant.base.OnlyAlertDialog;
import com.devdroid.sleepassistant.base.RestClient;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MyEquipsActivity extends BaseActivity {
    private ListView lvEquip;
    private List<CYEquip> equipsList = new ArrayList<CYEquip>();
    private EquipsListAdapter equipsListAdapter = new EquipsListAdapter();
//    private List<String> urlList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_equips);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutUI();
    }

    private void layoutUI() {

        lvEquip = (ListView)findViewById(R.id.equip_listview);
        lvEquip.setDividerHeight(0);
        /*
        adapter = new SimpleAdapter (this,
                getData(),
                R.layout.listitem_user,
                new String[]{"name"},
                new int[]{R.id.user_name});
        lvFriend.setAdapter(adapter);
        */
        lvEquip.setAdapter(equipsListAdapter);
//        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CYUser user = friendsList.get(position);
//                Intent intent = new Intent(FriendActivity.this, CYFriendActivity.class);
//                intent.putExtra("id", user.id);
//                startActivity(intent);
//            }
//        });
        getEquipsList();
    }

    public class EquipsListAdapter extends BaseAdapter {
        private class ViewHolder{
//            public ImageView headImg = null;
            public TextView equip_id = null;
            public TextView equip_name = null;
            //日期
//            public TextView  date = null;
//            public TextView  content = null;
        }
        @Override
        public int getCount() {
            return equipsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = MyEquipsActivity.this.getLayoutInflater().inflate(R.layout.listitem_equip, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.equip_id = (TextView) view.findViewById(R.id.equip_id);
                viewHolder.equip_name = (TextView) view.findViewById(R.id.equip_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CYEquip equip = (CYEquip) equipsList.get(position);
            viewHolder.equip_id.setText("设备号："+equip.getId());
            //ImageLoaderTool.loadCircleImage("http://images.cnblogs.com/cnblogs_com/kenshincui/613474/o_13.jpg", viewHolder.headImg);
            return view;
        }
    }
    private void getEquipsList(){
        String url = "";
        url = "user/equips";
        CYTools.getObjectList(MyEquipsActivity.this, url, new GetObjectListHandler() {
            @Override
            public void successHandle(String responseString) {
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                String realList = jsonObj.get("data").toString();
                JsonObject jsonObject = CYTools.stringToJsonObject(realList);
                System.out.println("wodeshebei"+responseString+"+"+realList);
                if (jsonObject.get("count").toString().equals("0")){
//                    friendsList = "";
                }else {
                    equipsList = CYTools.stringToArray(jsonObject.getAsJsonArray("list").toString(), CYEquip[].class);
                }
                equipsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failedHandle(String errorMessage) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restriction, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.item_restriction_add:
//                InputEquip(MyEquipsActivity.this,"请输入设备号","确定","取消");
//                final EditText inputServer = new EditText(MyEquipsActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MyEquipsActivity.this);
//                builder.setTitle("请输入设备号").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                        .setNegativeButton("取消", null);
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (inputServer.getText().toString().length()>0){
////                            Toast.makeText(UserCenterActivity.this,inputServer.getText().toString(),Toast.LENGTH_LONG).show();
//                            addEquipment(inputServer.getText().toString());
//                        }else {
//                            Toast.makeText(MyEquipsActivity.this,"设备号不能为空",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                builder.show();
                showAlertDialog();
                break;
        }
        return true;
    }

    private void showAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.activity_input_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.activity_input_dialog);
        TextView btnPositive = (TextView) dialog.findViewById(R.id.btn_save_pop);
//        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.text_input);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("设备号不能为空");
                } else {
                    dialog.dismiss();
                    addEquipment(etContent.getText().toString());
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

    public void addEquipment(String str) {
        RequestParams params = new RequestParams();
        params.put("equip_id", str);
        RestClient.postWithToken(MyEquipsActivity.this, "user/equips", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (statusCode == 404) responseString = "邀请码无效";
//                new android.app.AlertDialog.Builder(MyEquipsActivity.this)
//                        .setTitle("提示")
//                        .setMessage(responseString)
//                        .setPositiveButton("确定", null)
//                        .show();
                OnlyAlert(MyEquipsActivity.this,responseString,"确定");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("response:" + responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);
                if (jsonObj.get("code").toString().equals("0")) {
                    Toast.makeText(MyEquipsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    layoutUI();
                } else {

//                    new android.app.AlertDialog.Builder(MyEquipsActivity.this)
//                            .setTitle("提示")
//                            .setMessage(CYTools.getRealParam(jsonObj.get("desc").toString()))
//                            .setPositiveButton("确定", null)
//                            .show();
                    OnlyAlert(MyEquipsActivity.this,CYTools.getRealParam(jsonObj.get("desc").toString()),"确定");
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
