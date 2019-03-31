package org.autoride.driver.listeners;

import org.autoride.driver.model.AuthBean;

public interface ImageUpdateListener {

    void onLoadCompleted(AuthBean authBean);

    void onLoadFailed(String error);
}