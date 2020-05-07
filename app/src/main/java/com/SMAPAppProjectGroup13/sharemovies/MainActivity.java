package com.SMAPAppProjectGroup13.sharemovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ShareMoviesService shareMoviesService;
    private ServiceConnection shareMoviesServiceConnection;
    private User user_;
    private boolean bound = false;

    private static final int REQUESTCODE_SIGN_IN = 1000;
    private static final String LOG = MainActivity.class.getSimpleName();
   // ImageView im = findViewById(R.id.imageV_Main);


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
                FirebaseLogin();
            }
        });

        startShareMoviesService(); // is used to bind user to the grouplist
    }

    private void FirebaseLogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), REQUESTCODE_SIGN_IN);
    }

    private void startShareMoviesService() {
        // start service
        Intent shareMoviesServiceIntent = new Intent(MainActivity.this, ShareMoviesService.class);
        startService(shareMoviesServiceIntent);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: ");


        // here should the broadcast receiver be registered
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShareMoviesService.BROADCAST_SHAREMOVIES_SERVICE_RESULT_Main);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: ");
        setupConnectionToShareMoviesService();
        bindToShareMoviewService();
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: ");
        unbindShareMoviesService();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop: ");

    }

    private void unbindShareMoviesService() {
        if (bound){
            unbindService(shareMoviesServiceConnection);
            bound = false;
        }
    }


    private void bindToShareMoviewService() {
        bindService(new Intent(MainActivity.this, ShareMoviesService.class),
                shareMoviesServiceConnection, Context.BIND_AUTO_CREATE);

        bound = true;
        Log.d(TAG, "bindToShareMoviewService: called");
    }


    private void setupConnectionToShareMoviesService() {
        shareMoviesServiceConnection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                shareMoviesService = ((ShareMoviesService.ShareMoviesServiceBinder)service).getService();
                Log.d(TAG, "onServiceConnected: ");

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: service disconneccted");
            }
        };
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
                Log.d(LOG, "userUid:" + user.getUid());
                ((TextView)findViewById(R.id.userId)).setText(user.getUid());

                // sender userUid med i metoden
                shareMoviesService.checkUser(user.getUid());


            } else
            {
                Log.d(LOG, response.getError().getMessage());
            }
        }
        Log.d(TAG, "onActivityResult: her skal jeg ikke være");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Broadcast received from background service, groupID");

            //Når der er hentet et groupID for brugeren
            Intent GroupIntent = new Intent(MainActivity.this, GroupListActivity.class);
            //intent.putExtra("gruopID", user_.getGroupID());
            startActivity(GroupIntent);
            finish();

        }
    };

}
