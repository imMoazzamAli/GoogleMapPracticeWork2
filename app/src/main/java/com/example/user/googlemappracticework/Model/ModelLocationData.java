package com.example.user.googlemappracticework.Model;

import java.io.Serializable;

public class ModelLocationData implements Serializable {

    private String name;
    private String latitude;
    private String longitude;

    public ModelLocationData() {
    }

    public ModelLocationData(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
