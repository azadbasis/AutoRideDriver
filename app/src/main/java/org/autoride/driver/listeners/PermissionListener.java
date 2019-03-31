package org.autoride.driver.listeners;

public interface PermissionListener {
    void onPermissionCheckCompleted(int requestCode, boolean isPermissionGranted);
}