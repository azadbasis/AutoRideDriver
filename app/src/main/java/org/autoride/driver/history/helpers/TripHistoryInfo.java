package org.autoride.driver.history.helpers;

import com.google.android.gms.maps.model.LatLng;

public class TripHistoryInfo {

    private String tripId;
    private String tripDate;
    private String tripAmount;
    private String tripVehicleDesc;
    private String tripVehicleType;
    private String tripPaymentType;
    private String tripStatus;
    private String tripRiderName;
    private String tripRiderPhoto;
    private LatLng tripPickup;
    private LatLng tripDrop;
    private Double tripBaseFare;
    private Double tripDistanceFare;
    private Double tripTimeFare;
    private Double tripFareDiscount;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripAmount() {
        return tripAmount;
    }

    public void setTripAmount(String tripAmount) {
        this.tripAmount = tripAmount;
    }

    public String getTripVehicleDesc() {
        return tripVehicleDesc;
    }

    public void setTripVehicleDesc(String tripVehicleDesc) {
        this.tripVehicleDesc = tripVehicleDesc;
    }

    public String getTripVehicleType() {
        return tripVehicleType;
    }

    public void setTripVehicleType(String tripVehicleType) {
        this.tripVehicleType = tripVehicleType;
    }

    public String getTripPaymentType() {
        return tripPaymentType;
    }

    public void setTripPaymentType(String tripPaymentType) {
        this.tripPaymentType = tripPaymentType;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripRiderName() {
        return tripRiderName;
    }

    public void setTripRiderName(String tripRiderName) {
        this.tripRiderName = tripRiderName;
    }

    public String getTripRiderPhoto() {
        return tripRiderPhoto;
    }

    public void setTripRiderPhoto(String tripRiderPhoto) {
        this.tripRiderPhoto = tripRiderPhoto;
    }

    public LatLng getTripPickup() {
        return tripPickup;
    }

    public void setTripPickup(LatLng tripPickup) {
        this.tripPickup = tripPickup;
    }

    public LatLng getTripDrop() {
        return tripDrop;
    }

    public void setTripDrop(LatLng tripDrop) {
        this.tripDrop = tripDrop;
    }

    public Double getTripBaseFare() {
        return tripBaseFare;
    }

    public void setTripBaseFare(Double tripBaseFare) {
        this.tripBaseFare = tripBaseFare;
    }

    public Double getTripDistanceFare() {
        return tripDistanceFare;
    }

    public void setTripDistanceFare(Double tripDistanceFare) {
        this.tripDistanceFare = tripDistanceFare;
    }

    public Double getTripTimeFare() {
        return tripTimeFare;
    }

    public void setTripTimeFare(Double tripTimeFare) {
        this.tripTimeFare = tripTimeFare;
    }

    public Double getTripFareDiscount() {
        return tripFareDiscount;
    }

    public void setTripFareDiscount(Double tripFareDiscount) {
        this.tripFareDiscount = tripFareDiscount;
    }
}