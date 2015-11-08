/**
 * Copyright (C) 2015 Philip Schwander
 * Popular Movies project for Udacity Android Nanodegree
 */

package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom grid view adaptor, uses Picasso to fill the grid view with the movie posters
 */
public class MovieGridViewAdapter extends BaseAdapter {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final Context mContext;
    private final List<Movie> movieArrayList = new ArrayList<>();
    private int mViewId = 0;
    private final String STRING_TRUE = "true";

    //private final LayoutInflater inflater;
    //private int resource = 0;

    public MovieGridViewAdapter(Context context, int view, ArrayList<Movie> list)
    {
        this.mContext = context;
        this.mViewId = view;
        movieArrayList.addAll(list);
    }

    public void add(Movie movie)
    {
        movieArrayList.add(movie);
    }

    public void clear()
    {
        movieArrayList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.grid_item_movies, parent, false);

            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon = (ImageView) convertView.findViewById(mViewId);
        viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.grid_item_movies_textView);
        viewHolder.tvTitle.setText(movieArrayList.get(position).getTitle());

        if (STRING_TRUE.equals(getItem(position).getFavorite())) {
            Picasso.with(mContext)
                    .load(new File(getItem(position).getPicturepath()))
                    .fit()
                    .into(viewHolder.ivIcon, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v(LOG_TAG, "Image loaded");
                        }

                        @Override
                        public void onError() {
                            Log.v(LOG_TAG, "Error while loading image");
                        }
                    });
        } else {
            Picasso.with(mContext)
                    .load(getItem(position).getPosterPath())
                    .fit()
                    .into(viewHolder.ivIcon, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v(LOG_TAG, "Image loaded");
                        }

                        @Override
                        public void onError() {
                            Log.v(LOG_TAG, "Error while loading image");
                        }
                    });
        }



        return convertView;
    }

    @Override public int getCount() {
        return movieArrayList.size();
    }

    @Override public Movie getItem(int position) {
        return movieArrayList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
    }
}


