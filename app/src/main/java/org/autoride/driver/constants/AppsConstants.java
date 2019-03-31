package org.autoride.driver.constants;

import org.autoride.driver.R;
import org.autoride.driver.app.AutoRideDriverApps;

public interface AppsConstants {

    String WEB_RESPONSE_STATUS_CODE = "statusCode";
    String WEB_RESPONSE_STATUS = "status";
    String WEB_RESPONSE_SUCCESS = "success";
    String WEB_RESPONSE_ERROR = "error";
    String WEB_RESPONSE_ERRORS = "errors";
    String IS_DATA = "is_data";
    String WEB_RESPONSE_MESSAGE = "message";
    String WEB_ERRORS_MESSAGE = AutoRideDriverApps.getInstance().getResources().getString(R.string.web_error_msg2);
    String UNABLE_FOUND_DRIVER = "Unable to found nearest Driver, Please try again";
    String UNABLE_FOUND_FARE = "Unable to found fare details";
    String SELECTED_LANGUAGE = "driver_selected_language";

    String WEB_RESPONSE_CODE_200 = "200";
    String WEB_RESPONSE_CODE_401 = "401";
    String WEB_RESPONSE_CODE_404 = "404";
    String WEB_RESPONSE_CODE_406 = "406";
    String WEB_RESPONSE_CODE_500 = "500";

    String WEB_RESPONSE_DATA = "data";
    String WEB_RESPONSE_FARE = "fare";
    String WEB_RESPONSE_USER = "user";
    String PARTNER = "partner";
    String IS_AVAILABLE = "is_available";

    String RIDING_MODE_STATUS = "modeStatus";
    String MODE_STATUS_RIDING = "riding";
    String MODE_STATUS_COMPLETE = "complete";
    String TRIP_DETAILS = "tripDetails";

    String SESSION_SHARED_PREFERENCES = "session";
    String ACCESS_TOKEN = "accessToken";
    String REMEMBER_TOKEN = "rememberToken";
    String RIDER_ID = "userId";
    String PUBLIC_KEY = "publicKey";
    String PRIVATE_KEY = "privateKey";

    String WEB_RESPONSE_DRIVER = "driver";
    String DRIVER_ID = "userId";
    String DRIVER_IDS = "driverId";
    String FIRE_BASE_TOKEN = "fireBaseToken";
    String DRIVER_LOCATION = "location";

    String PARTNER_VEHICLE = "vehicle";
    String VEHICLE_TYPE = "vehicleType";
    String VEHICLE_BRAND = "vehicleBrand";
    String VEHICLE_MODEL = "vehicleModel";
    String VEHICLE_NUMBER = "vehicleNumber";
    String PARTNER_RATING = "driverRating";
    String RATING = "rating";

    String PHONE = "phone";
    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String FULL_NAME = "fullName";
    String ROLE = "role";
    String LAT = "lat";
    String LNG = "lng";
    String PROFILE_PHOTO = "imageUrl";
    String COVER_PHOTO = "coverImageUrl";
    String PROMOTION_CODE = "promoCode";
    String EMAIL = "email";
    String RIDER_ACCOUNT_NO = "accountNo";
    String RIDER_MAIN_BALANCE = "balance";
    String USABLE_BALANCE = "usableBalance";
    String COMMISSION = "commission";
    String TOTAL_COMMISSION = "totalCommission";
    String TOTAL_RIDE = "totalRide";
    String RIDER_IDENTIFIER = "identifier";

    String ADDRESS = "address";
    String HOUSE = "house";
    String ROAD = "road";
    String ZIP_CODE = "zipCode";
    String FAX = "fax";
    String UNIT = "unit";
    String CITY = "stateProvince";
    String COUNTRY = "country";

    String PREDICTED_AMOUNT = "predictedAmount";
    String PREDICTED_KILOMETER = "predictedKilometer";
    String PREDICTED_MINUTE = "predictedMinute";
    String RUNNING_KILOMETER = "kilometer";

    String PICKUP_LOCATION = "pickupLocation";
    String DESTINATION = "destination";
    String DEST_LAT = "destinationLat";
    String DEST_LNG = "destinationLng";

    String VEHICLE_DETAILS = "vehicleDetails";
    String DRIVER_DETAILS = "driverDetails";
    String RIDER_DETAILS = "userDetails";

    String PROMOTION_CODE_START = "Promotion Code ( ";
    String PROMOTION_CODE_END = " )";
    String ACCOUNT_NO_START = "Account Number ( ";
    String ACCOUNT_NO_END = " )";

    String DOUBLE_QUOTES = "";
    String NULLS = "null";

    String HOUSES = "House : ";
    String ROADS = ", Road : ";
    String UNITS = "Unit : ";
    String ZIP_CODES = ", ZipCode : ";
    String FAXES = "Fax : ";
    String CITIES = "City : ";
    String COUNTRIES = ", Country : ";

    String PROTOCOL_HTTP = "http";
    String PROTOCOL_HTTPS = "https";
    String AMPERSAND = "&";

    String GET = "GET";
    String POST = "POST";

    String CAR = "car";
    String CAR_EXECUTIVE = "carExecutive";
    String CAR_HOURLY = "carHourly";
    String CAR_HOURLY_EXECUTIVE = "carExecutiveHourly";
    String BIKE = "bike";
    String BIKE_HOURLY = "bikeHourly";
    String CNG = "cng";
    String CNG_HOURLY = "cngHourly";
    String PICKUP = "pickup";

    String BASE_FARE = "baseFare";
    String MINIMUM_FARE = "minimumFare";
    String FARE_PER_MINUTE = "minuteFare";
    String FARE_PER_KILOMETER = "kilometerFare";
    String FARE_OUT_OF_CITY = "outOfCity";
    String USABLE_DISCOUNT = "usableDiscount";
    String TOTAL_KILOMETERS = "totalKilometer";
    String TOTAL_MINUTES = "totalMinute";
    String TOTAL_FARE = "totalFare";
    String FARE_QR_CODE = "fareQrCode";

    String LOCATION_NAME = "https://maps.googleapis.com/maps/api/geocode/json?";
    String POLY_POINTS = "https://maps.googleapis.com/maps/api/directions/json?";
    String SEPARATOR = "/";

    String ERROR_RESPONSE = "error_response ";
    String HTTP_RESPONSE = "ok_http_response ";

    String ACCESS_TOKENS = "access_token";
    String TRIP_HISTORY = "history";
    String TRIP_DATE_TIME = "startTime";
    String TRIP_AMOUNT = "amount";
    String TRIP_STATUS = "modeStatus";
    String VEHICLE = "vehicle";
    String TRIP_RECEIPT = "receipt";
    String DISTANCE_FARE = "distance";
    String TIME_FARE = "time";
    String FARE_DISCOUNT = "fareDiscount";

}