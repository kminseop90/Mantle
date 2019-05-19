package cracker.com.mantle.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cracker.com.mantle.model.NotiModel;

public class PreferenceUtil {

    public static final String APP_SHARED_PREFS = "thisApp.SharedPreference";

    public static final String PREF_PHONE_NUMBER_01 = "PREF_PHONE_NUMBER_01";
    public static final String PREF_PHONE_NUMBER_02 = "PREF_PHONE_NUMBER_02";
    public static final String PREF_PHONE_NUMBER_03 = "PREF_PHONE_NUMBER_03";
    public static final String PREF_EMERGENCY_COUNT = "PREF_EMERGENCY_COUNT";
    public static final String PREF_TYPE = "PREF_TYPE";
    public static final String PREF_CALENDAR = "PREF_CALENDAR";
    public static final String PREF_SENSOR_RIGHT = "PREF_SENSOR_RIGHT";
    public static final String PREF_SENSOR_LEFT = "PREF_SENSOR_LEFT";
    public static final String PREF_DEFAULT_SENSOR_VALUE = "PREF_DEFAULT_SENSOR_VALUE";
    public static final String PREF_LEFT_SENSOR_VALUE = "PREF_LEFT_SENSOR_VALUE";
    public static final String PREF_RIGHT_SENSOR_VALUE = "PREF_RIGHT_SENSOR_VALUE";

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

    public void setNotiValue(String key, NotiModel notiModel) {
        ArrayList<NotiModel> notiModels = getNotiValue(key);
        if(notiModels == null)
            notiModels = new ArrayList<>();

        notiModels.add(notiModel);

        String value = new Gson().toJson(notiModels);
        editor.putString(key, value);
        editor.commit();
    }

    public ArrayList<NotiModel> getNotiValue(String key) {
        String notiValue = sharedPreferences.getString(key, "");
        Type listType = new TypeToken<ArrayList<NotiModel>>(){}.getType();
        ArrayList<NotiModel> list = new Gson().fromJson(notiValue, listType);
        return list;
    }

    public String getPrefStringValue(String key, String defaultString) {
        return sharedPreferences.getString(key, defaultString);
    }

    public int getPrefIntValue(String key) {
        return sharedPreferences.getInt(key, 70);
    }


}
