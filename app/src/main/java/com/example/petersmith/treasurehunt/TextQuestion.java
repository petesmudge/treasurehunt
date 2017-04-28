package com.example.petersmith.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by peter.smith on 19/04/2017.
 */

class TextQuestion extends QuestionData{

    private static final String TAG = "TextQuestion";
    private View mView;
    //called on UI thread.
    @Override
    public void renderQuestion(final Activity activity, LinearLayout layout){
        //set a layout to the passed in layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        mView = inflater.inflate(R.layout.text_question, null, false);
        layout.addView(mView);

        TextView questionText = (TextView) mView.findViewById(R.id.QuestionText);
        questionText.setText(mQuestion);

        final EditText answer = (EditText) mView.findViewById(R.id.answerText);

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

    TextQuestion(String question,String answer)
    {
        initData(question,answer);
        mType = QuestionType.Q_TEXT;
    }

    public void cleanUp() {
        if (mView != null) {
            ViewGroup grp = (ViewGroup)mView.getParent();
            grp.removeView(mView);
        }
        mView = null;
    }


}
