package com.thomaskioko.myappportfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonMovies = (Button) findViewById(R.id.buttonPopularMovies);
        buttonMovies.setOnClickListener(this);
        Button buttonStock = (Button) findViewById(R.id.buttonStock);
        buttonStock.setOnClickListener(this);
        Button buttonBigger = (Button) findViewById(R.id.buttonBigger);
        buttonBigger.setOnClickListener(this);
        Button buttonMaterial = (Button) findViewById(R.id.buttonMaterial);
        buttonMaterial.setOnClickListener(this);
        Button buttonUbiquitous = (Button) findViewById(R.id.buttonUbiquitous);
        buttonUbiquitous.setOnClickListener(this);
        Button buttonCapstone = (Button) findViewById(R.id.buttonCapstone);
        buttonCapstone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPopularMovies:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_movies)));
                break;
            case R.id.buttonStock:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_stock)));
                break;
            case R.id.buttonBigger:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_build)));
                break;
            case R.id.buttonMaterial:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_material)));
                break;
            case R.id.buttonUbiquitous:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_ubiquitous)));
                break;
            case R.id.buttonCapstone:
                showToastMessage(getString(R.string.toast_message,
                        getString(R.string.button_capstone)));
                break;
            default:
                break;
        }
    }

    /**
     * Method that displays a toast.
     *
     * @param message {@link String} Message
     */
    private void showToastMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
