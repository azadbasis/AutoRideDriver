package org.autoride.driver.model;

public class FareInfo {

    private double carBaseFare;
    private double carMinimumFare;
    private double carFarePerMinute;
    private double carFarePerKm;
    private double carFareOutOfCity;

    private double carExeBaseFare;
    private double carExeMinimumFare;
    private double carExeFarePerMinute;
    private double carExeFarePerKm;
    private double carExeFareOutOfCity;

    private double carHouBaseFare;
    private double carHouMinimumFare;
    private double carHouFarePerMinute;
    private double carHouFarePerKm;
    private double carHouFareOutOfCity;

    private double carHouExeBaseFare;
    private double carHouExeMinimumFare;
    private double carHouExeFarePerMinute;
    private double carHouExeFarePerKm;
    private double carHouExeFareOutOfCity;

    private double bikeBaseFare;
    private double bikeMinimumFare;
    private double bikeFarePerMinute;
    private double bikeFarePerKm;
    private double bikeFareOutOfCity;

    private double bikeHouBaseFare;
    private double bikeHouMinimumFare;
    private double bikeHouFarePerMinute;
    private double bikeHouFarePerKm;
    private double bikeHouFareOutOfCity;

    private double cngBaseFare;
    private double cngMinimumFare;
    private double cngFarePerMinute;
    private double cngFarePerKm;
    private double cngFareOutOfCity;

    private double cngHouBaseFare;
    private double cngHouMinimumFare;
    private double cngHouFarePerMinute;
    private double cngHouFarePerKm;
    private double cngHouFareOutOfCity;

    private double pickupBaseFare;
    private double pickupMinimumFare;
    private double pickupFarePerMinute;
    private double pickupFarePerKm;
    private double pickupFareOutOfCity;

    private static double baseFare;
    private static double timeRate ;
    private static double distanceRate;
    private static double discount;
    private static double coupon;

    public double getCarBaseFare() {
        return carBaseFare;
    }

    public void setCarBaseFare(double carBaseFare) {
        this.carBaseFare = carBaseFare;
    }

    public double getCarMinimumFare() {
        return carMinimumFare;
    }

    public void setCarMinimumFare(double carMinimumFare) {
        this.carMinimumFare = carMinimumFare;
    }

    public double getCarFarePerMinute() {
        return carFarePerMinute;
    }

    public void setCarFarePerMinute(double carFarePerMinute) {
        this.carFarePerMinute = carFarePerMinute;
    }

    public double getCarFarePerKm() {
        return carFarePerKm;
    }

    public void setCarFarePerKm(double carFarePerKm) {
        this.carFarePerKm = carFarePerKm;
    }

    public double getCarFareOutOfCity() {
        return carFareOutOfCity;
    }

    public void setCarFareOutOfCity(double carFareOutOfCity) {
        this.carFareOutOfCity = carFareOutOfCity;
    }

    public double getCarExeBaseFare() {
        return carExeBaseFare;
    }

    public void setCarExeBaseFare(double carExeBaseFare) {
        this.carExeBaseFare = carExeBaseFare;
    }

    public double getCarExeMinimumFare() {
        return carExeMinimumFare;
    }

    public void setCarExeMinimumFare(double carExeMinimumFare) {
        this.carExeMinimumFare = carExeMinimumFare;
    }

    public double getCarExeFarePerMinute() {
        return carExeFarePerMinute;
    }

    public void setCarExeFarePerMinute(double carExeFarePerMinute) {
        this.carExeFarePerMinute = carExeFarePerMinute;
    }

    public double getCarExeFarePerKm() {
        return carExeFarePerKm;
    }

    public void setCarExeFarePerKm(double carExeFarePerKm) {
        this.carExeFarePerKm = carExeFarePerKm;
    }

    public double getCarExeFareOutOfCity() {
        return carExeFareOutOfCity;
    }

    public void setCarExeFareOutOfCity(double carExeFareOutOfCity) {
        this.carExeFareOutOfCity = carExeFareOutOfCity;
    }

    public double getCarHouBaseFare() {
        return carHouBaseFare;
    }

    public void setCarHouBaseFare(double carHouBaseFare) {
        this.carHouBaseFare = carHouBaseFare;
    }

    public double getCarHouMinimumFare() {
        return carHouMinimumFare;
    }

    public void setCarHouMinimumFare(double carHouMinimumFare) {
        this.carHouMinimumFare = carHouMinimumFare;
    }

    public double getCarHouFarePerMinute() {
        return carHouFarePerMinute;
    }

    public void setCarHouFarePerMinute(double carHouFarePerMinute) {
        this.carHouFarePerMinute = carHouFarePerMinute;
    }

    public double getCarHouFarePerKm() {
        return carHouFarePerKm;
    }

    public void setCarHouFarePerKm(double carHouFarePerKm) {
        this.carHouFarePerKm = carHouFarePerKm;
    }

    public double getCarHouFareOutOfCity() {
        return carHouFareOutOfCity;
    }

    public void setCarHouFareOutOfCity(double carHouFareOutOfCity) {
        this.carHouFareOutOfCity = carHouFareOutOfCity;
    }

    public double getCarHouExeBaseFare() {
        return carHouExeBaseFare;
    }

    public void setCarHouExeBaseFare(double carHouExeBaseFare) {
        this.carHouExeBaseFare = carHouExeBaseFare;
    }

    public double getCarHouExeMinimumFare() {
        return carHouExeMinimumFare;
    }

    public void setCarHouExeMinimumFare(double carHouExeMinimumFare) {
        this.carHouExeMinimumFare = carHouExeMinimumFare;
    }

    public double getCarHouExeFarePerMinute() {
        return carHouExeFarePerMinute;
    }

    public void setCarHouExeFarePerMinute(double carHouExeFarePerMinute) {
        this.carHouExeFarePerMinute = carHouExeFarePerMinute;
    }

    public double getCarHouExeFarePerKm() {
        return carHouExeFarePerKm;
    }

    public void setCarHouExeFarePerKm(double carHouExeFarePerKm) {
        this.carHouExeFarePerKm = carHouExeFarePerKm;
    }

    public double getCarHouExeFareOutOfCity() {
        return carHouExeFareOutOfCity;
    }

    public void setCarHouExeFareOutOfCity(double carHouExeFareOutOfCity) {
        this.carHouExeFareOutOfCity = carHouExeFareOutOfCity;
    }

    public double getBikeBaseFare() {
        return bikeBaseFare;
    }

    public void setBikeBaseFare(double bikeBaseFare) {
        this.bikeBaseFare = bikeBaseFare;
    }

    public double getBikeMinimumFare() {
        return bikeMinimumFare;
    }

    public void setBikeMinimumFare(double bikeMinimumFare) {
        this.bikeMinimumFare = bikeMinimumFare;
    }

    public double getBikeFarePerMinute() {
        return bikeFarePerMinute;
    }

    public void setBikeFarePerMinute(double bikeFarePerMinute) {
        this.bikeFarePerMinute = bikeFarePerMinute;
    }

    public double getBikeFarePerKm() {
        return bikeFarePerKm;
    }

    public void setBikeFarePerKm(double bikeFarePerKm) {
        this.bikeFarePerKm = bikeFarePerKm;
    }

    public double getBikeFareOutOfCity() {
        return bikeFareOutOfCity;
    }

    public void setBikeFareOutOfCity(double bikeFareOutOfCity) {
        this.bikeFareOutOfCity = bikeFareOutOfCity;
    }

    public double getBikeHouBaseFare() {
        return bikeHouBaseFare;
    }

    public void setBikeHouBaseFare(double bikeHouBaseFare) {
        this.bikeHouBaseFare = bikeHouBaseFare;
    }

    public double getBikeHouMinimumFare() {
        return bikeHouMinimumFare;
    }

    public void setBikeHouMinimumFare(double bikeHouMinimumFare) {
        this.bikeHouMinimumFare = bikeHouMinimumFare;
    }

    public double getBikeHouFarePerMinute() {
        return bikeHouFarePerMinute;
    }

    public void setBikeHouFarePerMinute(double bikeHouFarePerMinute) {
        this.bikeHouFarePerMinute = bikeHouFarePerMinute;
    }

    public double getBikeHouFarePerKm() {
        return bikeHouFarePerKm;
    }

    public void setBikeHouFarePerKm(double bikeHouFarePerKm) {
        this.bikeHouFarePerKm = bikeHouFarePerKm;
    }

    public double getBikeHouFareOutOfCity() {
        return bikeHouFareOutOfCity;
    }

    public void setBikeHouFareOutOfCity(double bikeHouFareOutOfCity) {
        this.bikeHouFareOutOfCity = bikeHouFareOutOfCity;
    }

    public double getCngBaseFare() {
        return cngBaseFare;
    }

    public void setCngBaseFare(double cngBaseFare) {
        this.cngBaseFare = cngBaseFare;
    }

    public double getCngMinimumFare() {
        return cngMinimumFare;
    }

    public void setCngMinimumFare(double cngMinimumFare) {
        this.cngMinimumFare = cngMinimumFare;
    }

    public double getCngFarePerMinute() {
        return cngFarePerMinute;
    }

    public void setCngFarePerMinute(double cngFarePerMinute) {
        this.cngFarePerMinute = cngFarePerMinute;
    }

    public double getCngFarePerKm() {
        return cngFarePerKm;
    }

    public void setCngFarePerKm(double cngFarePerKm) {
        this.cngFarePerKm = cngFarePerKm;
    }

    public double getCngFareOutOfCity() {
        return cngFareOutOfCity;
    }

    public void setCngFareOutOfCity(double cngFareOutOfCity) {
        this.cngFareOutOfCity = cngFareOutOfCity;
    }

    public double getCngHouBaseFare() {
        return cngHouBaseFare;
    }

    public void setCngHouBaseFare(double cngHouBaseFare) {
        this.cngHouBaseFare = cngHouBaseFare;
    }

    public double getCngHouMinimumFare() {
        return cngHouMinimumFare;
    }

    public void setCngHouMinimumFare(double cngHouMinimumFare) {
        this.cngHouMinimumFare = cngHouMinimumFare;
    }

    public double getCngHouFarePerMinute() {
        return cngHouFarePerMinute;
    }

    public void setCngHouFarePerMinute(double cngHouFarePerMinute) {
        this.cngHouFarePerMinute = cngHouFarePerMinute;
    }

    public double getCngHouFarePerKm() {
        return cngHouFarePerKm;
    }

    public void setCngHouFarePerKm(double cngHouFarePerKm) {
        this.cngHouFarePerKm = cngHouFarePerKm;
    }

    public double getCngHouFareOutOfCity() {
        return cngHouFareOutOfCity;
    }

    public void setCngHouFareOutOfCity(double cngHouFareOutOfCity) {
        this.cngHouFareOutOfCity = cngHouFareOutOfCity;
    }

    public double getPickupBaseFare() {
        return pickupBaseFare;
    }

    public void setPickupBaseFare(double pickupBaseFare) {
        this.pickupBaseFare = pickupBaseFare;
    }

    public double getPickupMinimumFare() {
        return pickupMinimumFare;
    }

    public void setPickupMinimumFare(double pickupMinimumFare) {
        this.pickupMinimumFare = pickupMinimumFare;
    }

    public double getPickupFarePerMinute() {
        return pickupFarePerMinute;
    }

    public void setPickupFarePerMinute(double pickupFarePerMinute) {
        this.pickupFarePerMinute = pickupFarePerMinute;
    }

    public double getPickupFarePerKm() {
        return pickupFarePerKm;
    }

    public void setPickupFarePerKm(double pickupFarePerKm) {
        this.pickupFarePerKm = pickupFarePerKm;
    }

    public double getPickupFareOutOfCity() {
        return pickupFareOutOfCity;
    }

    public void setPickupFareOutOfCity(double pickupFareOutOfCity) {
        this.pickupFareOutOfCity = pickupFareOutOfCity;
    }

    public static double getBaseFare() {
        return baseFare;
    }

    public static void setBaseFare(double baseFare) {
        FareInfo.baseFare = baseFare;
    }

    public static double getTimeRate() {
        return timeRate;
    }

    public static void setTimeRate(double timeRate) {
        FareInfo.timeRate = timeRate;
    }

    public static double getDistanceRate() {
        return distanceRate;
    }

    public static void setDistanceRate(double distanceRate) {
        FareInfo.distanceRate = distanceRate;
    }

    public static double getDiscount() {
        return discount;
    }

    public static void setDiscount(double discount) {
        FareInfo.discount = discount;
    }

    public static double getCoupon() {
        return coupon;
    }

    public static void setCoupon(double coupon) {
        FareInfo.coupon = coupon;
    }
}