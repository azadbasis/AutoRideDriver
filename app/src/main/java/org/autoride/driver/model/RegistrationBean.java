package org.autoride.driver.model;

public class RegistrationBean extends BaseBean {

    private String lastName;
    private String firstName;
    private String fullName;
    private String phone;
    private String password;
    private String currentPassword;
    private String newPassword;
    private String retypePassword;
    private String userRole;
    private String promoCode;
    private String registrationLat;
    private String registrationLng;
    private String email;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }
}