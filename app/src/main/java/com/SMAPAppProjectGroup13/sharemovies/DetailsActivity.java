package com.SMAPAppProjectGroup13.sharemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private ShareMoviesService shareMoviesService;
    private ServiceConnection shareMoviesServiceConnection;
    private boolean bound = false;

    private Button b_Share;
    private Button b_Delete;
    private Button b_Back;
    private TextView tv_movieName;
    private TextView tv_genre;
    private TextView tv_description;
    private TextView tv_comments;
    private TextView tv_IMDBrating;
    private TextView tv_yourRating;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setupConnectionToShareMoviesService();

        b_Back = findViewById(R.id.b_back);
        b_Delete = findViewById(R.id.b_delete);
        b_Share = findViewById(R.id.b_share);

        b_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        b_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        b_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: ");
        Intent intent = new Intent(DetailsActivity.this, ShareMoviesService.class);
        bindService(intent, shareMoviesServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart: bound to service");

    }

    @Override
    public void onStop(){
        super.onStop();
        if (bound){
            unbindService(shareMoviesServiceConnection);
            bound = false;
        }
        Log.d(TAG, "onStop: ");
    }

    private void setupConnectionToShareMoviesService() {
        shareMoviesServiceConnection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                shareMoviesService = ((ShareMoviesService.ShareMoviesServiceBinder)service).getService();
                Log.d(TAG, "onServiceConnected: ");

                // update list here

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: service disconneccted");
            }
        };
    }
}
