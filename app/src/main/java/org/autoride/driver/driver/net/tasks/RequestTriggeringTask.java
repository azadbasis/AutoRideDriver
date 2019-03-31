package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.invokers.RequestTriggeringInvoker;
import org.json.JSONObject;

public class RequestTriggeringTask extends AsyncTask<String, Integer, BasicBean> {

    private RequestTriggeringTaskListener requestTriggeringTaskListener;

    private JSONObject postData;

    public RequestTriggeringTask(JSONObject postData) {
        super();
        this.postData = postData;
    }

    @Override
    protected BasicBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        RequestTriggeringInvoker requestTriggeringInvoker = new RequestTriggeringInvoker(null, postData);
        return requestTriggeringInvoker.invokerequestTriggeringWS();
    }

    @Override
    protected void onPostExecute(BasicBean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result != null)
            requestTriggeringTaskListener.dataDownloadedSuccessfully(result);
        else
            requestTriggeringTaskListener.dataDownloadFailed(result);
    }

    public static interface RequestTriggeringTaskListener {

        void dataDownloadedSuccessfully(BasicBean basicBean);

        void dataDownloadFailed(BasicBean basicBean);
    }

    public RequestTriggeringTaskListener getRequestTriggeringTaskListener() {
        return requestTriggeringTaskListener;
    }

    public void setRequestTriggeringTaskListener(RequestTriggeringTaskListener requestTriggeringTaskListener) {
        this.requestTriggeringTaskListener = requestTriggeringTaskListener;
    }
}