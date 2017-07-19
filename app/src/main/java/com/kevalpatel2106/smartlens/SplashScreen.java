package com.kevalpatel2106.smartlens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Keval on 30-Jan-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Add logic for checking user login
        startActivity(new Intent(this, Dashboard.class));
    }
}
