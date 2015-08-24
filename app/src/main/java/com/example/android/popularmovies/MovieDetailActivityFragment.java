/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Creates the movie detail layout. Has a public update method for the activity to update it's data
 * This class preserves it's data when the orientation changes or when it gets paused
 */
public class MovieDetailActivityFragment extends Fragment {
    static final String STATE_RELEASE_DATE = "releaseDate";
    static final String STATE_PLOT = "plot";
    static final String STATE_VOTE_AVERAGE = "voteAverage";
    static final String STATE_TITLE = "title";
    static final String STATE_POSTER_PATH = "posterPath";

    private Movie mMovie;
    private View mRootView;

    public MovieDetailActivityFragment() {
    }

    public void updateContent (Movie movie)
    {
        mMovie = movie;
        updateViews();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_RELEASE_DATE, mMovie.getReleaseDate());
        savedInstanceState.putString(STATE_PLOT, mMovie.getPlot());
        savedInstanceState.putString(STATE_VOTE_AVERAGE, mMovie.getVoteAverage());
        savedInstanceState.putString(STATE_TITLE, mMovie.getTitle());
        savedInstanceState.putString(STATE_POSTER_PATH, mMovie.getPosterPath());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        //if this has been created by an intent with extras update the data
        if(intent != null && intent.hasExtra(getString(R.string.movie_intent_extra_key))) {
            mMovie = intent.getParcelableExtra(getString(R.string.movie_intent_extra_key));

            updateViews();
        }

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            if(mMovie == null) {
                mMovie = new Movie();
            }
            // Restore value of members from saved state
            mMovie.setReleaseDate(savedInstanceState.getString(STATE_RELEASE_DATE));
            mMovie.setPlot(savedInstanceState.getString(STATE_PLOT));
            mMovie.setVoteAverage(savedInstanceState.getString(STATE_VOTE_AVERAGE));
            mMovie.setTitle(savedInstanceState.getString(STATE_TITLE));
            mMovie.setPosterPath(savedInstanceState.getString(STATE_POSTER_PATH));

            updateViews();

        }

        //TODO: not working
        getActivity().setTitle(getString(R.string.title_detail_movie_activity));

        return mRootView;
    }

    private void updateViews ()
    {
        Picasso.with(getActivity())
                .load(mMovie.getPosterPath())
                .into(((ImageView) mRootView.findViewById(R.id.detail_movies_imageView)));

        ((TextView) mRootView.findViewById(R.id.textViewDate)).setText(mMovie.getReleaseDate().substring(0,4));
        ((TextView) mRootView.findViewById(R.id.textViewPlot)).setText(mMovie.getPlot());
        ((TextView) mRootView.findViewById(R.id.textViewRating)).setText(mMovie.getVoteAverage() + "/10");
        ((TextView) mRootView.findViewById(R.id.textViewTitle)).setText(mMovie.getTitle());
    }
}
