package com.example.sakuraanime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class WatchActivity extends AppCompatActivity {

    int playingSource;
    String finalUrl;
    JzvdStd jzvdStd;
    String title;
    List<String> episodeName = new ArrayList<>();
    List<String> episodePlayUrl = new ArrayList<>();
    Map<String,String> episodeUrl;
    ArrayList<Episode> episodeList = new ArrayList<Episode>();
    EpisodeListAdapter episodeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

//        String playUrl = anime.getEpisodeList().get(0).getEpisodeUrl().entrySet().iterator().next().getValue();
//        String title = anime.getEpisodeList().get(0).getEpisodeUrl().entrySet().iterator().next().getKey();
        jzvdStd = (JzvdStd) findViewById(R.id.jz_video);
//        jzvdStd.setUp("https://gss3.baidu.com/6LZ0ej3k1Qd3ote6lo7D0j9wehsv/tieba-smallvideo/60_10b466747b9ae5fd5b8cfe3308f460bc.mp4"
//                , "女高中生虚度日常");
//        String finalUrl ="";
        Bundle data = getIntent().getExtras();
        finalUrl = data.getString("finalUrl");
        title = data.getString("title");
//        try{
//            finalUrl = getFinalUrl(playUrl);
//            if(finalUrl.equals("")){
//                Toast.makeText(this,"加载异常，请换源",Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        jzvdStd.setUp(finalUrl, title, JzvdStd.SCREEN_NORMAL);
        jzvdStd.posterImageView.setImageResource(R.drawable.a1);

        episodeList = (ArrayList<Episode>)getIntent().getSerializableExtra("EpisodeList");
        int playingSource = 0;
        episodeUrl= episodeList.get(playingSource).getEpisodeUrl();
        for(Map.Entry<String,String> entry : episodeUrl.entrySet()){
            episodeName.add(entry.getKey());
            episodePlayUrl.add(entry.getValue());
        }


        RecyclerView sourceRecyclerView = findViewById(R.id.episode_source_recycler);
        LinearLayoutManager sourceLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        sourceRecyclerView.setLayoutManager(sourceLayoutManager);
        SourceAdapter sourceAdapter = new SourceAdapter(episodeList,title,this);
        sourceRecyclerView.setAdapter(sourceAdapter);

        RecyclerView episodeListRecyclerView = findViewById(R.id.episode_list_recycler);
        LinearLayoutManager episodeListLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        episodeListRecyclerView.setLayoutManager(episodeListLayoutManager);
        episodeListAdapter = new EpisodeListAdapter(episodeName,episodePlayUrl,this);
        episodeListRecyclerView.setAdapter(episodeListAdapter);


        LocalBroadcastManager.getInstance(this).registerReceiver(sourceMessageReceiver,
                new IntentFilter("source-change"));
        LocalBroadcastManager.getInstance(this).registerReceiver(episodeMessageReceiver,
                new IntentFilter("episode-change"));

    }

    public BroadcastReceiver sourceMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                int newSource = intent.getIntExtra("newSource", 0);
                String newUrl = intent.getStringExtra("newUrl");
                title = intent.getStringExtra("newTittle");
                if(newSource != playingSource&&newSource<episodeList.size()){
                    playingSource = newSource;
                    if(newUrl.equals("")){
                        Toast.makeText(WatchActivity.this,"该集异常，请换源",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        finalUrl = newUrl;
                        jzvdStd.setUp(finalUrl, title, JzvdStd.SCREEN_NORMAL);
                        jzvdStd.posterImageView.setImageResource(R.drawable.a1);

                        episodeUrl= episodeList.get(playingSource).getEpisodeUrl();
                        episodeName.clear();
                        episodePlayUrl.clear();
                        for(Map.Entry<String,String> entry : episodeUrl.entrySet()){
                            episodeName.add(entry.getKey());
                            episodePlayUrl.add(entry.getValue());
                        }

                        episodeListAdapter.notifyDataSetChanged();

                        Toast.makeText(WatchActivity.this,"换源成功",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    };

    public BroadcastReceiver episodeMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newUrl = intent.getStringExtra("newUrl");
            title = intent.getStringExtra("newTittle");
            if(newUrl.equals("")){
                Toast.makeText(WatchActivity.this,"该集异常，请换源",Toast.LENGTH_LONG).show();
            }else {
                finalUrl = newUrl;
                jzvdStd.setUp(finalUrl, title, JzvdStd.SCREEN_NORMAL);
                jzvdStd.posterImageView.setImageResource(R.drawable.a1);
                Toast.makeText(WatchActivity.this,"换集成功",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }



    public static String getFinalUrl(String playUrl)throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);


        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10 * 1000);                   // 设置连接超时时间

        HtmlPage page = webClient.getPage(playUrl);
        webClient.waitForBackgroundJavaScript(1* 1000);               // 等待js后台执行30秒
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
        if(matcher.find()){
            return matcher.group(1);
        }else{
            return "";
        }

//        String finalUrl = doc.select("div[class=dplayer-video-wrap]").select("video").attr("src");

    }
}