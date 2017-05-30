package com.example.user.lovemessages;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by minh.nt on 5/30/2017.
 */

public class ListMessageFragment extends Fragment implements SearchView.OnQueryTextListener {
    private LoveMessageAdapter adapterMessage;
    private RecyclerView lvMessage;
    private RealmResults<LoveMessageObject> list;
    private SearchView searchView;
    private List<LoveMessageObject> temp = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_list_message, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        addControl(view);
    }

    private void addControl(View view) {
        lvMessage = (RecyclerView) view.findViewById(R.id.lvMessage);
        lvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        lvMessage.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10, 10);
            }
        });
        list = Realm.getDefaultInstance().where(LoveMessageObject.class).findAllSorted("days");
        temp.addAll(list);
        adapterMessage = new LoveMessageAdapter(temp);
        lvMessage.setAdapter(adapterMessage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem itemSearch = menu.findItem(R.id.menu_item_member_list_search_member);
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    private void filter(String newText) {
        newText = newText.toLowerCase();
        temp.clear();
        for (LoveMessageObject message : list) {
            String id = message.getId().toLowerCase();
            int date = message.getDays();
            if (id.contains(newText) || String.valueOf(date).contains(newText)) {
                temp.add(message);
            }
        }
        adapterMessage.notifyDataSetChanged();
    }
}
