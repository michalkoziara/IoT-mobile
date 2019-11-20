package c.michalkoziara.iot_mobile;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExecutiveDeviceFragment extends ListFragment {
    private ExecutiveDeviceListener callback;
    private ExecutiveDeviceAdapter adapter;
    private View view;

    private Map<String, String> executiveDeviceKeyByNames;
    private List<String> executiveDeviceNames = new ArrayList<>();

    void setExecutiveDeviceListener(ExecutiveDeviceFragment.ExecutiveDeviceListener callback) {
        this.callback = callback;
    }

    public interface ExecutiveDeviceListener {
        void createExecutiveDevices();

        void passExecutiveDeviceKeyByNamesToMain(Map<String, String> executiveDeviceKeyByNames);

        void openControllerActivity(String executiveDeviceName, String executiveDeviceKey);
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
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MaterialTextView materialTextView = (MaterialTextView) v;
        String selectedExecutiveDeviceName = materialTextView.getText() != null
                ? materialTextView.getText().toString()
                : "";

        if (executiveDeviceKeyByNames != null && executiveDeviceKeyByNames.containsKey(selectedExecutiveDeviceName)) {
            String executiveDeviceKey = executiveDeviceKeyByNames.get(selectedExecutiveDeviceName);
            callback.openControllerActivity(selectedExecutiveDeviceName, executiveDeviceKey);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_executive_devices, container, false);
            view.setTag("executiveFragmentView");
        }

        return view;
    }

    void setExecutiveDeviceKeyByNames(Map<String, String> executiveDeviceKeyByNames) {
        this.executiveDeviceKeyByNames = executiveDeviceKeyByNames;
        this.executiveDeviceNames.clear();
        this.executiveDeviceNames.addAll(executiveDeviceKeyByNames.keySet());
        Collections.sort(executiveDeviceNames, String.CASE_INSENSITIVE_ORDER);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (callback != null) {
            callback.passExecutiveDeviceKeyByNamesToMain(executiveDeviceKeyByNames);
        }
    }

    private void populateListView() {
        callback.createExecutiveDevices();
        if (isAdded() && adapter == null) {
            if (executiveDeviceKeyByNames != null) {
                executiveDeviceNames = new ArrayList<>(executiveDeviceKeyByNames.keySet());
            }
            Collections.sort(executiveDeviceNames, String.CASE_INSENSITIVE_ORDER);
            adapter = new ExecutiveDeviceAdapter(getContext(), executiveDeviceNames);

            setListAdapter(adapter);
        }
    }
}