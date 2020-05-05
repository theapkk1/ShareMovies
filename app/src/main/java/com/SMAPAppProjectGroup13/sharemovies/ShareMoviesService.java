package com.SMAPAppProjectGroup13.sharemovies;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShareMoviesService extends Service {

    private static final String TAG = "ShareMoviesService";
    public static final String BROADCAST_SHAREMOVIES_SERVICE_RESULT = "com.SMAPAppProjectGroup13.sharemovies";
    private static final String CHANNEL_ID = "ShareMoviesChannel";
    private static final int NOTIFY_ID = 101;
    private final IBinder binder = new ShareMoviesServiceBinder();
    private boolean started = false;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ListenerRegistration moviesListener;
    private User user;
    private String localDocumentReference;

    private RequestQueue mRequestqueue;

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

        getAllMoviesFromDatabase();
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
        moviesListener = firestore.collection("movies").document("group1").collection("movies1").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                //Hvis listen ikke er tom
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.getDocuments().isEmpty())
                {
                    //loop over hver movie i listen
                    //List<Movie> movies = new ArrayList<>();
                    movieList.clear();
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments())
                    {
                        movieList.add((Movie) snapshot.toObject(Movie.class));
                    }
                    //send broadcast
                    sendBroadcastResult();

                    //send notifikation når listen ændrer sig
                    Notification notification = new NotificationCompat.Builder(ShareMoviesService.this, CHANNEL_ID)
                            .setContentTitle("ShareMovies")
                            .setSmallIcon(R.drawable.sharemovies)
                            .setContentText("newmovie" + "was added to your grouplist!")
                            .build();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ShareMoviesService.this);
                    notificationManager.notify(NOTIFY_ID,notification);
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
        //moviesListener.remove();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return binder;
    }

    public Movie getMovie(int position) {
        return movieList.get(position);
    }

    public void deleteMovie(Movie movie) {
        movieList.remove(movie);
        deleteMovieFromDataBase(movie);
        sendBroadcastResult();
    }

    private void deleteMovieFromDataBase(final Movie movie) {
        firestore.collection("movies").document("group1").collection("movies1").document(movie.getMovieId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void sendBroadcastResult(){
        Log.d(TAG,"broadcasting");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_SHAREMOVIES_SERVICE_RESULT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
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

        }); mRequestqueue.add(request);
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
            addMovie(newMovie);
            // send broadcast result
            sendBroadcastResult();

        } catch (JSONException e) {
            Log.d(TAG,"onResponse: JSON error");
        }
    }
    public List<Movie> getAllMoviesFromDatabase() {
        //Inspiration from: https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
        firestore.collection("movies").document("group1").collection("movies1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            movieList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                movieList.add((Movie) document.toObject(Movie.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return movieList;
    }

    public void addMovie(final Movie movie)
    {

        //Inspiration from: https://www.youtube.com/watch?v=fJmVhOzXNJQ&feature=youtu.be
        firestore.collection("movies").document("group1").collection("movies1").add(movie).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Added " + documentReference.getId());
                        localDocumentReference = documentReference.getId();
                        movie.setMovieId(localDocumentReference);
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
    public void checkUser(final String email)
    {
        // kig efter om brugerens email er i listen over users
        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        document.toObject(User.class);
                        String dataEmail = document.getData().toString();
                        if (email == dataEmail)
                        {
                            //hvis brugeren er der skal brugeren gruppeID hentes og gemmes ned
                             String group = firestore.collection("users").document(email).collection("group").get().toString();

                        }
                        // hvis brugeren ikke er der skal brugeren gemmes ned som ny bruger i users
                        // og have tilkoblet en ny gruppe --> evt. et metode kald
                        createUser(email);


                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



    }

    private void createUser(String email) {
    }

    public List<Movie> getallMovies(){
        return movieList;
    }
}
