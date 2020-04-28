package com.SMAPAppProjectGroup13.sharemovies;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupListActivity extends AppCompatActivity implements Adapter.OnMovieListener{

    Button addBtn;
    EditText searchField;
    RecyclerView movieListView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        addBtn = findViewById(R.id.addButton);
        searchField = findViewById(R.id.editText);
        movieListView = findViewById(R.id.recyclerView);
        adapter = new Adapter(this, this); // Indsæt parameter!
        movieListView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onMovieClick(int position) {

    }
}
