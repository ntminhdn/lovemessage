package com.example.user.lovemessages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by minh.nt on 5/30/2017.
 */

public class MainFragment extends Fragment {
    private ImageView imgKyn, imgNy, imgWall, imgPlay;
    private TextView tvKyn, tvNy, tvLove, tvDays;
    private TextView tvMessage;
    private Intent intentService;
    private String userChoosenTask;
    private final int REQUEST_CAMERA_WALL = 1, SELECT_FILE_WALL = 4;
    private final int REQUEST_CAMERA_KYN = 2, SELECT_FILE_KYN = 5;
    private final int REQUEST_CAMERA_NY = 3, SELECT_FILE_NY = 6;
    boolean isImageFitToScreen;
    private LoveMessageObject message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControl(view);
        init();
        addEvent();
    }

    private void addControl(View view) {
        tvKyn = (TextView) view.findViewById(R.id.tvKyn);
        tvNy = (TextView) view.findViewById(R.id.tvNy);
        tvLove = (TextView) view.findViewById(R.id.tvLove);
        tvDays = (TextView) view.findViewById(R.id.tvDays);
        tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        imgKyn = (ImageView) view.findViewById(R.id.imgKyn);
        imgNy = (ImageView) view.findViewById(R.id.imgNy);
        imgWall = (ImageView) view.findViewById(R.id.imgWall);
        imgPlay = (ImageView) view.findViewById(R.id.imageView4);
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
                if (message != null) {
                    intentService = new Intent(getContext(), BackgroundSoundService.class);
                    intentService.putExtra("music", message.getMusic());
                    getContext().startService(intentService);

                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("id", message.getId());
                    startActivity(intent);
                } else {
                    Utility.showMessage(getContext(), "Lỗi: Kiểm tra lại Internet");
                }
            }
        });
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getContext());

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
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
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
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_text, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
        message = Realm.getDefaultInstance().where(LoveMessageObject.class).equalTo("id", Utility.getNgayHienTai()).findFirst();

        if (message != null) {
            tvMessage.setText(message.getContent());
        }

        // Lắng nghe khi MainActivity get message xong
        ((MainActivity) getActivity()).setListener(new MainActivity.GotMessageListener() {
            @Override
            public void gotMessageListener(String content) {
                tvMessage.setText(content);
            }
        });

        //đếm ngày hiển thị lên view
        tvDays.setText(Utility.countDays(new DateTime()) + " ngày bên nhau");
        //firebase
        FirebaseMessaging.getInstance().subscribeToTopic("testfcm");
        String token = FirebaseInstanceId.getInstance().getToken();

    }
}
