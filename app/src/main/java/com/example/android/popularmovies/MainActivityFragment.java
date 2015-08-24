/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 *  This fragment contains a GridView which shows the movie posters received from a request to
 *  The Movie Database.
 *  It contains methods to parse the received JSON data and creates a list of movie objects
 *  containing the respective data
 *  It creates an AsyncTask to retrieve the data in the background.
 *  It does a callback to the activity if the user clicks on one of the grid items
 */
public class MainActivityFragment extends Fragment {
    static final String STATE_SCROLL = "scrollState";
    static int scrollIndex;
    static int lastClickedItem = 0;
    OnItemSelectedListener mCallback;

    //private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieGridViewAdapter mPopularMoviesAdapter;
    private static GridView mMovieGridView;
    private ArrayList<Movie> movieArrayList = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_SCROLL, mMovieGridView.getFirstVisiblePosition());
        scrollIndex = mMovieGridView.getFirstVisiblePosition();

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume(){
        mMovieGridView.setSelection(scrollIndex);
        mMovieGridView.invalidate();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        ArrayList<Movie> list = new ArrayList<>();

        mPopularMoviesAdapter = new MovieGridViewAdapter(
                getActivity(), // The current context (this activity)
                R.id.grid_item_movies_imageView,
                list);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.moviesGridView);
        mMovieGridView = gridView;
        gridView.setAdapter(mPopularMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastClickedItem = position;
                itemClicked(position);
            }
        });

        // Check whether a previously destroyed instance is being recreated
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            gridView.setSelection(savedInstanceState.getInt(STATE_SCROLL));
            gridView.invalidate();
        }

        return rootView;
    }

    private void itemClicked(int position)
    {
        if(movieArrayList != null) {
            if(movieArrayList.size() > 0) {
                mCallback.newMovieSelected(movieArrayList.get(position));
            }
        }
    }


    private void updateMovies() {
        new FetchMovieData().execute(getString(R.string.api_key));
    }

    private void updateGrid() {
        GridView gridView = (GridView) getActivity().findViewById(R.id.moviesGridView);
        gridView.setAdapter(mPopularMoviesAdapter);
        mMovieGridView.setSelection(scrollIndex);
        gridView.invalidate();
        if (lastClickedItem == 0) {
            itemClicked(lastClickedItem);
        }

    }


    public class FetchMovieData extends AsyncTask<String,Integer,ArrayList<Movie>> {

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if(result != null) {
                mPopularMoviesAdapter.clear();
                for(Movie singleMovie : result) {
                    //pass the string for the poster to the adapter
                    mPopularMoviesAdapter.add(singleMovie);
                    //Log.v(LOG_TAG, "GETCOUNT: " + mPopularMoviesAdapter.getCount());
                }
                updateGrid();
            }

        }

        @Override
        protected ArrayList<Movie> doInBackground(String... apiKey) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort_order = prefs.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                // Construct the URL for the MovieDB query
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, sort_order)
                        .appendQueryParameter(API_KEY, apiKey[0])
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String completeString = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // builder for debugging.
                    completeString += line + "\n";
                }
                builder.append(completeString);

                if (builder.length() != 0) {
                    moviesJsonStr = builder.toString();
                    movieArrayList = getMovieDataFromJson(moviesJsonStr);
                }

            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            return movieArrayList;
        }

        protected void onProgressUpdate(Integer... progress) {

        }


        /**
         * Take the String representing the complete movie in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_ORIG_TITLE = "original_title";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_PLOT_SYNOPSIS = "overview";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);


            ArrayList<Movie> movieArrayList = new ArrayList<>();//new String[moviesArray.length()];
            for (int ii = 0; ii < moviesArray.length(); ii++) {
                String original_title;
                String release_date;
                String movie_poster;
                String vote_average;
                String plot_synopsis;
                Movie tmpMovie = new Movie();

                // Get the JSON object representing one movie
                JSONObject movieJSONObject = moviesArray.getJSONObject(ii);

                // get original_title
                original_title = movieJSONObject.getString(TMDB_ORIG_TITLE);
                release_date = movieJSONObject.getString(TMDB_RELEASE_DATE);
                movie_poster = movieJSONObject.getString(TMDB_POSTER_PATH);
                vote_average = movieJSONObject.getString(TMDB_VOTE_AVERAGE);
                plot_synopsis = movieJSONObject.getString(TMDB_PLOT_SYNOPSIS);

                tmpMovie.setPlot(plot_synopsis);
                tmpMovie.setPosterPath(getString(R.string.poster_base_path) + movie_poster);
                tmpMovie.setReleaseDate(release_date);
                tmpMovie.setTitle(original_title);
                tmpMovie.setVoteAverage(vote_average);


                movieArrayList.add(tmpMovie);
            }

            return movieArrayList;

        }
    }
}

interface OnItemSelectedListener {
    void newMovieSelected(Movie movie);
}
