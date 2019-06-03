package cracker.com.mantle.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
            CrackerManager.getInstance().readGyro(300);
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

  /*      if(isEmergency(msg)) {
            Intent intent = new Intent(this, NotiActivity.class);
            intent.putExtra(NotiActivity.TYPE_EMERGENCY, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

    }*/
    }

    @Override
    public void onHeartReceive(String msg) {
        int N = callbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                callbacks.getBroadcastItem(i).valueChange(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbacks.finishBroadcast();
    }

    private boolean isEmergency(String msg) {
        try {
            String[] splitMsg = msg.split(" ");
            int accelerationX = Math.abs(Integer.valueOf(splitMsg[3]));
            int accelerationY = Math.abs(Integer.valueOf(splitMsg[4]));
            int accelerationZ = Math.abs(Integer.valueOf(splitMsg[5]));

            int sum = accelerationX + accelerationY + accelerationZ;
//            Log.d(TAG, "isEmergency: " + sum);
            return sum >= 200;
        } catch (Exception e) {
            return false;
        }
    }

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
            msg = date + CrackerManager.getInstance().getLocationMessage() + type + msg;
            msg += "\n";
            Log.d(TAG, "saveDataAtFile: " + msg);
            fos.write(msg.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
