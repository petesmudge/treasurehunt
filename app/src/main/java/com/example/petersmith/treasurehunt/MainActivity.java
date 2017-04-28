package com.example.petersmith.treasurehunt;

import android.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoadedCallback {

    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;


    private static final String TAG = "TreasureHunt";

    HuntData mHuntData;
    TextView mQuestion;
    TextView mAnswer;
    TextView mQuestionNum;
    Chronometer mTimer;
    ProgressBar mProgress;
    HighScoresDb mHighScores;
    QuestionData mCurrQ;

    SharedPreferences mPrefs;
    public static final String PREFS_NAME = "treasurehunt_prefs";

    //For BT/Beacon
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_COARSE_BL = 2;

    //From Firebase
    /*DatabaseReference mRemotePlayersRef;
    int mNumPlayers = 0;
    int MAX_PLAYERS = 2;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Initialise database */
        //Remote
        //setupFirebase();

        /* check capabilities */
        checkLocBT();
        checkBT();

        /* Load Quiz */
        mHuntData = new HuntData(this);

        // For settings (not yet used)
        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String str = mPrefs.getString(getString(R.string.username),"Nobody");
        this.setTitle(str + "'s Treasure Hunt");

        //Database:
        mHighScores = new HighScoresDb(this);


        /*Render initial question */
        //mQuestion = (TextView) findViewById(R.id.QuestionText);
        //mAnswer = (TextView) findViewById(R.id.AnswerText);
        mQuestionNum = (TextView) findViewById(R.id.QuestionNum);
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);

        mTimer = (Chronometer) findViewById(R.id.chronometer2);
        mTimer.start();

        final Button nextQuestionBut = (Button) findViewById(R.id.nextQuestionBut);
        nextQuestionBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestionBut.setVisibility(View.INVISIBLE);
                updateQuestion();
            }
        });

        //Question is "updated" when huntdata is loaded (below)

    }


    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onResume() {
        super.onResume();

        /* Load State*/

        /* Render Question */
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*Save state*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCurrQ != null)
            mCurrQ.cleanUp();
        //if (mAuthListener != null) {
        //    mAuth.removeAuthStateListener(mAuthListener);
        //}
    }

    //always called from UI Thread (when huntData db has finished being loaded)
    public void IsLoaded(){
        updateQuestion();
    }


    void updateQuestion() {
        //do any clean up needed
        if(mCurrQ != null)
            mCurrQ.cleanUp();
        //now get next question
        mCurrQ = mHuntData.getNextQuestion();
        if (mCurrQ != null) {
            mCurrQ.renderQuestion(this,(LinearLayout) findViewById(R.id.QuestionLayout));
            int a = mHuntData.getNumber();
            mQuestionNum.setText(Integer.toString(a));
            updateProgress(mHuntData.getProgress());
        } else {
            Toast.makeText(this, "You have finished!", Toast.LENGTH_SHORT).show();
            mTimer.stop();
            /*Store time somewhere? */
            addScoreToDB();
            updateProgress(100);

            // Jump to Top scores screen
            Intent StartScoresActivity = new Intent(MainActivity.this, ScoresActivity.class);
            startActivity(StartScoresActivity);
        }
    }

    private void updateProgress(int progress){ mProgress.setProgress(progress);}

    private void addScoreToDB(){
        long currentRow = 0;

        SQLiteDatabase db = mHighScores.getWritableDatabase();
        //Add check for DB size TODO

        ContentValues vals = new ContentValues();
        vals.put(ScoresContract.ScoresEntry.PLAYER_NAME, mPrefs.getString(getString(R.string.username),"Nobody"));
        vals.put(ScoresContract.ScoresEntry.PLAYER_TIME, (String) mTimer.getText());
        //keep a reference to the current time
        currentRow = db.insert(ScoresContract.ScoresEntry.TABLE_NAME,null,vals);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(getString(R.string.currentRow), Long.toString(currentRow));
        editor.apply();
    }

    //BT stuff
    private BluetoothAdapter mBTAdapter;

    private void checkBT(){
        //Check if device does support BT by hardware
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            //Toast shows a message on the screen for a LENGTH_SHORT period
            Toast.makeText(this, "BLUETOOTH NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Check if device does support BT Low Energy by hardware. Else close the app(finish())!
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast shows a message on the screen for a LENGTH_SHORT period
            Toast.makeText(this, "BLE NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            //If BLE is supported, get the BT adapter. Preparing for use!
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            //If getting the adapter returns error, close the app with error message!
            if (mBTAdapter == null) {
                Toast.makeText(this, "ERROR GETTING BLUETOOTH ADAPTER!", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                //Check if BT is enabled! This method requires BT permissions in the manifest.
                if (!mBTAdapter.isEnabled()) {
                    //If it is not enabled, ask user to enable it with default BT enable dialog! BT enable response will be received in the onActivityResult method.
                    Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTintent, PERMISSION_REQUEST_COARSE_BL);
                }
            }
        }
    }

    @TargetApi(23)
    private void checkLocBT(){
        //If Android version is M (6.0 API 23) or newer, check if it has Location permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //If Location permissions are not granted for the app, ask user for it! Request response will be received in the onRequestPermissionsResult.
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        //Check if permission request response is from Location
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //User granted permissions. Setup the scan settings
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    //User denied Location permissions. Here you could warn the user that without
                    //Location permissions the app is not able to scan for BLE devices and eventually
                    //Close the app
                    Toast.makeText(this, "Can't run without Bluetooth!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check if the response is from BT
        if(requestCode == PERMISSION_REQUEST_COARSE_BL){
            // User chose not to enable Bluetooth.
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


// Irrelevant junk
/*
    setupFirebase() {
            // Do authentication
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // get initial values from Firebase
                setupFirebaseValues();
            }
        };
        String email = "peter@low-level.co.uk";
        String password = "testpass1";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // Name, email address, and profile photo Url
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                                Uri photoUrl = user.getPhotoUrl();

                                // The user's ID, unique to the Firebase project. Do NOT use this value to
                                // authenticate with your backend server, if you have one. Use
                                // FirebaseUser.getToken() instead.
                                String uid = user.getUid();
                                Toast.makeText(MainActivity.this, "Authentication succeeded =" + name,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        // ...
                    }
                });
    }

    private void setupFirebaseValues()
    {
        //Get progress of all players from remote database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRemotePlayersRef = database.getReference("numPlayers");
        // Read from the database
        mRemotePlayersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Number of players is: " + value);
                mNumPlayers = Math.min(MAX_PLAYERS,Integer.valueOf(value));
                // what next?
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
*/

}
