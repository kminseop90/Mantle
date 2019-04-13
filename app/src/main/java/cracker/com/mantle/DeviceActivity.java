package cracker.com.mantle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ViewFlipper;

import cracker.com.mantle.components.DeviceSettingStep01;
import cracker.com.mantle.components.DeviceSettingStep02;
import cracker.com.mantle.components.DeviceSettingStep03;
import cracker.com.mantle.components.DeviceSettingStep04;
import cracker.com.mantle.components.DeviceSettingStep05;
import cracker.com.mantle.components.OnNextStepListener;
import cracker.com.mantle.components.OnStartStepListener;
import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.service.CrackerManager;
import cracker.com.mantle.service.DataStreamListener;
import cracker.com.mantle.util.PreferenceUtil;

public class DeviceActivity extends BaseActivity implements DataStreamListener {

    private ViewFlipper viewFlipper;
    private DeviceSettingStep01 deviceSettingStep01;
    private DeviceSettingStep02 deviceSettingStep02;
    private DeviceSettingStep03 deviceSettingStep03;
    private DeviceSettingStep04 deviceSettingStep04;
    private DeviceSettingStep05 deviceSettingStep05;

    private boolean isSettingFlag = false;
    private PreferenceUtil preferenceUtil;
    private String value;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        if(getIntent() != null) {
            isSettingFlag = getIntent().getBooleanExtra("flag", false);
        }

        preferenceUtil = new PreferenceUtil(this);

        viewFlipper = findViewById(R.id.view_device_flipper);
        deviceSettingStep01 = findViewById(R.id.view_device_setting_01);
        deviceSettingStep02 = findViewById(R.id.view_device_setting_02);
        deviceSettingStep03 = findViewById(R.id.view_device_setting_03);
        deviceSettingStep04 = findViewById(R.id.view_device_setting_04);
        deviceSettingStep05 = findViewById(R.id.view_device_setting_05);

        deviceSettingStep01.setOnNextClick(new OnNextStepListener() {
            @Override
            public void onNextClick() {
                viewFlipper.showNext();
            }
        });

        deviceSettingStep02.setOnNextClick(new OnNextStepListener() {
            @Override
            public void onNextClick() {
                preferenceUtil.setPrefValue(PreferenceUtil.PREF_DEFAULT_SENSOR_VALUE, value);
                viewFlipper.showNext();
            }
        });

        deviceSettingStep03.setOnNextClick(new OnNextStepListener() {
            @Override
            public void onNextClick() {
                showSettingCompleteDialog(new CheckDialog.OnNextClick() {
                    @Override
                    public void onNextClick(CheckDialog dialog) {
                        preferenceUtil.setPrefValue(PreferenceUtil.PREF_LEFT_SENSOR_VALUE, value);
                        dialog.dismissAllowingStateLoss();
                        viewFlipper.showNext();
                    }
                });

            }
        });

        deviceSettingStep04.setOnNextClick(new OnNextStepListener() {
            @Override
            public void onNextClick() {
                showSettingCompleteDialog(new CheckDialog.OnNextClick() {
                    @Override
                    public void onNextClick(CheckDialog dialog) {
                        preferenceUtil.setPrefValue(PreferenceUtil.PREF_RIGHT_SENSOR_VALUE, value);
                        dialog.dismissAllowingStateLoss();
                        viewFlipper.showNext();
                    }
                });
            }
        });

        deviceSettingStep05.setOnNextClick(new OnNextStepListener() {
            @Override
            public void onNextClick() {
                viewFlipper.showNext();
            }
        });

        deviceSettingStep05.setOnStartStepListener(new OnStartStepListener() {
            @Override
            public void onStartClick() {
                if(!isSettingFlag) {
                    startActivity(new Intent(DeviceActivity.this, MainActivity.class));
                }
                finish();
            }
        });

    }

    private void showSettingCompleteDialog(CheckDialog.OnNextClick onNextClick) {
        CheckDialog setting02CompleteDialog = new CheckDialog();
        setting02CompleteDialog.setMessage("설정이 저장되었습니다.");
        setting02CompleteDialog.setOnNextClick(onNextClick);
        setting02CompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
    }


    @Override
    protected void onStop() {
        super.onStop();
        CrackerManager.getInstance().removeDataStreamListener(this);
    }

    @Override
    protected void onResume() {
        CrackerManager.getInstance().addDataStreamListeners(this);
        super.onResume();
    }

    @Override
    public void onDataReceive(String msg) {
        //00 00 00 00 00 00
        if(TextUtils.isEmpty(msg)) return;
        String[] splitMsg = msg.split(" ");
        if(splitMsg.length >= 6) {
            String displayMsg = String.format("%s\n%s\n%s", splitMsg[0], splitMsg[1], splitMsg[2]);
            this.value = String.format("%s,%s,%s", splitMsg[0], splitMsg[1], splitMsg[2]);
            deviceSettingStep03.setData(displayMsg);
            deviceSettingStep04.setData(displayMsg);
        }
    }

    @Override
    public void onHeartReceive(String msg) {

    }
}
