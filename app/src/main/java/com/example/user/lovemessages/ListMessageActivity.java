package com.example.user.lovemessages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListMessageActivity extends AppCompatActivity {
    DatabaseReference database;
    Intent intentService;
    ArrayList<LoveMessage> listMessages = new ArrayList<>();
    LoveMessageAdapter adapterMessage;
    ListView lvMessage;
    int node = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);
        addControl();
        addEvent();
    }

    private void addEvent() {
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        intentService = new Intent(getApplicationContext(), BackgroundSoundService.class);
                        intentService.putExtra("music", adapterMessage.getItem(position).getMusic());
                        startService(intentService);
                    }
                }).start();

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", adapterMessage.getItem(position).getId());
                intent.putExtra("content", adapterMessage.getItem(position).getContent());
                intent.putExtra("image", adapterMessage.getItem(position).getImage());
                startActivity(intent);
            }
        });
    }

    private void addControl() {
        lvMessage = (ListView) findViewById(R.id.lvMessage);
        adapterMessage = new LoveMessageAdapter(ListMessageActivity.this,R.layout.item_list_message,listMessages);
        lvMessage.setAdapter(adapterMessage);
        database = FirebaseDatabase.getInstance().getReference();
        node = Integer.parseInt(AlarmReceiver.getValue());

        //tạm thời để từ 0 đến 3 sau này sửa từ 0 đến node
        for (int i = 0; i < 3; i++) {
            System.out.println(i+"   ====================");
            database.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LoveMessage loveMessage = dataSnapshot.getValue(LoveMessage.class);
                    listMessages.add(loveMessage);
                    adapterMessage.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}
