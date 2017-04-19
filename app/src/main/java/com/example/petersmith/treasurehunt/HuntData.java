package com.example.petersmith.treasurehunt;

import static com.example.petersmith.treasurehunt.QuestionData.QuestionType.Q_TEXT;

/**
 * Created by peter.smith on 14/02/2017.
 */

public class HuntData {

    //Temporary structure for questions
    private QuestionData[] mData = {
                new TextQuestion("What is your name?", "Rebecca"),
                new TextQuestion("What is your hair colour?", "brown"),
                new TextQuestion("What is your brother's name?", "Adam"),
                new TextQuestion("How old are you?", "6"),
        };

    private int mNumber;
    private boolean mFirst = true;
    private boolean mFinished = false;
    private boolean mReversed = false;

    private void InitHunt(boolean isReversed){
        mFirst = true;
        mFinished = false;
        mReversed = isReversed;
        if (mReversed) {
            mNumber = mData.length;
        } else {
            mNumber = 1;
        }
    }


    /* constructor */
    HuntData(boolean isReversed) {
        InitHunt(isReversed);
    }

    /* default constructor - not reversed */
    HuntData() {
        this(false);
    }

    /* Accessor functions */
    public QuestionData getNextQuestion(){
        QuestionData q = null;
        if(notFinished())
        {
            if(!mFirst) {
                 mNumber++;
            }
            mFirst=false;

            q= mData[getNumber() - 1];
        }
        else
        {
            mFinished = true;
        }
        return q;
    }

    public int getNumber(){
        int num = mNumber;
        if(mReversed){
            num = (mData.length - mNumber) + 1;
        }
        return num;
    }

    public int getProgress(){
        int val =((mNumber-1) * 100) / mData.length;
        if(mFinished){
            val = 100;
        }
        return val;
    }

    public boolean notFinished(){
        return ( mNumber < mData.length );
    }

}
