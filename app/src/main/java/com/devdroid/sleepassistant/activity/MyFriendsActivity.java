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

public class MyFriendsActivity extends BaseActivity {
    private DatabaseBackupTask mBackupTask;
    private Dialog mBackupDialog;
    private ListView lvFriend;
    private List<CYUser> friendsList = new ArrayList<CYUser>();
    private FriendsListAdapter friendsListAdapter = new FriendsListAdapter();
    private List<String> urlList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutUI();
    }

    private void layoutUI() {

        lvFriend = (ListView)findViewById(R.id.friend_listview);
        lvFriend.setDividerHeight(0);
        /*
        adapter = new SimpleAdapter (this,
                getData(),
                R.layout.listitem_user,
                new String[]{"name"},
                new int[]{R.id.user_name});
        lvFriend.setAdapter(adapter);
        */
        lvFriend.setAdapter(friendsListAdapter);
//        lvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                CYUser user = friendsList.get(position);
//                Intent intent = new Intent(FriendActivity.this, CYFriendActivity.class);
//                intent.putExtra("id", user.id);
//                startActivity(intent);
//            }
//        });
        getFriendsList();
    }

    public class FriendsListAdapter extends BaseAdapter {
        private class ViewHolder{
            public ImageView headImg = null;
            public TextView nickname = null;
            //日期
//            public TextView  date = null;
//            public TextView  content = null;
        }
        @Override
        public int getCount() {
            return friendsList.size();
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
                view = MyFriendsActivity.this.getLayoutInflater().inflate(R.layout.listitem_user, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.headImg = (ImageView) view.findViewById(R.id.user_head);
                viewHolder.nickname = (TextView) view.findViewById(R.id.user_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CYUser user = (CYUser)friendsList.get(position);
            viewHolder.nickname.setText(user.getnickname());
            if (urlList.get(position) != null) {
//                ImageLoaderTool.loadCircleImage(urlList.get(position), viewHolder.headImg);
                ImageLoaderTool.loadCircleImageWithDefaultRes(urlList.get(position), viewHolder.headImg, R.drawable.user_icon_default);

            }
            //ImageLoaderTool.loadCircleImage("http://images.cnblogs.com/cnblogs_com/kenshincui/613474/o_13.jpg", viewHolder.headImg);
            return view;
        }
    }
    private void getFriendsList(){
        String url = "";
        url = "user/friends";
        CYTools.getObjectList(MyFriendsActivity.this, url, new GetObjectListHandler() {
            @Override
            public void successHandle(String responseString) {
                JsonObject jsonObj = CYTools.stringToJsonObject(responseString);

                String realList = jsonObj.get("data").toString();
                JsonObject jsonObject = CYTools.stringToJsonObject(realList);
                System.out.println("wodepengyou"+responseString+"+"+realList);
                if (jsonObject.get("count").toString().equals("0")){
//                    friendsList = "";
                }else {
                    friendsList = CYTools.stringToArray(jsonObject.getAsJsonArray("list").toString(), CYUser[].class);
                }
                friendsListAdapter.notifyDataSetChanged();
                urlList.clear();
                for (CYUser user : friendsList) {
                    String url;
                    if (user.icon == null) {
                        urlList.add(null);
                    } else {
                        url = user.icon;
                        urlList.add(RestClient.getAbsoluteUrl(url));
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
