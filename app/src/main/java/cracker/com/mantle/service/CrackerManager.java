package cracker.com.mantle.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

public class CrackerManager {

    public static final String TAG = CrackerManager.class.getSimpleName();
    ConnectListener connectListener;
    private static CrackerManager instance;

    public static CrackerManager getInstance() {
        if(instance == null)
            instance = new CrackerManager();

        return instance;
    }

    private CrackerManager() {
        initialize();
    }

    private void initialize() {

    }

    public void connect(Context context, String address) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        final BluetoothDevice dv = bluetoothAdapter.getRemoteDevice(address);
        dv.connectGatt(context, false, gattCallback);
    }

    public void setConnectListener(ConnectListener listener) {
        this.connectListener = listener;
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange: " + newState);
            gatt.discoverServices();
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: ");
            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if(connectListener != null) {
                    connectListener.onServiceDiscovered();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead: ");
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite: ");
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged: ");
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };
}
