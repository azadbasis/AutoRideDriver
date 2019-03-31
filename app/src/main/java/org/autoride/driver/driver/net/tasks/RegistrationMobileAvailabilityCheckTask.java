package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.invokers.RegistrationMobileAvailabilityCheckInvoker;
import org.json.JSONObject;

public class RegistrationMobileAvailabilityCheckTask extends AsyncTask<String, Integer, BasicBean> {

    private RegistrationMobileAvailabilityCheckTaskListener registrationMobileAvailabilityCheckTaskListener;

    private JSONObject postData;

    public RegistrationMobileAvailabilityCheckTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected BasicBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        RegistrationMobileAvailabilityCheckInvoker registrationMobileAvailabilityCheckInvoker = new RegistrationMobileAvailabilityCheckInvoker(null, postData);
        return registrationMobileAvailabilityCheckInvoker.invokeRegistrationMobileAvailabilityCheckWS();
    }

    @Override
    protected void onPostExecute(BasicBean result) {
        super.onPostExecute(result);
        if (result != null)
            registrationMobileAvailabilityCheckTaskListener.dataDownloadedSuccessfully(result);
        else
            registrationMobileAvailabilityCheckTaskListener.dataDownloadFailed(result);
    }

    public static interface RegistrationMobileAvailabilityCheckTaskListener {

        void dataDownloadedSuccessfully(BasicBean basicBean);

        void dataDownloadFailed(BasicBean basicBean);
    }

    public RegistrationMobileAvailabilityCheckTaskListener getRegistrationMobileAvailabilityCheckTaskListener() {
        return registrationMobileAvailabilityCheckTaskListener;
    }

    public void setRegistrationMobileAvailabilityCheckTaskListener(RegistrationMobileAvailabilityCheckTaskListener registrationMobileAvailabilityCheckTaskListener) {
        this.registrationMobileAvailabilityCheckTaskListener = registrationMobileAvailabilityCheckTaskListener;
    }
}