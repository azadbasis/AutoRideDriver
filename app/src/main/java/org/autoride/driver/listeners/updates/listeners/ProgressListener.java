package org.autoride.driver.listeners.updates.listeners;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}