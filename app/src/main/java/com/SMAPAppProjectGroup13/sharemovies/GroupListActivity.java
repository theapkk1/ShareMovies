package com.SMAPAppProjectGroup13.sharemovies;

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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements Adapter.OnMovieListener {


    private static final String TAG = "GroupListActivity";
    public static final int REQUEST_CODE_DETAILSACTIVITY = 101;
    private ShareMoviesService shareMoviesService;
    private ServiceConnection shareMoviesServiceConnection;
    private boolean bound = false;
    private List<Movie> movieList = new ArrayList<>();
    private Movie movie;

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
        movieListView.setHasFixedSize(true);
        movieListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, movieList, this); // Indsæt parameter!
        movieListView.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMovie = searchField.getText().toString();
                if (newMovie.equals("")) {
                    Toast.makeText(GroupListActivity.this, "Please enter a movie", Toast.LENGTH_SHORT).show();
                } else
                    shareMoviesService.addMovie(newMovie);
                searchField.setText(""); // Clear search view after search

            }
        });

    }


    private void startShareMoviesService() {
        // start service
        Intent shareMoviesServiceIntent = new Intent(GroupListActivity.this, ShareMoviesService.class);
        startService(shareMoviesServiceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        // here should the broadcast receiver be registered
        IntentFilter filter = new IntentFilter();
        filter.addAction(ShareMoviesService.BROADCAST_SHAREMOVIES_SERVICE_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        setupConnectionToShareMoviesService();
        bindToShareMoviewService();

    }



    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        unbindShareMoviesService();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        // unregister receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void unbindShareMoviesService() {
        if (bound) {
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
        shareMoviesServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                shareMoviesService = ((ShareMoviesService.ShareMoviesServiceBinder) service).getService();
                Log.d(TAG, "onServiceConnected: ");

                // update list here
                updatedList();

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
        Intent intentDetailsActivity = new Intent(this, DetailsActivity.class);
        intentDetailsActivity.putExtra("position", position);
        startActivityForResult(intentDetailsActivity, REQUEST_CODE_DETAILSACTIVITY);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"Broadcast received from background service");
            updatedList();
        }
    };

    private void updatedList() {
        movieList.addAll(shareMoviesService.getallMovies());
        adapter.setMovies(movieList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAILSACTIVITY) {
            if (resultCode == RESULT_OK) {
                //setResult(RESULT_OK, data);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
