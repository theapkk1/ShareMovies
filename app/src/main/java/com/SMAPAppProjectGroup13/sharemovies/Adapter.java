package com.SMAPAppProjectGroup13.sharemovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context _context;
    private OnMovieListener _onMovieListener;

    public Adapter(Context context, OnMovieListener onMovieListener){
        _context = context;
        _onMovieListener = onMovieListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recyclerview,parent, false);
        ViewHolder vHolder = new ViewHolder(v, _onMovieListener);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0; // returner størrelse på liste
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView movie;
        TextView rating;
        TextView user;
        OnMovieListener onMovieListener;

        public ViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            movie = itemView.findViewById(R.id.movieText);
            rating = itemView.findViewById(R.id.ratingTxt);
            user = itemView.findViewById(R.id.userText);
            this.onMovieListener = onMovieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMovieListener.onMovieClick(getAdapterPosition());
        }

    }

    public interface OnMovieListener {
        void onMovieClick(int position);
    }

}
