package com.example.petersmith.treasurehunt;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.petersmith.treasurehunt.Helper.DEBUG;

/**
 * Created by peter.smith on 27/04/2017.
 */

class NfcQuestion extends QuestionData implements NfcReaderCallback.TagIdCallback {

    private static final String TAG = "NfcQuestion";
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

    private Activity mActivity;
    private View mView;
    public NfcReaderCallback mNfcReaderCb;

    NfcQuestion(String question,String answer){
            initData(question,answer);
            mType = QuestionType.Q_NFC;
    }

    public void renderQuestion(final Activity activity, LinearLayout layout){
        mActivity = activity;
        //Display question
        //set a layout to the passed in layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        //final View inflatedLayout= inflater.inflate(R.layout.beacon_question, null, false);
        mView = inflater.inflate(R.layout.nfc_question, null, false);
        //mLayout = layout;
        layout.addView(mView);

        TextView questionText = (TextView) mView.findViewById(R.id.QuestionText);
        questionText.setText(mQuestion);

        mNfcReaderCb = new NfcReaderCallback(this);
        enableReaderMode();
        //answer is actually the NFC tag id

    }

    @Override
    public void onTagReceived(final String tag){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView result = (TextView) mActivity.findViewById(R.id.resultText);
                if (tag.equalsIgnoreCase(mAnswer)) {
                    result.setBackgroundColor(mActivity.getColor(R.color.colorRight));
                    result.setText("CORRECT");
                    result.setVisibility(View.VISIBLE);
                    mActivity.findViewById(R.id.nextQuestionBut).setVisibility(View.VISIBLE);
                    //Quit running tasks is done in cleanUp()
                } else {
                    result.setBackgroundColor(mActivity.getColor(R.color.colorWrong));
                    if(DEBUG){
                        result.setText(tag);
                    } else {
                        result.setText("WRONG! ");
                    }
                    result.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void cleanUp(){
        disableReaderMode();
        if (mView != null) {
            ViewGroup grp = (ViewGroup)mView.getParent();
            grp.removeView(mView);
        }
        mView = null;
        mNfcReaderCb = null;
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(mActivity);
        if (nfc != null) {
            nfc.enableReaderMode(mActivity, mNfcReaderCb, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(mActivity);
        if (nfc != null) {
            nfc.disableReaderMode(mActivity);
        }
    }

}

