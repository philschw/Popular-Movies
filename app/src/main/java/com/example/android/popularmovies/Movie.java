/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds all the data for a single movie and implements "Parcelable" so it can be sent to
 * another activity through an intent
 */


public class Movie implements Parcelable {
    private String title = "";
    private String releaseDate = "";
    private String posterPath = "";
    private String voteAverage = "";
    private String plot = "";

    public Movie ()
    {

    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
        dest.writeString(plot);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        plot = in.readString();
    }
}
