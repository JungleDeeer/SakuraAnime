package com.example.sakuraanime;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.sakuraanime.AnimeAdapter.getFinalUrl;

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> {
    ArrayList<Episode> msourceList;
    private Context mContext;
    private Thread thread;
    private int layoutPosition = 0;
    List<String> mEpisodeName;
    List<String> mEpisodePlayUrl;
    private int selectedPos = RecyclerView.NO_POSITION;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemEpisode;
        View episodeView;
        public ViewHolder(View view){
            super(view);
            episodeView = view;
            itemEpisode = view.findViewById(R.id.item_episodes);
        }
    }

    @NonNull
    @Override
    public EpisodeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_episode,parent,false);
        final EpisodeListAdapter.ViewHolder holder = new EpisodeListAdapter.ViewHolder(view);
        holder.episodeView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notifyItemChanged(layoutPosition);
                layoutPosition = holder.getAbsoluteAdapterPosition();
                notifyItemChanged(layoutPosition);
                Toast.makeText(view.getContext(),"换集中，请稍候",Toast.LENGTH_LONG).show();
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int position = holder.getAbsoluteAdapterPosition();
                            String toPlayUrl = mEpisodePlayUrl.get(position);
                            String newTittle = mEpisodeName.get(position);
                            String finalUrl = "";
                            finalUrl = getFinalUrl(toPlayUrl);

                            Intent intent = new Intent("episode-change");
                            if(finalUrl == null||finalUrl.equals(""))
                            {
                                intent.putExtra("newUrl","");
                                intent.putExtra("newTittle",newTittle);
                                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                            }
                            else{
                                intent.putExtra("newUrl",finalUrl);
                                intent.putExtra("newTittle",newTittle);
                                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });
        return  holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull EpisodeListAdapter.ViewHolder holder, int position){
        if(position == layoutPosition){
            holder.episodeView.setForeground(
                    mContext.getResources().getDrawable(R.drawable.bg_selected));
            holder.itemEpisode.setText(mEpisodeName.get(position));
            holder.itemEpisode.setTextColor(
                    mContext.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.episodeView.setForeground(
                    mContext.getResources().getDrawable(R.drawable.bg_normal));
            holder.itemEpisode.setText(mEpisodeName.get(position));
            holder.itemEpisode.setTextColor(
                    mContext.getResources().getColor(R.color.black_alpha_45));
        }


    }

    @Override
    public int getItemCount(){return mEpisodePlayUrl.size();}

    public EpisodeListAdapter(List<String> episodeName, List<String> episodePlayUrl, Context context){
        mEpisodeName = episodeName;
        mEpisodePlayUrl = episodePlayUrl;
        mContext = context;}
}
