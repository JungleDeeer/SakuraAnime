package com.example.sakuraanime.database.model;

public class History {
    public static final String TABLE_NAME = "Historys";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FINALURL = "finalUrl";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BARTITLE = "barTitle";
    public static final String COLUMN_STREPISODELIST = "strEpisodeList";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String finalUrl;
    private String title;
    private String barTitle;
    private String strEpisodeList;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FINALURL + " TEXT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_BARTITLE + " TEXT,"
                    + COLUMN_STREPISODELIST + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    public History(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFinalUrl() {
        return finalUrl;
    }

    public void setFinalUrl(String finalUrl) {
        this.finalUrl = finalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarTitle() {
        return barTitle;
    }

    public void setBarTitle(String barTitle) {
        this.barTitle = barTitle;
    }

    public String getStrEpisodeList() {
        return strEpisodeList;
    }

    public void setStrEpisodeList(String strEpisodeList) {
        this.strEpisodeList = strEpisodeList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public History(int id, String finalUrl, String title, String barTitle, String strEpisodeList, String timestamp){
        this.id = id;
        this.finalUrl = finalUrl;
        this.title = title;
        this.barTitle = barTitle;
        this.strEpisodeList = strEpisodeList;
        this.timestamp = timestamp;
    }
}
