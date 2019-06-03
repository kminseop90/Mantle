package cracker.com.mantle.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cracker.com.mantle.MainActivity;
import cracker.com.mantle.R;

public class GPSService extends Service implements LocationListener {

    public static final String TAG = GPSService.class.getSimpleName();
    
    double longitude;
    double latitude;
    private String locationMessage;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        startForegroundService();
        requestLocation();
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "mantle_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Mantle Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.creatoslogo)
                .setContentTitle("Mantle")
                .setContentText("Mantle에서 데이터를 수집하는 중 입니다.")
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationListener();
    }

    private void requestLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
    }

    public void removeLocationListener() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null) return;

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        locationMessage = String.valueOf(latitude) + "," + String.valueOf(longitude);
        CrackerManager.getInstance().setLocationMessage(locationMessage);
        Log.d(TAG, "onLocationChanged: ");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
