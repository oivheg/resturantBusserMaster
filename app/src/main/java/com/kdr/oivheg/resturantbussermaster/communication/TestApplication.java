package com.kdr.oivheg.resturantbussermaster.communication;

import android.app.Application;

public class TestApplication extends Application {

    private static TestApplication mInstance;

    public static synchronized TestApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}
