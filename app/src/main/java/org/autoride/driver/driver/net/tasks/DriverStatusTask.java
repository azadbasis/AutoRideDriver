package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.DriverStatusInvoker;

public class DriverStatusTask extends AsyncTask<String, Integer, AuthBean> {

    private DriverStatusTaskListener driverStatusTaskListener;

    public DriverStatusTask() {
        super();
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        DriverStatusInvoker driverStatusInvoker = new DriverStatusInvoker();
        return driverStatusInvoker.invokeDriverStatusWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            driverStatusTaskListener.dataDownloadedSuccessfully(result);
        else
            driverStatusTaskListener.dataDownloadFailed(result);
    }

    public static interface DriverStatusTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public DriverStatusTaskListener getDriverStatusTaskListener() {
        return driverStatusTaskListener;
    }

    public void setDriverStatusTaskListener(DriverStatusTaskListener driverStatusTaskListener) {
        this.driverStatusTaskListener = driverStatusTaskListener;
    }
}