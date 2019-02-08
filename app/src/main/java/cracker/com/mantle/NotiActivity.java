package cracker.com.mantle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cracker.com.mantle.service.CrackerManager;
import cracker.com.mantle.util.PreferenceUtil;

public class NotiActivity extends BaseActivity {

    private PreferenceUtil preferenceUtil;
    public static final String TYPE_EMERGENCY = "TYPE_EMERGENCY";
    public static final String DEFAULT_PHONE_NUMBER = "000-0000-0000";

    private TextView countView;
    private ImageView saveView;
    private boolean isEmergency = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        if(getIntent() != null) {
            isEmergency = getIntent().getBooleanExtra(TYPE_EMERGENCY, false);
        }

        initializeViews();
        initializeData();
        if(isEmergency) {
            initializeEmergency();
            startTimer();
            CrackerManager.getInstance().write("F3");
        }
    }

    private void initializeViews() {
        countView = findViewById(R.id.text_noti_count);
        saveView = findViewById(R.id.button_noti_save);

        findViewById(R.id.icon_noti_count_plus).setOnClickListener(countClickListener);
        findViewById(R.id.icon_noti_count_minus).setOnClickListener(countClickListener);
        saveView.setOnClickListener(countClickListener);

        preferenceUtil = new PreferenceUtil(this);
    }

    private void initializeData() {
        if(preferenceUtil != null) {
            int count = preferenceUtil.getPrefIntValue(PreferenceUtil.PREF_EMERGENCY_COUNT);
            countView.setText(String.format("%03d", count));
        }
    }

    private void initializeEmergency() {
        findViewById(R.id.icon_noti_count_plus).setVisibility(View.GONE);
        findViewById(R.id.icon_noti_count_minus).setVisibility(View.GONE);
        saveView.setImageResource(R.drawable.button_release);

        ((TextView)findViewById(R.id.text_noti_message_01)).setText("숫자 카운팅이 끝나면 초기\n등록한 서브 번호로 사고 발생\n알림 메시지가 전송됩니다.");
        ((TextView)findViewById(R.id.text_noti_message_02)).setVisibility(View.GONE);
    }

    private void startTimer() {
        timer.sendEmptyMessage(0);
    }

    private Handler timer = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int count = Integer.valueOf(countView.getText().toString());
            if(count <= 0) {
                sendSMS();
                Toast.makeText(NotiActivity.this, "Boom!", Toast.LENGTH_SHORT).show();
                CrackerManager.getInstance().write("F4");
                return;
            }
            sendEmptyMessageDelayed(0, 1000);
            countView.setText(String.format("%03d", --count));
        }
    };


    private View.OnClickListener countClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            int count = getCount();
            switch (id) {
                case R.id.icon_noti_count_minus:
                    setCount(--count);
                    break;
                case R.id.icon_noti_count_plus:
                    setCount(++count);
                    break;
                case R.id.button_noti_save:
                    if(isEmergency) {
                        timer.removeCallbacks(null);
                    } else {
                        saveCount(count);
                    }
                    finish();
                    break;
            }
        }
    };

    private void sendSMS() {
        if(preferenceUtil == null) {
            preferenceUtil = new PreferenceUtil(this);
        }
        String phone01 = preferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_01, NotiActivity.DEFAULT_PHONE_NUMBER);
        String phone02 = preferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_02, NotiActivity.DEFAULT_PHONE_NUMBER);
        String phone03 = preferenceUtil.getPrefStringValue(PreferenceUtil.PREF_PHONE_NUMBER_03, NotiActivity.DEFAULT_PHONE_NUMBER);

        String googleMapBaseUrl = "https://map.google.com/maps/@%s,17z";
        String googleMapUrl = String.format(googleMapBaseUrl, getLocation());

        String defaultSendMessage = "긴급상황 도움요청이 왔습니다! MANTLE에 의해 발송 ";

        SmsManager smsManager = SmsManager.getDefault();

        if(!DEFAULT_PHONE_NUMBER.equals(phone01)) {
            smsManager.sendTextMessage(phone01, null, googleMapUrl, null, null);
        }
        if(!DEFAULT_PHONE_NUMBER.equals(phone02)) {
            smsManager.sendTextMessage(phone02, null, googleMapUrl, null, null);
        }
        if(!DEFAULT_PHONE_NUMBER.equals(phone03)) {
            smsManager.sendTextMessage(phone03, null, googleMapUrl, null, null);
        }
    }

    public String getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String strLoc = String.valueOf(latitude) + "," + String.valueOf(longitude);
        return strLoc;
    }

    private void saveCount(int count) {
        if(preferenceUtil != null) {
            preferenceUtil.setPrefValue(PreferenceUtil.PREF_EMERGENCY_COUNT, count);
        }
    }

    private int getCount() {
        String countString = countView.getText().toString();
        return Integer.parseInt(countString);
    }

    private void setCount(int count) {
        countView.setText(String.format("%03d", count));
    }
}
