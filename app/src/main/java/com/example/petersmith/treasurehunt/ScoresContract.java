package com.example.petersmith.treasurehunt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class ScoresContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ScoresContract() {}

    public static final String DATABASE_NAME = "Highscores.db";
    /* Inner class that defines the table contents */
    public static class ScoresEntry implements BaseColumns {
        public static final String TABLE_NAME = "Highscores";
        public static final String PLAYER_TIME = "Time";
        public static final String PLAYER_NAME = "Name";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScoresEntry.TABLE_NAME + " (" +
                    ScoresEntry._ID + " INTEGER PRIMARY KEY," +
                    ScoresEntry.PLAYER_TIME + " TEXT," +
                    ScoresEntry.PLAYER_NAME + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScoresEntry.TABLE_NAME;


}