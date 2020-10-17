package jp.syoboi.android.pushtoopen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import jp.syoboi.android.pushtoopen.client.sesame.Device;
import jp.syoboi.android.pushtoopen.databinding.ItemDeviceBinding;

public class DeviceAdapter extends BaseAdapter {

    @NonNull
    final List<Device> mItems;

    public DeviceAdapter(@NonNull List<Device> items) {
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Device getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        ItemDeviceBinding binding = (view != null ? ItemDeviceBinding.bind(view) :
                ItemDeviceBinding.inflate(LayoutInflater.from(context), viewGroup, false));
        Device device = mItems.get(i);
        binding.getRoot().setText(context.getString(R.string.keyFormat, device.nickname, device.serial));
        return binding.getRoot();
    }
}
