package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.RegistrationInvoker;
import org.json.JSONObject;

public class RegistrationTask extends AsyncTask<String, Integer, AuthBean> {

    private RegistrationTaskListener registrationTaskListener;

    private JSONObject postData;

    public RegistrationTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        RegistrationInvoker registrationInvoker = new RegistrationInvoker(null, postData);
        return registrationInvoker.invokeRegistrationWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            registrationTaskListener.dataDownloadedSuccessfully(result);
        else
            registrationTaskListener.dataDownloadFailed(result);
    }

    public static interface RegistrationTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public RegistrationTaskListener getRegistrationTaskListener() {
        return registrationTaskListener;
    }

    public void setRegistrationTaskListener(RegistrationTaskListener registrationTaskListener) {
        this.registrationTaskListener = registrationTaskListener;
    }
}