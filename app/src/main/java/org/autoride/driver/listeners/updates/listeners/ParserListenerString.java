package org.autoride.driver.listeners.updates.listeners;

public interface ParserListenerString {

    void onLoadCompleted(String info);

    void onLoadFailed(String info);
}