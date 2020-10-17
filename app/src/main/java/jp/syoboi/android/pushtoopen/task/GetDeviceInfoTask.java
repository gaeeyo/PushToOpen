package jp.syoboi.android.pushtoopen.task;

import androidx.annotation.NonNull;

import jp.syoboi.android.pushtoopen.client.sesame.DeviceInfo;
import jp.syoboi.android.pushtoopen.client.sesame.Sesame;

public class GetDeviceInfoTask extends SesameTask<DeviceInfo> {

    @NonNull
    final String mDeviceId;

    public GetDeviceInfoTask(@NonNull String deviceId, @NonNull Callback<DeviceInfo> callback) {
        super(callback);
        mDeviceId = deviceId;
    }

    @Override
    protected DeviceInfo exec(@NonNull Sesame sesame) throws Exception {
        return sesame.getDeviceInfo(mDeviceId);
    }
}
