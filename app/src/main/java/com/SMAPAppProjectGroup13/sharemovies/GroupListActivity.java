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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class GroupListActivity extends AppCompatActivity implements Adapter.OnMovieListener{

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ListenerRegistration moviesListener;
    private static final String TAG = "GroupListActivity";
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
        adapter = new Adapter(this, movieList,this); // Indsæt parameter!
        movieListView.setLayoutManager(new LinearLayoutManager(this));

        //testfilm tilføjes til listen
        //movie = new Movie("Test","Romance","Sjålålå","4.0","3.0","Testing","https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/d0e1bd5e-a7b8-439d-9a61-97710f16313d/dd6p462-b6c9bc68-3089-423d-882d-04e7def5372b.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcL2QwZTFiZDVlLWE3YjgtNDM5ZC05YTYxLTk3NzEwZjE2MzEzZFwvZGQ2cDQ2Mi1iNmM5YmM2OC0zMDg5LTQyM2QtODgyZC0wNGU3ZGVmNTM3MmIucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.MzlQdoFpCZzTFJNw_iivlijxvsBlfXrXWO7yKZzeXIQ");
        //movieList.add(movie);

        //Firestore sættes op
        /*
        Map<List<Movie>, Object> newmovie = new HashMap<>();
        newmovie.put(movieList,movie);
         */
        Map<String, Object> movie = new HashMap<>();
        movie.put("title","TITEL");
        movie.put("genre","GENRE");
        movie.put("description","D");
        movie.put("imdbRate","4.0");
        movie.put("personalRate","3.0");
        movie.put("note","NOTE");
        movie.put("image","");

        //Inspiration from: https://www.youtube.com/watch?v=fJmVhOzXNJQ&feature=youtu.be
        firestore.collection("movies").add(movie).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Added " + documentReference.getId());
                    }
                }
        )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                });
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
        bindToFireStore();
    }

    private void bindToFireStore() {
        //snapshot trigger hver kan noget ændrer sig i collection
        moviesListener = firestore.collection("movies").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                //Hvis listen ikke er tom
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty())
                {
                    //loop over hver movie i listen
                    //List<Movie> movies = new ArrayList<>();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                    {
                        movieList.add((Movie) snapshot.getData().get(movieList));
                    }
                    adapter.setMovies(movieList);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: ");
        unbindShareMoviesService();
        //stop listening for changes in the firestore
        moviesListener.remove();
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
