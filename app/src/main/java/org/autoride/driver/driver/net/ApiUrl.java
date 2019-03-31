package org.autoride.driver.driver.net;

public interface ApiUrl {

    String MAPS_DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/";

    String HTTP = "http://";
    String PROTOCOL = "192.168.0.118/";
    String PATH = "AutoRideInfo/phpFile/";
    String BASE_URL = HTTP + PROTOCOL + PATH;

    String UPLOAD_IMAGE = "http://192.168.0.122/AutoRide/image_set.php";

    String DRIVER_INFO_URL = BASE_URL + "GetDriver.php";
    String SEND_USER_INFO_URL = BASE_URL + "sendUserInfo.php";
    String REQUESTED_USER_URL = BASE_URL + "getRequestedUser.php";
    String SET_TOKEN_URL = BASE_URL + "setFcmToken.php";
    String SEND_NOTIFICATION_URL = BASE_URL + "sendNotification.php";

    String BASE_URL2 = "http://192.168.0.101/goldenreign/";
    String USER_REGISTRATION_URL = BASE_URL2 + "app/registration";
    String USER_PHONE_VERIFY_URL = BASE_URL2 + "app/";
    String USER_LOGIN_URL = BASE_URL2 + "app/";
}