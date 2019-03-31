package org.autoride.driver.driver.net.tasks;

import android.os.AsyncTask;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.driver.net.invokers.VehicleInfoInvoker;

public class VehicleInfoTask extends AsyncTask<String, Integer, AuthBean> {

    private VehicleInfoTaskListener vehicleInfoTaskListener;

    public VehicleInfoTask() {
        super();
    }

    @Override
    protected AuthBean doInBackground(String... params) {
        System.out.println(">>>>>>>>>doInBackground");
        VehicleInfoInvoker vehicleInfoInvoker = new VehicleInfoInvoker();
        return vehicleInfoInvoker.invokeVehicleInfoWS();
    }

    @Override
    protected void onPostExecute(AuthBean result) {
        super.onPostExecute(result);
        if (result != null)
            vehicleInfoTaskListener.dataDownloadedSuccessfully(result);
        else
            vehicleInfoTaskListener.dataDownloadFailed(result);
    }

    public static interface VehicleInfoTaskListener {

        void dataDownloadedSuccessfully(AuthBean authBean);

        void dataDownloadFailed(AuthBean authBean);
    }

    public VehicleInfoTaskListener getVehicleInfoTaskListener() {
        return vehicleInfoTaskListener;
    }

    public void setVehicleInfoTaskListener(VehicleInfoTaskListener vehicleInfoTaskListener) {
        this.vehicleInfoTaskListener = vehicleInfoTaskListener;
    }
}