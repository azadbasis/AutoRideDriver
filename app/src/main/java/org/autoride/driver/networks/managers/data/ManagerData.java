package org.autoride.driver.networks.managers.data;

import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.listeners.updates.listeners.ParserListener;
import org.autoride.driver.listeners.updates.listeners.ParserListenerString;
import org.autoride.driver.model.DriverInfo;
import org.autoride.driver.networks.tasks.PerformDriverTask;
import org.autoride.driver.networks.tasks.PerformDriverTaskWithHeader;
import org.autoride.driver.networks.tasks.PerformStringResponseTask;
import org.json.JSONObject;

public class ManagerData implements AppsConstants {

    public static void taskManager(String method, String apiUrl, JSONObject postBody, final ParserListener listener) {
        PerformDriverTask task = new PerformDriverTask(method, apiUrl, postBody);
        task.setTaskListener(new PerformDriverTask.PerformRiderTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(DriverInfo driverInfo) {
                if (driverInfo == null) {
                    listener.onLoadFailed(driverInfo);
                } else {
                    if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        listener.onLoadCompleted(driverInfo);
                    } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        listener.onLoadFailed(driverInfo);
                    } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        listener.onLoadFailed(driverInfo);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(DriverInfo driverInfo) {
                listener.onLoadFailed(driverInfo);
            }
        });
        task.execute();
    }

    public static void taskManager(String method, String apiUrl, JSONObject postBody, JSONObject postHeader, final ParserListener listener) {
        PerformDriverTaskWithHeader task = new PerformDriverTaskWithHeader(method, apiUrl, postBody, postHeader);
        task.setTaskListener(new PerformDriverTaskWithHeader.PerformRiderTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(DriverInfo driverInfo) {
                if (driverInfo == null) {
                    listener.onLoadFailed(driverInfo);
                } else {
                    if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_SUCCESS)) {
                        listener.onLoadCompleted(driverInfo);
                    } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERROR)) {
                        listener.onLoadFailed(driverInfo);
                    } else if (driverInfo.getStatus().equalsIgnoreCase(WEB_RESPONSE_ERRORS)) {
                        listener.onLoadFailed(driverInfo);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(DriverInfo driverInfo) {
                listener.onLoadFailed(driverInfo);
            }
        });
        task.execute();
    }

    public static void stringTaskManager(String method, String apiUrl, JSONObject postBody, JSONObject postHeader, final ParserListenerString listener) {
        PerformStringResponseTask task = new PerformStringResponseTask(method, apiUrl, postBody, postHeader);
        task.setTaskListener(new PerformStringResponseTask.PerformStringResponseTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(String response) {
                if (response == null) {
                    listener.onLoadFailed(response);
                } else {
                    listener.onLoadCompleted(response);
                }
            }

            @Override
            public void dataDownloadFailed(String response) {
                listener.onLoadFailed(response);
            }
        });
        task.execute();
    }
}