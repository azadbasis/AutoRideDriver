package org.autoride.driver.listeners;

import org.autoride.driver.model.BasicBean;

public interface BasicListener {

    void onLoadCompleted(BasicBean basicBean);

    void onLoadFailed(BasicBean basicBean);
}