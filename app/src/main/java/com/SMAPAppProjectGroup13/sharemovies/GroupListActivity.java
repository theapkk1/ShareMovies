package com.SMAPAppProjectGroup13.sharemovies;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupListActivity extends AppCompatActivity implements Adapter.OnMovieListener{

    private static final String TAG = "GroupListActivity";
    private ShareMoviesService shareMoviesService;
    private ServiceConnection shareMoviesServiceConnection;
    private boolean bound = false;

    Button addBtn;
    EditText searchField;
    RecyclerView movieListView;
    Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        startShareMoviesService(); // is used to bind user to the grouplist

        addBtn = findViewById(R.id.addButton);
        searchField = findViewById(R.id.editText);
        movieListView = findViewById(R.id.recyclerView);
        adapter = new Adapter(this, this); // Indsæt parameter!
        movieListView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void startShareMoviesService() {
        // start service
        Intent shareMoviesServiceIntent = new Intent(GroupListActivity.this, ShareMoviesService.class);
        startService(shareMoviesServiceIntent);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: ");

        // her should the broadcast receiver be registered
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

        // unregister receivers

    }

    private void unbindShareMoviesService() {
        if (bound){
            unbindService(shareMoviesServiceConnection);
            bound = false;
        }
    }


    private void bindToShareMoviewService() {
        bindService(new Intent(GroupListActivity.this, ShareMoviesService.class),
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

                // update list here

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected: service disconneccted");
            }
        };
    }

    @Override
    public void onMovieClick(int position) {

    }
}
