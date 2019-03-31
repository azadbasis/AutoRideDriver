package org.autoride.driver.listeners;

import org.autoride.driver.model.AuthBean;

public interface RegistrationListener {

    void onLoadCompleted(AuthBean authBean);

    void onLoadFailed(AuthBean authBean);
}