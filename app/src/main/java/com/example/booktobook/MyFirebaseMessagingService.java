package com.example.booktobook;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = FirebaseMessagingService.class.getSimpleName();
    String token;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: token="+s);
        //TODO:서버로 토큰을 보내서 저장한다
        token=s;


            sendRegistrationToServer(s);


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage != null && remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String click_action=remoteMessage.getData().get("click_action");
            sendNotification(remoteMessage,title,body,click_action);
        }



    }


    private void sendNotification(RemoteMessage remoteMessage, String title, String body, String click_action){

        Intent intent = null;
        if(click_action.equals("CHAT_ACTIVITY"))
        {
            intent= new Intent(this,ChatActivity.class);
            Log.d("chat","chat");
        }
        else if (click_action.equals("MESSAGE_ACTIVITY"))
        {
            intent= new Intent(this,MessageActivity.class);
        }

        //Intent intent= new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        String channelId="Channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelName="Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

            builder.setChannelId(channelId);
        }
        notificationManager.notify(0,builder.build());
    }

    private void sendRegistrationToServer(String token) {


//        Map<String,String> map= new HashMap<>();
//        map.put("token",token);

    }


}

