package org.autoride.driver.networks;

public interface DriverApiUrl {

    String BASE_URL = "http://128.199.80.10:80/golden/app/";

    String VERSION_URL = BASE_URL + "version";

    String REGISTRATION_URL = BASE_URL + "registration";

    String PHONE_NUMBER_EXIST_URL = BASE_URL + "phone/exist";

    String PROMO_CODE_EXIST_URL = BASE_URL + "promocode/exist";

    String FORGOT_PASSWORD_URL = BASE_URL + "forgot/password";

    String EDIT_DRIVER_PASS_URL = BASE_URL + "driver/reset/password";

    String USER_LOGIN_URL = BASE_URL + "login";

    String DRIVER_PROFILE_URL = BASE_URL + "driver/profile?";   // userId=

    String EDIT_DRIVER_PROFILE_IMG_URL = BASE_URL + "driver/profile/image/update";

    String EDIT_DRIVER_VEHICLE_IMG_URL = BASE_URL + "driver/profile/cover/image/update";

    String EDIT_DRIVING_LICENCE_IMG_URL = BASE_URL + "driver/driving/licence/upload";

    String EDIT_DRIVER_VEHICLE_INSURANCE_IMG_URL = BASE_URL + "driver/vehicle/insurance/upload";

    String EDIT_DRIVER_VEHICLE_BLUE_BOOK_IMG_URL = BASE_URL + "driver/vehicle/bluebook/upload";

    String EDIT_DRIVER_VEHICLE_TAX_TOKEN_IMG_URL = BASE_URL + "driver/vehicle/taxtoken/upload";

    String UPDATE_DRIVER_STATUS_URL = BASE_URL + "driver/service/status";

    String DRIVER_STATUS_URL = BASE_URL + "driver/service/status/check?";  // userId=

    String VEHICLE_TYPE_URL = BASE_URL + "vehicle/category";

    String VEHICLE_INFO_UPDATE_URL = BASE_URL + "driver/vehicle/info/update";

    String VEHICLE_INFO_URL = BASE_URL + "driver/vehicle/info?";   // userId=

    String UPDATE_FIRST_NAME_URL = BASE_URL + "driver/firstname/edit";

    String UPDATE_LAST_NAME_URL = BASE_URL + "driver/lastname/edit";

    String UPDATE_EMAIL_URL = BASE_URL + "driver/email/edit";

    String UPDATE_ADDRESS_URL = BASE_URL + "driver/address/edit";

    String UPDATE_DRIVER_LOCATION_URL = BASE_URL + "driver/set/location?";

    String SET_FIRE_BASE_TOKEN_URL = BASE_URL + "driver/fire/base/token/set";

    String CANCEL_REQUEST_URL = BASE_URL + "cancel/driver/request?";  //  userId=5a8d286dc31240461333c04b  // here userId is driverId

    String REQUEST_NOT_RECEIVED_URL = BASE_URL + "request/not/received?";

    String ACCEPT_REQUEST_URL = BASE_URL + "accept/driver/request?";  //  userId=5a94182cc312406b610f6051&driverId=5a801254c312401d1a66b0a1

    String REQUESTED_TRIP_CANCEL_URL = BASE_URL + "trip/cancel";

    String START_RIDING_URL = BASE_URL + "riding/start?";  // userId=5a801254c312401d1a66b0a1&predictedMinute=23&driverId=5a801254c312401d1a66b0a1&amount=560&predictedKilometer=24

    String UPDATE_RIDING_DISTANCE_URL = BASE_URL + "riding/distance/update?"; // driverId=5a801254c312401d1a66b0a1&kilometer=0.9898&lat=90.2345&lng=23.3458

    String COMPLETE_TRIP_URL = BASE_URL + "riding/stop";

    String RIDING_MODE_URL = BASE_URL + "driver/trip/data?";  // userId=5a7fee59c312401a12169e61  // here userId is driverId

    String CALCULATE_FARE_URL = BASE_URL + "riding/calculate/fare?"; // driverId=5a7fee59c312401a12169e61&lat=45645&lng=23423

    String TRIP_HISTORY_URL = BASE_URL + "driver/riding/history?";  // userId=5ac366cfc312403b0177d5b3

    /******************** unused url
     **********************/
    String FARE_RATE_URL = BASE_URL + "fare/rate";

    String EDIT_USER_INFO_URL = BASE_URL + "driver/profile/edit";

}