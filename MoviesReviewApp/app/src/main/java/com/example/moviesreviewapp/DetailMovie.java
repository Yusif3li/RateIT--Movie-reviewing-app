package com.example.moviesreviewapp;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class DetailMovie extends AppCompatActivity {

    private int movieId;
    private int userId;
    private Cursor userReview;
    private Boolean isFav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detail_movie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailmovie), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        movieId = intent.getIntExtra("movieId", -1);
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
//        userId = intent.getIntExtra("userId", -1);
        userId = prefs.getInt("userId", -1);

        MoviesDBHelper db = new MoviesDBHelper(getApplicationContext());
        Cursor movie = db.fetchMovie_by_id(movieId);

        TextView movieName = findViewById(R.id.movie_view_movie_name);
        TextView movieSynopsis = findViewById(R.id.movie_view_movie_description);

        if (movie != null && movie.moveToFirst())
        {
            movieName.setText(movie.getString(movie.getColumnIndexOrThrow("title")));
            movieSynopsis.setText(movie.getString(movie.getColumnIndexOrThrow("synopsis")));

            ImageView poster = findViewById(R.id.movie_view_movie_picture);
//            Glide.with(this)
//                    .load(movie.getString(movie.getColumnIndexOrThrow("posterUrl")))
//                    .into(poster);
            String drawableName = movie.getString(movie.getColumnIndexOrThrow("trailerUrl"));
            int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());

            Glide.with(this)
                    .load(resId)
                    .into(poster);

        }

        registerForContextMenu(movieSynopsis);


        ImageView favButton = findViewById(R.id.favourite_button);
        isFav = db.fetchFavExist(userId, movieId);
        if (isFav)
            favButton.setImageResource(android.R.drawable.btn_star_big_on);
        else
            favButton.setImageResource(android.R.drawable.btn_star_big_off);

        favButton.setOnClickListener(v ->
        {
            boolean success;
            if (isFav)
            {
                success = db.deleteFavourite(userId, movieId);
                if (success)
                {
                    favButton.setImageResource(android.R.drawable.btn_star_big_off);
                    isFav = false;
                    Toast.makeText(this, "Removed from favourites!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "FAILED to remove from favourites!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                success = db.insertFavourite(userId, movieId);
                if (success)
                {
                    favButton.setImageResource(android.R.drawable.btn_star_big_on);
                    isFav = true;
                    Toast.makeText(this, "Added to favourites!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Failed to add to favourites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RatingBar ratingBar = findViewById(R.id.movie_view_rating_bar);

        userReview = db.fetchReview(movieId, userId);
        if (userReview.moveToFirst())
        {
            float ratingVal = (float) userReview.getInt(userReview.getColumnIndexOrThrow("rating"));
            ratingBar.setRating(ratingVal/2);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                int ratingInt = (int)(rating*2);

                db.ChangeAverageRating(ratingInt, movieId, userId);

                if (userReview.moveToFirst())
                {
                    int reviewId = userReview.getInt(userReview.getColumnIndexOrThrow("id"));
                    String reviewBody = userReview.getString(userReview.getColumnIndexOrThrow("body"));
                    db.updateReview(reviewId, ratingInt, reviewBody);
                }
                else
                {
                    db.insertNewReview(movieId, userId, ratingInt, "", "");
                }
                userReview.close();
                userReview = db.fetchReview(movieId, userId);

                Toast.makeText(DetailMovie.this, "Rated: " + ratingInt + "/10", Toast.LENGTH_SHORT).show();
            }
        });

        Button reviewBtn = findViewById(R.id.movie_view_button_review);
        reviewBtn.setOnClickListener(v ->
        {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_review, null);
            EditText editText = dialogView.findViewById(R.id.movie_view_dialog_review);
            RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            Cursor cursor = db.fetchAllReviews(movieId);
            recyclerView.setAdapter(new ReviewCyclerAdapter(cursor,db));

            userReview.close();
            userReview = db.fetchReview(movieId, userId);
            if (userReview.moveToFirst())
            {
                String reviewBody = userReview.getString(userReview.getColumnIndexOrThrow("body"));
                editText.setText(reviewBody);
            }

            new AlertDialog.Builder(this)
                    .setTitle("Movie Review")
                    .setView(dialogView)
                    .setPositiveButton("CONFIRM", (dialog, which) ->
                    {
                        String reviewBody = editText.getText().toString();

                        if (userReview.moveToFirst())
                        {
                            int reviewId = userReview.getInt(userReview.getColumnIndexOrThrow("id"));
                            int rating = userReview.getInt(userReview.getColumnIndexOrThrow("rating"));
                            db.updateReview(reviewId, rating, reviewBody);
                        }
                        else
                        {
                            int ratingInt = (int)(ratingBar.getRating()*2);
                            db.insertNewReview(movieId, userId, ratingInt, reviewBody, "");
                        }

                        userReview.close();
                        userReview = db.fetchReview(movieId, userId);

                        Toast.makeText(DetailMovie.this, "Review saved!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel())
                    .show();
        });


        Button watch_button=findViewById(R.id.watch_button);
//initial show of button data
        if (db.fetchWatchExist(userId, movieId)) {
            watch_button.setText("Remove Watch Later");
        } else {
            watch_button.setText("Add Watch Later");
        }

//
        watch_button.setOnClickListener(v -> {
            try {
                if (db.fetchWatchExist(userId, movieId)) {
                    if (db.deleteWatchLater(userId, movieId)) {
                        watch_button.setText("Add Watch Later");
                        Toast.makeText(this, "Successfully removed from watch later", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error in removing from watch later", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (db.insertWatchLater(userId, movieId)) {
                        watch_button.setText("Remove Watch Later");
                        Toast.makeText(this, "Successfully added to watch later", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error in adding to watch later", Toast.LENGTH_SHORT).show();
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
//
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.movie_view_movie_description) {
            menu.setHeaderTitle("Options");
            menu.add(0, v.getId(), 0, "Copy Text");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Copy Text")) {
            TextView myTextView = findViewById(R.id.movie_view_movie_description);
            String textToCopy = myTextView.getText().toString();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}