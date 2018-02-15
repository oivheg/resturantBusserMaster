package com.kdr.oivheg.resturantbussermaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.kdr.oivheg.resturantbussermaster.fcm.FCMLogin;

/**
 * Created by oivhe on 15.02.2018.
 */

public class SplashActivity extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

// decide here whether to navigate to Login or Main Activity

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("activity_executed", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent activeUser = new Intent(this, FCMLogin.class);
            this.startActivity(activeUser);
        }
    }
}
