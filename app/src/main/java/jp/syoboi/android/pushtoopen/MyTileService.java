package jp.syoboi.android.pushtoopen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.util.Log;

import jp.syoboi.android.pushtoopen.client.sesame.ActionResult;
import jp.syoboi.android.pushtoopen.task.SesameTask;
import jp.syoboi.android.pushtoopen.task.SetLockTask;

public class MyTileService extends TileService {

    static final String TAG = MyTileService.class.getSimpleName();

    SetLockTask mSetLockTask;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        setTileState(Tile.STATE_INACTIVE);
    }

    void setTileState(int state) {
        Tile tile = getQsTile();
        if (tile == null) {
            Log.w(TAG, "tile == null");
            return;
        }
        tile.setState(state);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        if (mSetLockTask != null) {
            return;
        }

        String device_id = Prefs.get(Prefs.SESAME_DEVICE_ID);
        if (TextUtils.isEmpty(device_id)) {
            showNotification(getString(R.string.deviceIdIsEmpty));
            return;
        }

        setTileState(Tile.STATE_ACTIVE);

        showNotification(getString(R.string.processing));
        mSetLockTask = new SetLockTask(device_id, false, new SesameTask.Callback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                if (result.successful) {
                    showNotification(getString(R.string.successful));
                } else {
                    showNotification(getString(R.string.actionErrorFmt, result.error));
                }
            }

            @Override
            public void onError(Throwable e) {
                showNotification(ErrorMessage.getErrorMessage(getApplicationContext(), e));
            }

            @Override
            public void onFinish() {
                setTileState(Tile.STATE_INACTIVE);
                mSetLockTask = null;
            }
        });
        mSetLockTask.execute();
    }

    public static final String N_C_ID             = "TileServiceChannel";
    public static final int    N_ID_ACTION_RESULT = 1;

    void createNotificationChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel(N_C_ID, getString(R.string.tileNotificationCh),
                NotificationManager.IMPORTANCE_LOW));
    }

    void showNotification(String message) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        Notification n = new Notification.Builder(this, N_C_ID)
                .setContentTitle(message)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        nm.notify(N_ID_ACTION_RESULT, n);
    }

    void closeNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(N_ID_ACTION_RESULT);
    }
}
