package com.SMAPAppProjectGroup13.sharemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private ShareMoviesService shareMoviesService;
    private ServiceConnection shareMoviesServiceConnection;
    private boolean bound = false;
    private int position;
    private String note;
    private String rate_value;
    Movie movie;

    private Button b_Share, b_Delete, b_Back;
    private TextView tv_movieName, tv_genre, tv_description, tv_IMDBrating, tv_yourRating;
    private TextView tv_genreTitle, tv_descriptionTitle,tv_commentsTitle, tv_imdbRateTitle, tv_personalRateTitle;
    private EditText tv_note;
    private ImageView image;
    private SeekBar seekBar_rate;

    // Values for seekbar
    int min = 0, max=100, current = 0;

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
        tv_genreTitle = findViewById(R.id.TV_genreTitle);
        tv_genre = findViewById(R.id.TV_genre);
        tv_descriptionTitle = findViewById(R.id.TV_descriptionTitle);
        tv_description = findViewById(R.id.TV_showDescrip);
        tv_imdbRateTitle = findViewById(R.id.TV_imdbRateTitle);
        tv_IMDBrating = findViewById(R.id.TV_imdbRate);
        tv_personalRateTitle = findViewById(R.id.TV_personalRateTitle);
        tv_yourRating = findViewById(R.id.TV_personalRate);
        tv_commentsTitle = findViewById(R.id.TV_commentTitle);
        tv_note = findViewById(R.id.editText);
        image = findViewById(R.id.imageView);
        seekBar_rate = findViewById(R.id.seekBar);

        position = shareMoviesIntent.getIntExtra("position",0);

        // SeekBar for personal rating
        seekBar_rate.setMax(max);
        seekBar_rate.setProgress(current);
        tv_yourRating.setText(""+current);

        seekBar_rate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                current = progress;
                tv_yourRating.setText("" + (double)current/10);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        b_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save values
                setResult(Activity.RESULT_CANCELED);
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
                note = tv_note.getText().toString();
                movie.setNote(note);
                rate_value = tv_yourRating.getText().toString();
                movie.setPersonalRate(rate_value);// set rating
                // updatere liste
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

                if (bound && getIntent().hasExtra("position")){
                    movie = shareMoviesService.getMovie(position);
                    tv_movieName.setText(movie.getTitle());
                    tv_genre.setText(movie.getGenre());
                    tv_genre.setMovementMethod(new ScrollingMovementMethod());
                    tv_IMDBrating.setText(movie.getImdbRate());
                    tv_description.setText(movie.getDescription());
                    tv_description.setMovementMethod(new ScrollingMovementMethod());
                    Glide.with(DetailsActivity.this).load(movie.getImage()).into(image);
                    tv_note.setText(movie.getNote());
                }


            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: service disconneccted");
            }
        };
    }

}
