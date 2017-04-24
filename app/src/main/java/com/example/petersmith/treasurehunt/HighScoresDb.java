package com.example.petersmith.treasurehunt;

import android.content.Context;
import android.database.sqlite.*;
import android.provider.BaseColumns;
import android.webkit.ServiceWorkerClient;

/**
 * Created by peter.smith on 21/04/2017.
 */

public class HighScoresDb extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public HighScoresDb(Context context) {
        super(context, ScoresContract.DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScoresContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(ScoresContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
