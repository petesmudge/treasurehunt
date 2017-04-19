package com.example.petersmith.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by peter.smith on 19/04/2017.
 */

class TextQuestion extends QuestionData{

    //called on UI thread.
    @Override
    public void renderQuestion(final MainActivity activity, LinearLayout layout){
        //set a layout to the passed in layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View inflatedLayout= inflater.inflate(R.layout.text_question, null, false);
        if (layout.getChildCount() > 0) {
            layout.removeView(layout.getFocusedChild());
        }
        layout.addView(inflatedLayout);

        TextView questionText = (TextView) inflatedLayout.findViewById(R.id.QuestionText);
        questionText.setText(mQuestion);

        final EditText answer = (EditText) inflatedLayout.findViewById(R.id.answerText);
        Button answerButt = (Button) inflatedLayout.findViewById(R.id.answerButton);
        answerButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (answer.getText().toString().equalsIgnoreCase(mAnswer)) {
                            activity.updateQuestion();
                        }
                    }
                });
            }
        });


    }

    private void checkAnswer(String string){

    };

    TextQuestion(String question,String answer)
    {
        initData(question,answer);
        mType = QuestionType.Q_TEXT;
    }


}
