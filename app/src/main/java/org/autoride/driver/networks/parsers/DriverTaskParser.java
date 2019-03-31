package org.autoride.driver.networks.parsers;

import android.util.Log;

import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.model.DriverAsUser;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.model.RiderInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class DriverTaskParser implements AppsConstants {
    public static DriverInfo taskParse(String sResponse) {
        String TAG = "DriverTaskParser";
        DriverInfo driverInfo = new DriverInfo();
        JSONObject responseObj = null;
        try {
            if (sResponse != null) {
                responseObj = new JSONObject(sResponse);
                if (responseObj.has(WEB_RESPONSE_STATUS_CODE)) {

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(String.valueOf(WEB_RESPONSE_CODE_200))) {

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                            driverInfo.setStatus(responseObj.optString(WEB_RESPONSE_STATUS));
                            if (responseObj.has(WEB_RESPONSE_MESSAGE)) {
                                driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_MESSAGE));
                            } else {
                                driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                            }
                        }

                        if (responseObj.optString(WEB_RESPONSE_STATUS).equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                            driverInfo.setStatus(responseObj.optString(WEB_RESPONSE_STATUS));
                            if (responseObj.has(WEB_RESPONSE_MESSAGE)) {
                                driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_MESSAGE));
                            } else {
                                driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                            }
                        }

//                        if (responseObj.has(RIDER_FIRE_BASE_TOKEN)) {
//                            //  driverInfo.setRiderFireBaseToken(responseObj.getString(RIDER_FIRE_BASE_TOKEN));
//                        }

//                        if (responseObj.has(PROFILE_PHOTO)) {
//                            //  driverInfo.setProfilePhoto(responseObj.optString(PROFILE_PHOTO));
//                        }

                        if (responseObj.has(WEB_RESPONSE_DATA)) {
                            JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
                            if (dataObj != null) {

                                DriverAsUser driverAsUser = new DriverAsUser();
                                RiderInfo riderInfo = new RiderInfo();

                                if (dataObj.has(PARTNER)) {
                                    JSONObject partnerObj = dataObj.optJSONObject(PARTNER);
                                    if (partnerObj != null) {

                                        if (partnerObj.has(DRIVER_ID)) {
                                            driverInfo.setDriverId(partnerObj.optString(DRIVER_ID));
                                        }

                                        if (partnerObj.has(FIRST_NAME)) {
                                            driverAsUser.setFirstName(partnerObj.optString(FIRST_NAME));
                                        }

                                        if (partnerObj.has(LAST_NAME)) {
                                            driverAsUser.setLastName(partnerObj.optString(LAST_NAME));
                                        }
                                        driverAsUser.setFullName(driverAsUser.getFirstName() + " " + driverAsUser.getLastName());

                                        if (partnerObj.has(PROFILE_PHOTO)) {
                                            driverAsUser.setProfilePhoto(partnerObj.optString(PROFILE_PHOTO));
                                        }

                                        if (partnerObj.has(COVER_PHOTO)) {
                                            driverAsUser.setVehiclePhoto(partnerObj.optString(COVER_PHOTO));
                                        }

                                        if (partnerObj.has(PHONE)) {
                                            driverAsUser.setDriverPhone(partnerObj.optString(PHONE));
                                        }

                                        if (partnerObj.has(PARTNER_RATING)) {
                                            driverAsUser.setDriverRating(partnerObj.optString(PARTNER_RATING));
                                        }

                                        if (partnerObj.has(LAT)) {
                                            driverAsUser.setLastLatitude(partnerObj.optString(LAT));
                                        }

                                        if (partnerObj.has(LNG)) {
                                            driverAsUser.setLastLongitude(partnerObj.optString(LNG));
                                        }

                                        if (partnerObj.has(PARTNER_VEHICLE)) {
                                            JSONObject vehicleObj = partnerObj.optJSONObject(PARTNER_VEHICLE);
                                            if (vehicleObj != null) {

                                                if (vehicleObj.has(VEHICLE_TYPE)) {
                                                    driverAsUser.setVehicleType(vehicleObj.optString(VEHICLE_TYPE));
                                                }

                                                if (vehicleObj.has(VEHICLE_BRAND)) {
                                                    driverAsUser.setVehicleBrand(vehicleObj.optString(VEHICLE_BRAND));
                                                }

                                                if (vehicleObj.has(VEHICLE_MODEL)) {
                                                    driverAsUser.setDriverVehicleModel(vehicleObj.optString(VEHICLE_MODEL));
                                                }

                                                if (vehicleObj.has(VEHICLE_NUMBER)) {
                                                    driverAsUser.setVehicleNo(vehicleObj.optString(VEHICLE_NUMBER));
                                                }
                                            }
                                        }
                                    }
                                }

                                if (dataObj.has(WEB_RESPONSE_USER)) {
                                    JSONObject riderObj = dataObj.optJSONObject(WEB_RESPONSE_USER);
                                    if (riderObj != null) {

                                        if (riderObj.has(RIDER_ID)) {
                                            riderInfo.setRiderId(riderObj.optString(RIDER_ID));
                                        }

                                        if (riderObj.has(FIRST_NAME)) {
                                            riderInfo.setFirstName(riderObj.optString(FIRST_NAME));
                                        }

                                        if (riderObj.has(LAST_NAME)) {
                                            riderInfo.setLastName(riderObj.optString(LAST_NAME));
                                        }

                                        if (riderObj.has(PHONE)) {
                                            riderInfo.setPhone(riderObj.optString(PHONE));
                                        }

                                        if (riderObj.has(PROFILE_PHOTO)) {
                                            riderInfo.setProfilePhoto(riderObj.optString(PROFILE_PHOTO));
                                        }

                                        if (riderObj.has(LAT)) {
                                            riderInfo.setLat(riderObj.optString(LAT));
                                        }

                                        if (riderObj.has(LNG)) {
                                            riderInfo.setLng(riderObj.optString(LNG));
                                        }
                                    }
                                }

                                driverInfo.setDriverAsUser(driverAsUser);
                                driverInfo.setRiderInfo(riderInfo);
                            }
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_401)) {
                        driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + driverInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_404)) {
                        driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + driverInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_406)) {
                        driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + driverInfo.getWebMessage());
                        }
                    }

                    if (responseObj.optString(WEB_RESPONSE_STATUS_CODE).equals(WEB_RESPONSE_CODE_500)) {
                        driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                        if (responseObj.has(WEB_RESPONSE_ERROR)) {
                            driverInfo.setWebMessage(responseObj.optString(WEB_RESPONSE_ERROR));
                            Log.i(TAG, WEB_RESPONSE_ERRORS + " " + driverInfo.getWebMessage());
                        }
                    }
                } else {
                    driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                    driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
                }

//                if (responseObj.has(WEB_RESPONSE_DATA)) {
//                    JSONObject dataObj = responseObj.optJSONObject(WEB_RESPONSE_DATA);
//                    if (dataObj != null) {
//                        try {
//
//                            if (dataObj.has(ACCESS_TOKEN)) {
//                                driverInfo.setAccessToken(dataObj.optString(ACCESS_TOKEN));
//                            }
//
//                            if (dataObj.has(REMEMBER_TOKEN)) {
//                                driverInfo.setRememberToken(dataObj.optString(REMEMBER_TOKEN));
//                            }
//
//                            if (dataObj.has(PROMOTION_CODE)) {
//                                // driverInfo.setPromotionCode(dataObj.optString(PROMOTION_CODE));
//                            }
//
////                            if (dataObj.has(IS_AVAILABLE)) {
////                                driverInfo.setAvailable(dataObj.optBoolean(IS_AVAILABLE));
////                                if (driverInfo.isAvailable()) {
////                                    driverInfo.setStatus(WEB_RESPONSE_SUCCESS);
////                                } else if (!driverInfo.isAvailable()) {
////                                    driverInfo.setStatus(WEB_RESPONSE_SUCCESS);
////                                }
////                            }
//
////                            if (dataObj.has(WEB_RESPONSE_USER)) {
////                                JSONObject userObj = dataObj.optJSONObject(WEB_RESPONSE_USER);
////                                if (userObj != null) {
////
////                                    if (userObj.has(RIDER_ID)) {
////                                        driverInfo.setRiderId(userObj.optString(RIDER_ID));
////                                    }
////
////                                    if (userObj.has(PHONE)) {
////                                        riderInfo.setPhone(userObj.optString(PHONE));
////                                    }
////
////                                    if (userObj.has(FIRST_NAME)) {
////                                        riderInfo.setFirstName(userObj.optString(FIRST_NAME));
////                                    }
////
////                                    if (userObj.has(LAST_NAME)) {
////                                        riderInfo.setLastName(userObj.optString(LAST_NAME));
////                                    }
////                                    riderInfo.setFullName(userObj.optString(FIRST_NAME) + " " + userObj.optString(LAST_NAME));
////
////                                    if (userObj.has(ROLE)) {
////                                        riderInfo.setRole(userObj.optString(ROLE));
////                                    }
////
////                                    if (userObj.has(LAT)) {
////                                        riderInfo.setLastLatitude(userObj.optString(LAT));
////                                    }
////
////                                    if (userObj.has(LNG)) {
////                                        riderInfo.setLastLongitude(userObj.optString(LNG));
////                                    }
////
////                                    // this profile photo in data user
////                                    if (userObj.has(PROFILE_PHOTO)) {
////                                        riderInfo.setProfilePhoto(userObj.optString(PROFILE_PHOTO));
////                                    }
////
////                                    if (userObj.has(COVER_PHOTO)) {
////                                        riderInfo.setCoverPhoto(userObj.optString(COVER_PHOTO));
////                                    }
////
////                                    if (userObj.has(EMAIL)) {
////                                        riderInfo.setEmail(userObj.optString(EMAIL));
////                                    }
////
////                                    // this promotion code in data user
////                                    if (userObj.has(PROMOTION_CODE)) {
////                                        riderInfo.setPromotionCode(userObj.optString(PROMOTION_CODE));
////                                    }
////
////                                    if (userObj.has(RIDER_MAIN_BALANCE)) {
////                                        riderInfo.setMainBalance(userObj.optString(RIDER_MAIN_BALANCE));
////                                    }
////
////                                    if (userObj.has(USABLE_BALANCE)) {
////                                        riderInfo.setUsableBalance(userObj.optString(USABLE_BALANCE));
////                                    }
////
////                                    if (userObj.has(COMMISSION)) {
////                                        riderInfo.setCommission(userObj.optString(COMMISSION));
////                                    }
////
////                                    if (userObj.has(TOTAL_RIDE)) {
////                                        riderInfo.setTotalRide(userObj.optString(TOTAL_RIDE));
////                                    }
////
////                                    if (userObj.has(TOTAL_COMMISSION)) {
////                                        riderInfo.setTotalCommission(userObj.optString(TOTAL_COMMISSION));
////                                    }
////
////                                    if (userObj.has(RIDER_ACCOUNT_NO)) {
////                                        riderInfo.setRiderAccountNo(userObj.optString(RIDER_ACCOUNT_NO));
////                                    }
////
////                                    if (userObj.has(ADDRESS)) {
////                                        JSONObject addressObj = userObj.optJSONObject(ADDRESS);
////                                        if (addressObj != null) {
////                                            Address addressInfo = new Address();
////                                            if (addressObj.has(HOUSE)) {
////                                                addressInfo.setHouse(addressObj.optString(HOUSE));
////                                            }
////
////                                            if (addressObj.has(ROAD)) {
////                                                addressInfo.setRoad(addressObj.optString(ROAD));
////                                            }
////
////                                            if (addressObj.has(ZIP_CODE)) {
////                                                addressInfo.setZipCode(addressObj.optString(ZIP_CODE));
////                                            }
////
////                                            if (addressObj.has(FAX)) {
////                                                addressInfo.setFax(addressObj.optString(FAX));
////                                            }
////
////                                            if (addressObj.has(UNIT)) {
////                                                addressInfo.setUnit(addressObj.optString(UNIT));
////                                            }
////
////                                            if (addressObj.has(CITY)) {
////                                                addressInfo.setCity(addressObj.optString(CITY));
////                                            }
////
////                                            if (addressObj.has(COUNTRY)) {
////                                                addressInfo.setCountry(addressObj.optString(COUNTRY));
////                                            }
////                                            riderInfo.setRiderAddress(addressInfo);
////                                        }
////                                    }
////                                }
////                            }
//                        } catch (Exception e) {
//                            // riderInfo = null;
//                            driverInfo.setStatus(WEB_RESPONSE_ERRORS);
//                            driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
//                            e.printStackTrace();
//                        }
//                    }
//                }
            } else {
                driverInfo.setStatus(WEB_RESPONSE_ERRORS);
                driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
            }
        } catch (JSONException e) {
            // riderInfo = null;
            driverInfo.setStatus(WEB_RESPONSE_ERRORS);
            driverInfo.setWebMessage(WEB_ERRORS_MESSAGE);
            e.printStackTrace();
        }
        return driverInfo;
    }
}