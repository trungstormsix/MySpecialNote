package com.tsnanh.myspecialnote.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.controller.MSNAsyncTask;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        MSNAsyncTask msnAsyncTask = new MSNAsyncTask(this);
        msnAsyncTask.execute();
    }
}
