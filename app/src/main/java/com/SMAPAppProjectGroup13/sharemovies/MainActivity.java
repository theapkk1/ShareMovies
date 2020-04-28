package com.SMAPAppProjectGroup13.sharemovies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUESTCODE_SIGN_IN = 1000;
    private static final String LOG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inspiration from: Lecture 10 https://www.youtube.com/watch?v=DuRmGBSRF1Y&feature=youtu.be
        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List of providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),REQUESTCODE_SIGN_IN);
            }
        });
    }

    //tjekker om vi får requestCode tilbage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESTCODE_SIGN_IN )
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(LOG, user.getUid());
                ((TextView)findViewById(R.id.userId)).setText(user.getUid());

                //Når brugeren er logget ind vises den fælles liste
                finish();
                Intent intent = new Intent(MainActivity.this, GroupListActivity.class);
                startActivity(intent);
            } else
            {
                Log.d(LOG, response.getError().getMessage());
            }
        }
    }
}
