package com.example.petersmith.treasurehunt;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.INotificationSideChannel;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    /*private QuestionData[] mData = {
            new BeaconQuestion("What is this?", "watch","find Daddy to get question","C3:04:41:50:7C:4B"),
            new BeaconQuestion("What is under your feet", "grass","go to bottom of garden","DC:9F:31:59:12:F5"),
            new TextQuestion("What colour is Daddy's car?", "black"),
            new TextQuestion("What is 2 + 2?", "4")
    };*/

    /*
    private QuestionData[] mData = {
            new BeaconQuestion("What is this?", "watch","Find Peter","C3:04:41:50:7C:4B"),
            new BeaconQuestion("Where is the beacon?", "coat","At Peter's desk","DC:9F:31:59:12:F5"),
            new TextQuestion("What is 2 + 2?", "4")
    };*/

    private List<QuestionData> mData = null;
    private int mIndex;
    private LoadedCallback mCallbackLoaded;
    private boolean mFinished = false;
    //private AsyncTask mAsyncTask;

    /* default constructor - not reversed */
    HuntData(LoadedCallback callback){
        mFinished = false;
        mCallbackLoaded = callback;
        mIndex = -1;
        mData = new ArrayList<QuestionData>();
        new GetDataTask().execute("http://www.phomic.co.uk/testdata.php?option=1");
    }

    class GetDataTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = null;
                if (url != null) {
                    in = url.openStream();
                }
                loadHuntFromJSON(in);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                //return;
            }
            return false;
        }

        protected void onPostExecute(Boolean okay){
            if(okay) {
                mCallbackLoaded.IsLoaded();
            }
            else{
                //on error?
            }
            /*try {
                loadHuntFromJSON(in);
                //mCallbackLoaded.IsLoaded();
            } catch (IOException e) {
               e.printStackTrace();
            }*/
        }
    }

    private void loadHuntFromJSON(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try{
            reader.beginArray();
            while( reader.hasNext()){
                readQuestionData(reader);
            }
            reader.endArray();
        } finally {
            reader.close();
        }

    }

    private void readQuestionData(JsonReader reader) throws IOException {
        String type = null;
        String question = null;
        String answer = null;
        String hint = null;
        String id = null;
        //read a question into HuntData
        reader.beginObject();
        //check type
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("type")) {
                type = reader.nextString();
            } else if (name.equals("question")){
                question = reader.nextString();
            } else if (name.equals("answer")){
                answer = reader.nextString();
            } else if (name.equals("o_hint")){
                hint = reader.nextString();
            } else if (name.equals("o_id")){
                id = reader.nextString();
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();

        if(type.equals("Q_TEXT")){
            mData.add(new TextQuestion(question,answer));
        } else if(type.equals("Q_BEACON")){
            mData.add(new BeaconQuestion(question,answer,hint,id));
        }
        //Other question types to follow

    }

    /* Accessor functions */
    public QuestionData getNextQuestion(){
        QuestionData q = null;

        if((mData != null )&& (++mIndex < mData.size()))
        {
            q= mData.get(mIndex);
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
        int val =(mIndex * 100) / mData.size();
        if(mFinished){
            val = 100;
        }
        return val;
    }

}
