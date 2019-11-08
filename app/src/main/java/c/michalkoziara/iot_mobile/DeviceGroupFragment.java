package c.michalkoziara.iot_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

public class DeviceGroupFragment extends ListFragment {
//    private DeviceGroupListener callback;
//
//    void setDeviceGroupListener(DeviceGroupListener callback) {
//        this.callback = callback;
//    }
//
//    public interface DeviceGroupListener {
//        void onDeviceGroupCreate();
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] deviceGroupNames = new String[]{"Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2"};

        if (isAdded() && getActivity() != null) {

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getActivity(),
                    R.layout.layout_device_group,
                    deviceGroupNames);
            setListAdapter(adapter);
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_groups, container, false);

        view.setTag("deviceGroupFragmentView");
        return view;
    }
}