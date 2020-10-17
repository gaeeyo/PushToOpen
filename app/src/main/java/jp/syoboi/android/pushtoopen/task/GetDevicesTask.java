package jp.syoboi.android.pushtoopen.task;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import jp.syoboi.android.pushtoopen.client.sesame.Device;
import jp.syoboi.android.pushtoopen.client.sesame.Sesame;
import jp.syoboi.android.pushtoopen.client.sesame.SesameApiException;

public class GetDevicesTask extends SesameTask<List<Device>> {
    public GetDevicesTask(@NonNull Callback<List<Device>> callback) {
        super(callback);
    }

    @Override
    protected List<Device> exec(@NonNull Sesame sesame) throws IOException, JSONException, SesameApiException {
        return sesame.getDevices();
    }
}
