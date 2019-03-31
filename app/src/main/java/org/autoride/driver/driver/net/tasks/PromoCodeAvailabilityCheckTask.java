package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.invokers.PromoCodeAvailabilityCheckInvoker;
import org.json.JSONObject;

public class PromoCodeAvailabilityCheckTask extends AsyncTask<String, Integer, BasicBean> {

    private PromoCodeAvailabilityCheckTaskListener promoCodeAvailabilityCheckTaskListener;

    private JSONObject postData;

    public PromoCodeAvailabilityCheckTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected BasicBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        PromoCodeAvailabilityCheckInvoker promoCodeAvailabilityCheckInvoker = new PromoCodeAvailabilityCheckInvoker(null, postData);
        return promoCodeAvailabilityCheckInvoker.invokePromoCodeAvailabilityCheckWS();
    }

    @Override
    protected void onPostExecute(BasicBean result) {
        super.onPostExecute(result);
        if (result != null)
            promoCodeAvailabilityCheckTaskListener.dataDownloadedSuccessfully(result);
        else
            promoCodeAvailabilityCheckTaskListener.dataDownloadFailed(result);
    }

    public static interface PromoCodeAvailabilityCheckTaskListener {

        void dataDownloadedSuccessfully(BasicBean basicBean);

        void dataDownloadFailed(BasicBean basicBean);
    }

    public PromoCodeAvailabilityCheckTaskListener getPromoCodeAvailabilityCheckTaskListener() {
        return promoCodeAvailabilityCheckTaskListener;
    }

    public void setPromoCodeAvailabilityCheckTaskListener(PromoCodeAvailabilityCheckTaskListener promoCodeAvailabilityCheckTaskListener) {
        this.promoCodeAvailabilityCheckTaskListener = promoCodeAvailabilityCheckTaskListener;
    }
}