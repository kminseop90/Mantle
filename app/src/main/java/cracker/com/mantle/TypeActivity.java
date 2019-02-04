package cracker.com.mantle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cracker.com.mantle.util.PreferenceUtil;

public class TypeActivity extends BaseActivity implements View.OnClickListener {

    private boolean isSettingFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        findViewById(R.id.icon_type_kids).setOnClickListener(this);
        findViewById(R.id.icon_type_kickboard).setOnClickListener(this);
        findViewById(R.id.icon_type_mtb).setOnClickListener(this);
        findViewById(R.id.icon_type_road).setOnClickListener(this);

        if (getIntent() != null) {
            isSettingFlag = getIntent().getBooleanExtra("flag", false);
        }
    }

    private void startBLEConnectActivity() {
        if(!isSettingFlag) {
            Intent intent = new Intent(this, BLEConnectActivity.class);
            startActivity(intent);
        }
        finish();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.icon_type_kids:
                setType("kids");
                break;
            case R.id.icon_type_kickboard:
                setType("kickboard");
                break;
            case R.id.icon_type_mtb:
                setType("mtb");
                break;
            case R.id.icon_type_road:
                setType("road");
                break;
        }
        startBLEConnectActivity();
    }

    private void setType(String type) {
        PreferenceUtil preferenceUtil = new PreferenceUtil(this);
        preferenceUtil.setPrefValue(PreferenceUtil.PREF_TYPE, type);
    }
}
