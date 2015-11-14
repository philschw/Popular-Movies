/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Creates the movie detail layout. Has a public update method for the activity to update it's data
 * This class preserves it's data when the orientation changes or when it gets paused
 */
public class MovieDetailActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    static final String STATE_RELEASE_DATE = "releaseDate";
    static final String STATE_PLOT = "plot";
    static final String STATE_VOTE_AVERAGE = "voteAverage";
    static final String STATE_TITLE = "title";
    static final String STATE_POSTER_PATH = "posterPath";
    static final String STATE_FAVORITE = "favorite";
    static final String STATE_PICTURE_PATH = "picturePath";
    static final String STATE_TRAILER_URL = "trailerURL";


    private Movie mMovie;
    private View mRootView;
    private String mTrailerLink;

    public MovieDetailActivityFragment() {
    }

    public void updateContent (Movie movie)
    {
        mMovie = movie;

        ((MovieDetailActivity) getActivity()).setShareURL(mMovie.getTrailer_url());

        if(mRootView != null) {
            if (mMovie.getFavorite().equals("true") || checkIfInFavorites()) {
                ToggleButton toggleButton = (ToggleButton) mRootView.findViewById(R.id.toggleButtonFavorite);
                toggleButton.setChecked(true);
            }

        }

        updateViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMovie != null) {
            if (mMovie.getFavorite().equals("true") || checkIfInFavorites()) {
                ToggleButton toggleButton = (ToggleButton) mRootView.findViewById(R.id.toggleButtonFavorite);
                toggleButton.setChecked(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        if(mMovie != null) {
            savedInstanceState.putString(STATE_RELEASE_DATE, mMovie.getReleaseDate());
            savedInstanceState.putString(STATE_PLOT, mMovie.getPlot());
            savedInstanceState.putString(STATE_VOTE_AVERAGE, mMovie.getVoteAverage());
            savedInstanceState.putString(STATE_TITLE, mMovie.getTitle());
            savedInstanceState.putString(STATE_POSTER_PATH, mMovie.getPosterPath());
            if(checkIfInFavorites()) { mMovie.setFavorite("true"); };
            savedInstanceState.putString(STATE_FAVORITE, mMovie.getFavorite());
            savedInstanceState.putString(STATE_PICTURE_PATH, mMovie.getPicturepath());
            savedInstanceState.putString(STATE_TRAILER_URL, mMovie.getTrailer_url());
        } else {
            //nothing to save, fill with empty strings
            savedInstanceState.putString(STATE_RELEASE_DATE, "");
            savedInstanceState.putString(STATE_PLOT, "");
            savedInstanceState.putString(STATE_VOTE_AVERAGE, "");
            savedInstanceState.putString(STATE_TITLE, "");
            savedInstanceState.putString(STATE_POSTER_PATH, "");
            savedInstanceState.putString(STATE_FAVORITE, "");
            savedInstanceState.putString(STATE_PICTURE_PATH, "");
            savedInstanceState.putString(STATE_TRAILER_URL, "");
        }

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
            mMovie.setFavorite(savedInstanceState.getString(STATE_FAVORITE));
            mMovie.setPicturepath(savedInstanceState.getString(STATE_PICTURE_PATH));
            mMovie.setTrailer_url(savedInstanceState.getString(STATE_TRAILER_URL));

            updateViews();

        }

        //TODO: not working
        getActivity().setTitle(getString(R.string.title_detail_movie_activity));

        //add listener to the toggle image button to add favorite in the db
        mRootView.findViewById(R.id.toggleButtonFavorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Test
                ContentValues values = new ContentValues();

                values.put(MovieContract.MovieEntry.COLUMN_PLOT, mMovie.getPlot());
                values.put(MovieContract.MovieEntry.COLUMN_ID, mMovie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_PLAY_TIME, mMovie.getPlayingtime());
                values.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getVoteAverage());
                values.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
                values.put(MovieContract.MovieEntry.COLUMN_YEAR, mMovie.getReleaseDate());

                if(checkIfInFavorites()) {
                    //Toast.makeText(getActivity().getBaseContext(), String.valueOf("In Favorites!"), Toast.LENGTH_LONG).show();

                    int nrDeletedLines = getActivity().getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_ID + "=?",
                            new String[] {mMovie.getId()});
                } else {
                    FileHelper fh = new FileHelper();

                    try {
                        mMovie.setPicturepath(
                                fh.saveToInternalSorage(
                                        getActivity().getApplicationContext(),
                                        ((BitmapDrawable) ((ImageView) mRootView.findViewById(R.id.detail_movies_imageView)).getDrawable()).getBitmap(),
                                        mMovie.getId() + ".jpg"));

                        mMovie.setFavorite("true");
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity().getBaseContext(), String.valueOf("Wasn't able to save bitmap"), Toast.LENGTH_LONG).show();
                        Log.e(LOG_TAG, e.toString());
                    }


                    values.put(MovieContract.MovieEntry.COLUMN_PICTURE_PATH, mMovie.getPicturepath());

                    Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                }
            }
        });

        mRootView.findViewById(R.id.imageButtonTrailers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setData(Uri.parse(mTrailerLink));
                startActivity(intent);
            }
        });

        return mRootView;
    }

    private boolean checkIfInFavorites()
    {
        if(mMovie != null) {
            String selection = MovieContract.MovieEntry.COLUMN_ID + "='" + mMovie.getId() + "'";

            String[] projection = {
                    MovieContract.MovieEntry.COLUMN_ID
            };

            Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, projection, selection, null, null);

            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } else {
            return false;
        }
    }

    private void updateViews () throws IllegalArgumentException
    {
        FetchMovieData fetchMovieData = new FetchMovieData();
        fetchMovieData.execute(new String[]{getString(R.string.api_key), "135397"});

        if (mMovie == null) {return;} //if movie null there's nothing to show

        if(!mMovie.getFavorite().equals("true")) {
            // if poster path is empty don't try to load image
            if (!mMovie.getPosterPath().equals("")) {
                try {
                    Picasso.with(getActivity())
                            .load(mMovie.getPosterPath())
                            .into(((ImageView) mRootView.findViewById(R.id.detail_movies_imageView)));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        } else {
            // if picture path is empty don't try to load image
            if (!mMovie.getPicturepath().equals("")) {
                try {
                    Picasso.with(getActivity())
                            .load(new File(mMovie.getPicturepath()))
                            .into(((ImageView) mRootView.findViewById(R.id.detail_movies_imageView)));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if(mMovie.getReleaseDate().length() > 4){
            ((TextView) mRootView.findViewById(R.id.textViewDate)).setText(mMovie.getReleaseDate().substring(0,4));
        } else{
            ((TextView) mRootView.findViewById(R.id.textViewDate)).setText(mMovie.getReleaseDate());
        }

        ((TextView) mRootView.findViewById(R.id.textViewPlot)).setText(mMovie.getPlot());

        if(mMovie.getVoteAverage().length() > 0){
            ((TextView) mRootView.findViewById(R.id.textViewRating)).setText(mMovie.getVoteAverage() + "/10");
        } else{
            ((TextView) mRootView.findViewById(R.id.textViewRating)).setText("na/10");
        }
        ((TextView) mRootView.findViewById(R.id.textViewTitle)).setText(mMovie.getTitle());

        ((MovieDetailActivity) getActivity()).setShareURL(mMovie.getTrailer_url());
    }

    public class FetchMovieData extends AsyncTask<String,Integer,ArrayList<String[]>> {

        @Override
        protected void onPostExecute(ArrayList<String[]> result) {
            if(result != null) {
                ((TextView) mRootView.findViewById(R.id.textViewReview)).setText(result.get(0)[0]);
                mTrailerLink = result.get(1)[0];
                ((TextView) mRootView.findViewById(R.id.textViewTrailer)).setText("Trailer 1");
                mMovie.setTrailer_url(mTrailerLink);
                ((MovieDetailActivity) getActivity()).setShareURL(mMovie.getTrailer_url());
            }
        }

        //Argument 0 = API Key, Argument 1 = Movie ID
        @Override
        protected ArrayList<String[]> doInBackground(String... arguments) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.


            //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //String sort_order = prefs.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_default));

            // Will contain the raw JSON response as a string.
            ArrayList<String[]> movieDetails = new ArrayList<>();
            String movieDetailJson;


            // Construct the URL for the MovieDB query
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";
            final String REVIEWS_PATH = "reviews";
            final String VIDEOS_PATH = "videos";

            //build trailer uri
            Uri trailerUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(arguments[1]) //add path to movie ID
                    .appendPath(VIDEOS_PATH) //add path to videos
                    .appendQueryParameter(API_KEY, arguments[0])
                    .build();

            //build review uri
            Uri reviewUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(arguments[1]) //add path to movie ID
                    .appendPath(REVIEWS_PATH) //add path to reviews
                    .appendQueryParameter(API_KEY, arguments[0])
                    .build();

            try {
                movieDetailJson = fetchStringFromMovieDB(reviewUri);
                movieDetails.add(getMovieReviewFromJson(movieDetailJson));

                movieDetailJson = fetchStringFromMovieDB(trailerUri);
                movieDetails.add(getMovieTrailersFromJson(movieDetailJson));
            } catch (org.json.JSONException e) {
                Log.e("MainActivityFragment", "JSON Error ", e);
            }

            return movieDetails;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        private String fetchStringFromMovieDB(Uri uri) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String fetchedString = "";

            try {
                URL url = new URL(uri.toString());

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
                    fetchedString = builder.toString();
                    //movieArrayList = getMovieDataFromJson(moviesJsonStr);
                }
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
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

            return fetchedString;
        }

        /**
         * Take the String representing the complete movie in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMovieReviewFromJson(String movieReviewJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_REVIEW_AUTHOR = "author";
            final String TMDB_REVIEW_CONTENT = "content";


            JSONObject moviesReviewJson = new JSONObject(movieReviewJsonStr);
            JSONArray moviesReviewArray = moviesReviewJson.getJSONArray(TMDB_RESULTS);

            //Todo: fix hard coded constant
            String[] movieReviewList = new String[3];//new String[moviesArray.length()];

            for (int ii = 0; ii < moviesReviewArray.length(); ii++) {
                String review_author;
                String review_content;

                // Get the JSON object representing one movie
                JSONObject movieJSONObject = moviesReviewArray.getJSONObject(ii);

                // get original_title
                review_author = movieJSONObject.getString(TMDB_REVIEW_AUTHOR);
                review_content = movieJSONObject.getString(TMDB_REVIEW_CONTENT);

                movieReviewList[ii] = review_author + ":" + "\n\n" + review_content;

            }

            return movieReviewList;

        }

        /**
         * Take the String representing the complete movie in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMovieTrailersFromJson(String movieTrailerJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_TRAILER_SITE = "site";
            final String TMDB_SITE_URL_KEY = "key";

            final String BASE_URL = "https://www.youtube.com";
            final String YOUTUBE_WATCH = "watch";
            final String TRAILER_SITE_YOUTUBE = "YouTube";


            JSONObject moviesTrailerJson = new JSONObject(movieTrailerJsonStr);
            JSONArray moviesTrailerArray = moviesTrailerJson.getJSONArray(TMDB_RESULTS);

            //Todo: fix hard coded constant
            String[] movieTrailerList = new String[3];//new String[moviesArray.length()];

            for (int ii = 0; ii < moviesTrailerArray.length(); ii++) {
                String trailer_site;
                String trailer_key;

                // Get the JSON object representing one movie
                JSONObject movieJSONObject = moviesTrailerArray.getJSONObject(ii);

                // get original_title
                trailer_site = movieJSONObject.getString(TMDB_TRAILER_SITE);
                trailer_key = movieJSONObject.getString(TMDB_SITE_URL_KEY);

                if(trailer_site.equalsIgnoreCase(TRAILER_SITE_YOUTUBE)) {
                    Uri trailerUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(YOUTUBE_WATCH, trailer_key)
                            .build();

                    movieTrailerList[ii] = trailerUri.toString();
                }
            }

            return movieTrailerList;

        }
    }
}
