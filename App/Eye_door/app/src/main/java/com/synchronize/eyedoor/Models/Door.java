package com.synchronize.eyedoor.Models;

public class Door {
    String ip,status,name,id;

    public Door() {
    }

    public Door(String ip, String name, String status) {
        this.ip = ip;
        this.status = status;
        this.name = name;
    }

    public Door(String ip, String status, String name, String id) {
        this.ip = ip;
        this.status = status;
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
