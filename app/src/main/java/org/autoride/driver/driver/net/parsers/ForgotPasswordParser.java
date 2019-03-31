package org.autoride.driver.driver.net.parsers;

import org.autoride.driver.model.AuthBean;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordParser {

    public AuthBean parseForgotPasswordResponse(String wsResponseString) {
        AuthBean authBean = new AuthBean();
        JSONObject jsonObj = null;
        try {
            if (wsResponseString != null) {
                jsonObj = new JSONObject(wsResponseString);
                if (jsonObj.has("statusCode")) {
                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

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

                    if (jsonObj.optString("statusCode").equals("500")) {
                        authBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            authBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("406")) {
                        authBean.setStatus("Errors");
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