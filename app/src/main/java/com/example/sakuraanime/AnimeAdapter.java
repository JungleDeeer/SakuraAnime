package com.example.sakuraanime;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.ViewHolder> {
    private List<Anime> mAnimeList;
    private Dialog mDialog;
    private Thread threrad;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View animeView;
        TextView itemName;
        TextView itemAlias;
        TextView itemInfo;
        TextView itemContent;

        public ViewHolder(View view){
            super(view);
            animeView = view;
            itemName = view.findViewById(R.id.item_name);
            itemAlias = view.findViewById(R.id.item_alias);
            itemInfo = view.findViewById(R.id.item_info);
            itemContent = view.findViewById(R.id.item_content);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.animeView.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){


                mDialog = DialogThridUtils.showWaitDialog(view.getContext(), "加载中", true, true);
//                Toast.makeText(view.getContext(),"加载中，请稍候",Toast.LENGTH_LONG).show();
                threrad = new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try{
                                int position = holder.getAbsoluteAdapterPosition();
                                ArrayList<Episode> episodeList = mAnimeList.get(position).getEpisodeList();
                                String playUrl = episodeList.get(0).getEpisodeUrl().entrySet().iterator().next().getValue();
                                String title = episodeList.get(0).getEpisodeUrl().entrySet().iterator().next().getKey();
                                String finalUrl ="";
                                finalUrl = getFinalUrl(playUrl);
                                if(finalUrl == null || finalUrl.equals("")){
                                    Toast.makeText(view.getContext(),"加载异常，请换源",Toast.LENGTH_SHORT).show();
                                }else {
                                    Intent intent = new Intent();
                                    Bundle data = new Bundle();
                                    data.putString("finalUrl",finalUrl);
                                    data.putString("title",title);
                                    data.putSerializable("EpisodeList",episodeList);
                                    intent.putExtras(data);
                                    intent.setClass(view.getContext(),WatchActivity.class);
                                    DialogThridUtils.closeDialog(mDialog);
                                    view.getContext().startActivity(intent);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                    }
                });
                threrad.start();
            }
        });
        return holder;
    }

    @Override
    public void  onBindViewHolder(@NonNull ViewHolder holder ,int position){
        Anime anime = mAnimeList.get(position);
        holder.itemName.setText(anime.getName());
        holder.itemAlias.setText(anime.getAlias());
        holder.itemInfo.setText(anime.getInfo());
        holder.itemContent.setText(anime.getContent());
    }

    @Override
    public int getItemCount(){
        return mAnimeList.size();
    }

    public AnimeAdapter(List<Anime> animeList){
        mAnimeList = animeList;
    }

    public static String getFinalUrl(String playUrl)throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);


        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间

        HtmlPage page = webClient.getPage(playUrl);
        webClient.waitForBackgroundJavaScript(1 * 1000);               // 等待js后台执行30秒
        String pageAsXml = page.asXml();
        Document doc = Jsoup.parse(pageAsXml);
        String playerUrl = doc.select("div[class=player]").select("iframe").attr("src");


        page = webClient.getPage(playerUrl);
        pageAsXml = page.asXml();
        doc = Jsoup.parse(pageAsXml);
        String script = doc.select("script[type=text/javascript]").get(0).data();

        String patternString = "var video =  '{1}(\\S*)'";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(script);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }



}
