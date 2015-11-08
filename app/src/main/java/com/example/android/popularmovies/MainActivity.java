/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * MainActivity holds the MainActivityFragment in handset layout and MainActivityFragment as well
 * as the MovieDetailActivityFragment in tablet mode.
 * Implements OnItemSelectedListener so it can me informed about changes in the MainActivityFragment
 */

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart () {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xmyl.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void newMovieSelected(Movie movie, boolean first_init) {
        android.support.v4.app.Fragment f = getSupportFragmentManager().findFragmentById(R.id.detailFragment);
        if(f != null && f instanceof MovieDetailActivityFragment && f.isAdded()){
            // Fragment is in the layout (tablet layout),
            // so tell the fragment to update
            MovieDetailActivityFragment movieDetailActivityFragment = (MovieDetailActivityFragment) f;
            movieDetailActivityFragment.updateContent(movie);
        } else {
            // Fragment is not in the layout (handset layout),
            // so start Activity
            // and pass it the info about the selected item

            //first init is only necessary on tablets on handsets it would simulate a click which is wrong
            if(!first_init) {
                Intent showMovieDetailIntent = new Intent(this, MovieDetailActivity.class);
                showMovieDetailIntent.putExtra(getString(R.string.movie_intent_extra_key), movie);
                startActivity(showMovieDetailIntent);
            }
        }
    }
}
