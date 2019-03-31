package org.autoride.driver.model;

/**
 * Created by razu-razu on 1/29/2018.
 */

public class DriverAddress {

    private String house;
    private String road;
    private String stateProvince;
    private String country;
    private String unit;
    private String zipCode;
    private String fax;
    private String touchPointPhone;
    private double touchPointLat;
    private double touchPointLng;

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTouchPointPhone() {
        return touchPointPhone;
    }

    public void setTouchPointPhone(String touchPointPhone) {
        this.touchPointPhone = touchPointPhone;
    }

    public double getTouchPointLat() {
        return touchPointLat;
    }

    public void setTouchPointLat(double touchPointLat) {
        this.touchPointLat = touchPointLat;
    }

    public double getTouchPointLng() {
        return touchPointLng;
    }

    public void setTouchPointLng(double touchPointLng) {
        this.touchPointLng = touchPointLng;
    }
}