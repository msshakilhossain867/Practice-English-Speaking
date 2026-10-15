package com.citomoltd.englishspeakingpracticeapp;


public class ItemModel {
    private int id;
    private String name;
    private String summary;
    private int isFavorite;  // ⭐ ফেভারিট ফ্ল্যাগ
    private int isBookmarked; // 📌 বুকমার্ক ফ্ল্যাগ
    private String img;

    public ItemModel(int id, String name, String summary, int isFavorite, int isBookmarked, String img) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.isFavorite = isFavorite;
        this.isBookmarked = isBookmarked;
        this.img = img;
    }

    public ItemModel(int id, String name, String summary, int isFavorite, int isBookmarked) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.isFavorite = isFavorite;
        this.isBookmarked = isBookmarked;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }


    public int isFavorite() {
        return isFavorite;
    }

    public int isBookmarked() {
        return isBookmarked;
    }

    public void setFavorite(int favorite) {
        this.isFavorite = favorite;
    }

    public void setBookmarked(int bookmarked) {
        this.isBookmarked = bookmarked;
    }

    public String getImg() {
        return img;
    }
}

