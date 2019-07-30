package com.tsnanh.myspecialnote.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.ProgressBar;

import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.view.MainActivity;

public class MSNAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    Activity launcherActivity;

    public MSNAsyncTask(Activity launcherActivity) {
        this.launcherActivity = launcherActivity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        for (int i = 0; i < 1; i++) {
            SystemClock.sleep(100);
            publishProgress(i);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        ProgressBar progressBar = launcherActivity.findViewById(R.id.prgh_launcher);
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Intent intent = new Intent(launcherActivity, MainActivity.class);
        launcherActivity.startActivity(intent);
        launcherActivity.finish();
    }
}
