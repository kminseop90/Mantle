package cracker.com.mantle;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.service.BLEService;
import cracker.com.mantle.service.CrackerManager;

public class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();
    RemoteService remoteService;

    private void showConnectComplete() {
        CheckDialog connectCompleteDialog = new CheckDialog();
        connectCompleteDialog.setMessage("디바이스와 연결이 완료되었습니다.\n헬멧 착용 후 다음 버튼을 눌러주세요");
        connectCompleteDialog.setOnNextClick(new CheckDialog.OnNextClick() {
            @Override
            public void onNextClick(CheckDialog dialog) {
                dialog.dismissAllowingStateLoss();
                if(BLEConnectActivity.isSettingFlag) {
                    startActivity(new Intent(BaseActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(BaseActivity.this, DeviceActivity.class));
                }
                finish();

                try {
                    remoteService.readGyro();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        connectCompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
    }

    RemoteServiceCallback remoteServiceCallback = new RemoteServiceCallback.Stub() {

        @Override
        public void valueChange(String value) throws RemoteException {
            Log.d(TAG, "valueChange: " + value);
        }

        @Override
        public void onServiceDiscovered() throws RemoteException {
            showConnectComplete();
        }

        @Override
        public void onConnectFailed(final String address) throws RemoteException {
            //Toast.makeText(getApplicationContext(), "장치와 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
//            CrackerManager.getInstance().connect(BaseActivity.this, address);
        }
    };
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if(iBinder != null) {
                remoteService = RemoteService.Stub.asInterface(iBinder);
                try {
                    remoteService.registerCallback(remoteServiceCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(remoteService != null) {
                try {
                    remoteService.unregisterCallback(remoteServiceCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void startServiceBind() {
        Intent intent = new Intent(this, BLEService.class);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void stopServiceBind() {
        unbindService(connection);
        stopService(new Intent(this, BLEService.class));
    }

    @Override
    protected void onResume() {
        startServiceBind();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopServiceBind();
        super.onPause();
    }
}
