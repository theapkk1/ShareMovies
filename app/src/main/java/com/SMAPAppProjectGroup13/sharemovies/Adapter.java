package com.SMAPAppProjectGroup13.sharemovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    private Context _context;
    private OnMovieListener _onMovieListener;
    private List<Movie> _movieList;
    private Movie movie;
    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
    String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

    public Adapter(Context context, List<Movie> movieList, OnMovieListener onMovieListener){
        _context = context;
        _onMovieListener = onMovieListener;
        _movieList = movieList;
    }
    public void setMovies(List<Movie> movies)
    {
        this._movieList = movies;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recyclerview,parent, false);
        ViewHolder vHolder = new ViewHolder(v, _onMovieListener);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        movie = _movieList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.rating.setText(movie.getPersonalRate());
        holder.user.setText("Added " + date);

        String url = movie.getImage();
        Picasso.with(holder.image.getContext()).load(url).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return _movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView movieTitle;
        TextView rating;
        TextView user;
        RelativeLayout mainLayout;
        OnMovieListener onMovieListener;

        public ViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
            super(itemView);

            // Get widget references
            image = itemView.findViewById(R.id.imageView);
            movieTitle = itemView.findViewById(R.id.movieText);
            rating = itemView.findViewById(R.id.ratingTxt);
            user = itemView.findViewById(R.id.userText);
            mainLayout = itemView.findViewById(R.id.mainList);
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
