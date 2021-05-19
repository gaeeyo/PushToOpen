package jp.syoboi.android.pushtoopen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

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
            showResultNotification(getString(R.string.deviceIdIsEmpty));
            return;
        }

        setTileState(Tile.STATE_ACTIVE);

        showNotification(CH_ACTION_PROCESSING, NID_ACTION_PROCESSING, getString(R.string.processing),
                android.R.drawable.stat_notify_sync);
        mSetLockTask = new SetLockTask(device_id, false, new SesameTask.Callback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                if (result.successful) {
                    showResultNotification(getString(R.string.successful));
                } else {
                    showResultNotification(getString(R.string.actionErrorFmt, result.error));
                }
            }

            @Override
            public void onError(Throwable e) {
                showResultNotification(ErrorMessage.getErrorMessage(getApplicationContext(), e));
            }

            @Override
            public void onFinish() {
                closeNotification(NID_ACTION_PROCESSING);
                setTileState(Tile.STATE_INACTIVE);
                mSetLockTask = null;
            }
        });
        mSetLockTask.execute();
    }

    public static final String CH_ACTION_PROCESSING  = "ActionProcessing";
    public static final String CH_ACTION_RESULT      = "ActionResult";
    public static final int    NID_ACTION_RESULT     = 1;
    public static final int    NID_ACTION_PROCESSING = 2;

    void createNotificationChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.createNotificationChannel(new NotificationChannel(CH_ACTION_PROCESSING, getString(R.string.actionProcessingCh),
                NotificationManager.IMPORTANCE_LOW));
        nm.createNotificationChannel(new NotificationChannel(CH_ACTION_RESULT, getString(R.string.actionResultCh),
                NotificationManager.IMPORTANCE_DEFAULT));
    }

    void showResultNotification(@NonNull String message) {
        showNotification(CH_ACTION_RESULT, NID_ACTION_RESULT, message, R.drawable.ic_launcher_foreground);
    }

    void showNotification(@NonNull String ch, int id, String message, int icon) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        Notification.Builder n = new Notification.Builder(this, ch)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setShowWhen(true);
        nm.notify(id, n.build());
    }

    void closeNotification(int id) {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(id);
    }
}
