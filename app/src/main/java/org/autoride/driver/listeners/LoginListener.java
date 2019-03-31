package org.autoride.driver.listeners;

import org.autoride.driver.model.AuthBean;

public interface LoginListener {

    void onLoadCompleted(AuthBean authBean);

    void onLoadFailed(AuthBean authBean);
}