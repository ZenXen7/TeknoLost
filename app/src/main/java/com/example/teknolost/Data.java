package com.example.teknolost;

public class Data {

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataDate;


    private String dataImage;
    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }
    public Data(String dataTitle, String dataDesc, String dataLang, String dataImage,String date) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.dataDate = date;
    }
    public Data(){
    }
}
