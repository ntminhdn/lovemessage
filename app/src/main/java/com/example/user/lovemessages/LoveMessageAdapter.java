package com.example.user.lovemessages;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User on 28/02/2017.
 */

public class LoveMessageAdapter extends ArrayAdapter<LoveMessage> {
    Activity context;
    int resource;
    List<LoveMessage> objects;

    public LoveMessageAdapter(Activity context, int resource, List<LoveMessage> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Nullable
    @Override
    public LoveMessage getItem(int position) {
        return objects.get(position);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
        }
        ImageView imgItemImage = (ImageView) row.findViewById(R.id.imgItemImage);
        TextView tvTitle = (TextView) row.findViewById(R.id.tvTitle);
        TextView tvDate = (TextView) row.findViewById(R.id.tvDate);
        TextView tvContent = (TextView) row.findViewById(R.id.tvContent);

        LoveMessage loveMessage = objects.get(position);
        if(loveMessage.getImage() == null){
            imgItemImage.setImageResource(R.drawable.tim);
        } else {
            new DownloadImageTask(imgItemImage)
                    .execute(loveMessage.getImage());
        }

        tvTitle.setText(loveMessage.getId());
        tvContent.setText(loveMessage.getContent());
        tvDate.setText(MainActivity.getNgayHienTai().substring(0,MainActivity.getNgayHienTai().indexOf(" ")));

        return row;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
