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
    private int position;

    private Button b_Share, b_Delete, b_Back;
    private TextView tv_movieName, tv_genre, tv_description, tv_comments, tv_IMDBrating, tv_yourRating;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get intent from GrouplistActivity
        Intent shareMoviesIntent = getIntent();

        setupConnectionToShareMoviesService();

        // Get widget references
        b_Back = findViewById(R.id.b_back);
        b_Delete = findViewById(R.id.b_delete);
        b_Share = findViewById(R.id.b_share);
        tv_movieName = findViewById(R.id.TV_nameMovie);
        tv_genre = findViewById(R.id.TV_genreTitle);
        tv_description = findViewById(R.id.TV_descriptionTitle);
        tv_IMDBrating = findViewById(R.id.TV_imdbRateTitle);
        tv_yourRating = findViewById(R.id.TV_personalRateTitle);
        tv_comments = findViewById(R.id.TV_commentTitle);

        position = shareMoviesIntent.getIntExtra("position",0);

        b_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                setResult(RESULT_OK);
                finish();
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
                bound = true;
                Log.d(TAG, "onServiceConnected: ");


            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: service disconneccted");
            }
        };
    }

}
