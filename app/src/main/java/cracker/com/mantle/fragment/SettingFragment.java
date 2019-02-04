package cracker.com.mantle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cracker.com.mantle.DeviceActivity;
import cracker.com.mantle.NotiActivity;
import cracker.com.mantle.R;
import cracker.com.mantle.TypeActivity;
import cracker.com.mantle.util.PreferenceUtil;
import cracker.com.mantle.dialog.PhoneNumEditDialog;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView phoneNumber01View;
    private TextView phoneNumber02View;
    private TextView phoneNumber03View;

    private PreferenceUtil phonePreferenceUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        initializeViews(v);
        return v;
    }

    private void initializeViews(View parentView) {
        parentView.findViewById(R.id.layout_setting_noti).setOnClickListener(this);
        parentView.findViewById(R.id.btn_setting_phone_edit_01).setOnClickListener(this);
        parentView.findViewById(R.id.btn_setting_phone_edit_02).setOnClickListener(this);
        parentView.findViewById(R.id.btn_setting_phone_edit_03).setOnClickListener(this);
        parentView.findViewById(R.id.layout_setting_change_mode).setOnClickListener(this);
        parentView.findViewById(R.id.layout_setting_change_device).setOnClickListener(this);

        phoneNumber01View = parentView.findViewById(R.id.txt_setting_phone_number_01);
        phoneNumber02View = parentView.findViewById(R.id.txt_setting_phone_number_02);
        phoneNumber03View = parentView.findViewById(R.id.txt_setting_phone_number_03);

        phonePreferenceUtil = new PreferenceUtil(getContext());
        phoneNumber01View.setText(phonePreferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_01, NotiActivity.DEFAULT_PHONE_NUMBER));
        phoneNumber02View.setText(phonePreferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_02, NotiActivity.DEFAULT_PHONE_NUMBER));
        phoneNumber03View.setText(phonePreferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_03, NotiActivity.DEFAULT_PHONE_NUMBER));
    }

    private void showPhoneNumberEditDialog(final int id) {
        final PhoneNumEditDialog phoneNumEditDialog = new PhoneNumEditDialog();
        phoneNumEditDialog.setOnPhoneEditOkListener(new PhoneNumEditDialog.OnPhoneEditOkListener() {
            @Override
            public void onPhoneEditOkClick(String editPhoneNumber) {
                switch (id) {
                    case R.id.btn_setting_phone_edit_01:
                        phonePreferenceUtil.setPrefValue(PreferenceUtil.PREF_PHONE_NUMBER_01, editPhoneNumber);
                        phoneNumber01View.setText(editPhoneNumber);
                        break;
                    case R.id.btn_setting_phone_edit_02:
                        phonePreferenceUtil.setPrefValue(PreferenceUtil.PREF_PHONE_NUMBER_02, editPhoneNumber);
                        phoneNumber02View.setText(editPhoneNumber);
                        break;
                    case R.id.btn_setting_phone_edit_03:
                        phonePreferenceUtil.setPrefValue(PreferenceUtil.PREF_PHONE_NUMBER_03, editPhoneNumber);
                        phoneNumber03View.setText(editPhoneNumber);
                        break;
                }
            }
        });
        phoneNumEditDialog.show(getFragmentManager(), "phoneNumberEdit");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_setting_noti:
                startActivity(new Intent(getActivity(), NotiActivity.class));
                break;
            case R.id.btn_setting_phone_edit_01:
            case R.id.btn_setting_phone_edit_02:
            case R.id.btn_setting_phone_edit_03:
                showPhoneNumberEditDialog(id);
                break;
            case R.id.layout_setting_change_mode:
                Intent i = new Intent(getContext(), TypeActivity.class);
                i.putExtra("flag", true);
                startActivity(i);
                break;
            case R.id.layout_setting_change_device:
                i = new Intent(getContext(), DeviceActivity.class);
                i.putExtra("flag", true);
                startActivity(i);
        }
    }
}
