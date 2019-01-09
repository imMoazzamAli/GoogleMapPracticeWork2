package com.example.user.googlemappracticework.Model;

import java.io.Serializable;

public class ModelLocationData implements Serializable {

    private String name;
    private Double latitude;
    private Double longitude;

    public ModelLocationData() {
    }

    public ModelLocationData(String name, Double latitude, Double longitude) {
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
