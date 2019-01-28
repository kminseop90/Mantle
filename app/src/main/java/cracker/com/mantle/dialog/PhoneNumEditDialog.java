package cracker.com.mantle.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cracker.com.mantle.R;

public class PhoneNumEditDialog extends AppCompatDialogFragment implements View.OnClickListener{

    private OnPhoneEditOkListener onPhoneEditOkListener;
    private EditText phoneNumberEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_phone_edit, container, false);

        view.findViewById(R.id.btn_phone_edit_ok).setOnClickListener(this);
        phoneNumberEditText = view.findViewById(R.id.edit_phone_number_input);
        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_phone_edit_ok:
                if(onPhoneEditOkListener != null) {
                    onPhoneEditOkListener.onPhoneEditOkClick(phoneNumberEditText.getText().toString());
                    dismissAllowingStateLoss();
                }
                break;
        }
    }


    public void setOnPhoneEditOkListener(OnPhoneEditOkListener onPhoneEditOkListener) {
        this.onPhoneEditOkListener = onPhoneEditOkListener;
    }

    public interface OnPhoneEditOkListener {
        void onPhoneEditOkClick(String editPhoneNumber);
    }
}
