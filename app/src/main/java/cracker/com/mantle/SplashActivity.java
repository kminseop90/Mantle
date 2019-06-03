package cracker.com.mantle;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class SplashActivity extends BaseActivity {
    private Handler splashHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        requestPermission();
    }

    private void requestPermission() {
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        splashHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                startServiceBind();
                                startActivity(new Intent(SplashActivity.this, TypeActivity.class));
                                finish();
                            }
                        }, 2000);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        finish();
                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
