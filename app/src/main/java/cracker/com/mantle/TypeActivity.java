package cracker.com.mantle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class TypeActivity extends BaseActivity implements View.OnClickListener {

    private boolean isFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        findViewById(R.id.icon_type_kids).setOnClickListener(this);
        findViewById(R.id.icon_type_kickboard).setOnClickListener(this);
        findViewById(R.id.icon_type_mtb).setOnClickListener(this);
        findViewById(R.id.icon_type_road).setOnClickListener(this);

        if(getIntent() != null) {
            isFlag = getIntent().getBooleanExtra("flag", false);
        }
    }

    private void startBLEConnectActivity() {
        Intent intent = new Intent(this, BLEConnectActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.icon_type_kids:
                break;
            case R.id.icon_type_kickboard:
                break;
            case R.id.icon_type_mtb:
                break;
            case R.id.icon_type_road:
                break;
        }
        if(!isFlag) {
            startBLEConnectActivity();
        } else {
            finish();
        }
    }
}