package com.example.petersmith.treasurehunt;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoresActivity extends AppCompatActivity {

    //Database:
    HighScoresDb mHighScores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        mHighScores = new HighScoresDb(this);

        // Display top 10 times

        //Get cursor/query result of top ten list ordered by time
        Cursor cur = getTopTen();
        //Display table of results:
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

                    // inner for loop
                    for (int j = 0; j < cols; j++) {

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

}
