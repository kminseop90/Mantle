package cracker.com.mantle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BLEConnectActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = BLEConnectActivity.class.getSimpleName();
    public static final String DEVICE_NAME = "CRACKER1";

    private ImageView statusIcon;
    private TextView statusMessage;
    private ImageView nextView;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

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
        if (isScan) {
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

    @Override
    protected void onStop() {
        super.onStop();
        remoteServiceCallback = null;
    }

    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && !TextUtils.isEmpty(device.getName())) {
                Log.d(TAG, "onLeScan: " + device.getName());
                if (device.getName().equals(DEVICE_NAME)) {
                    isScanFinish = true;
                    bluetoothAdapter.stopLeScan(scanCallback);
                    connect(device.getAddress());
                }
            }
        }
    };


    protected void connect(String address) {
        try {
            remoteService.connect(address);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
