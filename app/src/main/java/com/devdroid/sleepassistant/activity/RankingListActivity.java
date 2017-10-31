package com.devdroid.sleepassistant.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RankingListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            case R.id.rl_number1:
                break;
            case R.id.rl_number2:
                break;
            case R.id.rl_number3:
                break;
            case R.id.rl_number4:
                break;
            case R.id.rl_number5:
                break;
            case R.id.rl_number6:
                break;
            case R.id.rl_number7:
                break;
            case R.id.rl_number8:
                break;
            case R.id.rl_number9:
                break;
        }
    }
}
