package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.LoginInvoker;
import org.json.JSONObject;

public class LoginTask extends AsyncTask<String, Integer, AuthBean> {

    private LoginTaskListener loginTaskListener;

    private JSONObject postData;

    public LoginTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        LoginInvoker loginInvoker = new LoginInvoker(null, postData);
        return loginInvoker.invokeLoginWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            loginTaskListener.dataDownloadedSuccessfully(result);
        else
            loginTaskListener.dataDownloadFailed(result);
    }

    public static interface LoginTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public LoginTaskListener getLoginTaskListener() {
        return loginTaskListener;
    }

    public void setLoginTaskListener(LoginTaskListener loginTaskListener) {
        this.loginTaskListener = loginTaskListener;
    }
}