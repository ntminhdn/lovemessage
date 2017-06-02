package com.example.user.lovemessages;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {
    String LOG_TAG = "kakaka";
    private ViewPager pager;
    private TabLayout tabLayout;
    private LoveMessage loveMessage;
    private LoveMessageObject message;
    private Realm realm;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private RMS rms;
    private int countMessage = 0;
    private GotMessageListener listener;
    private List<LoveMessageObject> listMessage = new ArrayList<>();
    private ProgressDialog progressDialog;

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

        addControl();
        Log.d(LOG_TAG, "On Create");
        if (rms.isFirstLaunchApp()) {
            Log.d(LOG_TAG, "First lauch: Before check and down");
            checkDownAndSave();
        } else {
            Log.d(LOG_TAG, "On Create: Before check and get");
            checkGetAndSave();
        }
    }

    private void checkGetAndSave() {
        if (Utility.isNetworkAvailable(this)) {
            getMessageAndSave();
        } else {
            Utility.showNetworkUnavailableDialog(MainActivity.this, "Lỗi: Chưa có kết nối mạng. Vui lòng kết nối mạng sau đó bấm thử lại", new NetworkConnectionCallback() {
                        @Override
                        public void onTryAgain() {
                            checkGetAndSave();
                        }
                    }
            );
        }
    }

    private void checkDownAndSave() {
        if (Utility.isNetworkAvailable(this)) {
            downloadMessageAndSave();
        } else {
            Utility.showNetworkUnavailableDialog(MainActivity.this, "Lỗi: Chưa có kết nối mạng. Vui lòng kết nối mạng sau đó bấm thử lại", new NetworkConnectionCallback() {
                        @Override
                        public void onTryAgain() {
                            checkDownAndSave();
                        }
                    }
            );
        }
    }

    private void addControl() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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
        progressDialog.setTitle("Đang tải tất cả tin nhắn");
        progressDialog.setMessage("Vui lòng đợi");

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        for (int i = 0; i <= Integer.valueOf(Utility.getValue()); i++) {
            mDatabase.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loveMessage = dataSnapshot.getValue(LoveMessage.class);
                    LoveMessageObject message = new LoveMessageObject(loveMessage.getContent(), loveMessage.getId(), loveMessage.getImage(), loveMessage.getMusic());
                    listMessage.add(message);
                    message.saveOrUpdate();
                    countMessage++;
                    Log.d(LOG_TAG, "Downloading");
                    if (progressDialog.isShowing() && countMessage == (Integer.valueOf(Utility.getValue()) + 1)) {
                        rms.increaseNumberOfLaunchApp();
                        progressDialog.dismiss();

                        Log.d(LOG_TAG, "After check and down");
                    }

                    if (message.getDays() == Utility.countDays(new DateTime())) {
                        // notify.
                        listener.gotMessageListener(message.getContent());
                        notifyMessage(message);
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
            progressDialog.setTitle("Đang tải tin nhắn của hôm nay");
            progressDialog.setMessage("Vui lòng đợi");

            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

            mDatabase.child(Utility.getValue()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // thêm message hôm nay vào DB.
                    loveMessage = dataSnapshot.getValue(LoveMessage.class);
                    message = new LoveMessageObject(loveMessage.getContent(), loveMessage.getId(), loveMessage.getImage(), loveMessage.getMusic());
                    message.saveOrUpdate();

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Log.d(LOG_TAG, "After check and get");
                    }

                    // Nếu tính đến hôm nay lượng message ko đủ thì download.
                    if (realm.where(LoveMessageObject.class).findAll().size() != (Utility.countDays(new DateTime()) - 721 + 1)) {
                        Log.d(LOG_TAG, "Not enough and down");
                        downloadMessageAndSave();
                    }

                    // notify.
                    listener.gotMessageListener(message.getContent());
                    notifyMessage(message);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

    private void notifyMessage(LoveMessageObject loveMessage) {
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

    public interface GotMessageListener {
        void gotMessageListener(String content);
    }


    public void setListener(GotMessageListener listener) {
        this.listener = listener;
    }
}
