package com.devdroid.sleepassistant.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.base.CYTools;
import com.devdroid.sleepassistant.base.CYUser;
import com.devdroid.sleepassistant.base.GetObjectListHandler;
import com.devdroid.sleepassistant.base.ImageLoaderTool;
import com.devdroid.sleepassistant.base.NetResultHandler;
import com.devdroid.sleepassistant.base.RestClient;
import com.devdroid.sleepassistant.bluetooth.AnyScanActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;
import com.google.gson.JsonObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FriendsRequestActivity extends BaseActivity {
    private ListView inviteListView = null;
    private List<CYUser> inviteList = new ArrayList<CYUser>();
    private inviteAdapter Adapter = new inviteAdapter();
    private List<String> urlList = new ArrayList<String>();
    private ImageView ivBack;
    private String team_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutUI();
    }

    private void layoutUI() {

        inviteListView = (ListView)findViewById(R.id.friend_request_listview);
        inviteListView.setDividerHeight(0);
        /*
        adapter = new SimpleAdapter (this,
                getData(),
                R.layout.listitem_user,
                new String[]{"name"},
                new int[]{R.id.user_name});
        lvFriend.setAdapter(adapter);
        */
        inviteListView.setAdapter(Adapter);
//        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CYUser user = friendsList.get(position);
//                Intent intent = new Intent(FriendActivity.this, CYFriendActivity.class);
//                intent.putExtra("id", user.id);
//                startActivity(intent);
//            }
//        });
        getApplicationList();
    }
    public class inviteAdapter extends BaseAdapter {
        private class ViewHolder{
            public ImageView headImg = null;
            public TextView nickname = null;
            //日期
            public TextView  date = null;
            public TextView  message = null;
            public Button acceptButton = null;
            public Button declineButton = null;
        }
        @Override
        public int getCount() {
            return inviteList.size();
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
            final inviteAdapter.ViewHolder viewHolder;
            if (convertView == null){
                view = getLayoutInflater().inflate(R.layout.listitem_friends_request,parent,false);
                viewHolder = new inviteAdapter.ViewHolder();
                viewHolder.headImg = (ImageView)view.findViewById(R.id.cy_head);
                viewHolder.nickname = (TextView)view.findViewById(R.id.nickname);
                viewHolder.date = (TextView)view.findViewById(R.id.date);
                viewHolder.message = (TextView)view.findViewById(R.id.content);
                viewHolder.acceptButton = (Button)view.findViewById(R.id.accept_button);
                viewHolder.declineButton = (Button)view.findViewById(R.id.decline_button);
                view.setTag(viewHolder);
            }else{
                viewHolder = (inviteAdapter.ViewHolder)view.getTag();
            }
            final CYUser user = (CYUser) inviteList.get(position);
            viewHolder.nickname.setText(user.getnickname());
            viewHolder.message.setText(user.getmessage());
            if (urlList.get(position) != null) {
                ImageLoaderTool.loadCircleImageWithDefaultRes(urlList.get(position), viewHolder.headImg, R.drawable.user_icon_default);
            }
            viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "user/friends";
                    CYTools.acceptSomeRequest2(FriendsRequestActivity.this, url,user.getapplication_id(), new NetResultHandler() {
                        @Override
                        public void successHandle(String responseString, int statusCode) {
                            Toast.makeText(getBaseContext(), "您同意了"+user.getName()+"的申请!", Toast.LENGTH_LONG).show();
                            getApplicationList();
                        }

                        @Override
                        public void failedHandle(String responseString, int statusCode) {
                            //if(statusCode == "")
                            Toast.makeText(getBaseContext(), "操作失败！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            viewHolder.declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "users/friends" + user.getUsername();
                    CYTools.declineSomeRequest2(FriendsRequestActivity.this, url,user.getapplication_id(), new NetResultHandler() {
                        @Override
                        public void successHandle(String responseString, int statusCode) {
                            Toast.makeText(getBaseContext(), "您拒绝了"+user.getName()+"的申请!", Toast.LENGTH_LONG).show();
                            getApplicationList();
                        }

                        @Override
                        public void failedHandle(String responseString, int statusCode) {
                            Toast.makeText(getBaseContext(), "操作失败！", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });


            if (urlList.get(position) != null) {
                ImageLoaderTool.loadCircleImage(urlList.get(position), viewHolder.headImg);
            }
            return  view;
        }
    }

    private void getApplicationList() {
        String url = "user/friends_apply";
        CYTools.getObjectList(FriendsRequestActivity.this, url, new GetObjectListHandler() {
            @Override
            public void successHandle(String responseString) {
//                Log.e("qqq", responseString);
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);

                String realList = jsonObj.get("data").toString();
                JsonObject jsonObject = CYTools.stringToJsonObject(realList);
                System.out.println("haoyouqingqiu"+responseString+"+"+realList);
                inviteList = CYTools.stringToArray(jsonObject.getAsJsonArray("receive").toString(), CYUser[].class);
                Adapter.notifyDataSetChanged();
                urlList.clear();
                for (CYUser user :inviteList) {
                    String url;
                    if (user.icon == null) {
                        urlList.add(null);
                    }else {
                        if (user.icon.length() == 0){
                            urlList.add("");
                        }else {
                            url = user.icon;
                            urlList.add(RestClient.getAbsoluteUrl(url));
                        }
                    }

                }

            }
            @Override
            public void failedHandle(String errorMessage) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

//    public void onClick(View view){
//        switch (view.getId()){
//            case R.id.ll_add_friends:
//                startActivity(new Intent(this, AddFriendsActivity.class));
//                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
//                break;
//            case R.id.ll_my_friends:
//                startActivity(new Intent(this, MyFriendsActivity.class));
//                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
//                break;
//            case R.id.ll_friends_request:
//                startActivity(new Intent(this, MyFriendsActivity.class));
//                overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
//                break;
//        }
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
