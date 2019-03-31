package org.autoride.driver.listeners.updates.listeners;


import org.autoride.driver.model.DriverInfo;

public interface ParserListener {

    void onLoadCompleted(DriverInfo driverInfo);

    void onLoadFailed(DriverInfo driverInfo);
}