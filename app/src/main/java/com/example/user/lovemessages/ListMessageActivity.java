package com.example.user.lovemessages;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
    ArrayList<LoveMessage> listMessages = new ArrayList<>();
    LoveMessageAdapter adapterMessage;
    RecyclerView lvMessage;
    int node = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_message);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        addControl();
        addEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addEvent() {

    }

    private void addControl() {
        lvMessage = (RecyclerView) findViewById(R.id.lvMessage);
        lvMessage.setLayoutManager(new LinearLayoutManager(this));
        lvMessage.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10, 10);
            }
        });
        adapterMessage = new LoveMessageAdapter(listMessages);
        lvMessage.setAdapter(adapterMessage);
        database = FirebaseDatabase.getInstance().getReference();
        node = Integer.parseInt(AlarmReceiver.getValue());

        //tạm thời để từ 0 đến 3 sau này sửa từ 0 đến node
        for (int i = 0; i < node; i++) {
            System.out.println(i + "   ====================");
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
