package jp.syoboi.android.pushtoopen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import jp.syoboi.android.pushtoopen.ErrorMessage;
import jp.syoboi.android.pushtoopen.Prefs;
import jp.syoboi.android.pushtoopen.R;
import jp.syoboi.android.pushtoopen.client.sesame.ActionResult;
import jp.syoboi.android.pushtoopen.client.sesame.DeviceInfo;
import jp.syoboi.android.pushtoopen.databinding.ActivityMainBinding;
import jp.syoboi.android.pushtoopen.task.GetDeviceInfoTask;
import jp.syoboi.android.pushtoopen.task.SesameTask;
import jp.syoboi.android.pushtoopen.task.SetLockTask;

/**
 * メイン画面
 */
public class MainActivity extends Activity {


    ActivityMainBinding mBinding;
    boolean             mProgress;
    String              mErrorMessage;
    boolean mCanReload;
    DeviceInfo          mDeviceInfo;

    GetDeviceInfoTask mGetDeviceInfoTask;
    SetLockTask       mSetLockTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater(),
                (ViewGroup) findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelReload();
    }

    void updateView() {
        mBinding.progress.setVisibility(mProgress ? View.VISIBLE : View.GONE);
        mBinding.mainPage.setVisibility(!mProgress ? View.VISIBLE : View.GONE);
        mBinding.controlPage.setVisibility(!mProgress && mErrorMessage == null ? View.VISIBLE : View.GONE);
        mBinding.errorPage.setVisibility(mErrorMessage != null ? View.VISIBLE : View.GONE);
        mBinding.errorMessage.setText(mErrorMessage);
        mBinding.reload.setVisibility(!mProgress && mCanReload ? View.VISIBLE : View.GONE);
        if (mDeviceInfo != null) {
            mBinding.lock.setEnabled(!mDeviceInfo.locked);
            mBinding.unlock.setEnabled(mDeviceInfo.locked);
        }
    }

    void cancelReload() {
        if (mGetDeviceInfoTask != null) {
            mGetDeviceInfoTask.cancel(true);
            mGetDeviceInfoTask = null;
        }
    }

    void reload() {
        if (TextUtils.isEmpty(Prefs.get(Prefs.SESAME_API_KEY))) {
            mErrorMessage = getString(R.string.apiKeyIsEmpty);
            mCanReload = false;
            updateView();
            return;
        }
        if (TextUtils.isEmpty(Prefs.get(Prefs.SESAME_DEVICE_ID))) {
            mErrorMessage = getString(R.string.deviceIdIsEmpty);
            mCanReload = false;
            updateView();
            return;
        }

        cancelReload();

        mProgress = true;
        mErrorMessage = null;
        mDeviceInfo = null;
        updateView();

        mGetDeviceInfoTask = new GetDeviceInfoTask(Prefs.get(Prefs.SESAME_DEVICE_ID), new SesameTask.Callback<DeviceInfo>() {
            @Override
            public void onSuccess(DeviceInfo result) {
                mDeviceInfo = result;
            }

            @Override
            public void onError(Throwable e) {
                mErrorMessage = ErrorMessage.getErrorMessage(getApplicationContext(), e);
                mCanReload = true;
            }

            @Override
            public void onFinish() {
                mProgress = false;
                mGetDeviceInfoTask = null;
                updateView();
            }
        });
        mGetDeviceInfoTask.execute();
    }

    void setLock(final boolean lock) {
        mProgress = true;
        mErrorMessage = null;
        updateView();

        mSetLockTask = new SetLockTask(Prefs.get(Prefs.SESAME_DEVICE_ID), lock, new SesameTask.Callback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                if (result.successful) {
                    mDeviceInfo.locked = lock;
                } else {
                    mErrorMessage = getString(R.string.actionErrorFmt, result.error);
                    mCanReload = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                mErrorMessage = ErrorMessage.getErrorMessage(getApplicationContext(), e);
                mCanReload = false;
            }

            @Override
            public void onFinish() {
                mProgress = false;
                mSetLockTask = null;
                updateView();
            }
        });
        mSetLockTask.execute();
    }

    public void onClickSetup(View v) {
        startActivity(new Intent(this, SetupActivity.class));
    }

    public void onClickLock(View v) {
        setLock(true);
    }

    public void onClickUnlock(View v) {
        setLock(false);
    }

    public void onClickReload(View v) {
        reload();
    }

}