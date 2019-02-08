package cracker.com.mantle.util;

import android.content.Context;
import android.os.PowerManager;

public class AlarmWakeLock {

    private static final String TAG = AlarmWakeLock.class.getSimpleName();
    private static PowerManager.WakeLock mWakeLock;

    public static void wakeLock(Context context) {
        if(mWakeLock != null) return;

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "mantle:mantle_power");
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}
