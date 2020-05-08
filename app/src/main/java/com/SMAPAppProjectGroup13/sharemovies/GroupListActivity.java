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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

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
    private String groupID;

    Button addBtn, signOutBtn, addGroup, showList;
    EditText listgroupID, newGroupName, searchField;
    RecyclerView movieListView;
    Adapter adapter;
    TextView currentGroupTitle, currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        // Get the users groupID
        Intent MainIntent = getIntent();
        groupID = MainIntent.getStringExtra("group");

        startShareMoviesService();

        // Get widget references
        addBtn = findViewById(R.id.addButton);
        addGroup = findViewById(R.id.button_newGroup);
        showList = findViewById(R.id.button_showGroup);
        signOutBtn = findViewById(R.id.BtnLogOut);
        listgroupID = findViewById(R.id.editText_group);
        searchField = findViewById(R.id.editText);
        currentGroupTitle = findViewById(R.id.textView);
        currentGroup = findViewById(R.id.textView_groupID);
        newGroupName = findViewById(R.id.editText_newGroup);
        movieListView = findViewById(R.id.recyclerView);
        movieListView.setHasFixedSize(true);
        movieListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, movieList, this);
        movieListView.setAdapter(adapter);

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: add New Group button pushed");
                // Creates a new group and show this current group id
                shareMoviesService.addNewGroup(newGroupName.getText().toString());
                newGroupName.setText(""); // Clear search view after search
                shareMoviesService.getcurrentGroupID();

            }
        });

        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Shows the searched group list
                Log.d(TAG, "onClick: Group button pushed");
                shareMoviesService.getAllMoviesForGroupFromDatabase(listgroupID.getText().toString());
                listgroupID.setText(""); // Clear search view after search
                shareMoviesService.getcurrentGroupID();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMovie = searchField.getText().toString();
                if (newMovie.equals("")) {
                    Toast.makeText(GroupListActivity.this, getString(R.string.please_enter_a_movie), Toast.LENGTH_SHORT).show();
                } else
                    shareMoviesService.addMovie(newMovie);
                searchField.setText(""); // Clear search view after search

            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Toast.makeText(GroupListActivity.this, getString(R.string.signing_out), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startShareMoviesService() {
        // starts service
        Intent shareMoviesServiceIntent = new Intent(GroupListActivity.this, ShareMoviesService.class);
        startService(shareMoviesServiceIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        // Broadcast receiver registered
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
        // Unregister receivers
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

                // updating list
                updatedList();
                Log.d(TAG,"Size of movie list: " + movieList.size());
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
        movieList.clear();
        movieList.addAll(shareMoviesService.getallMovies());
        adapter.setMovies(movieList);
        currentGroup.setText(shareMoviesService.getcurrentGroupID());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAILSACTIVITY) {
            if (resultCode == RESULT_OK) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
