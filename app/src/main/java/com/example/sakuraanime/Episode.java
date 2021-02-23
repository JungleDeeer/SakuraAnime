package com.example.sakuraanime;

import java.io.Serializable;
import java.util.Map;

public class Episode implements Serializable {
    private String episodeNum;
    Map<String, String> episodeUrl;

    public Episode(String episodeNum) {
        this.episodeNum = episodeNum;

    }

    public String getEpisodeNum() {
        return episodeNum;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

    public Map<String, String> getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(Map<String, String> episodeUrl) {
        this.episodeUrl = episodeUrl;
    }
}
