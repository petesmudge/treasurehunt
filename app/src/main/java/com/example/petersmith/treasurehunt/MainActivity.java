package com.example.petersmith.treasurehunt;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;


    private static final String TAG = "TreasureHunt";

    HuntData mHuntData;
    TextView mQuestion;
    TextView mAnswer;
    TextView mQuestionNum;
    Chronometer mTimer;
    ProgressBar mProgress;

    //From Firebase
    DatabaseReference mRemotePlayersRef;
    int mNumPlayers = 0;
    int MAX_PLAYERS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Initialise database */
        //Remote
        //setupFirebase();
        /* Load Quiz */
        mHuntData = new HuntData();

        /*Render initial question */
        //mQuestion = (TextView) findViewById(R.id.QuestionText);
        //mAnswer = (TextView) findViewById(R.id.AnswerText);
        mQuestionNum = (TextView) findViewById(R.id.QuestionNum);
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);

        updateQuestion();

        final Button nextQuestionBut = (Button) findViewById(R.id.nextQuestionBut);
        nextQuestionBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextQuestionBut.setVisibility(View.INVISIBLE);
                        updateQuestion();
                    }
                });
            }
        });

        mTimer = (Chronometer) findViewById(R.id.chronometer2);
        mTimer.start();

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
        //if (mAuthListener != null) {
        //    mAuth.removeAuthStateListener(mAuthListener);
        //}
    }




    private void updateQuestion() {
        if (mHuntData.notFinished()) {
            QuestionData q = mHuntData.getNextQuestion();
            q.renderQuestion(this,(LinearLayout) findViewById(R.id.QuestionLayout));
            int a = mHuntData.getNumber();
            mQuestionNum.setText(Integer.toString(a));
            updateProgress(mHuntData.getProgress());
        } else {
            Toast.makeText(this, "You have finished!", Toast.LENGTH_SHORT).show();
            mTimer.stop();
            /*Store time somewhere? */
            updateProgress(100);
        }
    }

    private void updateProgress(int progress){ mProgress.setProgress(progress);}

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
