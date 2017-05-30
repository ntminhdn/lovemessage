package com.example.user.lovemessages;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.jar.*;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    ImageView imgKyn, imgNy, imgWall, imgPlay, imgShare, imgStop, imgList;
    TextView tvKyn, tvNy, tvLove, tvDays;
    TextView tvMessage;
    private PendingIntent pendingIntent;
    Intent intentService;
    //    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String userChoosenTask;
    private final int REQUEST_CAMERA_WALL = 1, SELECT_FILE_WALL = 4;
    private final int REQUEST_CAMERA_KYN = 2, SELECT_FILE_KYN = 5;
    private final int REQUEST_CAMERA_NY = 3, SELECT_FILE_NY = 6;
    boolean isImageFitToScreen;
    //    MediaPlayer player = new MediaPlayer();
    LoveMessage loveMessage;
    String id = "", content = "", image = "", music = "";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();

//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        player.setLooping(true);
        startAt6();
        setContentView(R.layout.activity_main);
        addControl();
        init();
        addEvent();

        //nhận data từ Alarm
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            content = intent.getStringExtra("content");
            image = intent.getStringExtra("image");
            music = intent.getStringExtra("music");
            loveMessage = new LoveMessage(content, id, image, music);
            tvMessage.setText(loveMessage.getContent());
            if (loveMessage.getContent() != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        notifyMessage();
                    }
                }).start();
            }
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

    private void addControl() {
        tvKyn = (TextView) findViewById(R.id.tvKyn);
        tvNy = (TextView) findViewById(R.id.tvNy);
        tvLove = (TextView) findViewById(R.id.tvLove);
        tvDays = (TextView) findViewById(R.id.tvDays);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        imgKyn = (ImageView) findViewById(R.id.imgKyn);
        imgNy = (ImageView) findViewById(R.id.imgNy);
        imgWall = (ImageView) findViewById(R.id.imgWall);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgStop = (ImageView) findViewById(R.id.imgStop);
        imgList = (ImageView) findViewById(R.id.imgList);
    }

    private void addEvent() {
        tvKyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nhapBietDanh("Biệt danh của Kyn: ", tvKyn);
            }
        });

        tvNy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nhapBietDanh("Biệt danh của Ny: ", tvNy);
            }
        });

        tvLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nhapBietDanh("Love status: ", tvLove);
            }
        });


        imgWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(REQUEST_CAMERA_WALL, SELECT_FILE_WALL);
            }
        });
        imgKyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(REQUEST_CAMERA_KYN, SELECT_FILE_KYN);
            }
        });
        imgNy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(REQUEST_CAMERA_NY, SELECT_FILE_NY);
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        intentService = new Intent(getApplicationContext(), BackgroundSoundService.class);
                        intentService.putExtra("music", loveMessage.getMusic());
                        startService(intentService);
                    }
                }).start();

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", loveMessage.getId());
                intent.putExtra("content", loveMessage.getContent());
                intent.putExtra("image", loveMessage.getImage());
                startActivity(intent);
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, loveMessage.getId());
                share.putExtra(Intent.EXTRA_TEXT, loveMessage.getId() + "\n" + loveMessage.getContent() + "\n" + "Music: " + loveMessage.getMusic().substring(0, loveMessage.getMusic().length() - 1) + "0");
                startActivity(Intent.createChooser(share, "Chia sẻ tin vui.."));
            }
        });

        imgStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intentService);
            }
        });

        imgList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListMessageActivity.class);
                startActivity(intent);
            }
        });

    }

    private void countDays() {
        DateTime start = new DateTime(2015, 03, 19, 0, 0, 0, 0);
        DateTime end = new DateTime();
        Days days = Days.daysBetween(start, end);
        tvDays.setText((days.getDays() + 1) + " ngày bên nhau");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent(requestCode);
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent(requestCode);
                } else {

                }
                break;
        }
    }

    //chọn ảnh bằng cách chụp hoặc chọn từ album
    private void selectImage(final int camera, final int select) {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent(camera);

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent(select);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //hàm dùng intent gửi dữ liệu: ảnh được chọn từ album
    private void galleryIntent(int SELECT_FILE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    //hàm dùng intent gửi dữ liệu: ảnh chụp từ máy ảnh
    private void cameraIntent(int REQUEST_CAMERA) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    //hàm get dữ liệu từ intent và chọn xử lý chụp từ camera hay chọn từ album
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE_WALL:
                    onSelectFromGalleryResult(data, SELECT_FILE_WALL);
                    break;
                case SELECT_FILE_KYN:
                    onSelectFromGalleryResult(data, SELECT_FILE_KYN);
                    break;
                case SELECT_FILE_NY:
                    onSelectFromGalleryResult(data, SELECT_FILE_NY);
                    break;
                case REQUEST_CAMERA_WALL:
                    onCaptureImageResult(data, REQUEST_CAMERA_WALL);
                    break;
                case REQUEST_CAMERA_KYN:
                    onCaptureImageResult(data, REQUEST_CAMERA_KYN);
                    break;
                case REQUEST_CAMERA_NY:
                    onCaptureImageResult(data, REQUEST_CAMERA_NY);
                    break;
            }
        }
    }

    //hàm get dữ liệu từ Intent và hiển thị ảnh chụp từ camera
    private void onCaptureImageResult(Intent data, int camera) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (camera) {
            case REQUEST_CAMERA_WALL:
                imgWall.setImageBitmap(thumbnail);
                break;
            case REQUEST_CAMERA_KYN:
                imgKyn.setImageBitmap(thumbnail);
                break;
            case REQUEST_CAMERA_NY:
                imgNy.setImageBitmap(thumbnail);
                break;
        }
    }

    //hàm get dữ liệu từ Intent và hiển thị ảnh chọn từ album
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data, int select) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (select) {
            case SELECT_FILE_WALL:
                fitFullScreen(imgWall);
                imgWall.setImageBitmap(bm);
                break;
            case SELECT_FILE_KYN:
                imgKyn.setImageBitmap(bm);
                break;
            case SELECT_FILE_NY:
                imgNy.setImageBitmap(bm);
                break;
        }
    }

    //hàm fit ảnh full ImageView
    public void fitFullScreen(ImageView img) {
        if (isImageFitToScreen) {
            isImageFitToScreen = false;
            img.setLayoutParams(new LinearLayout.LayoutParams(img.getWidth(), img.getHeight()));
            img.setAdjustViewBounds(true);
        } else {
            isImageFitToScreen = true;
            img.setLayoutParams(new LinearLayout.LayoutParams(img.getWidth(), img.getHeight()));
            img.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }


    private void nhapBietDanh(String name, final TextView tv) {
        //tạo dialog từ layout
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_text, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        //get widget từ layout và tùy chỉnh
        TextView tvNhapBietDanh = (TextView) promptView.findViewById(R.id.tvNhapBietDanh);
        tvNhapBietDanh.setText(name);
        final EditText edNhapBietDanh = (EditText) promptView.findViewById(R.id.edNhapBietDanh);

        //set event cho dialog
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tv.setText(edNhapBietDanh.getText());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog myAlertDialog = alertDialogBuilder.create();
        myAlertDialog.show();
    }

    private void init() {
        //đếm ngày hiển thị lên view
        countDays();

        //firebase
        FirebaseMessaging.getInstance().subscribeToTopic("testfcm");
        String token = FirebaseInstanceId.getInstance().getToken();

    }

    private void startAt6() {
        Intent launchIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, launchIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 07);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        System.out.println(calendar.getTime());

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24* 60* 1 * 60 * 1000, pendingIntent);


//        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30*1000, 1*60*1000,  pendingIntent);
//        manager.setRepeating(AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime(), interval,
//                pendingIntent);
    }

    private LoveMessage docDoiTuongTuFileLocal() {
        LoveMessage Message = null;
        try {

            // Mở một luồng đọc file.
            FileInputStream in = this.openFileInput("datalovemessage.txt");

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append(";");
            }
            String[] mangData = sb.toString().substring(0, sb.length() - 1).split(";");
            Message = new LoveMessage(mangData[0], mangData[1], mangData[2], mangData[3]);

        } catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return Message;
    }

    public static String getNgayHienTai() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        DateTime dt = new DateTime();
        return df.format(dt.toDate());
    }


}
