package cracker.com.mantle.service;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private StringBuffer gattDataBuffer = new StringBuffer();

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    private FileOutputStream fos;
//    private VODataFilter filterData;

    public void startFileSave() {
        String dirPath = "sdcard/BLEHelmet";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String date = dataFormat.format(calendar.getTime());
        File saveFile = new File(dirPath + String.format("/GattData_%s.txt", date));
        try {
            fos = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public void setFilterData(VODataFilter filterData) {
//        this.filterData = filterData;
//        this.filterData.setFilterCallBack(filterCallBack);
//    }
//
//    VODataFilter.FilterCallBack filterCallBack = new VODataFilter.FilterCallBack() {
//        @Override
//        public void sendDataBLE(String data) {
//            List<BluetoothGattService> services = mBluetoothGatt.getServices();
//            List<BluetoothGattCharacteristic> characteristics = services.get(services.size() - 1).getCharacteristics();
////            String unixTimeString = Integer.toHexString(flag);
////            byte[] byteArray = new BigInteger(unixTimeString, 16).toByteArray();
//            byte[] byteArray = new byte[4];
//            for(int  i = 0 ; i < data.split(" ").length; i++) {
//                byteArray[i] = (byte)((int)Integer.valueOf(data.split(" ")[i]));
//            }
//            characteristics.get(0).setValue(byteArray);
//            boolean status = writeCharacteristic(characteristics.get(0));
//            Log.d(TAG, "sendDataBLE: " + status);
//        }

//        @Override
//        public void sendMessage(int x, int y, int z) {
//            String KEY_SENDMESSAGE = "is_send_msg";
//            String KEY_PHONE_PREFERENCE = "user_phone";
//            String KEY_PHONE_PREFERENCE_SUB_1 = "user_phone_sub_1";
//            String KEY_PHONE_PREFERENCE_SUB_2 = "user_phone_sub_2";
//            String KEY_ID_PREFERENCE = "user_id";
//
//            SharedPreferences prefs = getApplicationContext().getSharedPreferences("PrefName", getApplicationContext().MODE_PRIVATE);
//            boolean isSendMsg = prefs.getBoolean(KEY_SENDMESSAGE, true);
//            String userPhone = prefs.getString(KEY_PHONE_PREFERENCE, "");
//            String userSubPhone1 = prefs.getString(KEY_PHONE_PREFERENCE_SUB_1, "");
//            String userSubPhone2 = prefs.getString(KEY_PHONE_PREFERENCE_SUB_2, "");
//            String userName = prefs.getString(KEY_ID_PREFERENCE, "NoName");
//
//            String sendMessage = userName + "로부터 긴급상황  도움요청이 왔습니다! \n" +
//                    " CREATOS에 의해 발송";
//
////            String str_Result = "Accel X : " + x + " Accel Y : " + y + " Accel Z : " + z;
////            str_Result += "\n" + getLocation();
//
//            if (isSendMsg && !TextUtils.isEmpty(userPhone)) {
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(userPhone, null, sendMessage, null, null);
//                    if (!TextUtils.isEmpty(userSubPhone1)) {
//                        smsManager.sendTextMessage(userSubPhone1, null, sendMessage, null, null);
//                    }
//                    if (!TextUtils.isEmpty(userSubPhone2)) {
//                        smsManager.sendTextMessage(userSubPhone2, null, sendMessage, null, null);
//                    }
//                    Log.d(TAG, "sendMessage: " + isSendMsg);
//                } catch (Exception e) {
//                    Log.d(TAG, "sendMessage: " + isSendMsg);
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//    };


//    public void finishFileSave() {
//        try {
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onServiceDiscovered: ");
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d(TAG, "onCharacteristicRead: ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged: ");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();

            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02d ", byteChar));
                }
                // 컨트롤러에서 받은 값을 기준으로 모듈에 데이터를 날려주거나 폰에 메세지를 송신하는 메소드를 호출한다
//                filterData.filter(stringBuilder.toString());
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                gattDataSave(stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public String getLocation(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return"";
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String strLoc = String.valueOf(latitude) + " " + String.valueOf(longitude) + " ";
        return strLoc;
    }

    public void gattDataSave(String data) {
        if (data != null) {
//            SimpleDateFormat format = new SimpleDateFormat();
//            gattDataBuffer.append(format.format(System.currentTimeMillis()) + "_");
//            String unixTimeString = Integer.toHexString(unixTime);
//            byte[] byteArray = new BigInteger(unixTimeString, 16).toByteArray();
            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dataFormat = new SimpleDateFormat("hh:mm:ss");
                String date = dataFormat.format(calendar.getTime()) + " - ";

                data = date + getLocation()+ data;
                data += "\n";
                fos.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            gattDataBuffer.append(code[1]);
//            gattDataBuffer.append("\n");
        }
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onServiceDiscovered(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onServiceDiscovered(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
