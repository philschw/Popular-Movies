package com.example.android.popularmovies;

// A string that defines the SQL statement for creating a table

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Helper class that actually creates and manages the provider's underlying data repository.
 */
public final class MainDatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            "movie " +                       // Table's name
            "(" +                           // The columns in the table
            " _ID INTEGER PRIMARY KEY, " +
            " themoviedbid INTEGER," +
            " title TEXT," +
            " year INTEGER," +
            " playtime INTEGER," +
            " rating TEXT," +
            " description TEXT)";

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
        db.execSQL(SQL_CREATE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        //TODO: Test
        SQLiteDatabase db = super.getWritableDatabase();
        //db.execSQL(SQL_CREATE_MAIN);
        //db.execSQL("drop table movie");
        return db;
    }

}