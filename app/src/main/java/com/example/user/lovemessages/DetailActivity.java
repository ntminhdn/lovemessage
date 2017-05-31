package com.example.user.lovemessages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;

/**
 * Created by User on 27/02/2017.
 */

public class DetailActivity extends AppCompatActivity {
    private ImageView imgImage;
    private TextView tvDetailMessage, tvID;
    private LoveMessageObject message;
    private String id = "";

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
        }

        message = Realm.getDefaultInstance().where(LoveMessageObject.class).equalTo("id", id).findFirst();
        tvID.setText(message.getId());
        tvDetailMessage.setText(message.getContent());
        if (message.getImage() != null && Utility.isNetworkAvailable(this)) {
            Picasso.with(this).load(message.getImage()).into(imgImage);
        } else {
            imgImage.setImageResource(R.drawable.tim);
        }
    }

    private void addControl() {
        imgImage = (ImageView) findViewById(R.id.imgImage);
        tvDetailMessage = (TextView) findViewById(R.id.tvDetailMessage);
        tvID = (TextView) findViewById(R.id.tvID);
    }
}
