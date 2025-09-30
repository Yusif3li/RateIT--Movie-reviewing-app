package com.example.moviesreviewapp;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

class ReviewCyclerAdapter extends RecyclerView.Adapter<ReviewCyclerAdapter.ViewHolder> {

    public Cursor cursor;
    private MoviesDBHelper db;
    public ReviewCyclerAdapter(Cursor cursor, MoviesDBHelper db) {
        this.cursor = cursor;
        this.db = db;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @NonNull
    @Override
    public ReviewCyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_recycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if (!cursor.moveToPosition(position))
            return;

        int id = cursor.getInt(cursor.getColumnIndexOrThrow("userId"));
        String username = db.fetchUserName(id);
        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow("rating"));

        holder.username.setText(username);
        holder.comment.setText(body);
        holder.ratingBar.setRating(((float)rating)/2);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        TextView comment;

        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.reviews_recycler_username);
            comment = view.findViewById(R.id.reviews_recycler_comment);
            ratingBar = view.findViewById(R.id.reviews_recycler_ratingbar);
        }
    }
}