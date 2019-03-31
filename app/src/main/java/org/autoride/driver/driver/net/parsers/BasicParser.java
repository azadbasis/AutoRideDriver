package org.autoride.driver.driver.net.parsers;

import org.autoride.driver.model.BasicBean;
import org.json.JSONException;
import org.json.JSONObject;

public class BasicParser {

    public BasicBean parseBasicResponse(String wsResponseString) {
        BasicBean basicBean = new BasicBean();
        JSONObject jsonObj = null;
        try {
            if (wsResponseString != null) {
                jsonObj = new JSONObject(wsResponseString);
                if (jsonObj.has("statusCode")) {
                    if (jsonObj.optString("statusCode").equals(String.valueOf(200))) {

                        if (jsonObj.optString("status").equalsIgnoreCase("Success")) {
                            basicBean.setStatus("Error");
                            if (jsonObj.has("message")) {
                                basicBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                basicBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }

                        if (jsonObj.optString("status").equalsIgnoreCase("Error")) {
                            basicBean.setStatus("Success");
                            if (jsonObj.has("message")) {
                                basicBean.setErrorMsg(jsonObj.optString("message"));
                            } else {
                                basicBean.setErrorMsg("Something Went Wrong. Please Try Again Later!!!");
                            }
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("401")) {
                        basicBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            basicBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("404")) {
                        basicBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            basicBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("406")) {
                        basicBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            basicBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.optString("statusCode").equals("500")) {
                        basicBean.setStatus("Errors");
                        if (jsonObj.has("error")) {
                            basicBean.setErrorMsg(jsonObj.optString("error"));
                        }
                    }

                    if (jsonObj.has("message")) {
                        basicBean.setErrorMsg(jsonObj.optString("message"));
                    }
                }

                if (jsonObj.has("response")) {
                    basicBean.setErrorMsg(jsonObj.optString("response"));
                }

                if (jsonObj.has("message")) {
                    basicBean.setErrorMsg(jsonObj.optString("message"));
                }

                if (jsonObj.has("data")) {
                    JSONObject dataObj = jsonObj.optJSONObject("data");
                    if (dataObj != null) {
                        try {
                            if (dataObj.has("is_available")) {
                                basicBean.setPhoneAvailable(dataObj.optBoolean("is_available"));
                                if (basicBean.isPhoneAvailable()) {
                                    basicBean.setStatus("Success");
                                }
                            }
                            if (dataObj.has("message")) {
                                basicBean.setWebMessage(dataObj.optString("message"));
                            }
                        } catch (Exception e) {
                            basicBean = null;
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                basicBean = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            basicBean = null;
        }
        return basicBean;
    }
}