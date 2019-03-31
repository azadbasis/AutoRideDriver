package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.ForgotPasswordInvoker;
import org.json.JSONObject;

public class ForgotPasswordTask extends AsyncTask<String, Integer, AuthBean> {

    private ForgotPasswordTaskListener forgotPasswordTaskListener;

    private JSONObject postData;

    public ForgotPasswordTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        ForgotPasswordInvoker forgotPasswordInvoker = new ForgotPasswordInvoker(null, postData);
        return forgotPasswordInvoker.invokeForgotPasswordWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            forgotPasswordTaskListener.dataDownloadedSuccessfully(result);
        else
            forgotPasswordTaskListener.dataDownloadFailed(result);
    }

    public static interface ForgotPasswordTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public ForgotPasswordTaskListener getForgotPasswordTaskListener() {
        return forgotPasswordTaskListener;
    }

    public void setForgotPasswordTaskListener(ForgotPasswordTaskListener forgotPasswordTaskListener) {
        this.forgotPasswordTaskListener = forgotPasswordTaskListener;
    }
}