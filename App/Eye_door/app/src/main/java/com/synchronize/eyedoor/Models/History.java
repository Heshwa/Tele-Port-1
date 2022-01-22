package com.synchronize.eyedoor.Models;

public class History {
    private String imgUrl,time,name,status;
    int type;

    public History() {
    }
    public History(String imgUrl, String time, String name, String status) {
        this.imgUrl = imgUrl;
        this.time = time;
        this.name = name;
        this.status = status;
    }

    public History(String imgUrl, String time, String name, String status, int type) {
        this.imgUrl = imgUrl;
        this.time = time;
        this.name = name;
        this.status = status;
        this.type = type;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
