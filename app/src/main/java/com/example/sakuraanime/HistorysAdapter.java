package com.example.sakuraanime;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sakuraanime.database.model.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class HistorysAdapter extends RecyclerView.Adapter<HistorysAdapter.MyViewHolder> {

    private Context context;
    private List<History> HistorysList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView historyTitle;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            historyTitle = view.findViewById(R.id.history_title);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public HistorysAdapter(Context context, List<History> HistorysList) {
        this.context = context;
        this.HistorysList = HistorysList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = HistorysList.get(position);

        holder.historyTitle.setText(history.getBarTitle()+" "+history.getTitle());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(history.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return HistorysList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d   HH:mm");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}