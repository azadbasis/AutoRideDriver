package org.autoride.driver.listeners;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}