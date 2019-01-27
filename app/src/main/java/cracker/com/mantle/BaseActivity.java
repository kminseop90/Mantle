package cracker.com.mantle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.model.BluetoothDevice;
import cracker.com.mantle.service.BluetoothLeService;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                isConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                isConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                ConnectOKDialog dialog = new ConnectOKDialog(MainActivity.this);
//                dialog.show();
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                showConnectComplete();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                if (isDataConnection) {
//                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                }
            }
        }
    };

    private void showConnectComplete() {
        CheckDialog connectCompleteDialog = new CheckDialog();
        connectCompleteDialog.setMessage("디바이스와 연결이 완료되었습니다.\n헬멧 착용 후 다음 버튼을 눌러주세요");
        connectCompleteDialog.setOnNextClick(new CheckDialog.OnNextClick() {
            @Override
            public void onNextClick(CheckDialog dialog) {
                dialog.dismissAllowingStateLoss();
                startActivity(new Intent(BaseActivity.this, DeviceActivity.class));
                finish();
            }
        });
        connectCompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mGattUpdateReceiver);
        super.onPause();
    }

    BluetoothDevice device;

    protected void startBLEService(BluetoothDevice device) {
        this.device = device;
        Intent gattServiceIntent = new Intent(BaseActivity.this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }



    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(device.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
}
