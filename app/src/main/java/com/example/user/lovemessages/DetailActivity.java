package com.example.user.lovemessages;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        intent = getIntent();
        addControl();
        setControl();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Picasso.with(this).load(url).into(imgImage);
    }

    private void addControl() {
        imgImage = (ImageView) findViewById(R.id.imgImage);
        tvDetailMessage = (TextView) findViewById(R.id.tvDetailMessage);
        tvID = (TextView) findViewById(R.id.tvID);
    }
}
