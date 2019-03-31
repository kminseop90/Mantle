package cracker.com.mantle.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import cracker.com.mantle.R;

public class DeviceSettingStep01 extends FrameLayout {

    private OnNextStepListener onNextClick;

    public void setOnNextClick(OnNextStepListener onNextClick) {
        this.onNextClick = onNextClick;
    }

    public DeviceSettingStep01(@NonNull Context context) {
        super(context);
        initialize();
    }

    public DeviceSettingStep01(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DeviceSettingStep01(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_device_setting_01, this, false);
        addView(view);

        findViewById(R.id.btn_device_setting_01).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onNextClick != null) {
                    onNextClick.onNextClick();
                }
            }
        });
    }
}
