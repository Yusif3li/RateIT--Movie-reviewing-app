package com.example.moviesreviewapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    MoviesDBHelper db;
    private Res_Adapter adapter;

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        SubMenu subMenu = menu.addSubMenu(Menu.NONE, R.id.submenu, 5, R.string.submenu);
        subMenu.add(Menu.NONE, R.id.suboption1, 1, R.string.suboption1);
        subMenu.add(Menu.NONE, R.id.suboption2, 2, R.string.suboption2);
        subMenu.add(Menu.NONE, R.id.suboption3, 3, R.string.suboption3);

        // Search part
        MenuItem search_item = menu.findItem(R.id.app_bar_search);
        SearchView search_view = (SearchView) search_item.getActionView();
        assert search_view != null;
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
               Cursor cursor =db.fetchMovie_by_name(newText);
                 adapter.ChangeCurrentCursor(cursor);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Cursor cursor = db.fetchMovie_by_name(query);
                adapter.ChangeCurrentCursor(cursor);
                search_view.clearFocus();
                return true;
            }
        });


        return true;
    }

    // Select Item from menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.home_m) {
            Toast.makeText(this, "You are already in this screen !", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.fav_m) {
            startActivity(new Intent(this, FavouritesActivity.class));

        } else if (id == R.id.prof_m) {
            startActivity(new Intent(this, ProfileActivity.class));

        } else if (id == R.id.logout_m) {
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            startActivity(new Intent(this, Splash_Logout.class));
            finish();

        } else if (id == R.id.suboption1) {

            startActivity(new Intent(this, WatchActivity.class));

        }
        else if (id == R.id.suboption2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveDarkModePref(true);

        }
        else if (id == R.id.suboption3) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveDarkModePref(false);

        }
        else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    // onCreate MUST be inside the class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isDarkModeOn()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.main_screen);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });



         //filter button

        Button filter_button = findViewById(R.id.filter_button);

        filter_button.setOnClickListener(v-> {
                    PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenu().add(R.string.Genre1);
            popup.getMenu().add(R.string.Genre2);
            popup.getMenu().add(R.string.Genre3);
            popup.getMenu().add(R.string.Genre4);
            popup.getMenu().add(R.string.Genre5);


            popup.setOnMenuItemClickListener(item -> {
                        String selectedGenre = item.getTitle().toString();
                        Cursor filteredCursor = db.fetchMovies_by_genre(selectedGenre);

                        adapter.ChangeCurrentCursor(filteredCursor);

                        return true;
                    });
            popup.show();
                });
       //

        // RecyclerView
        final RecyclerView RV = findViewById(R.id.RV_LIST);

        RV.setLayoutManager(new LinearLayoutManager(this));

//        deleteDatabase("movieDatabase");

         db = new MoviesDBHelper(this);
        Cursor checkCursor = db.fetchAllMovies();


        if (checkCursor.getCount() == 0)
        {
            db.insertNewUser("woof", "woof@gmail.com", "1", "Male", "User");

            String inceptionPosterUrl = "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_FMjpg_UY1037_.jpg";
            db.insertNewMovie("Inception", "2010", "Sci-Fi", inceptionPosterUrl, "poster_inception", "The Inception meow", 0.0F, 0, 0);
            String darkKnightPosterUrl = "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_FMjpg_UY2048_.jpg";
            db.insertNewMovie("The Dark Knight", "2008", "Action", darkKnightPosterUrl, "poster_batman", "The Dark meow", 0.0F, 0, 0);
            String interstellarPosterUrl = "https://m.media-amazon.com/images/M/MV5BYzdjMDAxZGItMjI2My00ODA1LTlkNzItOWFjMDU5ZDJlYWY3XkEyXkFqcGc@._V1_FMjpg_UY3600_.jpg";
            db.insertNewMovie("Interstellar", "2014", "Sci-Fi", interstellarPosterUrl, "poster_interstellar", "The interstellar meow", 0.0F, 0, 0);

            String synopsis = "The Godfather is a 1972 American epic gangster film directed by Francis Ford Coppola, who co-wrote " +
                    "the screenplay with Mario Puzo based on Puzo\'s best-selling 1969 novel. The film features an ensemble cast that " +
                    "includes Marlon Brando, Al Pacino, James Caan, Richard Castellano, Robert Duvall, Sterling Hayden, John Marley, Richard " +
                    "Conte and Diane Keaton. It is the first installment in The Godfather trilogy, which chronicles the Corleone family under patriarch " +
                    "Vito Corleone (Brando) and the transformation of his youngest son, Michael Corleone (Pacino), from reluctant family outsider to ruthless mafia boss.";
            String godfatherPosterUrl = "https://m.media-amazon.com/images/M/MV5BNGEwYjgwOGQtYjg5ZS00Njc1LTk2ZGEtM2QwZWQ2NjdhZTE5XkEyXkFqcGc@._V1_FMjpg_UY1982_.jpg";
            db.insertNewMovie("The Godfather", "1972", "Crime", godfatherPosterUrl, "poster_godfather", synopsis, 0.0F, 0, 0);
        }

        checkCursor.close();

        Cursor my_cursor = db.fetchAllMovies();

        adapter = new Res_Adapter(my_cursor, this); // field assigned
        RV.setAdapter(adapter);
//



Button refresh_button=findViewById(R.id.refresh_button);

        refresh_button.setOnClickListener(v -> {
            Cursor newCursor = db.fetchAllMovies(); // fetch updated data
            adapter.ChangeCurrentCursor(newCursor); // refresh adapter
            Toast.makeText(this, "Movies refreshed!", Toast.LENGTH_SHORT).show();
        });





    }

    // Helper functions
    private Boolean isDarkModeOn() {
        SharedPreferences switch_pref = getSharedPreferences("switch_mode", MODE_PRIVATE);
        boolean val = switch_pref.getBoolean("switch_mode", false);

        return val;
    }

    private void saveDarkModePref(boolean isOn) {
        SharedPreferences switch_pref = getSharedPreferences("switch_mode", MODE_PRIVATE);
        SharedPreferences.Editor editor = switch_pref.edit();
        editor.putBoolean("switch_mode", isOn);
        editor.apply();

        recreate();
    }

    private void show_popup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            return false;
        });

    }
}
