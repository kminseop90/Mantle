package cracker.com.mantle.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

    public static final String APP_SHARED_PREFS = "thisApp.SharedPreference";
    public static final String PREF_PHONE_NUMBER_01 = "PREF_PHONE_NUMBER_01";
    public static final String PREF_PHONE_NUMBER_02 = "PREF_PHONE_NUMBER_02";
    public static final String PREF_PHONE_NUMBER_03 = "PREF_PHONE_NUMBER_03";
    public static final String PREF_EMERGENCY_COUNT = "PREF_EMERGENCY_COUNT";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceUtil(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void setPrefValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setPrefValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public String getPrefStringValue(String key) {
        return sharedPreferences.getString(key, "010-0000-0000");
    }

    public int getPrefIntValue(String key) {
        return sharedPreferences.getInt(key, 70);
    }
}
