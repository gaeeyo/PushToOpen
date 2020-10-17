package jp.syoboi.android.pushtoopen.client.sesame;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfo {
    public boolean locked;
    public int battery;
    public boolean responsive;

    public DeviceInfo(JSONObject o) throws JSONException {
        locked = o.getBoolean("locked");
        battery = o.optInt("battery");
        responsive = o.optBoolean("responsive", false);
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "locked=" + locked +
                ", battery=" + battery +
                ", responsive=" + responsive +
                '}';
    }
}
