package com.example.sakuraanime;


import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.JzvdStd;

import static android.content.ContentValues.TAG;

public class ListActivity extends AppCompatActivity {

    private List<Anime> animeList = new ArrayList<>();
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private AnimeAdapter animeAdapter;
    private int lastLoad;
    private int amount;
    private Elements eAnime;
    private int page;
    private String searchResultUrl;
    private String search;
    private androidx.appcompat.app.ActionBar actionBar;
    Handler getDataHandler = null;

    private AnimationDrawable mAnimationDrawable;
    ImageView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle = getIntent().getExtras();
        searchResultUrl = bundle.getString("searchResultUrl");
        search = bundle.getString("search");

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("搜索结果："+search);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads().detectDiskWrites().detectNetwork()
//                    .penaltyLog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                    .penaltyLog().penaltyDeath().build());

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        lastLoad = animeList.size();
        page = 1;
        try {
            Document doc = Jsoup.connect(searchResultUrl).timeout(30000).get();
            Elements eNum = doc.select("div").select("div").select("span").select("em");
            amount = Integer.parseInt(eNum.get(0).text());
            if(amount > 10){
                eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
                for(int i = 0;i < 10;i++) {
                    animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
                }
            }
            else {
                Elements eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
                for(int i = 0;i < amount;i++) {
                    animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }



        loadEpisode(animeList,lastLoad);
        Toast.makeText(ListActivity.this,"搜到"+amount+"条结果",Toast.LENGTH_SHORT).show();

        mLoadingView = findViewById(R.id.iv_search_loading);
        mLoadingView.setImageResource(R.drawable.anim_search_loading);
        mAnimationDrawable = (AnimationDrawable) mLoadingView.getDrawable();

        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mPullLoadMoreRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
        mPullLoadMoreRecyclerView.setRefreshing(true);
        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        mPullLoadMoreRecyclerView.setLinearLayout();

        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                reSetData();
            }

            @Override
            public void onLoadMore() {
                new getDataThread().start();
            }
        });
        animeAdapter = new AnimeAdapter(animeList);
        mPullLoadMoreRecyclerView.setAdapter(animeAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(listBeginReceiver,
                new IntentFilter("list-begin"));
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        getDataHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: my getData msg recievd");
                animeAdapter.notifyDataSetChanged();
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        };


    }


    public static List<Anime> analyse(String url, List<Anime> animeList) {
        String num ;
        int amount ;
        int i = 0;
        int page = 1;
        try {
            Document doc = Jsoup.connect(url).timeout(30000).get();
            Elements eNum = doc.select("div").select("div").select("span").select("em");
            num = eNum.get(0).text();
            amount = Integer.parseInt(num);
            if(amount <= 10){
                Elements eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
                for(;i < 10;i++) {
                    animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
                }
            }
            else {

            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return animeList;
    }

    public static void loadEpisode(List<Anime> animeList,int lastLoad){

        try {
            int amountOfAnime = animeList.size();
            for (int k = lastLoad; k < amountOfAnime; k++){
                Anime anime = animeList.get(k);
                Document doc = Jsoup.connect(anime.getAnimeUrl()).timeout(30000).get();
                Elements ele = doc.select("div.movurl");
                int amount = ele.size();
                int partSize;

                for (int i = 0; i < amount; i++) {
                    Elements ePart = ele.get(i).select("a");
                    partSize = ePart.size();
                    Episode epi = new Episode(Integer.toString(partSize));
                    Map<String, String> episodeUrl = new LinkedHashMap<String, String>();

                    for (int j = 0; j < partSize; j++) {
                        episodeUrl.put(ePart.get(j).attr("title"), "http://www.imomoe.ai/"+ePart.get(j).attr("href"));
                    }
                    epi.setEpisodeUrl(episodeUrl);
                    anime.addToEsopideList(epi);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void reSetData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                    }
                });

            }
        }, 1000);

    }

     private void showSearchAnim() {
        mLoadingView.setVisibility(View.VISIBLE);
        mAnimationDrawable.start();
    }

     private void hideSearchAnim() {
        mLoadingView.setVisibility(View.GONE);
        mAnimationDrawable.stop();
    }

    public BroadcastReceiver listBeginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean signal = intent.getBooleanExtra("signal",false);

            if(signal){
                Log.d(TAG, "onReceive: my signal is true");
                showSearchAnim();
            }else {
                Log.d(TAG, "onReceive: my signal is false");
                hideSearchAnim();
            }
        }
    };

    class getDataThread extends Thread{
        @Override
        public void run(){
            super.run();

            if(animeList.size()>=amount){
                Toast.makeText(ListActivity.this,"没啦没啦",Toast.LENGTH_LONG);
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                return;
            }

            try {
                lastLoad = animeList.size();
                page = page + 1;
                Document doc = Jsoup.connect(searchResultUrl+"&page="+page).timeout(30000).get();
                eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
                while (eAnime.size()==0){
                    if(amount-lastLoad<10){
                        break;
                    }
                    page = page + 1;
                    doc = Jsoup.connect(searchResultUrl+"&page="+page).timeout(30000).get();
                    eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
                    amount = amount - 10;
                }


                if(amount - lastLoad>= 10){
                    for(int i = 0;i < 10;i++) {
                        animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
                    }
                }
                else {
                    for(int i =0;i < amount - animeList.size();i++) {
                        animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
            loadEpisode(animeList,lastLoad);
            Message msg = Message.obtain();
            msg.arg1 = 1;
            getDataHandler.sendMessage(msg);
            Log.d(TAG, "run: my getData msg sent");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}