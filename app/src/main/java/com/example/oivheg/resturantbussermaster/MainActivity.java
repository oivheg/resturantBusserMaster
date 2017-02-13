package com.example.oivheg.resturantbussermaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_ROWS = 0;
    private static final int NUM_COL = 0;
    Server server;
    TextView infoip, msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        server = new Server(this);
        infoip.setText(server.getIpAddress() + ":" + server.getPort());
        populateClients();
    }

    private void populateClients() {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COL; col++) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

}
