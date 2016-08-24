package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.thomaskioko.jokedisplay.DisplayJokeActivity;
import com.udacity.gradle.builditbigger.interfaces.JokeInterface;
import com.udacity.gradle.builditbigger.tasks.EndpointsAsyncTask;
import com.udacity.gradle.builditbigger.utils.DeviceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment used to display ads and fetch a joke from the server
 *
 * @author Thomas Kioko
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener, JokeInterface {


    @Bind(R.id.btn_joke)
    Button mBtnJoke;
    @Bind(R.id.tv_error_message)
    TextView mTvErrorMessage;
    @Bind(R.id.instructions_text_view)
    TextView mTvInstructions;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.adView)
    AdView mAdView;

    private InterstitialAd mInterstitialAd;
    private int counter = 0;

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

        //Check if the device is connected to the internet.
        if (!DeviceUtils.isNetworkConnected(getActivity())) {

            //Hide the views since there is no internet connection
            mTvInstructions.setVisibility(View.GONE);
            mBtnJoke.setVisibility(View.GONE);
            //Display no internet connection message.
            mTvErrorMessage.setText(getResources().getString(R.string.no_internet_connection));

        } else {
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

            requestNewInterstitial();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_joke:

                if (counter == 3) {
                    mTvErrorMessage.setText(getResources().getString(R.string.get_paid_version));
                } else {
                    getJokes();
                }
                break;
        }
    }

    @Override
    public void onJokeFetched(String joke, Exception exception) {
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

    /**
     * Helper method used to initialise and show the banner
     */
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
        mAdView.loadAd(adRequest);

        mInterstitialAd.show();

    }

    /**
     * Helper method to get jokes from GAE Server. It also increments a counter which determines
     * how many more jokes a user on a free version can see.
     */
    private void getJokes() {
        counter++;
        mProgressBar.setVisibility(View.VISIBLE);
        new EndpointsAsyncTask(this).execute();
    }
}