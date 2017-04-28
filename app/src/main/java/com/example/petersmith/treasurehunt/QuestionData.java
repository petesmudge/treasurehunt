package com.example.petersmith.treasurehunt;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

/**
 * Created by peter.smith on 14/02/2017.
 */

//basic data type, but with functions that render sepcific views and functionality for different questions.
//Nee to ensure if "renderQuestion()" is called, then "cleanUp()" is called if

public abstract class QuestionData {

    public enum QuestionType {
        Q_TEXT,
        Q_BEACON,
        Q_NFC,
        Q_IMAGE
    };

    String mQuestion;
    String mAnswer;
    QuestionType mType;

    public void initData(String question,String answer){
        mQuestion = question;
        mAnswer = answer;
    }

    public String getQuestion(){
        return mQuestion;
    }
    public String getAnswer(){
        return mAnswer;
    }
    public QuestionType getType(){
        return mType;
    }

    public abstract void renderQuestion(final Activity activity, LinearLayout layout);
    public abstract void cleanUp();

}
