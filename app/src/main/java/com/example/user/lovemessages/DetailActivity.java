package com.example.user.lovemessages;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

/**
 * Created by User on 27/02/2017.
 */

public class DetailActivity extends AppCompatActivity {
    Intent intent;
    ImageView imgImage;
    TextView tvDetailMessage,tvID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        intent = getIntent();
        addControl();
        setControl();
    }

    private void setControl() {
        if(intent != null){
            String id = intent.getStringExtra("id");
            String content = intent.getStringExtra("content");
            String image = intent.getStringExtra("image");
            tvID.setText(id);
            tvDetailMessage.setText(content);
            hienThiAnh(image);
        }
    }

    private void hienThiAnh(String url) {
        new DownloadImageTask(imgImage)
                .execute(url);
    }

    private void addControl() {
        imgImage = (ImageView) findViewById(R.id.imgImage);
        tvDetailMessage = (TextView) findViewById(R.id.tvDetailMessage);
        tvID = (TextView) findViewById(R.id.tvID);
    }
}
