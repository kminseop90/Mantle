package cracker.com.mantle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.service.BluetoothLeService;

public class BLEConnectActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = BLEConnectActivity.class.getSimpleName();
    public static final String DEVICE_NAME = "CRACKER1";

    private ImageView statusIcon;
    private TextView statusMessage;
    private ImageView nextView;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

    private cracker.com.mantle.model.BluetoothDevice crackerDevice;

    private boolean isFirst = true;
    boolean isScanFinish = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);


        initializeViews();
    }

    private void initializeViews() {
        statusIcon = findViewById(R.id.icon_connect_status);
        statusMessage = findViewById(R.id.text_connect_status);
        nextView = findViewById(R.id.button_connect_next);

        handler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        nextView.setOnClickListener(this);
    }

    private void scanDevice() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.stopLeScan(scanCallback);
                changeView(false);
            }
        }, 10000);
        bluetoothAdapter.startLeScan(scanCallback);
        changeView(true);

    }

    private void changeView(boolean isScan) {
        if(isScan) {
            statusIcon.setImageResource(R.drawable.icon_broadcasting);
            statusMessage.setText("디바이스와 연결중 입니다.");
            nextView.setVisibility(View.INVISIBLE);
        } else {
            statusIcon.setImageResource(R.drawable.icon_sun_2);
            statusMessage.setText("등 점멸중입니다.");
            nextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_connect_next:
                scanDevice();
                break;
        }
    }

    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "onLeScan: " + device.getName());
            if (device != null && !TextUtils.isEmpty(device.getName()) && device.getName().startsWith(DEVICE_NAME) && isFirst) {
                isFirst = false;
                isScanFinish = true;
                bluetoothAdapter.stopLeScan(scanCallback);

                crackerDevice = new cracker.com.mantle.model.BluetoothDevice(device.getName(), device.getAddress());
//                startBLEService(crackerDevice);

                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

                final BluetoothDevice dv = bluetoothAdapter.getRemoteDevice(crackerDevice.getAddress());
                BluetoothGatt bluetoothGatt = dv.connectGatt(BLEConnectActivity.this, false, gattCallback);
            }
        }
    };

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }
    };


}
