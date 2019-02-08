package cracker.com.mantle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cracker.com.mantle.components.EditBox;
import cracker.com.mantle.dialog.CheckDialog;
import cracker.com.mantle.service.CrackerManager;
import cracker.com.mantle.service.DataStreamListener;

public class DeviceActivity extends BaseActivity implements View.OnClickListener, DataStreamListener {

    public static final int STEP_01 = 1;
    public static final int STEP_02 = 2;
    public static final int STEP_03 = 3;
    public static final int STEP_04 = 4;

    private int currentStep = STEP_01;

    private RelativeLayout step01Layout;
    private RelativeLayout step02Layout;
    private RelativeLayout step03Layout;
    private RelativeLayout step04Layout;

    private EditBox leftEditBox;
    private EditBox rightEditBox;

    private ImageView saveView;
    private boolean isSettingFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        if(getIntent() != null) {
            isSettingFlag = getIntent().getBooleanExtra("flag", false);
        }

        step01Layout = findViewById(R.id.layout_device_step_01);
        step02Layout = findViewById(R.id.layout_device_step_02);
        step03Layout = findViewById(R.id.layout_device_step_03);
        step04Layout = findViewById(R.id.layout_device_step_04);

        leftEditBox = findViewById(R.id.view_edit_box_left);
        rightEditBox = findViewById(R.id.view_edit_box_right);

        saveView = findViewById(R.id.btn_device_next);
        saveView.setOnClickListener(this);
        CrackerManager.getInstance().addDataStreamListeners(this);
    }


    private void nextClick() {
        switch (currentStep) {
            case STEP_01:
                // 자전거 준비
                saveView.setImageResource(R.drawable.button_next);
                step01Layout.setVisibility(View.GONE);
                step02Layout.setVisibility(View.VISIBLE);
                step03Layout.setVisibility(View.GONE);
                step04Layout.setVisibility(View.GONE);
                break;
            case STEP_02:
                // 왼쪽 측정
                saveView.setImageResource(R.drawable.button_save);
                step01Layout.setVisibility(View.GONE);
                step02Layout.setVisibility(View.GONE);
                step03Layout.setVisibility(View.VISIBLE);
                step04Layout.setVisibility(View.GONE);
                break;
            case STEP_03:
                // 오른쪽 측정
                CheckDialog setting01CompleteDialog = new CheckDialog();
                setting01CompleteDialog.setMessage("설정이 저장되었습니다.");
                setting01CompleteDialog.setOnNextClick(new CheckDialog.OnNextClick() {
                    @Override
                    public void onNextClick(CheckDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        step01Layout.setVisibility(View.GONE);
                        step02Layout.setVisibility(View.GONE);
                        step03Layout.setVisibility(View.GONE);
                        step04Layout.setVisibility(View.VISIBLE);
                    }
                });
                setting01CompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
                break;
            case STEP_04:
                CheckDialog setting02CompleteDialog = new CheckDialog();
                setting02CompleteDialog.setMessage("설정이 저장되었습니다.");
                setting02CompleteDialog.setOnNextClick(new CheckDialog.OnNextClick() {
                    @Override
                    public void onNextClick(CheckDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        if(!isSettingFlag) {
                            startActivity(new Intent(DeviceActivity.this, MainActivity.class));
                        }
                        finish();
                    }
                });
                setting02CompleteDialog.show(getSupportFragmentManager(), CheckDialog.TAG);
                break;
        }
        currentStep++;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_device_next:
                nextClick();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CrackerManager.getInstance().removeDataStreamListener(this);
    }

    @Override
    public void onDataReceive(String msg) {
        //00 00 00 00 00 00
        if(TextUtils.isEmpty(msg)) return;

        String[] splitMsg = msg.split(" ");
        if(splitMsg.length >= 3) {
            String displayMsg = String.format("%s\n%s\n%s", splitMsg[0], splitMsg[1], splitMsg[2]);
            leftEditBox.setText(displayMsg);
            rightEditBox.setText(displayMsg);
        }
    }
}
