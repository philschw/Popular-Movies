/**
 * Created by Philip Schwander on 17/08/15.
 *
 * Copyright (C) belongs to the creator, source see link below
 * Source: http://www.rogcg.com/blog/2013/11/01/gridview-with-auto-resized-images-on-android
 */
package com.example.android.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A custom image view which overrides "OnMeasure" to make it easier to change the behaviour of
 * the grid view. Can be used to make square images.
 */

public class ResizedImageView extends ImageView
{
    public ResizedImageView(Context context)
    {
        super(context);
    }

    public ResizedImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ResizedImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = (int) (getMeasuredWidth() * 1.5);

        setMeasuredDimension(getMeasuredWidth(), height); //Snap to width
    }
}