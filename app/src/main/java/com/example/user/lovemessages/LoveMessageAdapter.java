package com.example.user.lovemessages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 28/02/2017.
 */

public class LoveMessageAdapter extends RecyclerView.Adapter {
    List<LoveMessage> objects;

    public LoveMessageAdapter(List<LoveMessage> objects) {
        this.objects = objects;
    }

    class LoveMessageVH extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDate, tvContent;
        private ImageView imgItemImage;
        private LoveMessage message;

        public LoveMessageVH(final View itemView) {
            super(itemView);
            imgItemImage = (ImageView) itemView.findViewById(R.id.imgItemImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intentService = new Intent(itemView.getContext(), BackgroundSoundService.class);
                            intentService.putExtra("music", message.getMusic());
                            itemView.getContext().startService(intentService);
                        }
                    }).start();

                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra("id", message.getId());
                    intent.putExtra("content", message.getContent());
                    intent.putExtra("image", message.getImage());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void setData(LoveMessage loveMessage) {
            message = loveMessage;

            if (loveMessage.getImage() == null) {
                imgItemImage.setImageResource(R.drawable.tim);
            } else {
                Picasso.with(itemView.getContext()).load(loveMessage.getImage()).into(imgItemImage);
            }

            tvTitle.setText(loveMessage.getId());
            tvContent.setText(loveMessage.getContent());
            tvDate.setText(MainActivity.getNgayHienTai().substring(0, MainActivity.getNgayHienTai().indexOf(" ")));

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LoveMessageVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_message, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((LoveMessageVH) holder).setData(objects.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return objects == null ? 0 : objects.size();
    }
}
