package com.example.user.lovemessages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import io.realm.Realm;

/**
 * Created by User on 27/02/2017.
 */

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ImageView imgImage;
    private TextView tvDetailMessage, tvID;
    private LoveMessageObject message;
    private String id = "";
    private String music = "";
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            music = getIntent().getStringExtra("music");
        }

        message = Realm.getDefaultInstance().where(LoveMessageObject.class).equalTo("id", id).findFirst();
        tvID.setText(message.getId());
        tvDetailMessage.setText(message.getContent());
        if (message.getImage() != null && Utility.isNetworkAvailable(this)) {
            Picasso.with(this).load(message.getImage()).into(imgImage);
        } else {
            imgImage.setImageResource(R.drawable.tim);
        }

        if (!TextUtils.isEmpty(music)) {
            Sound.getInstance().playSoundFromUrl(music);
        }
    }

    private void addControl() {
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.detailLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        imgImage = (ImageView) findViewById(R.id.imgImage);
        tvDetailMessage = (TextView) findViewById(R.id.tvDetailMessage);
        tvID = (TextView) findViewById(R.id.tvID);
    }

    @Override
    public void onRefresh() {
        setControl();
        swipeLayout.setRefreshing(false);
    }
}
