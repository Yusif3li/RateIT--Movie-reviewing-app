package com.example.moviesreviewapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoviesDBHelper extends SQLiteOpenHelper {
    private static String dbName = "movieDatabase";
    SQLiteDatabase moviesDatabase;

    public MoviesDBHelper(Context context)
    {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(" +
                "id integer primary key autoincrement, " +
                "name text not null," +
                "email text not null, " +
                "password text not null, " +
                "gender text not null, " +
                "role text not null)");

        db.execSQL("create table movies(" +
                "id integer primary key autoincrement, " +
                "title text not null, " +
                "year text, " +
                "genre text, " +
                "posterUrl text, " +
                "trailerUrl text, " +
                "synopsis text, " +
                "avg_rating REAL, " +
                "no_of_ratings INTEGER DEFAULT 0, " +
                "sum_of_ratings INTEGER DEFAULT 0)");

        db.execSQL("create table reviews(" +
                "id integer primary key autoincrement, " +
                "movieId integer not null, " +
                "userId integer not null, " +
                "rating integer not null, " +
                "body text, " +
                "createdAt text not null," +
                "foreign key(movieId) references movies(id) on delete cascade," +
                "foreign key(userId) references users(id) on delete cascade)");

        db.execSQL("create table favourites(" +
                "movieId integer not null," +
                "userId integer not null," +
                "primary key (movieId, userId)," +
                "foreign key (userId) references users(id) on delete cascade," +
                "foreign key (movieId) references movies(id) on delete cascade)");

        db.execSQL("create table watch_later(" +
                "movieId integer not null," +
                "userId integer not null," +
                "primary key (movieId, userId)," +
                "foreign key (userId) references users(id) on delete cascade," +
                "foreign key (movieId) references movies(id) on delete cascade)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        db.execSQL("drop table if exists movies");
        db.execSQL("drop table if exists reviews");
        db.execSQL("drop table if exists favourites");
        db.execSQL("drop table if exists watch_later");
        onCreate(db);
    }


    public boolean insertNewUser(String name, String email, String password, String gender, String role)
    {
        moviesDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("gender", gender);
        values.put("role", role);

        long result = moviesDatabase.insert("users", null, values);

        return result != -1;
    }

    public boolean checkUserExists(String email)
    {
        moviesDatabase = getReadableDatabase();

        Cursor cursor = moviesDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public boolean checkUserLogin(String email, String password)
    {
        moviesDatabase = getReadableDatabase();

        Cursor cursor = moviesDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public int fetchUserId(String email)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("users", null, "email = ?", new String[]{String.valueOf(email)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        return userId;
    }
    public String fetchUserEmail(int id)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("users",  new String[]{"email"}, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
String email="";
        if (cursor != null) {
            if(cursor.moveToFirst()){
                email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            }
cursor.close();
        }
        moviesDatabase.close();
        return email;
    }


    public void insertNewMovie(String title, String year, String genre, String posterUrl, String trailerUrl, String synopsis, float avgRating, int numOfRatings, int sumOfRatings)
    {
        ContentValues row = new ContentValues();
        row.put("title", title);
        row.put("year", year);
        row.put("genre", genre);
        row.put("posterUrl", posterUrl);
        row.put("trailerUrl", trailerUrl);
        row.put("synopsis", synopsis);
        row.put("avg_rating", avgRating);
        row.put("no_of_ratings", numOfRatings);
        row.put("sum_of_ratings", sumOfRatings);

        moviesDatabase = getWritableDatabase();
        moviesDatabase.insert("movies", null, row);
        moviesDatabase.close();
    }
    public void insertNewReview(int movieId, int userId, int rating, String body, String createdAt)
    {
        ContentValues row = new ContentValues();
        row.put("movieId", movieId);
        row.put("userId", userId);
        row.put("rating", rating);
        row.put("body", body);
        row.put("createdAt", createdAt);

        moviesDatabase = getWritableDatabase();
        moviesDatabase.insert("reviews", null, row);
        moviesDatabase.close();
    }

    public Cursor fetchAllMovies()
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("movies", null, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }

    public Cursor fetchAllReviews(int movieId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("reviews", null, "movieId = ?", new String[]{String.valueOf(movieId)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }
    public void ChangeAverageRating(int rating,int movieId, int userId){
        Cursor review = fetchReview(movieId, userId);

        moviesDatabase = getWritableDatabase();
        if (review.moveToFirst())
        {
            int ratingOld = review.getInt(review.getColumnIndexOrThrow("rating"));
            moviesDatabase.execSQL("UPDATE movies SET sum_of_ratings = sum_of_ratings - ? WHERE id = ?", new Object[]{ratingOld, movieId});
            moviesDatabase.execSQL("UPDATE movies SET sum_of_ratings = sum_of_ratings + ? WHERE id = ?", new Object[]{rating, movieId});
        }
        else
        {
            moviesDatabase.execSQL("UPDATE movies SET sum_of_ratings = sum_of_ratings + ?, no_of_ratings = no_of_ratings + 1 WHERE id = ?", new Object[]{rating, movieId});
        }
        moviesDatabase.execSQL("UPDATE movies SET avg_rating = CAST(sum_of_ratings AS REAL) / no_of_ratings WHERE id = ?", new Object[]{movieId});

        moviesDatabase.close();
    }

    public Cursor fetchMovie_by_id(int movieId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("movies", null, "id = ?", new String[]{String.valueOf(movieId)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }
    public Cursor fetchMovie_by_name(String movieName)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.rawQuery       (
                "SELECT * FROM movies WHERE title LIKE ?",
                new String[]{"%" + movieName + "%"}    );

        if (cursor != null)
            cursor.moveToFirst();


        return cursor;
    }

    public Cursor fetchMovies_by_genre(String genre)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor;
        if(genre.equals("All")){
            cursor=moviesDatabase.query("movies", null, null, null, null, null, null);

        }
         else {
            cursor = moviesDatabase.rawQuery(
                    "SELECT * FROM movies WHERE genre LIKE ?",
                    new String[]{"%" + genre + "%"});
        }

        if (cursor != null)
            cursor.moveToFirst();


        return cursor;
    }
    public Cursor fetchUser(int userId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("users", null, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }

    public String fetchUserName(int id)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("users",  new String[]{"name"}, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        String name="";
        if (cursor != null) {
            if(cursor.moveToFirst()){
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
            cursor.close();
        }
        moviesDatabase.close();
        return name;
    }

    //
    public Cursor fetchReview(int movieId, int userId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("reviews", null, "movieId = ? and userId = ?", new String[]{String.valueOf(movieId), String.valueOf(userId)}, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }

    public void updateReview(int reviewId, int rating, String body)
    {
        moviesDatabase = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("rating", rating);
        row.put("body", body);
        moviesDatabase.update("reviews", row, "id = ?", new String[]{String.valueOf(reviewId)});

        moviesDatabase.close();
    }



    public int getReviewCount(int userId) {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.rawQuery(
                "SELECT COUNT(*) FROM reviews WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        moviesDatabase.close();
        return count;
    }



    //


    public boolean insertFavourite(int userId, int movieId)
    {
        moviesDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("movieId", movieId);

        long result = moviesDatabase.insert("favourites", null, values);

        return result != -1;
    }

    public Cursor fetchFavourites(int userId)
    {
        moviesDatabase = getReadableDatabase();
        String query = "SELECT m.* FROM movies m " +
                "INNER JOIN favourites f ON m.id = f.movieId " +
                "WHERE f.userId = ?";

        Cursor cursor = moviesDatabase.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }
    public boolean fetchFavExist(int userId,int movieId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("favourites", null, "userId = ? and movieId = ?", new String[]{String.valueOf(userId), String.valueOf(movieId)}, null, null, null);
        boolean ret = cursor.getCount()>0;
        moviesDatabase.close();

        return ret;
    }
    public Cursor fetchFav_by_genre(int userId,String genre)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor;
        if(genre.equals("All")){
String all_query="SELECT m.* from movies m INNER JOIN favourites f on m.id=f.movieId where userId=? ";
cursor=moviesDatabase.rawQuery(all_query,new String[]{String.valueOf(userId)});

        }
        else {
            String query="SELECT m.* from movies m INNER JOIN favourites f on m.id=f.movieId where userId=? and genre LIKE ? ";
            cursor=moviesDatabase.rawQuery(query,new String[]{String.valueOf(userId),"%" +genre+ "%"});
        }

        if (cursor != null)
            cursor.moveToFirst();


        return cursor;
    }
    public boolean fetchWatchExist(int userId,int movieId)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.query("watch_later", null, "userId = ? and movieId = ?", new String[]{String.valueOf(userId), String.valueOf(movieId)}, null, null, null);
        boolean ret = cursor.getCount()>0;
        moviesDatabase.close();

        return ret;
    }
    public boolean deleteFavourite(int userId, int movieId)
    {
        moviesDatabase = getWritableDatabase();
        int ret = moviesDatabase.delete("favourites", "userId = ? and movieId = ?", new String[]{String.valueOf(userId), String.valueOf(movieId)});
        moviesDatabase.close();

        return ret != 0;
    }

    public int getFavouritesCount(int userId) {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.rawQuery(
                "SELECT COUNT(*) FROM favourites WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        moviesDatabase.close();
        return count;
    }



    //

    public boolean insertWatchLater(int userId, int movieId)
    {
        moviesDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("movieId", movieId);

        long result = moviesDatabase.insert("watch_later", null, values);

        return result != -1;
    }

    public Cursor fetchWatchLater(int userId)
    {
        moviesDatabase = getReadableDatabase();


        String query = "SELECT m.* FROM movies m " +
                "INNER JOIN watch_later w ON m.id = w.movieId " +
                "WHERE w.userId = ?";

        Cursor cursor = moviesDatabase.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null)
            cursor.moveToFirst();
        moviesDatabase.close();

        return cursor;
    }


    public boolean deleteWatchLater(int userId, int movieId)
    {
        moviesDatabase = getWritableDatabase();
        int ret = moviesDatabase.delete("watch_later", "userId = ? and movieId = ?", new String[]{String.valueOf(userId), String.valueOf(movieId)});
        moviesDatabase.close();

        return ret != 0;
    }
    public Cursor fetchWatch_by_genre(int userId,String genre)
    {
        moviesDatabase = getReadableDatabase();
        Cursor cursor;
        if(genre.equals("All")){
            String all_query="SELECT m.* from movies m INNER JOIN watch_later w on m.id=w.movieId where userId=? ";
            cursor=moviesDatabase.rawQuery(all_query,new String[]{String.valueOf(userId)});

        }
        else {
            String query="SELECT m.* from movies m INNER JOIN watch_later w on m.id=w.movieId where userId=? and genre=? ";
            cursor=moviesDatabase.rawQuery(query,new String[]{String.valueOf(userId),"%" +genre+"%"});
        }

        if (cursor != null)
            cursor.moveToFirst();


        return cursor;
    }
    public int getWatchCount(int userId) {
        moviesDatabase = getReadableDatabase();
        Cursor cursor = moviesDatabase.rawQuery(
                "SELECT COUNT(*) FROM watch_later WHERE userId = ?",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        moviesDatabase.close();
        return count;
    }



}
