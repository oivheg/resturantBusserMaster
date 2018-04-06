package com.NBD.NbData.StaffBusserMaster.communication;

import android.app.Application;

// --Commented out by Inspection START (31.01.2018 09.07):
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
