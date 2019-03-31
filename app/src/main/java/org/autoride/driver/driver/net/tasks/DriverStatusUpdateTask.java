package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.DriverStatusUpdateInvoker;
import org.json.JSONObject;

public class DriverStatusUpdateTask extends AsyncTask<String, Integer, AuthBean> {

    private DriverStatusUpdateTaskListener driverStatusUpdateTaskListener;

    private JSONObject postData;

    public DriverStatusUpdateTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        DriverStatusUpdateInvoker statusUpdateInvoker = new DriverStatusUpdateInvoker(null, postData);
        return statusUpdateInvoker.invokeDriverStatusUpdateWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            driverStatusUpdateTaskListener.dataDownloadedSuccessfully(result);
        else
            driverStatusUpdateTaskListener.dataDownloadFailed(result);
    }

    public static interface DriverStatusUpdateTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public DriverStatusUpdateTaskListener getDriverStatusUpdateTaskListener() {
        return driverStatusUpdateTaskListener;
    }

    public void setDriverStatusUpdateTaskListener(DriverStatusUpdateTaskListener driverStatusUpdateTaskListener) {
        this.driverStatusUpdateTaskListener = driverStatusUpdateTaskListener;
    }
}