package cracker.com.mantle.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrackerManager {

    public static final String TAG = CrackerManager.class.getSimpleName();
    public static final String GATT_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static final String GATT_CHARACTERISTIC_01 = "0000fff1-0000-1000-8000-00805f9b34fb"; // 가속도, 자이로
    public static final String GATT_CHARACTERISTIC_02 = "0000fff2-0000-1000-8000-00805f9b34fb"; // 심박, 배터리
    public static final String GATT_CHARACTERISTIC_03 = "0000fff3-0000-1000-8000-00805f9b34fb"; // cmd 명령어 확인 용도
    public static final String GATT_CHARACTERISTIC_04 = "0000fff4-0000-1000-8000-00805f9b34fb"; // write용도

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private int dataIntervalSecond = 1000;
    private String lastReceiveData = "";

    /*F1 : LEFT 10회 점등, Buzzer
    F2 : RIGHT 10회 점등, Buzzer
    F3 : HEAD Emergency ON
    F4 : HEAD Emergency OFF
    F5 : HEAD Emergency Buzzer ON
    F6 : HEAD Emergency Buzzer OFF*/


    private ConnectListener connectListener;
    private ArrayList<DataStreamListener> dataStreamListeners = new ArrayList<>();

    private static CrackerManager instance;
    private BluetoothGatt bluetoothGatt;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    public static CrackerManager getInstance() {
        if (instance == null)
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
        bluetoothGatt = dv.connectGatt(context, false, gattCallback);
        bluetoothGatt.connect();
    }

    public void disconnect() {
        if(bluetoothGatt != null) bluetoothGatt.disconnect();
    }

    public void readGyro(int millisecond) {
        this.dataIntervalSecond = millisecond;
        gyroHandler.sendEmptyMessage(0);
    }

    public String endGyro() {
        if(gyroHandler.hasMessages(0)) {
            gyroHandler.removeMessages(0);
        }
        return lastReceiveData;
    }

    private String msg;

    public void setLocationMessage(String msg) {
        this.msg = msg;
    }

    public String getLocationMessage() {
        return this.msg;
    }

    public void setLocationListener(Handler handler) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = msg;

        handler.sendMessage(msg);
    }

    public void write(String order) {
            /*F1 : LEFT 10회 점등, Buzzer
    F2 : RIGHT 10회 점등, Buzzer
    F3 : HEAD Emergency ON
    F4 : HEAD Emergency OFF
    F5 : HEAD Emergency Buzzer ON
    F6 : HEAD Emergency Buzzer OFF*/
        BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(2).get(3);
        byte[] byteArray = hexStringToByteArray(order);
        characteristic.setValue(byteArray);
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setConnectListener(ConnectListener listener) {
        this.connectListener = listener;
    }

    public void addDataStreamListeners(DataStreamListener dataStreamListener) {
        if(dataStreamListeners != null) {
            dataStreamListeners.add(dataStreamListener);
        }
    }

    public void removeDataStreamListener(DataStreamListener dataStreamListener) {
        if(dataStreamListeners != null && dataStreamListeners.contains(dataStreamListener)) {
            dataStreamListeners.remove(dataStreamListener);
        }
    }

    private void settingGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unknownService";
        String unknownCharaString = "unknownCharacteristic";

        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    boolean isFlag = false;

    private Handler gyroHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(isFlag) {
                bluetoothGatt.readCharacteristic(mGattCharacteristics.get(2).get(0));

            } else {
                bluetoothGatt.readCharacteristic(mGattCharacteristics.get(2).get(1));
            }
            isFlag = !isFlag;
            sendEmptyMessageDelayed(0, dataIntervalSecond);
        }
    };

    private Handler heartHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            sendEmptyMessageDelayed(0, dataIntervalSecond);
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange: " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (connectListener != null) {
                    connectListener.onConnectFailed(gatt.getDevice().getAddress());
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: ");
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                settingGattServices(gatt.getServices());
                if (connectListener != null) {
                    connectListener.onServiceDiscovered();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (GATT_CHARACTERISTIC_01.equals(characteristic.getUuid().toString())) {
                final byte[] data = characteristic.getValue();
                String stringData = "";
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
//                        stringBuilder.append(String.format("%02d ", byteChar & 0xFF)); unsinged int
                        stringBuilder.append(String.format("%02d ", byteChar));
                    }
                    stringData = stringBuilder.toString();
                }

                lastReceiveData = stringData;
                if(dataStreamListeners != null && !dataStreamListeners.isEmpty()) {
                    for(DataStreamListener dataStreamListener : dataStreamListeners) {
                        dataStreamListener.onDataReceive(stringData);
                    }
                }
            } else if (GATT_CHARACTERISTIC_02.equals(characteristic.getUuid().toString())) {
                final byte[] data = characteristic.getValue();
                String stringData = "";
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02d ", byteChar & 0xFF));
                    }
                    stringData = stringBuilder.toString();
                }

                lastReceiveData = stringData;
                if(dataStreamListeners != null && !dataStreamListeners.isEmpty()) {
                    for(DataStreamListener dataStreamListener : dataStreamListeners) {
                        dataStreamListener.onHeartReceive(stringData);
                    }
                }
            } else if (GATT_CHARACTERISTIC_03.equals(characteristic.getUuid())) {

            }
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
