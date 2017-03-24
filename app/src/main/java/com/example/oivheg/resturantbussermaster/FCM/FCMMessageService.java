package com.example.oivheg.resturantbussermaster.FCM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.oivheg.resturantbussermaster.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by oivhe on 24.03.2017.
 */

public class FCMMessageService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Refreshmaster(remoteMessage.getData());

//        showNotification(remoteMessage.getData().get("message"));
    }

    private void Refreshmaster(Map<String, String> data) {


        Intent intent = new Intent();
        intent.setAction("com.my.app.onMessageReceived");
        sendBroadcast(intent);
//        try {
//
//            MainActivity.getInstace().refreshTable();
//        } catch (Exception e) {
//            System.out.println("FCMMESSAGE: ERROR  " + e);
//        }
    }

//    private void showNotification(String message) {
//        Intent i = new Intent(this, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle("FCm Test")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        manager.notify(0,builder.build());
//    }


}
