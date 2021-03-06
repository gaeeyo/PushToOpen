package jp.syoboi.android.pushtoopen.task;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import jp.syoboi.android.pushtoopen.BuildConfig;
import jp.syoboi.android.pushtoopen.client.sesame.ActionResult;
import jp.syoboi.android.pushtoopen.client.sesame.Sesame;

public class SetLockTask extends SesameTask<ActionResult> {
    @NonNull
    final String  mDeviceId;
    final boolean mLock;

    public SetLockTask(@NonNull String device_id, boolean lock, @NonNull Callback<ActionResult> callback) {
        super(callback);
        mDeviceId = device_id;
        mLock = lock;
    }

    @Override
    protected ActionResult exec(@NonNull Sesame sesame) throws Exception {
//        if (BuildConfig.DEBUG) {
//            Thread.sleep(5000);
//            JSONObject j = new JSONObject();
//            j.put("status", ActionResult.STATUS_TERMINATED);
//            j.put("successful", true);
//            return new ActionResult(j);
//        }

        String taskId = sesame.setLock(mDeviceId, mLock);

        Thread.sleep(1000);
        for (int j = 0; j < 8; j++) {
            ActionResult r = sesame.getActionResult(taskId);
            if (ActionResult.STATUS_TERMINATED.equals(r.status)) {
                return r;
            }
            Thread.sleep(1500);
        }
        return null;
    }
}
