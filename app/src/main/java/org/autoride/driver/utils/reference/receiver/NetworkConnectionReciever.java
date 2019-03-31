package org.autoride.driver.utils.reference.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.autoride.driver.SpeedMeter.service.BackService;
import org.autoride.driver.SpeedMeter.service.LocationMonitoringService;
import org.autoride.driver.app.AutoRideDriverApps;

/**
 * Created by goldenreign on 5/13/2018.
 */

public class NetworkConnectionReciever extends BroadcastReceiver {

    public static ConnectivityRecieverListener connectivityRecieverListener;

    Context myContext;
    Intent intent1;
    PendingIntent pintent1;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(connectivityRecieverListener !=null){
            connectivityRecieverListener.OnNetworkChange(isConnected);
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startService();
            startBackService();
            if (AutoRideDriverApps.LOCATION != null) {
                stopService();
            }
        }
    }

    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) AutoRideDriverApps.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    public interface ConnectivityRecieverListener{
        public void OnNetworkChange(boolean inConnected);
    }


    void startBackService() {
        intent1 = new Intent(AutoRideDriverApps.getInstance(), BackService.class);
        pintent1 = PendingIntent.getService(AutoRideDriverApps.getInstance(), 0, intent1, 0);
        AlarmManager alarm = (AlarmManager)myContext. getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, 6 * 60 * 1000, pintent1);
    }
    void startService() {
        Intent intent1 = new Intent(AutoRideDriverApps.getInstance(), LocationMonitoringService.class);
        PendingIntent pintent1 = PendingIntent.getService(AutoRideDriverApps.getInstance(), 0, intent1, 0);
        AlarmManager alarm = (AlarmManager)myContext. getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4 * 60 * 1000, 5 * 60 * 1000, pintent1);
    }
    public void stopService() {
        Intent intent2 = new Intent(AutoRideDriverApps.getInstance(), LocationMonitoringService.class);
        PendingIntent pintent2 = PendingIntent.getService(AutoRideDriverApps.getInstance(), 0, intent2, 0);
        AlarmManager alarm = (AlarmManager)myContext. getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4 * 60 * 1000, 5 * 60 * 1000, pintent2);
        if (intent2.filterEquals(intent1)) {
            myContext.stopService(intent2);
            alarm.cancel(pintent1);
        }
    }
}