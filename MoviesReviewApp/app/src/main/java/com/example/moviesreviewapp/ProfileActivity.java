package com.example.moviesreviewapp;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
private MoviesDBHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = new MoviesDBHelper(this);

        TextView tv_user = findViewById(R.id.tv_username);
        TextView tv_email = findViewById(R.id.tv_email);
        TextView tv_fav_count = findViewById(R.id.tv_fav_count);
        TextView tv_watch_later_count = findViewById(R.id.tv_watch_later_count);
        TextView tv_reviews_count = findViewById(R.id.tv_reviews_count);
        TextView tv_role = findViewById(R.id.tv_role);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        String username=db.fetchUserName(userId);
        String email=db.fetchUserEmail(userId);
        Cursor user = db.fetchUser(userId);
        String role=user.getString(user.getColumnIndexOrThrow("role"));
        //get user info

        //get info from other screens
        int Fav_count=db.getFavouritesCount(userId);
        int Watch_count=db.getWatchCount(userId);
        int Reviews_count=db.getReviewCount(userId);

        //Set info
        tv_user.setText(String.valueOf(username));
        tv_email.setText(email);
        tv_fav_count.setText(String.valueOf(Fav_count));
        tv_watch_later_count.setText(String.valueOf(Watch_count));
        tv_reviews_count.setText(String.valueOf(Reviews_count));
        tv_role.setText(role);
    }
}