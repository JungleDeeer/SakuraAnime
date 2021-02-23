package com.example.sakuraanime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Anime implements Serializable {
    private String name;
    private String animeUrl;
    private String alias;
    private String info;
    private String content;
    private List<Episode> episodeList = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setAnimeUrl(String animeUrl) {
        this.animeUrl = animeUrl;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Episode> getEpisodeList() {
        return episodeList;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        this.episodeList = episodeList;
    }

    public Anime(String name, String animeUrl, String alias, String info, String content) {
        this.name = name;
        this.animeUrl = animeUrl;
        this.alias = alias;
        this.info = info;
        this.content = content;

    }

    public String getName() {
        return name;
    }

    public String getAnimeUrl() {
        return "http://www.imomoe.ai/"+animeUrl;
    }

    public String getAlias() {
        return alias;
    }

    public String getInfo() {
        return info;
    }

    public String getContent() {
        return content;
    }

    public void addToEsopideList(Episode episode){
        this.episodeList.add(episode) ;
    }


}

