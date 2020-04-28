package com.SMAPAppProjectGroup13.sharemovies;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareMoviesService extends Service {

    private static final String TAG = "ShareMoviesService";
    private final IBinder binder = new ShareMoviesServiceBinder();
    private boolean started = false;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ListenerRegistration moviesListener;

    private boolean runAsForegroundService = true; // to notification
    private RequestQueue mRequestqueue;
    private Adapter adapter;

    private List<Movie> movieList = new ArrayList<>();

    public class ShareMoviesServiceBinder extends Binder{
        ShareMoviesService getService(){return ShareMoviesService.this;}
    }

    public ShareMoviesService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        //testfilm tilføjes til listen
        //movie = new Movie("Test","Romance","Sjålålå","4.0","3.0","Testing","https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/d0e1bd5e-a7b8-439d-9a61-97710f16313d/dd6p462-b6c9bc68-3089-423d-882d-04e7def5372b.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcL2QwZTFiZDVlLWE3YjgtNDM5ZC05YTYxLTk3NzEwZjE2MzEzZFwvZGQ2cDQ2Mi1iNmM5YmM2OC0zMDg5LTQyM2QtODgyZC0wNGU3ZGVmNTM3MmIucG5nIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.MzlQdoFpCZzTFJNw_iivlijxvsBlfXrXWO7yKZzeXIQ");
        //movieList.add(movie);

        //Firestore sættes op
        Map<String, Object> movie = new HashMap<>();
        movie.put("title","TITEL");
        movie.put("genre","GENRE");
        movie.put("description","D");
        movie.put("imdbRate","4.0");
        movie.put("personalRate","3.0");
        movie.put("note","NOTE");
        movie.put("image","Hello");
        /*
        Map<List<Movie>, Object> movie = new HashMap<>();
        movie.put(movieList,movie);

         */
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started && intent != null) {
            Log.d(TAG, "onStartCommand: called");
            started = true;
            bindToFireStore();
        }
        else
        {
            Log.d(TAG, "onStartCommand: already started");
        }
        return START_STICKY;

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
    public void onDestroy() {
        started = false;
        Log.d(TAG,"Background service destroyed");
        super.onDestroy();

        //stop listening for changes in the firestore
        moviesListener.remove();
    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return binder;
    }

    public void addMovie(String movie){
        try {
            sendRequest(movie);
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Invalid search!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(final String movie) {
        if (mRequestqueue == null) {
            mRequestqueue = Volley.newRequestQueue(this);
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.omdbapi.com")
                .appendPath("")
                .appendQueryParameter("t", movie)
                .appendQueryParameter("apikey", "6ac79944");
                String url = builder.build().toString();
                Log.d(TAG,"URL builed: " + url);

                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                parseJSON(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Volley error is " + error);
            }
        });
    }

    private void parseJSON(String response) {
        Log.d(TAG, "parseJSON() called");
        try {
            JSONObject jsonObject = new JSONObject(response);
            String movieTitle = jsonObject.getString("Title");
            String genre = jsonObject.getString("Genre");
            String description = jsonObject.getString("Plot");
            String imdbRate = jsonObject.getString("imdbRating");
            String imageURL = jsonObject.getString("Poster");

            Movie newMovie = new Movie(movieTitle,genre,description,imdbRate,"","",imageURL);
            movieList.add(newMovie);
            // add to database
            // send broadcast result

        } catch (JSONException e) {
            Log.d(TAG,"onResponse: JSON error");
        }
    }
}
