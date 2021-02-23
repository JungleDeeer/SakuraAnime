package com.example.sakuraanime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity {

    private List<Anime> animeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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

        String searchResultUrl = getIntent().getStringExtra("searchResultUrl");
        animeList = analyse(searchResultUrl,animeList);
        loadEpisode(animeList);
        Toast.makeText(ListActivity.this,"搜到"+Integer.toString(animeList.size())+"条结果",Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(ListActivity.this,DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AnimeAdapter animeAdapter = new AnimeAdapter(animeList);
        recyclerView.setAdapter(animeAdapter);
    }

    public static List<Anime> analyse(String url, List<Anime> animeList) {
        String num ;
        int amount ;

        try {
            Document doc = Jsoup.connect(url).timeout(30000).get();
            Elements eNum = doc.select("div").select("div").select("span").select("em");
            num = eNum.get(0).text();

            amount = Integer.parseInt(num);

            Elements eAnime = doc.select("div").select("div").select("div").select("ul").select("li:has(a[target])");
            for(int i=0;i < amount;i++) {
                animeList.add(new Anime(eAnime.get(i).select("h2").select("a").text(),eAnime.get(i).select("h2").select("a").attr("href"),eAnime.get(i).select("span").get(0).text(),eAnime.get(i).select("span").get(1).text(), eAnime.get(i).select("p").text()));
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return animeList;

    }

    public static void loadEpisode(List<Anime> animeList){

        try {
            int amountOfAnime = animeList.size();
            for (int k = 0; k < amountOfAnime; k++){
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
}