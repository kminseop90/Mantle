package cracker.com.mantle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cracker.com.mantle.fragment.CalendarFragment;
import cracker.com.mantle.fragment.SettingFragment;
import cracker.com.mantle.fragment.StatusFragment;
import cracker.com.mantle.service.CrackerManager;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final String FRAGMENT_TAG_STATUS = "status";
    private static final String FRAGMENT_TAG_CALENDAR = "calendar";
    private static final String FRAGMENT_TAG_SETTING = "setting";

    private StatusFragment statusFragment;
    private CalendarFragment calendarFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
//        CrackerManager.getInstance().write("F1");
    }

    private void initialize() {
        final FragmentManager manager = getSupportFragmentManager();
        statusFragment = (StatusFragment) manager.findFragmentByTag(FRAGMENT_TAG_STATUS);
        calendarFragment = (CalendarFragment) manager.findFragmentByTag(FRAGMENT_TAG_CALENDAR);
        settingFragment = (SettingFragment) manager.findFragmentByTag(FRAGMENT_TAG_SETTING);

        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment();
        }
        if (statusFragment == null) {
            statusFragment = new StatusFragment();
        }
        if (settingFragment == null) {
            settingFragment = new SettingFragment();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_bar_calendar:
                        switchFragment(calendarFragment, FRAGMENT_TAG_CALENDAR);
                        break;
                    case R.id.bottom_bar_status:
                        switchFragment(statusFragment, FRAGMENT_TAG_STATUS);
                        break;
                    case R.id.bottom_bar_setting:
                        switchFragment(settingFragment, FRAGMENT_TAG_SETTING);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_bar_status);
//        switchFragment(statusFragment, FRAGMENT_TAG_STATUS);
    }

    private void switchFragment(@NonNull Fragment fragment, String tag) {
        if (fragment.isAdded()) {
            return;
        }

        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction ft = manager.beginTransaction();

        final Fragment currentFragment = manager.findFragmentById(R.id.container);
        if (currentFragment != null) {
            ft.detach(currentFragment);
        }
        if (fragment.isDetached()) {
            ft.attach(fragment);
        } else {
            ft.add(R.id.container, fragment, tag);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
