package com.example.sakuraanime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.search);
        searchButton = findViewById(R.id.search_button);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchEditText.getText().toString();
                String url = getUrl(search);

//                List<Anime> animeList = new ArrayList<>();
//                animeList = analyse(url,animeList);
//                loadEpisode(animeList);
                Toast.makeText(v.getContext(),"搜索资源中，请等等哦",Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("searchResultUrl",url);
                intent.setClass(MainActivity.this,ListActivity.class);
                startActivity(intent);

            }
        });
    }

    public static String getUrl(String key) {
        String url = "http://www.imomoe.ai/search.asp?searchword=";
        StringBuffer buf = new StringBuffer();
        try {
            byte[] bytes = key.getBytes("GB2312");
            for(byte b:bytes) {
                String code = Integer.toHexString(b);
                buf.append(code.substring(code.length()-2));
            }
            url = url + buf.toString();

        }catch (Exception e){

            e.printStackTrace();
        }
        return url;
    }



}