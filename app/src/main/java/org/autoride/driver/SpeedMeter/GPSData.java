package org.autoride.driver.SpeedMeter;

/**
 * Created by goldenreign on 7/31/2018.
 */

public class GPSData {

  private Double latitude;
  private Double longitude;
  private String dateTime;
  private Double distance;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public GPSData() {
    }

    public GPSData(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GPSData(Double latitude, Double longitude, String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
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
