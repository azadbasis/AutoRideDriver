package org.autoride.driver.driver.net.parsers;

import android.util.Log;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.utils.AppsUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginParser {

    public AuthBean parseLoginResponse(String wsResponseString) {
        AuthBean authBean = new AuthBean();
        JSONObject jsonObj = null;
        try {
            if (wsResponseString != null) {
                jsonObj = new JSONObject(wsResponseString);
                if (jsonObj.has("statusCode")) {
                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

                        if (jsonObj.optString("status").equalsIgnoreCase("Success")) {

                            String st = AppsUtils.status();
                            Log.i("tag", "sadsds" + st);

                            authBean.setStatus(jsonObj.optString("status"));
                            if (jsonObj.has("message")) {
                                authBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                authBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }

                        if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                            authBean.setStatus(jsonObj.optString("status"));
                            if (jsonObj.has("message")) {
                                authBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                authBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("500")) {
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

                    if (jsonObj.optString("statusCode").equals("404")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("401")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }
                    if (jsonObj.has("message")) {
                        authBean.setErrorMsg(jsonObj.optString("message"));
                    }
                }

                if (jsonObj.has("response")) {
                    authBean.setErrorMsg(jsonObj.optString("response"));
                }

                if (jsonObj.has("data")) {
                    JSONObject dataObj = jsonObj.optJSONObject("data");
                    if (dataObj != null) {
                        try {

                            if (dataObj.has("accessToken")) {
                                authBean.setAccessToken(dataObj.optString("accessToken"));
                            }

                            if (dataObj.has("rememberToken")) {
                                authBean.setRememberToken(dataObj.optString("rememberToken"));
                            }

                            if (dataObj.has("user")) {

                                JSONObject userObj = dataObj.optJSONObject("user");

                                if (userObj != null) {

                                    if (userObj.has("userId")) {
                                        authBean.setUserID(userObj.optString("userId"));
                                    }

                                    if (userObj.has("phone")) {
                                        authBean.setUserPhone(userObj.optString("phone"));
                                    }

                                    if (userObj.has("firstName")) {
                                        authBean.setFirstName(userObj.optString("firstName"));
                                    }

                                    if (userObj.has("lastName")) {
                                        authBean.setLastName(userObj.optString("lastName"));
                                    }

                                    if (userObj.has("verificationStatus")) {
                                        authBean.setVerificationStatus(userObj.optString("verificationStatus"));
                                    }

                                    if (userObj.has("role")) {
                                        authBean.setUserRole(userObj.optString("role"));
                                    }

                                    if (userObj.has("lat")) {
                                        authBean.setLastLatitude(userObj.optString("lat"));
                                    }

                                    if (userObj.has("lng")) {
                                        authBean.setLastLongitude(userObj.optString("lng"));
                                    }

                                    if (userObj.has("imageUrl")) {
                                        authBean.setProfilePhoto(userObj.optString("imageUrl"));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            authBean = null;
                            e.printStackTrace();
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