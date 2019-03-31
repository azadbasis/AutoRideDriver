package org.autoride.driver.history.helpers;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.maps.android.ui.IconGenerator;

import org.autoride.driver.R;
import org.autoride.driver.constants.AppsConstants;
import org.autoride.driver.notifications.commons.Common;
import org.autoride.driver.notifications.helper.GoogleAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryRecyclerViewAdapter extends RecyclerView.Adapter<TripHistoryRecyclerViewAdapter.TripHistoryHolder> implements AppsConstants {

    private AppCompatActivity context;
    private List<TripHistoryInfo> tripHistoryInfoList;
    private OnItemClickListener clickListener;

    public TripHistoryRecyclerViewAdapter(AppCompatActivity context, List<TripHistoryInfo> tripHistoryInfoList, OnItemClickListener clickListener) {
        this.context = context;
        this.tripHistoryInfoList = tripHistoryInfoList;
        this.clickListener = clickListener;
    }

    @Override
    public TripHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_history_item_list, parent, false);
        return new TripHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(TripHistoryHolder holder, int position) {

        holder.setIsRecyclable(false);

        holder.tvTripDate.setText(tripHistoryInfoList.get(position).getTripDate());
        holder.tvTripAmount.setText(tripHistoryInfoList.get(position).getTripAmount());
        holder.tvTripVehicle.setText(tripHistoryInfoList.get(position).getTripVehicleDesc());
        holder.tvTripPaymentType.setText(tripHistoryInfoList.get(position).getTripPaymentType().toUpperCase());
        String status = tripHistoryInfoList.get(position).getTripStatus();
        if (status.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {
            holder.tvTripStatus.setVisibility(View.GONE);
        } else {
            holder.tvTripStatus.setText(status);
        }

        holder.initializeMapView();
    }

    @Override
    public void onViewAttachedToWindow(final TripHistoryHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(TripHistoryHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return tripHistoryInfoList.size();
    }

    class TripHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {

        private static final String TAG = "HistoryHolder";
        private TextView tvTripDate, tvTripAmount, tvTripVehicle, tvTripPaymentType, tvTripStatus;
        private GoogleMap tripHMap;
        private MapView mapViewTripHistory;
        private static final float MAP_ZOOM = 16.0f;
        private Marker pickupMarker, destinationMarker;
        private Polyline polyline;
        private GoogleAPI mService;
        private List<LatLng> polyLineList;

        TripHistoryHolder(View itemView) {
            super(itemView);
            tvTripDate = (TextView) itemView.findViewById(R.id.tv_trip_date);
            tvTripAmount = (TextView) itemView.findViewById(R.id.tv_trip_amount);
            tvTripVehicle = (TextView) itemView.findViewById(R.id.tv_trip_vehicle_desc);
            tvTripPaymentType = (TextView) itemView.findViewById(R.id.tv_trip_payment_type);
            tvTripStatus = (TextView) itemView.findViewById(R.id.tv_trip_status);
            mapViewTripHistory = (MapView) itemView.findViewById(R.id.map_view_trip_history);

            mService = Common.getGoogleAPI();

            itemView.setOnClickListener(this);
        }

        void initializeMapView() {
            if (mapViewTripHistory != null) {
                mapViewTripHistory.onCreate(null);
                mapViewTripHistory.onResume();
                mapViewTripHistory.getMapAsync(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view, getAdapterPosition());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context.getApplicationContext());
            tripHMap = googleMap;
            tripHMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            tripHMap.setMyLocationEnabled(false);
            tripHMap.getUiSettings().setZoomControlsEnabled(false);
            tripHMap.getUiSettings().setAllGesturesEnabled(false);
            GoogleMapOptions options = new GoogleMapOptions().liteMode(true);

            tripHMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    clickListener.onClick(mapViewTripHistory, getAdapterPosition());
                }
            });

            LatLng pickups = (tripHistoryInfoList.get(getAdapterPosition()).getTripPickup());
            LatLng drops = (tripHistoryInfoList.get(getAdapterPosition()).getTripDrop());
            String status = tripHistoryInfoList.get(getAdapterPosition()).getTripStatus();

            tripHMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickups, MAP_ZOOM));
            detailsRoute(pickups, drops, status);
        }

        private void detailsRoute(final LatLng pickup, LatLng destination, final String rStatus) {
            try {

                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                if (destinationMarker != null) {
                    destinationMarker.remove();
                }
                if (polyline != null) {
                    polyline.remove();
                }

                mService.getPath(Common.directionsApi(pickup, destination, context)).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyLine = poly.getString("points");
                                polyLineList = Common.decodePoly(polyLine);
                            }

                            /*JSONObject object = jsonArray.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legsObject = legs.getJSONObject(0);

                            String startPoint = legsObject.getString("start_address");
                            String endPoint = legsObject.getString("end_address");*/

                            if (rStatus.equalsIgnoreCase(MODE_STATUS_COMPLETE)) {

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng latLng : polyLineList) {
                                    builder.include(latLng);
                                }

                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 130);
                                tripHMap.moveCamera(mCameraUpdate);

                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.BLACK);
                                polylineOptions.width(8);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                polyline = tripHMap.addPolyline(polylineOptions);

                                IconGenerator destIconGen = new IconGenerator(context);
                                int destShapeSize = context.getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                                Drawable shapeDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.trip_destination_marker, null);
                                destIconGen.setBackground(shapeDrawable);
                                View destView = new View(context);
                                destView.setLayoutParams(new ViewGroup.LayoutParams(destShapeSize, destShapeSize));
                                destIconGen.setContentView(destView);
                                Bitmap destBitmap = destIconGen.makeIcon();
                                Bitmap destBitmapResized = Bitmap.createScaledBitmap(destBitmap, 25, 25, false);
                                destinationMarker = tripHMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(destBitmapResized)));
                            }

                            IconGenerator pickupIconGen = new IconGenerator(context);
                            int pickupShapeSize = context.getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
                            Drawable shapeDrawablePickup = ResourcesCompat.getDrawable(context.getResources(), R.drawable.trip_pickup_marker, null);
                            pickupIconGen.setBackground(shapeDrawablePickup);
                            View pickupView = new View(context);
                            pickupView.setLayoutParams(new ViewGroup.LayoutParams(pickupShapeSize, pickupShapeSize));
                            pickupIconGen.setContentView(pickupView);
                            Bitmap pickupBitmap = pickupIconGen.makeIcon();
                            Bitmap pickupBitmapResized = Bitmap.createScaledBitmap(pickupBitmap, 28, 28, false);
                            pickupMarker = tripHMap.addMarker(new MarkerOptions().position(pickup).icon(BitmapDescriptorFactory.fromBitmap(pickupBitmapResized)));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i(TAG, "Throwable " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}