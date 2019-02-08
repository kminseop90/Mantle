package cracker.com.mantle.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cracker.com.mantle.R;

public class CheckDialog extends AppCompatDialogFragment {

    public static final String TAG = CheckDialog.class.getSimpleName();
    private OnNextClick onNextClick;

    private String message;
    private TextView messageView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_check, container, false);
        setStyle(AppCompatDialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);

        messageView = view.findViewById(R.id.text_check_message);
        messageView.setText(message);

        view.findViewById(R.id.button_check_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onNextClick != null) {
                    onNextClick.onNextClick(CheckDialog.this);
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public interface OnNextClick {
        void onNextClick(CheckDialog dialog);
    }

    public void setOnNextClick(OnNextClick onNextClick) {
        this.onNextClick = onNextClick;
    }
}
