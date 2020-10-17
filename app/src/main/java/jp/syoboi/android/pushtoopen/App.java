package jp.syoboi.android.pushtoopen;

import android.app.Application;
import android.preference.PreferenceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.init(PreferenceManager.getDefaultSharedPreferences(this));
    }

}
