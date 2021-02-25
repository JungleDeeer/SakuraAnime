package com.example.sakuraanime;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Iterator;

import static com.example.sakuraanime.AnimeAdapter.getFinalUrl;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder>{
    ArrayList<Episode> msourceList = new ArrayList<>();
    private Context mContext;
    private Thread thread;
    private int layoutPosition = 0;
    String playingTittle;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemSource;
        View sourceView;
        public ViewHolder(View view){
            super(view);
            sourceView = view;
            itemSource = view.findViewById(R.id.item_sources);
        }
    }

    @NonNull
    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_source,parent,false);
        final SourceAdapter.ViewHolder holder = new ViewHolder(view);
        holder.sourceView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notifyItemChanged(layoutPosition);
                layoutPosition = holder.getAbsoluteAdapterPosition();
                notifyItemChanged(layoutPosition);
                Toast.makeText(view.getContext(),"换源中，请稍候",Toast.LENGTH_LONG).show();
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int position = holder.getAbsoluteAdapterPosition();
                            String sourceUrl = msourceList.get(position).getEpisodeUrl().get(playingTittle);
                            if(sourceUrl == null||sourceUrl.equals(""))
                            {
                                Intent intent = new Intent("source-change");
                                intent.putExtra("newSource",position);
                                intent.putExtra("newUrl","");
                                LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                            }
                            else{
                                String finalUrl = "";
                                finalUrl = getFinalUrl(sourceUrl);
                                if(finalUrl == null || finalUrl.equals("")){
                                    Intent intent = new Intent("source-change");
                                    intent.putExtra("newSource",position);
                                    intent.putExtra("newUrl","");
                                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                                }else{
                                    Intent intent = new Intent("source-change");
                                    intent.putExtra("newSource",position);
                                    intent.putExtra("newUrl",finalUrl);
                                    intent.putExtra("newTittle",playingTittle);
                                    LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            }
        });
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void  onBindViewHolder(@NonNull SourceAdapter.ViewHolder holder , int position){
//        Episode episode = msourceList.get(position);
        if(position == layoutPosition){
            holder.sourceView.setForeground(
                    mContext.getResources().getDrawable(R.drawable.bg_selected));
            holder.itemSource.setText("源"+position);
            holder.itemSource.setTextColor(
                    mContext.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.sourceView.setForeground(
                    mContext.getResources().getDrawable(R.drawable.bg_normal));
            holder.itemSource.setText("源"+position);
            holder.itemSource.setTextColor(
                    mContext.getResources().getColor(R.color.black_alpha_45));
        }



    }

    @Override
    public int getItemCount(){
        return msourceList.size();
    }



    public SourceAdapter(ArrayList<Episode> sourceList,String tittle, Context context){
        msourceList= sourceList;
        playingTittle = tittle;
        mContext = context;}

}
