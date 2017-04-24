package com.example.petersmith.treasurehunt;

import static com.example.petersmith.treasurehunt.QuestionData.QuestionType.Q_TEXT;

/**
 * Created by peter.smith on 14/02/2017.
 */

public class HuntData {

    //Temporary structure for questions
    /*private QuestionData[] mData = {
                new TextQuestion("What is your name?", "Rebecca"),
                new TextQuestion("What is your hair colour?", "brown"),
                new TextQuestion("What is your brother's name?", "Adam"),
                new TextQuestion("How old are you?", "6"),
        };*/
    /*
    //beacon1
        mBeaconScan.setAddress("DC:9F:31:59:12:F5");
    //myShine
        mBeaconScan.setAddress("C3:04:41:50:7C:4B");
     */
    private QuestionData[] mData = {
            new BeaconQuestion("What is your name?", "Adam","find Daddy to get question","C3:04:41:50:7C:4B"),
            new BeaconQuestion("How old are you?", "4","find car seat","DC:9F:31:59:12:F5"),
            new TextQuestion("What colour is Daddy's car?", "black"),
            new TextQuestion("What is your sisters name?", "Rebecca")
    };

    private int mIndex;
    private boolean mFirst = true;
    private boolean mFinished = false;

    /* default constructor - not reversed */
    HuntData(){
        mFinished = false;
        mIndex = -1;
    }

    /* Accessor functions */
    public QuestionData getNextQuestion(){
        QuestionData q = null;
        if(++mIndex < mData.length)
        {
            q= mData[mIndex];
        }
        else
        {
            mFinished = true;
        }
        return q;
    }

    public int getNumber(){
        return mIndex +1;
    }

    public int getProgress(){
        int val =(mIndex * 100) / mData.length;
        if(mFinished){
            val = 100;
        }
        return val;
    }

}
