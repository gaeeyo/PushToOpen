package jp.syoboi.android.pushtoopen.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.List;

import jp.syoboi.android.pushtoopen.DeviceAdapter;
import jp.syoboi.android.pushtoopen.ErrorMessage;
import jp.syoboi.android.pushtoopen.Prefs;
import jp.syoboi.android.pushtoopen.activity.base.BaseSetupActivity;
import jp.syoboi.android.pushtoopen.client.sesame.Device;
import jp.syoboi.android.pushtoopen.databinding.ActivityKeyBinding;
import jp.syoboi.android.pushtoopen.task.GetDevicesTask;
import jp.syoboi.android.pushtoopen.task.SesameTask;

public class KeyActivity extends BaseSetupActivity {

    ActivityKeyBinding mBinding;

    GetDevicesTask mGetDevicesTask;
    String         mErrorMessage;
    boolean        mProgress;
    List<Device>   mDevices;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityKeyBinding.inflate(getLayoutInflater(),
                (ViewGroup) findViewById(android.R.id.content));

        mBinding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateView();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshKey();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGetDevicesTask != null) {
            mGetDevicesTask.cancel(true);
            mGetDevicesTask = null;
        }
    }

    public void onClickReload(View v) {
        refreshKey();
    }

    public void onClickDone(View v) {
        ListView lv  = mBinding.list;
        int      pos = lv.getCheckedItemPosition();
        if (pos != -1) {
            Object o = lv.getItemAtPosition(pos);
            if (o instanceof Device) {
                Prefs.set(Prefs.SESAME_DEVICE_ID, ((Device) o).device_id);
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    void updateView() {
        mBinding.progress.setVisibility(mProgress ? View.VISIBLE : View.GONE);
        mBinding.errorPage.setVisibility(!mProgress && mErrorMessage != null ? View.VISIBLE : View.GONE);
        if (!mProgress && mErrorMessage != null) {
            mBinding.errorMessage.setText(mErrorMessage);
        }
        mBinding.listPage.setVisibility(mDevices != null ? View.VISIBLE : View.GONE);
        mBinding.done.setEnabled(mBinding.list.getCheckedItemPosition() != -1);
    }

    void refreshKey() {
        mProgress = true;
        mErrorMessage = null;
        mDevices = null;
        updateView();

        if (mGetDevicesTask != null) {
            mGetDevicesTask.cancel(true);
        }

        mGetDevicesTask = new GetDevicesTask(new SesameTask.Callback<List<Device>>() {
            @Override
            public void onSuccess(List<Device> result) {
                mDevices = result;
                mBinding.list.setAdapter(new DeviceAdapter(result));
                String device_id = Prefs.get(Prefs.SESAME_DEVICE_ID);
                int pos = Device.findBySerial(result, device_id);
                if (pos != -1) {
                    mBinding.list.setItemChecked(pos, true);
                }
            }

            @Override
            public void onError(Throwable e) {
                mErrorMessage = ErrorMessage.getErrorMessage(getApplicationContext(), e);
            }

            @Override
            public void onFinish() {
                mProgress = false;
                updateView();
            }
        });

        mGetDevicesTask.execute();
    }
}
