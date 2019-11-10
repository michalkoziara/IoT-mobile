package c.michalkoziara.iot_mobile;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceGroupFragment extends ListFragment {
    private DeviceGroupListener callback;
    private DeviceGroupAdapter adapter;
    private View view;

    private Map<String, String> deviceGroupProductKeyByNames;
    private List<String> deviceGroupNames = new ArrayList<>();
    private Integer selectedPosition;

    void setDeviceGroupListener(DeviceGroupListener callback) {
        this.callback = callback;
    }

    public interface DeviceGroupListener {
        void createDeviceGroups();

        void onDeviceGroupClick(String deviceGroupName);

        void passDeviceGroupProductKeyByNamesToMain(Map<String, String> deviceGroupProductKeyByNames);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectedPosition != null) {
            outState.putInt("selectedPosition", selectedPosition);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt("selectedPosition");
        }

        populateListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MaterialTextView materialTextView = (MaterialTextView) v;
        materialTextView.setTextColor(Color.parseColor("#6200EE"));
        materialTextView.setBackgroundColor(Color.parseColor("#e0e0e0"));

        if (selectedPosition != null && selectedPosition != position) {
            MaterialTextView previouslySelectedMaterialTextView = (MaterialTextView) l.getChildAt(selectedPosition);

            if (previouslySelectedMaterialTextView != null) {
                previouslySelectedMaterialTextView.setTextColor(Color.parseColor("#757575"));
                previouslySelectedMaterialTextView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
        adapter.setSelectedPosition(position);
        this.selectedPosition = position;

        callback.onDeviceGroupClick(materialTextView.getText().toString());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_device_groups, container, false);
            view.setTag("deviceGroupFragmentView");
        }

        return view;
    }

    void populateListView() {
        callback.createDeviceGroups();
        if (isAdded() && adapter == null) {
            if (deviceGroupProductKeyByNames != null) {
                deviceGroupNames = new ArrayList<>(deviceGroupProductKeyByNames.keySet());
            }
            adapter = new DeviceGroupAdapter(getContext(), deviceGroupNames);

            if (selectedPosition != null) {
                adapter.setSelectedPosition(this.selectedPosition);
            }
            setListAdapter(adapter);
        }
    }

    void setDeviceGroupProductKeyByNames(Map<String, String> deviceGroupProductKeyByNames) {
        this.deviceGroupProductKeyByNames = deviceGroupProductKeyByNames;
        this.deviceGroupNames.clear();
        this.deviceGroupNames.addAll(deviceGroupProductKeyByNames.keySet());

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (callback != null) {
            callback.passDeviceGroupProductKeyByNamesToMain(deviceGroupProductKeyByNames);
        }
    }
}