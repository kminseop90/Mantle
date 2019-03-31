package cracker.com.mantle.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cracker.com.mantle.R;

public class EditBox extends FrameLayout {

    private TextView sensorValue;

    public EditBox(@NonNull Context context) {
        super(context);
        initialize();
    }

    public EditBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_editbox, this, false);
        addView(view);

        sensorValue = view.findViewById(R.id.txt_editbox_value);
    }

    public void setText(String text) {
        if(sensorValue != null) {
            sensorValue.setText(text);
            invalidate();
        }
    }

    public String getText() {
        String value = "";
        if(sensorValue != null) {
            value = sensorValue.getText().toString();
        }
        return value;
    }
}
