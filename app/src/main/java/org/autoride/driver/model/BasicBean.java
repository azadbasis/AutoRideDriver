package org.autoride.driver.model;

public class BasicBean extends BaseBean {

    private String id;
    private String otpCode;
    private String authToken;
    private int requestStatus;
    private boolean isPhoneAvailable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public boolean isPhoneAvailable() {
        return isPhoneAvailable;
    }

    public void setPhoneAvailable(boolean phoneAvailable) {
        isPhoneAvailable = phoneAvailable;
    }
}