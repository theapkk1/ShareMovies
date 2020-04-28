package com.SMAPAppProjectGroup13.sharemovies;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShareMoviesService extends Service {

    private static final String TAG = "ShareMoviesService";
    private final IBinder binder = new ShareMoviesServiceBinder();
    private boolean started = false;

    private boolean runAsForegroundService = true; // to notification
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started && intent != null) {
            Log.d(TAG, "onStartCommand: called");
            started = true;
        }
        else
        {
            Log.d(TAG, "onStartCommand: already started");
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        started = false;
        Log.d(TAG,"Background service destroyed");
        super.onDestroy();
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
