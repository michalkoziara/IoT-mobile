package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;
import java.util.Map;

public class SensorAdapter extends BaseAdapter {
    private Context context;

    private Map<String, String> sensorValuesByNames;
    private List<String> sensorNames;

    SensorAdapter(Context context, List<String> sensorNames, Map<String, String> sensorValuesByNames) {
        this.context = context;
        this.sensorValuesByNames = sensorValuesByNames;
        this.sensorNames = sensorNames;
    }

    @Override
    public int getCount() {
        return sensorNames.size();
    }

    @Override
    public Object getItem(int position) {
        return sensorNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_sensor, parent, false);
        }

        String sensorName = (String) getItem(position);

        MaterialTextView sensorTextView = convertView.findViewById(R.id.sensor_text);
        sensorTextView.setText(sensorName);

        MaterialTextView sensorValueTextView = convertView.findViewById(R.id.sensor_value_text);
        sensorValueTextView.setText(sensorValuesByNames.get(sensorName));

        return convertView;
    }
}
