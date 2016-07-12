package com.thomaskioko.sunshine.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.SettingsActivity;

/**
 *
 */
public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }

    private String mPassedData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mPassedData = intent.getStringExtra(Intent.EXTRA_TEXT);

            TextView detailWeather = (TextView) rootView.findViewById(R.id.detail_weather);
            detailWeather.setText(mPassedData);
        }
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail_settings, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentShare.putExtra(Intent.EXTRA_TEXT, mPassedData + "#SunshineApp");
        intentShare.setType("text/plain");
        mShareActionProvider.setShareIntent(intentShare);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
