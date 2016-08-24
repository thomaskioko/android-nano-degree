package com.udacity.gradle.builditbigger.tasks;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.thomaskioko.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.interfaces.JokeInterface;

import java.io.IOException;

/**
 * AsyncTask class that invokes GAE server and fetches jokes
 *
 * @author Thomas Kioko
 */
public class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private MyApi myApiService = null;
    private JokeInterface mJokeInterface;
    private Exception mException;

    /**
     * Constructor
     *
     * @param jokeInterface Joke interface
     */
    public EndpointsAsyncTask(JokeInterface jokeInterface) {
        this.mJokeInterface = jokeInterface;
    }

    @Override
    protected final String doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://build-it-bigger-141220.appspot.com/_ah/api/");

            myApiService = builder.build();
        }

        try {
            return myApiService.getJoke().execute().getData();
        } catch (IOException e) {
            mException = e;
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String joke) {
        mJokeInterface.onJokeFetched(joke, mException);
    }
}
