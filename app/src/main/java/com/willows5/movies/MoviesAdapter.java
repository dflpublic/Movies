package com.willows5.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.willows5.movies.data.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private final MoviesAdapterOnClickHandler _clickHandler;
    private       Movie[]                     _movies;
    private       Context                     context;

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        _clickHandler = clickHandler;
    }

    public void setMovies(Movie[] movies) {
        _movies = movies;
        notifyDataSetChanged();
    }

    public Movie getMovie(int n) {
        return _movies[n];
    }

    @NonNull
    @Override
    public MoviesAdapter.MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int            layoutId = R.layout.grid_content;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MoviesAdapterViewHolder holder, int position) {
        Movie movie = _movies[position];
//        holder.tvTitle.setText(movie.getTitle());
        Picasso.with(context)
                .load(Movie.IMAGE_MAIN_PATH + movie.getPoster())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        if (_movies == null) {
            return 0;
        } else {
            return _movies.length;
        }
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(int n);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements
                                                                         OnClickListener {
        @BindView(R.id.iv_poster_main)
        ImageView ivPoster;
//        public final TextView  tvTitle;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
//            tvTitle = (TextView)itemView.findViewById(R.id.tv_title_main);
//            ivPoster = (ImageView)itemView.findViewById(R.id.iv_poster_main);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            _clickHandler.onClick(adapterPosition);
        }

    }

}
