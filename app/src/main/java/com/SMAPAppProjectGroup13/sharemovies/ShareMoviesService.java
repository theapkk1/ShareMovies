package com.SMAPAppProjectGroup13.sharemovies;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ShareMoviesService extends Service {

    private static final String TAG = "ShareMoviesService";
    private final IBinder binder = new ShareMoviesServiceBinder();
    private boolean started = false;

    private boolean runAsForegroundService = true; // to notification

    public class ShareMoviesServiceBinder extends Binder{
        ShareMoviesService getService(){return ShareMoviesService.this;}
    }

    public ShareMoviesService() {

    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started && intent != null) {
            Log.d(TAG, "onStartCommand: called");
            started = true;
        }
        else
        {
            Log.d(TAG, "onStartCommand: already started");
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        started = false;
        Log.d(TAG,"Background service destroyed");
        super.onDestroy();
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return binder;
    }
}
