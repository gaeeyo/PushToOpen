package jp.syoboi.android.pushtoopen;

import android.app.Application;
import android.preference.PreferenceManager;

public class App extends Application {

    public static final String USER_AGENT = "PushToOpen/" + BuildConfig.VERSION_NAME
            + " (+https://github.com/gaeeyo/PushToOpen)";

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.init(PreferenceManager.getDefaultSharedPreferences(this));
    }

}
