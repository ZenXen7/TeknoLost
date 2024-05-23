package com.example.teknolost;

import java.io.Serializable;

public class Items implements Serializable {
    private String dataDesc;
    private String dataImage;
    private String dataLang;
    private String dataTitle;
    private String status;
    private String userId;
    private String dataDate;
    private String itemId;


    public Items() {
        // Default constructor required for calls to DataSnapshot.getValue(Items.class)
    }

    public Items(String dataDesc, String dataImage, String dataLang, String dataTitle, String status, String userId,String dataDate) {
        this.dataDesc = dataDesc;
        this.dataImage = dataImage;
        this.dataLang = dataLang;
        this.dataTitle = dataTitle;
        this.status = status;
        this.dataDate = dataDate;
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    // Add other getters and setters as needed


    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public String getDataLang() {
        return dataLang;
    }

    public void setDataLang(String dataLang) {
        this.dataLang = dataLang;
    }

    public String s() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

