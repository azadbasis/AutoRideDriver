package org.autoride.driver.listeners.updates.listeners;

public interface PermissionListener {
    void onPermissionCheckCompleted(int requestCode, boolean isPermissionGranted);
}