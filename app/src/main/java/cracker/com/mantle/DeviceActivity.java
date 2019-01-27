package cracker.com.mantle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cracker.com.mantle.dialog.CheckDialog;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    public static final int STEP_01 = 1;
    public static final int STEP_02 = 2;
    public static final int STEP_03 = 3;
    public static final int STEP_04 = 4;

    private int currentStep = STEP_01;

    private RelativeLayout step01Layout;
    private RelativeLayout step02Layout;
    private RelativeLayout step03Layout;
    private RelativeLayout step04Layout;

    private ImageView saveView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        step01Layout = findViewById(R.id.layout_device_step_01);
        step02Layout = findViewById(R.id.layout_device_step_02);
        step03Layout = findViewById(R.id.layout_device_step_03);
        step04Layout = findViewById(R.id.layout_device_step_04);

        saveView = findViewById(R.id.btn_device_next);
        saveView.setOnClickListener(this);
    }

    private void nextClick() {
        switch (currentStep) {
            case STEP_01:
                saveView.setImageResource(R.drawable.button_next);
                step01Layout.setVisibility(View.GONE);
                step02Layout.setVisibility(View.VISIBLE);
                step03Layout.setVisibility(View.GONE);
                step04Layout.setVisibility(View.GONE);
                break;
            case STEP_02:
                saveView.setImageResource(R.drawable.button_save);
                step01Layout.setVisibility(View.GONE);
                step02Layout.setVisibility(View.GONE);
                step03Layout.setVisibility(View.VISIBLE);
                step04Layout.setVisibility(View.GONE);
                break;
            case STEP_03:
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
                        startActivity(new Intent(DeviceActivity.this, MainActivity.class));
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
}
