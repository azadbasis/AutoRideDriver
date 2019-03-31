package org.autoride.driver.driver.net.parsers;

import org.autoride.driver.model.DriverAddress;
import org.autoride.driver.model.DriverAsUser;
import org.autoride.driver.model.DriverInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverInfoParser {

    public List<DriverInfo> parseDriverInfoRequest(String vResponseString) {

        DriverInfo driverInfo = new DriverInfo();
        DriverAsUser driverAsUser = new DriverAsUser();
        DriverAddress driverAddress = new DriverAddress();
        List<DriverInfo> driverInfoList = new ArrayList<DriverInfo>();
        JSONObject jsonObj = null;

        try {
            if (vResponseString != null) {
                jsonObj = new JSONObject(vResponseString);
                if (jsonObj.has("statusCode")) {
                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {
                        // problem here no status with success
                        driverInfo.setStatus("Success");

                        if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                            driverInfo.setStatus("Error");
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("401")) {
                        driverInfo.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            driverInfo.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("404")) {
                        driverInfo.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            driverInfo.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("406")) {
                        driverInfo.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            driverInfo.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("500")) {
                        driverInfo.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            driverInfo.setErrorMsg(jsonObj.optString("error"));
                        }
                    }
                }

                if (jsonObj.has("message")) {
                    driverInfo.setErrorMsg(jsonObj.optString("message"));
                }

                if (jsonObj.has("data")) {

                    JSONObject dataObj = jsonObj.optJSONObject("data");

                    if (dataObj != null) {
                        try {

                            if (dataObj.has("accessToken")) {
                                driverInfo.setAccessToken(dataObj.optString("accessToken"));
                            }

                            if (dataObj.has("rememberToken")) {
                                driverInfo.setRememberToken(dataObj.optString("rememberToken"));
                            }

                            if (dataObj.has("publicKey")) {
                                driverInfo.setPublicKey(dataObj.optString("publicKey"));
                            }

                            if (dataObj.has("privateKey")) {
                                driverInfo.setPrivateKey(dataObj.optString("privateKey"));
                            }

                            if (dataObj.has("user")) {

                                JSONObject userObj = dataObj.optJSONObject("user");

                                if (userObj != null) {

                                    if (userObj.has("userId")) {
                                        driverAsUser.setUserId(userObj.optString("userId"));
                                    }

                                    if (userObj.has("accountNo")) {
                                        driverAsUser.setAccountNo(userObj.optString("accountNo"));
                                    }

                                    if (userObj.has("identifier")) {
                                        driverAsUser.setIdentifier(userObj.optString("identifier"));
                                    }
                                    if (userObj.has("balance")) {
                                        driverAsUser.setMainBalance(userObj.optString("balance"));
                                    }

                                    if (userObj.has("usableBalance")) {
                                        driverAsUser.setUsableBalance(userObj.optString("usableBalance"));
                                    }

                                    if (userObj.has("commission")) {
                                        driverAsUser.setCommission(userObj.optString("commission"));
                                    }

                                    if (userObj.has("totalCommission")) {
                                        driverAsUser.setTotalCommission(userObj.optString("totalCommission"));
                                    }

                                    if (userObj.has("lostCommission")) {
                                        driverAsUser.setLostCommission(userObj.optString("lostCommission"));
                                    }

                                    if (userObj.has("driverRating")) {
                                        driverAsUser.setDriverRating(userObj.optString("driverRating"));
                                    }

                                    if (userObj.has("verificationStatus")) {
                                        driverAsUser.setVerificationStatus(userObj.optString("verificationStatus"));
                                    }

                                    if (userObj.has("phone")) {
                                        driverAsUser.setDriverPhone(userObj.optString("phone"));
                                    }

                                    if (userObj.has("email")) {
                                        driverAsUser.setDriverEmail(userObj.optString("email"));
                                    }

                                    if (userObj.has("vehicleModel")) {
                                        driverAsUser.setDriverVehicleModel(userObj.optString("vehicleModel"));
                                    }

                                    if (userObj.has("firstName")) {
                                        driverAsUser.setFirstName(userObj.optString("firstName"));
                                    }

                                    if (userObj.has("lastName")) {
                                        driverAsUser.setLastName(userObj.optString("lastName"));
                                    }

                                    if (userObj.has("promoCode")) {
                                        driverAsUser.setPromoCode(userObj.optString("promoCode"));
                                    }

                                    if (userObj.has("role")) {
                                        driverAsUser.setUserRole(userObj.optString("role"));
                                    }

                                    if (userObj.has("totalTrip")) {
                                        driverAsUser.setTotalTrip(userObj.optString("totalTrip"));
                                    }

                                    if (userObj.has("dailyTarget")) {
                                        driverAsUser.setDailyTarget(userObj.optString("dailyTarget"));
                                    }

                                    if (userObj.has("weeklyTarget")) {
                                        driverAsUser.setWeeklyTarget(userObj.optString("weeklyTarget"));
                                    }

                                    if (userObj.has("monthlyTarget")) {
                                        driverAsUser.setMonthlyTarget(userObj.optString("monthlyTarget"));
                                    }

                                    if (userObj.has("drivingYears")) {
                                        driverAsUser.setDrivingYears(userObj.optString("drivingYears"));
                                    }

                                    if (userObj.has("pickupRate")) {
                                        driverAsUser.setPickupRate(userObj.optString("pickupRate"));
                                    }

                                    if (userObj.has("speed")) {
                                        driverAsUser.setSpeed(userObj.optString("speed"));
                                    }

                                    if (userObj.has("lat")) {
                                        driverAsUser.setLastLatitude(userObj.optString("lat"));
                                    }

                                    if (userObj.has("lng")) {
                                        driverAsUser.setLastLongitude(userObj.optString("lng"));
                                    }

                                    if (userObj.has("imageUrl")) {
                                        driverAsUser.setProfilePhoto(userObj.optString("imageUrl"));
                                    }

                                    if (userObj.has("coverImageUrl")) {
                                        driverAsUser.setVehiclePhoto(userObj.optString("coverImageUrl"));
                                    }

                                    if (userObj.has("licenceImageUrl")) {
                                        driverAsUser.setLicencePhoto(userObj.optString("licenceImageUrl"));
                                    }

                                    if (userObj.has("insuranceImageUrl")) {
                                        driverAsUser.setInsurancePhoto(userObj.optString("insuranceImageUrl"));
                                    }

                                    if (userObj.has("bluebookImageUrl")) {
                                        driverAsUser.setBlueBookPhoto(userObj.optString("bluebookImageUrl"));
                                    }

                                    if (userObj.has("taxtokenImageUrl")) {
                                        driverAsUser.setTexTokenPhoto(userObj.optString("taxtokenImageUrl"));
                                    }

                                    driverAsUser.setFullName(driverAsUser.getFirstName() + " " + driverAsUser.getLastName());

                                    if (userObj.has("touchService")) {
                                        JSONObject touchServiceObj = userObj.optJSONObject("touchService");
                                        if (touchServiceObj != null) {

                                            if (touchServiceObj.has("touchPointPhone")) {
                                                driverAddress.setTouchPointPhone(touchServiceObj.optString("touchPointPhone"));
                                            }

                                            if (touchServiceObj.has("lat")) {
                                                driverAddress.setTouchPointLat(touchServiceObj.optDouble("lat"));
                                            }

                                            if (touchServiceObj.has("lng")) {
                                                driverAddress.setTouchPointLng(touchServiceObj.optDouble("lng"));
                                            }
                                        }
                                    }

                                    if (userObj.has("address")) {
                                        JSONObject dataObjAddress = userObj.optJSONObject("address");
                                        if (dataObjAddress != null) {

                                            if (dataObjAddress.has("house")) {
                                                driverAddress.setHouse(dataObjAddress.optString("house"));
                                            }

                                            if (dataObjAddress.has("road")) {
                                                driverAddress.setRoad(dataObjAddress.optString("road"));
                                            }

                                            if (dataObjAddress.has("stateProvince")) {
                                                driverAddress.setStateProvince(dataObjAddress.optString("stateProvince"));
                                            }

                                            if (dataObjAddress.has("country")) {
                                                driverAddress.setCountry(dataObjAddress.optString("country"));
                                            }

                                            if (dataObjAddress.has("unit")) {
                                                driverAddress.setUnit(dataObjAddress.optString("unit"));
                                            }

                                            if (dataObjAddress.has("zipCode")) {
                                                driverAddress.setZipCode(dataObjAddress.optString("zipCode"));
                                            }

                                            if (dataObjAddress.has("fax")) {
                                                driverAddress.setFax(dataObjAddress.optString("fax"));
                                            }
                                        }
                                    }

                                    if (userObj.has("vehicle")) {
                                        JSONObject dataObjVehicle = userObj.optJSONObject("vehicle");
                                        if (dataObjVehicle != null) {
                                            if (dataObjVehicle.has("vehicleType")) {
                                                driverAsUser.setVehicleType(dataObjVehicle.optString("vehicleType"));
                                            }

                                            if (dataObjVehicle.has("vehicleBrand")) {
                                                driverAsUser.setVehicleBrand(dataObjVehicle.optString("vehicleBrand"));
                                            }

                                            if (dataObjVehicle.has("vehicleModel")) {
                                                driverAsUser.setDriverVehicleModel(dataObjVehicle.optString("vehicleModel"));
                                            }

                                            if (dataObjVehicle.has("vehicleNumber")) {
                                                driverAsUser.setVehicleNo(dataObjVehicle.optString("vehicleNumber"));
                                            }
                                        }
                                    }
                                    driverAsUser.setDriverAddress(driverAddress);
                                    driverInfo.setDriverAsUser(driverAsUser);
                                }
                            }
                        } catch (Exception e) {
                            driverInfoList = null;
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                driverInfoList = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            driverInfoList = null;
        }
        driverInfoList.add(driverInfo);
        return driverInfoList;
    }
}