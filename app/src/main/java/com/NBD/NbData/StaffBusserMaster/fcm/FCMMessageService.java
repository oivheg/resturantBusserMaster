package com.NBD.NbData.StaffBusserMaster.fcm;

import android.content.Intent;

import com.NBD.NbData.StaffBusserMaster.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by oivhe on 24.03.2017.
 */

public class FCMMessageService extends FirebaseMessagingService {


    private boolean _justUpdated = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        System.out.print("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            System.out.print("Message data payload: " + remoteMessage.getData());

            String tmpdata = remoteMessage.getData().get("Action");

            switch (remoteMessage.getData().get("Action")) {
                case "refresh":
                    Refreshmaster(remoteMessage.getData());
                    break;
                case "recieved":
                    String tmpUser = remoteMessage.getData().get("user");
                    BtnOk(tmpUser);
                    break;
            }


        }



//        showNotification(remoteMessage.getData().get("message"));
    }

    private void BtnOk(String user) {

        MainActivity.getInstace().StopbtnBlink(user);

    }

    private boolean getUpdated() {
        return _justUpdated;
    }
    private void Refreshmaster(Map<String, String> data) {

        Intent intent = new Intent();
        intent.setAction("com.my.app.onMessageReceived");
        sendBroadcast(intent);
//        MainActivity.getInstace().refreshTable();
        while (_justUpdated) {
            //Waiting until last process is finished.
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("Waiting for previous iternation to finish.");
            _justUpdated = getUpdated();
        }

//        Try catch is because this class runs 2 times and crashes the app,  should look for why and might fix
        try {
            _justUpdated = true;
            //MainActivity.getInstace().refreshTable();
            _justUpdated = false;
        } catch (Exception e) {
            System.out.println("FCMMESSAGE: ERROR  " + e);
        }
    }


}
