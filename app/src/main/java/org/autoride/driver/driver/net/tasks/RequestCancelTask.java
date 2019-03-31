package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.invokers.RequestCancelInvoker;
import org.json.JSONObject;

public class RequestCancelTask extends AsyncTask<String, Integer, BasicBean> {

    private RequestCancelTaskListener requestCancelTaskListener;

    private JSONObject postData;

    public RequestCancelTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected BasicBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        RequestCancelInvoker requestCancelInvoker = new RequestCancelInvoker(null, postData);
        return (BasicBean) requestCancelInvoker.invokeRequestCancelWS();
    }

    @Override
    protected void onPostExecute(BasicBean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result != null)
            requestCancelTaskListener.dataDownloadedSuccessfully(result);
        else
            requestCancelTaskListener.dataDownloadFailed(result);
    }

    public static interface RequestCancelTaskListener {

        void dataDownloadedSuccessfully(BasicBean basicBean);

        void dataDownloadFailed(BasicBean basicBean);
    }

    public RequestCancelTaskListener getRequestCancelTaskListener() {
        return requestCancelTaskListener;
    }

    public void setRequestCancelTaskListener(RequestCancelTaskListener requestCancelTaskListener) {
        this.requestCancelTaskListener = requestCancelTaskListener;

    }
}