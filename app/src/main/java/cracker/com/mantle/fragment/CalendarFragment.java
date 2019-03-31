package cracker.com.mantle.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cracker.com.mantle.R;
import cracker.com.mantle.components.CalendarView;
import cracker.com.mantle.model.CalendarModel;

public class CalendarFragment extends Fragment implements LocationListener {
    public static final String TAG = CalendarFragment.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION = 1234;

    private TextView todayView;
    private TextView todayDetailView;
    private TextView addressView;
    private CalendarView calendarView;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeViews(v);
//        initializeData();
        return v;
    }

    private void initializeViews(View parent) {
        todayView = parent.findViewById(R.id.text_calendar_today_date);
        todayDetailView = parent.findViewById(R.id.text_calendar_date);
        addressView = parent.findViewById(R.id.text_calendar_address);
        calendarView = parent.findViewById(R.id.view_calendar_list);
        calendarView.setOnCalendarListener(new CalendarView.OnCalendarListener() {
            @Override
            public void onCalendarDayClick(CalendarModel calendarModel) {
                if(calendarModel != null) {
                    todayView.setText(String.format("%d월%d일", calendarModel.getMonth(), calendarModel.getDay()));
                    if(calendarModel.getNoti() != null) {
                        addressView.setText(getAddress(calendarModel.getNoti().getLatitude(), calendarModel.getNoti().getLongitude()));
                        todayDetailView.setText(calendarModel.getNoti().getTime());
                    }
                }
            }
        });
    }


    private void initializeData() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) +1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        todayView.setText(String.format("%d월%d일", month, day));

        double latitude = Double.parseDouble(getLocation().split(",")[0]);
        double longitude = Double.parseDouble(getLocation().split(",")[1]);
        addressView.setText(getAddress(latitude, longitude));
    }


    public String getLocation() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        String strLoc = String.valueOf(latitude) + "," + String.valueOf(longitude);
        return strLoc;
    }


    private void requestAddress() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates("gps", 0, 0, this);
        } catch (SecurityException ex) {
            Log.d("gps", "Location permission did not work!");
        }
    }

    private String getAddress(double latitude, double longitude) {
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        if(getContext() == null) return nowAddress;

        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(latitude, longitude, 1);
                if (address != null && address.size() > 0) {
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;
                }
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return nowAddress;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && addressView != null) {
            addressView.setText(getAddress(location.getLatitude(), location.getLatitude()));
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }
}
