package org.autoride.driver.networks.parsers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.DriverAsUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseParser {

    public static AuthBean responseParser(String responseString) {

        AuthBean authBean = new AuthBean();
        List<DriverAsUser> driverInfoList = new ArrayList<DriverAsUser>();
        DriverAsUser driverInfo = new DriverAsUser();
        JSONObject jsonObj = null;

        try {
            if (responseString != null) {
                jsonObj = new JSONObject(responseString);
                if (jsonObj.has("statusCode")) {

                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

                        if (jsonObj.has("data")) {
                            JSONObject dataObj = jsonObj.optJSONObject("data");
                            if (dataObj != null) {
                                if (dataObj.has("user")) {
                                    JSONObject userObj = dataObj.optJSONObject("user");
                                    if (userObj != null) {
                                        if (userObj.has("firstName")) {
                                            authBean.setFirstName(userObj.getString("firstName"));
                                        }

                                        if (userObj.has("lastName")) {
                                            authBean.setLastName(userObj.getString("lastName"));
                                        }

                                        if (userObj.has("email")) {
                                            authBean.setEmail(userObj.getString("email"));
                                        }

                                        if (userObj.has("address")) {
                                            JSONObject addressObj = userObj.optJSONObject("address");
                                            if (addressObj != null) {

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (jsonObj.has("imageUrl")) {
                            authBean.setProfilePhoto(jsonObj.optString("imageUrl"));
                        }

                        if (jsonObj.has("vehicle")) {
                            JSONObject dataObj = jsonObj.optJSONObject("vehicle");
                            if (dataObj != null) {
                                if (dataObj.has("vehicleType")) {
                                    driverInfo.setVehicleType(dataObj.getString("vehicleType"));
                                }

                                if (dataObj.has("vehicleBrand")) {
                                    driverInfo.setVehicleBrand(dataObj.getString("vehicleBrand"));
                                }

                                if (dataObj.has("vehicleModel")) {
                                    driverInfo.setDriverVehicleModel(dataObj.getString("vehicleModel"));
                                }

                                if (dataObj.has("vehicleNumber")) {
                                    driverInfo.setVehicleNo(dataObj.getString("vehicleNumber"));
                                }

                                driverInfoList.add(driverInfo);
                                authBean.setDriverInfoList(driverInfoList);
                            }
                        }

                        if (jsonObj.optString("status").equalsIgnoreCase("Success")) {
                            authBean.setStatus("Success");
                            if (jsonObj.has("message")) {
                                authBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                authBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }

                        if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                            authBean.setStatus("Error");
                            if (jsonObj.has("message")) {
                                authBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                authBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("401")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("404")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("406")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("500")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }
                } else {
                    authBean = null;
                }
            } else {
                authBean = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            authBean = null;
        }
        return authBean;
    }
}