package com.citomoltd.englishspeakingpracticeapp;

public class CategoryModel {
    private int id;
    private String name;
    private String img;
    private String img2;
    private String img3;
    private String img4;

    public CategoryModel(int id, String name, String img, String img2, String img3, String img4) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg3() {
        return img3;
    }

    public String getImg4() {
        return img4;
    }

}

