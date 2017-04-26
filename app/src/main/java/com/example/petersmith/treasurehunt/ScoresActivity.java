package com.example.petersmith.treasurehunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static com.example.petersmith.treasurehunt.MainActivity.PREFS_NAME;

public class ScoresActivity extends AppCompatActivity {

    //Database:
    HighScoresDb mHighScores;
    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        mHighScores = new HighScoresDb(this);
        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        //Register TryAgain Button
        Button but = (Button) findViewById(R.id.tryAgainButton);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartNameEntryActivity = new Intent(ScoresActivity.this, StartActivity.class);
                startActivity(StartNameEntryActivity);
            }
       });

        // Display top 10 times


        //Get cursor/query result of top ten list ordered by time
        Cursor cur = getTopTen();

        //find most recent entry (to be highlighted)
        String currRow = mPrefs.getString(getString(R.string.currentRow),"");

        //Display table of results:
        renderTopTen(cur,currRow);



        //if db > 100 entries, delete all but top ten.

    }

    private void renderTopTen(Cursor cur, String currentID){
        TableLayout mainTable = (TableLayout) findViewById(R.id.ScoresTable);
        mainTable.setStretchAllColumns(true);
        try {
            if (cur.moveToFirst()) {
                int rows = cur.getCount();
                int cols = cur.getColumnCount();

                // outer for loop
                for (int i = 0; i < rows; i++) {

                    TableRow row = new TableRow(this);
                    TableLayout.LayoutParams tableRowParams =
                            new TableLayout.LayoutParams();
                    row.setLayoutParams(tableRowParams);

                    // inner for loop - skip ID column (first col)
                    for (int j = 1; j < cols; j++) {

                        TextView tv = new TextView(this);
                        tv.setLayoutParams(new TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.MATCH_PARENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(18);
                        tv.setPadding(5, 5, 5, 5);

                        tv.setText(cur.getString(j));
                        row.addView(tv);

                    }
                    //if this row is the current entry
                    if(cur.getString(0).equals(currentID))
                    {
                        row.setBackgroundColor(getColor(R.color.colorHot));
                    }
                    mainTable.addView(row);
                    cur.moveToNext();
                }
            }
        } finally {
            cur.close();
        }
    }

    private Cursor getTopTen(){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ScoresContract.ScoresEntry._ID,
                ScoresContract.ScoresEntry.PLAYER_NAME,
                ScoresContract.ScoresEntry.PLAYER_TIME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ScoresContract.ScoresEntry.PLAYER_TIME + " ASC";

        SQLiteDatabase db = mHighScores.getReadableDatabase();
        Cursor cursor = db.query(
                ScoresContract.ScoresEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,                                // The sort order
                "10"                                      // The number of results
        );

        return cursor;

    }

    private Cursor getCurrentID(){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ScoresContract.ScoresEntry._ID,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                ScoresContract.ScoresEntry.PLAYER_TIME + " ASC";

        SQLiteDatabase db = mHighScores.getReadableDatabase();
        Cursor cursor = db.query(
                ScoresContract.ScoresEntry.TABLE_NAME,    // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder,                                // The sort order
                "10"                                      // The number of results
        );

        return cursor;

    }

}
