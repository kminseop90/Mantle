package cracker.com.mantle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.service.ConnectListener;
import cracker.com.mantle.service.CrackerManager;

public class BLEConnectActivity extends BaseActivity implements View.OnClickListener, ConnectListener{

    public static final String TAG = BLEConnectActivity.class.getSimpleName();
    public static final String DEVICE_NAME = "CRACKER1";

    private ImageView statusIcon;
    private TextView statusMessage;
    private ImageView nextView;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

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
        if (isScan) {
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
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && !TextUtils.isEmpty(device.getName())) {
                Log.d(TAG, "onLeScan: " + device.getName());
                if (device.getName().equals(DEVICE_NAME)) {
                    isScanFinish = true;
                    bluetoothAdapter.stopLeScan(scanCallback);
                    CrackerManager.getInstance().setConnectListener(BLEConnectActivity.this);
                    CrackerManager.getInstance().connect(BLEConnectActivity.this, device.getAddress());
                }
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
                startActivity(new Intent(BLEConnectActivity.this, DeviceActivity.class));
                finish();
            }
        });
        connectCompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
    }

    @Override
    protected void onStop() {
        CrackerManager.getInstance().setConnectListener(null);
        super.onStop();
    }

    @Override
    public void onServiceDiscovered() {
        showConnectComplete();
    }

    @Override
    public void onConnectFailed(final String address) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(BLEConnectActivity.this);
                builder.setMessage("연결이 실패하였습니다, 다시 시도할까요?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CrackerManager.getInstance().connect(BLEConnectActivity.this, address);
                    }
                });
                builder.show();
            }
        });
    }
}
