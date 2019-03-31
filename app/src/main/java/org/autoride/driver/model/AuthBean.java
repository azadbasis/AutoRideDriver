package org.autoride.driver.model;

import java.util.List;

public class AuthBean extends BaseBean {

    private String accessToken;
    private String rememberToken;
    private String userID;
    private String promoCode;
    private String firstName;
    private String lastName;
    private String userFullName;
    private String verificationStatus;
    private String driverCurrentStatus;
    private String userPhone;
    private String userRole;
    private String password;
    private String email;
    private String dob;
    private String gender;
    private String profilePhoto;
    private String vehiclePhoto;
    private String lastLocation;
    private String lastLatitude;
    private String lastLongitude;
    private String registrationLocation;
    private String registrationLatitude;
    private String registrationLongitude;
    private String versionName;
    private boolean isPhoneVerified;
    private List<DriverAsUser> driverInfoList;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(String lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public String getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(String lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getRegistrationLocation() {
        return registrationLocation;
    }

    public void setRegistrationLocation(String registrationLocation) {
        this.registrationLocation = registrationLocation;
    }

    public String getRegistrationLatitude() {
        return registrationLatitude;
    }

    public void setRegistrationLatitude(String registrationLatitude) {
        this.registrationLatitude = registrationLatitude;
    }

    public String getRegistrationLongitude() {
        return registrationLongitude;
    }

    public void setRegistrationLongitude(String registrationLongitude) {
        this.registrationLongitude = registrationLongitude;
    }

    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVehiclePhoto() {
        return vehiclePhoto;
    }

    public void setVehiclePhoto(String vehiclePhoto) {
        this.vehiclePhoto = vehiclePhoto;
    }

    public String getDriverCurrentStatus() {
        return driverCurrentStatus;
    }

    public void setDriverCurrentStatus(String driverCurrentStatus) {
        this.driverCurrentStatus = driverCurrentStatus;
    }

    public List<DriverAsUser> getDriverInfoList() {
        return driverInfoList;
    }

    public void setDriverInfoList(List<DriverAsUser> driverInfoList) {
        this.driverInfoList = driverInfoList;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}