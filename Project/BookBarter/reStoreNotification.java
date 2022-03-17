package com.csc301.students.BookBarter;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import java.util.Map;

import com.csc301.students.BookBarter.SearchAds.Data;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class reStoreNotification extends FirebaseMessagingService {
    private Intent intent;
    Data postData;

    public reStoreNotification() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        //android.util.Log.d("strToIntTest", "title=" +remoteMessage.getData());
        //setupPost(remoteMessage.getData());
        android.util.Log.d("strToIntTest", "post=" +remoteMessage.getData().get("image")
        + remoteMessage.getData().get("_id"));
        android.util.Log.d("strToIntTest", "post=" +remoteMessage.getData());
        postData=new Data(
                remoteMessage.getData().get("image"),
                remoteMessage.getData().get("email"),
                remoteMessage.getData().get("description"),
                remoteMessage.getData().get("title"),
                remoteMessage.getData().get("id"),
                remoteMessage.getData().get("price")

        );
        android.util.Log.d("strToIntTest", "post=" +postData.getEmail());

        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        //android.util.Log.d("strToIntTest", "title=" +title +",body="+body);
    }
    private void setupPost(Map<String,String> post){
        android.util.Log.d("strToIntTest", "title=" +post);



            android.util.Log.d("strToIntTest", "title=" +post.toString());
            postData=new Data("no image",
                                    "no email",
                                    "no description",
                                    "no title",
                                    "no id",
                                    "no price");
            if (post.get("image")!=null){

                postData.setImage(post.get("image"));
            }
            if (post.get("email")!=null){
                postData.setImage(post.get("email"));
            }
            if (post.get("description")!=null){
                postData.setImage(post.get("description"));
            }
            if (post.get("title")!=null){
                postData.setImage(post.get("title"));
            }
            if (post.get("id")!=null){
                postData.setImage(post.get("id"));
            }
            if (post.get("price")!=null){
                postData.setImage("price: "+post.get("price"));
            }




    }
    private void showNotification(String title,String body){



        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID="book subscribe channel";
        android.util.Log.d("strToIntTest", "title=" +title +",body="+body);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Notification",notificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("EDMT Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[] {Notification.DEFAULT_VIBRATE});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);


        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);


        intent = new Intent(this,
                getNotice.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.util.Log.d("strToIntTest", "post=" +postData);
        intent.putExtra("data", postData);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        notificationBuilder//.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.bookbarter_icon)
                .setContentIntent(pendingIntent)
               ;


        notificationManager.notify(0,notificationBuilder.build());




    }



}
