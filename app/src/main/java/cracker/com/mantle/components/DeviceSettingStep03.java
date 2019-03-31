package cracker.com.mantle.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import cracker.com.mantle.R;

public class DeviceSettingStep03 extends FrameLayout {
    private OnNextStepListener onNextClick;
    private EditBox leftEditBox;

    public void setOnNextClick(OnNextStepListener onNextClick) {
        this.onNextClick = onNextClick;
    }

    public DeviceSettingStep03(@NonNull Context context) {
        super(context);
        initialize();
    }

    public DeviceSettingStep03(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DeviceSettingStep03(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_device_setting_03, this, false);
        addView(view);

        leftEditBox = view.findViewById(R.id.view_edit_box_left);
        view.findViewById(R.id.btn_device_setting_03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onNextClick != null) {
                    onNextClick.onNextClick();
                }
            }
        });
    }

    public void setData(String msg) {
        if(!TextUtils.isEmpty(msg)) {
            leftEditBox.setText(msg);
        }
    }

    public String getData() {
        String data = leftEditBox.getText();
        return data;
    }
}
