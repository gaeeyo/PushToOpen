package jp.syoboi.android.pushtoopen;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public final class Prefs {


    public static final StrValue SESAME_API_KEY   = new StrValue("sesameApiKey");
    public static final StrValue SESAME_DEVICE_ID = new StrValue("sesameDeviceId");

    private static SharedPreferences sPrefs;

    public static void init(@NonNull SharedPreferences prefs) {
        sPrefs = prefs;
    }

    @NonNull
    public static String get(@NonNull StrValue key) {
        String value = sPrefs.getString(key.key, null);
        return (value != null ? value : "");
    }

    public static void set(@NonNull StrValue key, @NonNull String value) {
        sPrefs.edit().putString(key.key, value).apply();
    }

    private static class StrValue {
        @NonNull
        private final String key;

        public StrValue(@NonNull String key) {
            this.key = key;
        }
    }
}
