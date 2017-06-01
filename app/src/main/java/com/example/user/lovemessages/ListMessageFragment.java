package com.example.user.lovemessages;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by minh.nt on 5/30/2017.
 */

public class ListMessageFragment extends Fragment implements SearchView.OnQueryTextListener {
    private LoveMessageAdapter adapterMessage;
    private RecyclerView lvMessage;
    private RealmResults<LoveMessageObject> list;
    private SearchView searchView;
    private RealmList<LoveMessageObject> temp = new RealmList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = Realm.getDefaultInstance().where(LoveMessageObject.class).findAllSorted("days", Sort.DESCENDING);
        list.addChangeListener(new RealmChangeListener<RealmResults<LoveMessageObject>>() {
            @Override
            public void onChange(RealmResults<LoveMessageObject> element) {
                temp.clear();
                temp.addAll(element);
                adapterMessage.notifyDataSetChanged();
            }
        });
        temp.addAll(list);
        adapterMessage = new LoveMessageAdapter(temp);
    }

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
