package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.ImageUpdateInvoker;
import org.json.JSONObject;

import java.util.List;

public class ImageUpdateTask extends AsyncTask<String, Integer, AuthBean> {

    private ImageUpdateTaskListener imageUpdateTaskListener;

    private JSONObject postData;
    List<String> list;

    public ImageUpdateTask(JSONObject postData, List<String> list) {
        super();
        this.postData = postData;
        this.list = list;
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        ImageUpdateInvoker imageUpdateInvoker = new ImageUpdateInvoker(null, postData);
        return imageUpdateInvoker.invokeImageUpdateWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            imageUpdateTaskListener.dataDownloadedSuccessfully(result);
        else
            imageUpdateTaskListener.dataDownloadFailed();
    }

    public static interface ImageUpdateTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed();
    }

    public ImageUpdateTaskListener getImageUpdateTaskListener() {
        return imageUpdateTaskListener;
    }

    public void setImageUpdateTaskListener(ImageUpdateTaskListener imageUpdateTaskListener) {
        this.imageUpdateTaskListener = imageUpdateTaskListener;
    }
}