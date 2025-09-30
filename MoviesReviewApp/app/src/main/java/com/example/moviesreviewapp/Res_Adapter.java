package com.example.moviesreviewapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;


public class Res_Adapter extends RecyclerView.Adapter<Res_Adapter.ViewHolder> {
   private Cursor cursor;
   private Context context;
   private MoviesDBHelper dbHelper;
public Res_Adapter(Cursor cursor,Context context){
    this.cursor=cursor;
    this.context=context;
}
//
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
//
    @NonNull
    @Override
    public Res_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        return new ViewHolder(view );
    }
//
    @Override
    public void onBindViewHolder(@NonNull Res_Adapter.ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        //get
        SharedPreferences prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        int movieID=cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String movieName = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String AVG_rating=cursor.getString(cursor.getColumnIndexOrThrow("avg_rating"));
        int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
        String posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("posterUrl"));
        String drawableName = cursor.getString(cursor.getColumnIndexOrThrow("trailerUrl"));
        int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());

        //set
        holder.tv_title.setText(movieName +" ("+ year + ")");
        holder.tv_rating.setText("Average Rating: "+String.valueOf(AVG_rating));
        Glide.with(context)
                .load(resId)
                .override(Target.SIZE_ORIGINAL)
                .placeholder(android.R.drawable.ic_menu_revert)
                .error(android.R.drawable.btn_dialog)
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMovie.class);

            intent.putExtra("movieId", movieID);
            //##############
            intent.putExtra("userId", userId);
            //##############
            context.startActivity(intent);
        });

    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        TextView tv_rating;
        ImageView poster;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            poster = view.findViewById(R.id.poster);
            tv_rating=view.findViewById(R.id.rating);

        }
    }
    public void ChangeCurrentCursor(Cursor newCursor){
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    this.cursor=newCursor;
    notifyDataSetChanged();
    }
}
