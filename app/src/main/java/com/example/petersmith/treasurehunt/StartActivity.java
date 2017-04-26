package com.example.petersmith.treasurehunt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.petersmith.treasurehunt.MainActivity.PREFS_NAME;

public class StartActivity extends AppCompatActivity {

    SharedPreferences mPrefs;
    EditText mName;
    HighScoresDb mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // For settings (not yet used)
        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mName = (EditText) findViewById(R.id.nameEntry);
        mName.setText(mPrefs.getString(getString(R.string.username),""));
        mDb = new HighScoresDb(this);

        Button startBut = (Button) findViewById(R.id.startButton);

        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(getString(R.string.username), mName.getText().toString());
                editor.apply();
                Intent StartMainActivity = new Intent(StartActivity.this, MainActivity.class);
                startActivity(StartMainActivity);
            }
        });

        Button clearBut = (Button) findViewById(R.id.ClearScoresButton);
        clearBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = mDb.getWritableDatabase();
                mDb.cleanDb(db);
            }
        });
    }
}
