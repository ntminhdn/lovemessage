package com.example.user.lovemessages;

/**
 * Created by User on 23/02/2017.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {
    Map<String,String> mapNgayThang = new HashMap<>();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String dt = MainActivity.getNgayHienTai();

        //toast thử kiểm tra
        Toast.makeText(context, dt, Toast.LENGTH_LONG).show();

        setData();
        Set<String> listKey = (Set) mapNgayThang.keySet();
        Iterator it = listKey.iterator();
        while(it.hasNext()){
            String key = it.next().toString();
            System.out.println(key);
            System.out.println(mapNgayThang.get(key));

            if(dt.equalsIgnoreCase(key)) {
                mDatabase.child(mapNgayThang.get(key)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LoveMessage loveMessage = dataSnapshot.getValue(LoveMessage.class);

                        //notify tin nhắn
//                        thongBao(context,loveMessage.getContent(),loveMessage.getId());

                        //send data to MainActivity
                        Intent intent1 = new Intent(context,MainActivity.class);
                        intent1.putExtra("id", loveMessage.getId());
                        intent1.putExtra("content", loveMessage.getContent());
                        intent1.putExtra("image", loveMessage.getImage());
                        intent1.putExtra("music", loveMessage.getMusic());
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
                break;
            }
        }
    }

    private void luuObjectXuongFileLocal(Context context,LoveMessage loveMessage) {

        try {
            // Mở một luồng ghi file.
            FileOutputStream out = context.openFileOutput("datalovemessage.txt", Context.MODE_PRIVATE);
            // Ghi dữ liệu.
            out.write(loveMessage.getContent().getBytes());
            out.write("\n".getBytes());
            out.write(loveMessage.getId().getBytes());
            out.write("\n".getBytes());
            out.write(loveMessage.getImage().getBytes());
            out.write("\n".getBytes());
            out.write(loveMessage.getMusic().getBytes());
            out.write("\n".getBytes());
            out.close();
            Toast.makeText(context,"File saved!",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

//    public static void thongBao(Context context,String content, String title) {
//        PendingIntent contentIntent = PendingIntent.getActivity(
//                context,
//                0,
//                new Intent(), // add this
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.tim)
//                .setContentTitle(title)
//                .setContentText(content)
//                .setAutoCancel(true)
//                .setSound(sound)
//                .setContentIntent(contentIntent);
//        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//    }

    public void setData(){
        //hàm chuẩn đúng 7h30 mỗi ngày bắt đầu từ 8/3
        mapNgayThang.put(getKey(),getValue());

//        mapNgayThang.put("28-02-2017 07:30","0");
//        mapNgayThang.put("01-03-2017 07:30","1");
//        mapNgayThang.put("02-03-2017 07:30","2");
    }

    //hàm get node của child firebase
    public static String getValue(){
        DateTime start = new DateTime(2015, 03, 19, 0, 0, 0, 0);
        DateTime end = new DateTime();

        //số ngày yêu nhau - 1
        Days days = Days.daysBetween(start, end);

        //số ngày yêu nhau tính đến 8/3 được tính = 1
        int value = days.getDays() + 1 - 720;

        return String.valueOf(value);
    }

    public String getKey(){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        DateTime dt = new DateTime();
        return df.format(dt.toDate());
    }
}
