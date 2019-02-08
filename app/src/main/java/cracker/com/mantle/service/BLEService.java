package cracker.com.mantle.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cracker.com.mantle.NotiActivity;
import cracker.com.mantle.RemoteService;
import cracker.com.mantle.RemoteServiceCallback;
import cracker.com.mantle.util.PreferenceUtil;

public class BLEService extends Service implements ConnectListener, DataStreamListener {

    public static final String TAG = BLEService.class.getSimpleName();

    final RemoteCallbackList<RemoteServiceCallback> callbacks = new RemoteCallbackList();
    private FileOutputStream fos;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final RemoteService.Stub mBinder = new RemoteService.Stub() {
        @Override
        public boolean registerCallback(RemoteServiceCallback cb) throws RemoteException {
            Log.d(TAG, "registerCallback: ");
            boolean flag = false;
            if (cb != null) {
                flag = callbacks.register(cb);
            }
            return flag;
        }

        @Override
        public boolean unregisterCallback(RemoteServiceCallback cb) throws RemoteException {
            Log.d(TAG, "unregisterCallback: ");
            boolean flag = false;
            if (cb != null) {
                flag = unregisterCallback(cb);
            }
            return flag;
        }

        @Override
        public void connect(String address) throws RemoteException {
            CrackerManager.getInstance().setConnectListener(BLEService.this);
            CrackerManager.getInstance().connect(BLEService.this, address);
        }

        @Override
        public void readGyro() throws RemoteException {
            initFileSave();
            CrackerManager.getInstance().addDataStreamListeners(BLEService.this);
            CrackerManager.getInstance().readGyro(500);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
//        handler.sendEmptyMessage(0);
    }

    @Override
    public void onServiceDiscovered() {
        Log.d(TAG, "onServiceDiscovered: ");
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                callbacks.getBroadcastItem(i).onServiceDiscovered();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbacks.finishBroadcast();
    }

    @Override
    public void onConnectFailed(String address) {
        Log.d(TAG, "onConnectFailed: ");
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                callbacks.getBroadcastItem(i).onConnectFailed(address);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbacks.finishBroadcast();
    }

    @Override
    public void onDataReceive(final String msg) {
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                callbacks.getBroadcastItem(i).valueChange(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        saveDataAtFile(msg);
        callbacks.finishBroadcast();

        time--;
        Log.d(TAG, "onDataReceive: " + time);
        Intent intent = new Intent(this, NotiActivity.class);
        intent.putExtra(NotiActivity.TYPE_EMERGENCY, true);
        if(time == 0) {
            startActivity(intent);
        }

    }

    private long time = 100;

    private void initFileSave() {
        String dirPath = "sdcard/Mantle";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String date = dataFormat.format(calendar.getTime());
        File saveFile = new File(dirPath + String.format("/%s_GattData.txt", date));
        try {
            fos = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveDataAtFile(String msg) {
        try {
            PreferenceUtil preferenceUtil = new PreferenceUtil(this);
            String type = preferenceUtil.getPrefStringValue(PreferenceUtil.PREF_TYPE, "bike");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dataFormat = new SimpleDateFormat("hh:mm:ss");
            String date = dataFormat.format(calendar.getTime()) + " - ";
            type += "_";
            msg = date + getLocation() + type + msg;
            msg += "\n";
            fos.write(msg.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String strLoc = String.valueOf(latitude) + " " + String.valueOf(longitude) + " ";
        return strLoc;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(fos != null) {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }
}