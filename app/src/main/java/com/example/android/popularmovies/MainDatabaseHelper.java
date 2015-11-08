package com.example.android.popularmovies;

// A string that defines the SQL statement for creating a table

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Helper class that actually creates and manages the provider's underlying data repository.
 */
public final class MainDatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_MOVIE_TABLE =
            "movie " +                       // Table's name
            "(" +                           // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            " themoviedbid INTEGER," +
            " title TEXT," +
            " year INTEGER," +
            " playtime INTEGER," +
            " rating TEXT," +
            " description TEXT," +
            " picturepath TEXT)";

    private static final String SQL_CREATE_COMMAND = "CREATE TABLE ";

    private static final String SQL_CHECK_TABLE_COMMAND = "create table if not exists ";

    private static final String SQL_CHECK_AND_CREATE = SQL_CHECK_TABLE_COMMAND + SQL_MOVIE_TABLE;
    private static final String SQL_CREATE_TABLE_MOVIE = SQL_CREATE_COMMAND + SQL_MOVIE_TABLE;

    /*
     * Instantiates an open helper for the provider's SQLite data repository
     * Do not do database creation and upgrade here.
     */
    MainDatabaseHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, 1);
    }

    /*
     * Creates the data repository. This is called when the provider attempts to open the
     * repository and SQLite reports that it doesn't exist.
     */
    public void onCreate(SQLiteDatabase db) {
        // Creates the main table
        db.execSQL(SQL_CREATE_TABLE_MOVIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        //TODO: Test
        SQLiteDatabase db = super.getWritableDatabase();

        db.execSQL(SQL_CHECK_AND_CREATE);

        //db.execSQL("drop table movie");
        return db;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = super.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        } catch (SQLiteException e) {
            super.getWritableDatabase().execSQL(SQL_CHECK_AND_CREATE);
        }

        cursor.close();
        return db;
    }

}