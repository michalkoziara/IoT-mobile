package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ExecutiveDeviceAdapter extends BaseAdapter {
    List<String> executiveDeviceNames;
    Context context;

    private Integer selectedPosition;

    ExecutiveDeviceAdapter(Context context, List<String> executiveDeviceNames) {
        this.context = context;
        this.executiveDeviceNames = executiveDeviceNames;
    }

    @Override
    public int getCount() {
        return executiveDeviceNames.size();
    }

    @Override
    public Object getItem(int position) {
        return executiveDeviceNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_device_group, parent, false);
        }

        MaterialTextView materialTextView = (MaterialTextView) convertView;
        materialTextView.setText((String) getItem(position));

        return convertView;
    }
}
