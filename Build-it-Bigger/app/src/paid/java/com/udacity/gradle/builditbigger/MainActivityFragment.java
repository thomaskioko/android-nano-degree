package com.udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.gradle.builditbigger.interfaces.JokeFetchListener;
import com.udacity.gradle.builditbigger.tasks.EndpointsAsyncTask;
import com.udacity.gradle.builditbigger.utils.DeviceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thomas Kioko
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener, JokeFetchListener {

    @Bind(R.id.btn_joke)
    Button mBtnJoke;
    @Bind(R.id.tv_error_message)
    TextView mTvJoke;
    @Bind(R.id.instructions_text_view)
    TextView mTvInstructions;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    /**
     * Default constructor
     */
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        mBtnJoke.setOnClickListener(this);

        if (!DeviceUtils.isNetworkConnected(getActivity())) {  //Hide the views since there is no internet connection
            mTvInstructions.setVisibility(View.GONE);
            mBtnJoke.setVisibility(View.GONE);
            mTvJoke.setText(getResources().getString(R.string.no_internet_connection));
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_joke:
                mProgressBar.setVisibility(View.VISIBLE);
                new EndpointsAsyncTask(this).execute();
                break;
        }
    }

    @Override
    public void onJokeFetched(String joke, Exception e) {
        mProgressBar.setVisibility(View.GONE);
        if (!joke.equals("")) {
            Intent displayIntent = new Intent(getActivity(), DisplayJokeActivity.class);
            displayIntent.putExtra(DisplayJokeActivity.EXTRA_JOKE, joke);
            startActivity(displayIntent);
        } else {
            Intent displayIntent = new Intent(getActivity(), DisplayJokeActivity.class);
            displayIntent.putExtra(DisplayJokeActivity.EXTRA_JOKE, joke);
            startActivity(displayIntent);
        }
    }

}