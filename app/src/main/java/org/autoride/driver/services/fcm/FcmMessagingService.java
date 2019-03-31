package org.autoride.driver.services.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.autoride.driver.DriverMainActivity;
import org.autoride.driver.R;
import org.autoride.driver.rider.request.RiderRideRequestActivity;

import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FcmMService";
    private Handler handler;

    @Override
    public void onMessageReceived(RemoteMessage rm) {

        if (rm.getData() != null) {

            Map<String, String> data = rm.getData();
            String title = data.get("title");

            if (title.equalsIgnoreCase("rider_ride_request")) {

                handler = new Handler(Looper.getMainLooper());

                final String riderToken = data.get("rider_fcm_token");
                final String vehicleType = data.get("vehicle_type");
                final String riderId = data.get("rider_id");
                final String riderDestPlace = data.get("rider_destination_place");
                final String confirmFare = data.get("confirm_fare");
                final String pickupLocation = data.get("rider_pickup_location");
                final String destination = data.get("rider_destination");
                final String destKm = data.get("dest_km");
                final String destMin = data.get("dest_min");
                final String riderRating = data.get("rider_rating");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notificationsBuilder("rider_ride_request", riderToken, vehicleType,
                                riderId, riderDestPlace, confirmFare, destKm, destMin, pickupLocation, destination, riderRating);
                    }
                });
            } else if (title.equalsIgnoreCase("rider_trip_canceled")) {

                handler = new Handler(Looper.getMainLooper());

                final String message = data.get("message");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FcmMessagingService.this, DriverMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra("notification_status", "rider_trip_canceled");
                        intent.putExtra("notifications_msg", message);

                        startActivity(intent);
                    }
                });
            } else if (title.equalsIgnoreCase("rider_destination_update")) {

                handler = new Handler(Looper.getMainLooper());

                final String message = data.get("message");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notificationsBuilder("Rider Change Destination", message, "");
                    }
                });
            }
        }
    }

    private void notificationsBuilder(String nStatus, String token, String vType, String riderId, String riderDestPlace, String confirmFare,
                                      String km, String min, String pickupLocation, String destination, String riderRating) {
        try {

            LatLng rPickupLocation = new Gson().fromJson(pickupLocation, LatLng.class);
            LatLng rDestination = new Gson().fromJson(destination, LatLng.class);

            Intent intent = new Intent(FcmMessagingService.this, RiderRideRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", "Auto Ride Rider Request");
            intent.putExtra("notification_status", nStatus);
            intent.putExtra("rider_fcm_token", token);
            intent.putExtra("vehicle_type", vType);
            intent.putExtra("rider_id", riderId);
            intent.putExtra("rider_destination_place", riderDestPlace);
            intent.putExtra("confirm_fare", confirmFare);
            intent.putExtra("pickup_lat", rPickupLocation.latitude);
            intent.putExtra("pickup_lng", rPickupLocation.longitude);
            intent.putExtra("dest_lat", rDestination.latitude);
            intent.putExtra("dest_lng", rDestination.longitude);
            intent.putExtra("dest_km", km);
            intent.putExtra("dest_min", min);
            intent.putExtra("rider_rating", riderRating);

            startActivity(intent);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

//            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            vib.vibrate(15000);
//
//            Notification notification = builder.build();
//            notification.sound = Uri.parse("android.resource://org.autoride.driver/" + R.raw.notification_tone);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notificationsBuilder(String title, String mBody, String driverLatLng) {
        try {

            Intent intent = new Intent(FcmMessagingService.this, DriverMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_auto_ride))
                    .setSmallIcon(R.drawable.ic_directions_bus_black_24dp)
                    .setContentTitle(title)
                    .setContentText(mBody)
                    .setContentIntent(contentIntent);

            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(5000);

            NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}