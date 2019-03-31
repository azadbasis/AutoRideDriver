package org.autoride.driver.driver.net.parsers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.DriverAsUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverStatusParser {

    public static AuthBean parseDriverStatus(String wsResponseString) {

        AuthBean authBean = new AuthBean();
        List<DriverAsUser> driverInfoList = new ArrayList<DriverAsUser>();
        DriverAsUser driverInfo = new DriverAsUser();
        JSONObject jsonObj = null;
        try {
            if (wsResponseString != null) {
                jsonObj = new JSONObject(wsResponseString);
                if (jsonObj.has("statusCode")) {

                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

                        if (jsonObj.has("data")) {
                            JSONObject dataObj = jsonObj.optJSONObject("data");
                            if (dataObj != null) {
                                if (dataObj.has("status")) {
                                    authBean.setDriverCurrentStatus(dataObj.getString("status"));
                                }
                            }
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