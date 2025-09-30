package com.example.moviesreviewapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WatchActivity extends AppCompatActivity {
    MoviesDBHelper db;
    private Res_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_watch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // RecyclerView
        final RecyclerView RV = findViewById(R.id.RV_LIST_Watch);

        RV.setLayoutManager(new LinearLayoutManager(this));

//        deleteDatabase("movieDatabase");

        db = new MoviesDBHelper(this);

        //get user id to get favourites
        SharedPreferences prefs=getSharedPreferences("user",MODE_PRIVATE);
        int userID= prefs.getInt("userId",-1);
        Cursor cursor = db.fetchWatchLater(userID);


        adapter = new Res_Adapter(cursor, this); // field assigned
        RV.setAdapter(adapter);





        //filter button

        Button filter_button = findViewById(R.id.filter_button_watch);

        filter_button.setOnClickListener(v-> {
            PopupMenu popup = new PopupMenu(WatchActivity.this, v);
            popup.getMenu().add(R.string.Genre1);
            popup.getMenu().add(R.string.Genre2);
            popup.getMenu().add(R.string.Genre3);
            popup.getMenu().add(R.string.Genre4);
            popup.getMenu().add(R.string.Genre5);

            popup.setOnMenuItemClickListener(item -> {
                String selectedGenre = item.getTitle().toString();
                Cursor filteredCursor = db.fetchFav_by_genre(userID,selectedGenre);

                adapter.ChangeCurrentCursor(filteredCursor);

                return true;
            });
            popup.show();
        });
    }




    }

