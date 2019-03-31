package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.invokers.MobileAvailabilityCheckInvoker;
import org.json.JSONObject;

public class MobileAvailabilityCheckTask extends AsyncTask<String, Integer, BasicBean> {

    private MobileAvailabilityCheckTaskListener mobileAvailabilityCheckTaskListener;

    private JSONObject postData;

    public MobileAvailabilityCheckTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected BasicBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        MobileAvailabilityCheckInvoker mobileAvailabilityCheckInvoker = new MobileAvailabilityCheckInvoker(null, postData);
        return mobileAvailabilityCheckInvoker.invokeMobileAvailabilityCheckWS();
    }

    @Override
    protected void onPostExecute(BasicBean result) {
        super.onPostExecute(result);
        if (result != null)
            mobileAvailabilityCheckTaskListener.dataDownloadedSuccessfully(result);
        else
            mobileAvailabilityCheckTaskListener.dataDownloadFailed(result);
    }

    public static interface MobileAvailabilityCheckTaskListener {

        void dataDownloadedSuccessfully(BasicBean basicBean);

        void dataDownloadFailed(BasicBean basicBean);
    }

    public MobileAvailabilityCheckTaskListener getMobileAvailabilityCheckTaskListener() {
        return mobileAvailabilityCheckTaskListener;
    }

    public void setMobileAvailabilityCheckTaskListener(MobileAvailabilityCheckTaskListener mobileAvailabilityCheckTaskListener) {
        this.mobileAvailabilityCheckTaskListener = mobileAvailabilityCheckTaskListener;
    }
}