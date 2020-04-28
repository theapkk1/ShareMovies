package com.SMAPAppProjectGroup13.sharemovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

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
}
