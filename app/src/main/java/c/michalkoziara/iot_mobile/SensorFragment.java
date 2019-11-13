package c.michalkoziara.iot_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorFragment extends ListFragment {
    private SensorFragment.SensorListener callback;
    private SensorAdapter adapter;
    private View view;

    private Map<String, String> sensorValuesByNames = new HashMap<>();
    private List<String> sensorNames = new ArrayList<>();

    void setSensorListener(SensorFragment.SensorListener callback) {
        this.callback = callback;
    }

    public interface SensorListener {
        void createSensors();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateListView();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateListView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sensors, container, false);
            view.setTag("sensorFragmentView");
        }

        return view;
    }

    void setSensorValuesByNames(Map<String, String> sensorValuesByNames) {
        this.sensorValuesByNames.clear();
        this.sensorValuesByNames.putAll(sensorValuesByNames);

        sensorNames.clear();
        if(!this.sensorValuesByNames.isEmpty()) {
            sensorNames.addAll(sensorValuesByNames.keySet());
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void populateListView() {
        callback.createSensors();
        if (isAdded() && adapter == null) {
            adapter = new SensorAdapter(getContext(), sensorNames, sensorValuesByNames);
            setListAdapter(adapter);
        }
    }
}