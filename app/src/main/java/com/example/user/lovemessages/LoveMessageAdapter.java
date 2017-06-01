package com.example.user.lovemessages;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by User on 28/02/2017.
 */

public class LoveMessageAdapter extends RecyclerView.Adapter {
    private RealmList<LoveMessageObject> objects = new RealmList<>();

    public LoveMessageAdapter(RealmList<LoveMessageObject> objects) {
        this.objects = objects;
    }

    class LoveMessageVH extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDate, tvContent;
        private ImageView imgItemImage;
        private LoveMessageObject message;

        public LoveMessageVH(final View itemView) {
            super(itemView);
            imgItemImage = (ImageView) itemView.findViewById(R.id.imgItemImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isNetworkAvailable(itemView.getContext())) {
                        Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                        intent.putExtra("id", message.getId());
                        intent.putExtra("music", message.getMusic());
                        itemView.getContext().startActivity(intent);
                    } else {
                        Utility.showMessage(itemView.getContext(), "Lỗi: Kiểm tra lại Internet");
                    }
                }
            });
        }

        public void setData(LoveMessageObject loveMessage) {
            message = loveMessage;


            if (loveMessage.getImage() == null) {
                imgItemImage.setImageResource(R.drawable.tim);
            } else {
//                    Picasso.with(itemView.getContext()).load(message.getImage()).into(imgItemImage);

                Glide.with(itemView.getContext())
                        .load(loveMessage.getImage())
                        .thumbnail(Glide.with(itemView.getContext()).load(R.drawable.loading_spinner))
                        .fitCenter()
                        .crossFade()
                        .into(imgItemImage);
            }
            tvTitle.setText(loveMessage.getId());
            tvContent.setText(loveMessage.getContent());

            tvDate.setText(String.valueOf(Utility.countDays(Utility.convertToDate(loveMessage.getId()))) + " ngày bên nhau");
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
