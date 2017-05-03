package com.example.petersmith.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.petersmith.treasurehunt.MainActivity;
import com.example.petersmith.treasurehunt.QuestionData;

/**
 * Created by peter.smith on 20/04/2017.
 */

class BeaconQuestion extends QuestionData implements ProxCallback {

    private static final String TAG = "BeaconQuestion";

    //Handler.Callback mCallback;
    BeaconScan mBeaconScan;
    String mHintText,mAddress;
    Activity mActivity;
    int mTickCheck = 0;
    //ifs timer handling "good" state
    private boolean mOkay = false;
    View mView;

    //for timer
    Handler mTimerHdl;
    Runnable beaconTimer = new Runnable() {
        @Override
        public void run() {
            //set visible
            TextView proxView = (TextView) mActivity.findViewById(R.id.QuestionText);
            EditText answer = (EditText) mActivity.findViewById(R.id.answerText);

            //set true idf we have had more than 1 good reading
            if(mTickCheck > 1){
                proxView.setText(mQuestion);
                answer.setVisibility(View.VISIBLE);

                //reschedule timer - stay good for at least this long
                mTimerHdl.postDelayed(beaconTimer, 5000);
                mOkay=true;
            } else {
                proxView.setText("");
                mOkay=false;
            }
            //reset - don't reschedule
            mTickCheck = 0;
        }
    };

    BeaconQuestion(String question,String answer,String hint,String address)
    {
        initData(question,answer);
        mHintText = hint;
        mAddress = address;
        mType = QuestionType.Q_BEACON;
    }

    //called on UI thread.
    @Override
    public void renderQuestion(final Activity activity, LinearLayout layout){
        /*Initialise BT*/
        mBeaconScan = new BeaconScan(this);
        mTimerHdl = new Handler();
        //set a layout to the passed in layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        //final View inflatedLayout= inflater.inflate(R.layout.beacon_question, null, false);
        mView = inflater.inflate(R.layout.text_question, null, false);

        /*mLayout = layout;
        //added in now? TODO
        if (mLayout.getChildCount() > 0) {
            mLayout.removeView(mLayout.getFocusedChild());
        }*/
        //test this

        layout.addView(mView);

        TextView hintText = (TextView) activity.findViewById(R.id.ExtraText);
        hintText.setText(mHintText);
        hintText.setVisibility(View.VISIBLE);
        TextView proxView = (TextView) activity.findViewById(R.id.QuestionText);
        proxView.setBackgroundColor(activity.getColor(R.color.colorCold));
        TextView result = (TextView) mView.findViewById(R.id.resultText);
        result.setVisibility(View.INVISIBLE);
        mActivity = activity;
        mBeaconScan.initBT(activity);
        //mBeaconScan.setUuid("00000000000000000000000000123456-0008-0008");
        //mBeaconScan.setUuid("00000000000000000000000000000000-0000-0000");
        mBeaconScan.setAddress(mAddress);
        mBeaconScan.startLeScan(true);

        //same as TextQuestion
        final EditText answer = (EditText) mView.findViewById(R.id.answerText);
        answer.setVisibility(View.INVISIBLE);

        answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                Log.d(TAG, "onEditorAction " + actionId + ", " + keyEvent);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "onEditorAction" + answer.getText().toString());
                    TextView result = (TextView) mView.findViewById(R.id.resultText);
                    if (answer.getText().toString().equalsIgnoreCase(mAnswer)) {
                        result.setBackgroundColor(activity.getColor(R.color.colorRight));
                        result.setText("CORRECT");
                        result.setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.nextQuestionBut).setVisibility(View.VISIBLE);
                        //Quit running tasks
                        //This is fine if we get here, but who cleans up if sent to background, etc?
                        //cleanUp();
                    } else {
                        result.setBackgroundColor(activity.getColor(R.color.colorWrong));
                        result.setText("WRONG!");
                        result.setVisibility(View.VISIBLE);
                    }
                }

                return false;
            }
        });
    }

    /* callback function for BeaconScan */
    public void CallbackRssi (int rssi){
        Log.d(TAG,"Rssi = " + Integer.toString(rssi));
        final int inner_rssi = rssi;
        if(mActivity != null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView proxView = (TextView) mActivity.findViewById(R.id.QuestionText);
                    //Change colour according to rssi (-60 being HOT, -80 = COLD)
                    int percent;
                    //int distance = (int) ConvertRssiToDistanceM(inner_rssi);
                    //int distance = (int) calculateAccuracy(-60,inner_rssi);
                    // Other measures are rubbish, just use basic rssi and threshold (based on -60 = 1m TxPower)
                    if (inner_rssi > -60){
                        percent = 100;
                    }
                    else if (inner_rssi < -80){
                        percent = 0;
                    }
                    else{
                        percent = ((inner_rssi + 80) * 100) / (80-60);
                    }

                    Log.d(TAG,"percent = " + percent);
                    int bkColor = getColor(mActivity.getColor(R.color.colorCold),
                            mActivity.getColor(R.color.colorHot),(float)percent/100);
                    Log.d(TAG,"bkColor = " + bkColor);
                    //need to check this!!
                    proxView.setBackgroundColor(bkColor);
                    if(percent == 100) {
                        //try to smooth with timer.
                        //if more than 1 100% reading in 2secs, then activate
                        if(mTickCheck == 0 && !mOkay){
                            mTimerHdl.postDelayed(beaconTimer, 2000);
                        }
                        mTickCheck++;
                    }
                }
            });
        }

    }

    public void cleanUp() {
        mBeaconScan.startLeScan(false);
        mBeaconScan = null;

        //timer vars
        mTimerHdl.removeCallbacks(beaconTimer);
        mTimerHdl = null;
        mTickCheck = 0;
        mOkay = false;

        //view vars
        if (mView != null) {
            ViewGroup grp = (ViewGroup)mView.getParent();
            grp.removeView(mView);
        }
        mView = null;
    }
/*
    private double ConvertRssiToDistanceM(int rssi){
        double distance;
        //assuming txPower is the 1m ref power = -60
        //RSSI = -10nlogd + A

        distance = Math.pow(10d, ((-60.0 - rssi) / (10 * 2.5)));
        if (rssi > -40){
            distance = 100;
        }
        else if (rssi < -100){
            distance = 0;
        }
        else{
            distance = ((rssi + 100) * 10) / 6;
        }
        Log.d(TAG,"distance = " + Double.toString(distance));

        return distance;
    }

    protected static double calculateAccuracy(int txPower, int rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }*/


    private int getColor(int c0, int c1, float p) {
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        return Color.argb(a, r, g, b);
    }
    private int ave(int src, int dst, float p) {
        return src + java.lang.Math.round(p * (dst - src));
    }

}
