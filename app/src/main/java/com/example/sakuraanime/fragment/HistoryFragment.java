package com.example.sakuraanime.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sakuraanime.Episode;
import com.example.sakuraanime.HistorysAdapter;
import com.example.sakuraanime.R;
import com.example.sakuraanime.WatchActivity;
import com.example.sakuraanime.database.DatabaseHelper;
import com.example.sakuraanime.database.model.History;
import com.example.sakuraanime.utils.MyDividerItemDecoration;
import com.example.sakuraanime.utils.RecyclerTouchListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {
    private HistorysAdapter mHistoryAdapter;
    private List<History> historyList = new ArrayList<>();
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private ImageView noHistoryView;

    private DatabaseHelper dbHelper;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        frameLayout = view.findViewById(R.id.frame_layout);
        recyclerView = view.findViewById(R.id.history_recyclerView);
        noHistoryView = view.findViewById(R.id.iv_history);
        noHistoryView.setImageResource(R.drawable.ic_history_none);

        dbHelper = new DatabaseHelper(getActivity().getApplicationContext());

        historyList.addAll(dbHelper.getAllHistorys());
        mHistoryAdapter = new HistorysAdapter(getActivity().getApplicationContext(), historyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mHistoryAdapter);

        toggleEmptyHistorys();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                History history = historyList.get(position);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Episode>>() {}.getType();

                ArrayList<String> episodeList = gson.fromJson(history.getStrEpisodeList(), type);

                Intent intent = new Intent();
                Bundle data = new Bundle();
                data.putString("finalUrl",history.getFinalUrl());
                data.putString("title",history.getTitle());
                data.putString("barTitle",history.getBarTitle());
                data.putSerializable("EpisodeList",episodeList);
                intent.putExtras(data);
                intent.setClass(view.getContext(), WatchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                getActivity().getApplicationContext().startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        return view;
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

    }

    private void toggleEmptyHistorys() {
        // you can check notesList.size() > 0

        if (dbHelper.getHistorysCount() > 0) {
            noHistoryView.setVisibility(View.GONE);
        } else {
            noHistoryView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteHistory(int position) {
        // deleting the note from db
        dbHelper.deleteHistory(historyList.get(position));

        // removing the note from the list
        historyList.remove(position);
        mHistoryAdapter.notifyItemRemoved(position);

        toggleEmptyHistorys();
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{ "删除"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选项");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    deleteHistory(position);

            }
        });
        builder.show();
    }


    @Override
    public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (!hidden && isAdded()){
            //显示ing
            historyList.clear();
            historyList.addAll(dbHelper.getAllHistorys());
            mHistoryAdapter.notifyDataSetChanged();
            toggleEmptyHistorys();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            historyList.clear();
            historyList.addAll(dbHelper.getAllHistorys());
            mHistoryAdapter.notifyDataSetChanged();
            toggleEmptyHistorys();
        } else {
            //相当于Fragment的onPause
        }
    }
}