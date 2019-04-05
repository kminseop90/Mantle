package cracker.com.mantle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cracker.com.mantle.model.NotiModel;
import cracker.com.mantle.service.CrackerManager;
import cracker.com.mantle.util.AlarmWakeLock;
import cracker.com.mantle.util.PreferenceUtil;

public class NotiActivity extends BaseActivity {

    private PreferenceUtil preferenceUtil;
    public static final String TYPE_EMERGENCY = "TYPE_EMERGENCY";
    public static final String DEFAULT_PHONE_NUMBER = "000-0000-0000";

    private TextView countView;
    private ImageView saveView;
    private boolean isEmergency = false;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if(getIntent() != null) {
            isEmergency = getIntent().getBooleanExtra(TYPE_EMERGENCY, false);
        }

        initializeViews();
        initializeData();
        if(isEmergency) {
            initializeEmergency();
            startTimer();
            CrackerManager.getInstance().write("F3");
            saveNoti();
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

        AlarmWakeLock.wakeLock(this);
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
                Toast.makeText(NotiActivity.this, "Send Message", Toast.LENGTH_SHORT).show();
                CrackerManager.getInstance().write("F4");
                finish();
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
                    if(count > 50) {
                        setCount(--count);
                    }
                    break;
                case R.id.icon_noti_count_plus:
                    if(count <= 149) {
                        setCount(++count);
                    }
                    break;
                case R.id.button_noti_save:
                    if(isEmergency) {
                        CrackerManager.getInstance().write("F4");
                        timer.removeMessages(0);
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

        String googleMapBaseUrl = "https://map.google.com/maps/search/?api=1&query=%s";
        String googleMapUrl = String.format(googleMapBaseUrl, getLocation());

        String defaultSendMessage = "긴급상황 도움요청이 왔습니다! MANTLE에 의해 발송 ";

        SmsManager smsManager = SmsManager.getDefault();

        if(!DEFAULT_PHONE_NUMBER.equals(phone01)) {
            smsManager.sendTextMessage(phone01, null, googleMapUrl, null, null);
            smsManager.sendTextMessage(phone01, null, defaultSendMessage, null, null);
        }
        if(!DEFAULT_PHONE_NUMBER.equals(phone02)) {
            smsManager.sendTextMessage(phone02, null, googleMapUrl, null, null);
            smsManager.sendTextMessage(phone01, null, defaultSendMessage, null, null);
        }
        if(!DEFAULT_PHONE_NUMBER.equals(phone03)) {
            smsManager.sendTextMessage(phone03, null, googleMapUrl, null, null);
            smsManager.sendTextMessage(phone01, null, defaultSendMessage, null, null);
        }
    }

    public String getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = 0;
        double latitude = 0;
        if(location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

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

    private void saveNoti() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        NotiModel notiModel = new NotiModel();
        notiModel.setYear(year + "");
        notiModel.setMonth(month+ "");
        notiModel.setDay(day + "");
        SimpleDateFormat f = new SimpleDateFormat("MM월dd일 a h:mm", Locale.KOREA);
        notiModel.setTime(f.format(new Date()));
        notiModel.setLatitude(Double.parseDouble(getLocation().split(",")[0]));
        notiModel.setLongitude(Double.parseDouble(getLocation().split(",")[1]));

        preferenceUtil.setNotiValue(String.format("%04d%02d%02d", year, month, day), notiModel);
    }
}
