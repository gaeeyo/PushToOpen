package jp.syoboi.android.pushtoopen.client.sesame;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Device {
    @NonNull
    public final String device_id;
    @NonNull
    public final String serial;
    @NonNull
    public final String nickname;

    public Device(JSONObject o) throws JSONException {
        device_id = o.getString("device_id");
        serial = o.getString("serial");
        nickname = o.getString("nickname");
    }

    @Override
    public String toString() {
        return "Device{" +
                "device_id='" + device_id + '\'' +
                ", serial='" + serial + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    public static int findBySerial(@NonNull List<Device> devices, @NonNull String device_id) {
        for (int j=0, size= devices.size(); j<size; j++) {
            if (device_id.equals(devices.get(j).device_id)) {
                return j;
            }
        }
        return -1;
    }
}
