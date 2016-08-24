package com.thomaskioko.jokedisplay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Activity to display jokes passed from MainActivityFragment in the app.
 *
 * @author Thomas Kioko
 */
public class DisplayJokeActivity extends AppCompatActivity {

    public static final String EXTRA_JOKE = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_joke);

        TextView tvJoke = (TextView) findViewById(R.id.tv_joke);
        tvJoke.setText(getIntent().getExtras().getString(EXTRA_JOKE));
    }
}
