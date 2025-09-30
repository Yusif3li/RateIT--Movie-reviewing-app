package com.example.moviesreviewapp;

public class Movies {
    private int id;
    private String title;
    private String genre;
    private int year;
    private int rating;
    private double avg_rating;
    private int no_of_ratings=0;
    private int sum_of_ratings=0;

    public Movies(String title, String genre, int year,int rating) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating=rating;
        sum_of_ratings=0;
        no_of_ratings=0;
        this.avg_rating= 0.0;
    }

    public Movies(int id, String title, String genre, int year) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getRating() { return rating; }
    public void addRating(int rating) {
        this.rating = rating;
        this.sum_of_ratings += rating;
        this.no_of_ratings++;
        this.avg_rating = (double) sum_of_ratings / no_of_ratings;
    }
    public double getAvgRating() { return avg_rating ;}



}
