package org.autoride.driver.model;

/**
 * Created by razu-razu on 1/29/2018.
 */

public class DriverInfo extends BaseBean {

    private String accessToken;
    private String rememberToken;
    private String driverId;
    private String publicKey;
    private String privateKey;
    private String password;
    private String currentPassword;
    private String newPassword;
    private String retypePassword;
    private String registrationLat;
    private String registrationLng;
    private DriverAsUser driverAsUser;
    private RiderInfo riderInfo;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public String getRegistrationLat() {
        return registrationLat;
    }

    public void setRegistrationLat(String registrationLat) {
        this.registrationLat = registrationLat;
    }

    public String getRegistrationLng() {
        return registrationLng;
    }

    public void setRegistrationLng(String registrationLng) {
        this.registrationLng = registrationLng;
    }

    public DriverAsUser getDriverAsUser() {
        return driverAsUser;
    }

    public void setDriverAsUser(DriverAsUser driverAsUser) {
        this.driverAsUser = driverAsUser;
    }

    public RiderInfo getRiderInfo() {
        return riderInfo;
    }

    public void setRiderInfo(RiderInfo riderInfo) {
        this.riderInfo = riderInfo;
    }
}