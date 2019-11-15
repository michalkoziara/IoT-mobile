package c.michalkoziara.iot_mobile;

import android.os.Bundle;
import android.os.Handler;
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
    final private Handler handler = new Handler();
    final private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            reloadListView();
        }
    };

    private Boolean isTimerRunning = false;
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

        Boolean isTimerOn();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isTimerRunning) {
            reloadListView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isTimerRunning) {
            reloadListView();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sensors, container, false);
            view.setTag("sensorFragmentView");
        }

        return view;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    void setSensorValuesByNames(Map<String, String> sensorValuesByNames) {
        this.sensorValuesByNames.clear();
        this.sensorValuesByNames.putAll(sensorValuesByNames);

        sensorNames.clear();
        if (!this.sensorValuesByNames.isEmpty()) {
            sensorNames.addAll(sensorValuesByNames.keySet());
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void reloadListView() {
        isTimerRunning = true;
        if (callback.isTimerOn()) {
            populateListView();
            handler.postDelayed(runnable, 3000);
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