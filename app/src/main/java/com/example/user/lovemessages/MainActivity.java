package com.example.user.lovemessages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.danlew.android.joda.JodaTimeAndroid;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private TabLayout tabLayout;
    private LoveMessage loveMessage;
    private LoveMessageObject message;
    private Realm realm;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private RMS rms;
    private int countMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_layout);
        JodaTimeAndroid.init(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        rms = RMS.getInstance();
        rms.init(this);
        rms.load();

        if (rms.isFirstLaunchApp()) {
            downloadMessageAndSave();
        }
        rms.increaseNumberOfLaunchApp();
        getMessageAndSave();

        addControl();
    }

    private void addControl() {
        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        FragmentManager manager = getSupportFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    private void downloadMessageAndSave() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Đang tải tin nhắn");
        progressDialog.setMessage("Vui lòng đợi");
        progressDialog.show();
        for (int i = 0; i < Integer.valueOf(Utility.getValue()); i++) {
            mDatabase.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loveMessage = dataSnapshot.getValue(LoveMessage.class);
                    LoveMessageObject message = new LoveMessageObject(loveMessage.getContent(), loveMessage.getId(), loveMessage.getImage(), loveMessage.getMusic());
                    message.saveOrUpdate();
                    countMessage++;
                    if (progressDialog.isShowing() && countMessage == Integer.valueOf(Utility.getValue())) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

    public void getMessageAndSave() {
        message = Realm.getDefaultInstance().where(LoveMessageObject.class).equalTo("id", Utility.getNgayHienTai()).findFirst();
        if (message == null) {
            mDatabase.child(Utility.getValue()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loveMessage = dataSnapshot.getValue(LoveMessage.class);
                    message = new LoveMessageObject(loveMessage.getContent(), loveMessage.getId(), loveMessage.getImage(), loveMessage.getMusic());
                    message.saveOrUpdate();
                    notifyMessage();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

    private void notifyMessage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.tim)
                .setContentTitle(loveMessage.getId())
                .setContentText(loveMessage.getContent())
                .setAutoCancel(true)
                .setSound(sound)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
