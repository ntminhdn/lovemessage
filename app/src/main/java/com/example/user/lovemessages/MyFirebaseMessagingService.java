package com.example.user.lovemessages;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * Created by User on 23/02/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!= null)
        {
            hienThiThongBao(remoteMessage.getNotification().getBody());
        }
        hienThiThongBao(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
//        truyenMessageQuaUI(remoteMessage.getNotification().getBody());
    }

//    private void truyenMessageQuaUI(String message) {
//        Intent intent = new Intent(this,MainActivity.class);
//        intent.putExtra("message",message);
//        startActivity(intent);
//    }

    private void hienThiThongBao(String body,String title) {
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.tim)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
    private void hienThiThongBao(String body) {
        hienThiThongBao(body,"google");
    }
}
