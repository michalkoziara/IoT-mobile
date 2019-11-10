package c.michalkoziara.iot_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class UserGroupFragment extends Fragment {
    private UserGroupListener callback;
    private UserGroupAdapter adapter;
    private View view;

    private List<String> userGroupNames = new ArrayList<>();
    private Integer selectedPosition;

    void setUserGroupListener(UserGroupListener callback) {
        this.callback = callback;
    }

    public interface UserGroupListener {
        void passUserGroupNamesToMain(List<String> userGroupNames);

        void createUserGroups();

        View.OnClickListener createSensorButtonOnClickListener();

        View.OnClickListener createExecutiveButtonOnClickListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateListView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_groups, container, false);

            ConstraintLayout constraintLayout = (ConstraintLayout) view;
            MaterialButton sensorButton = constraintLayout.findViewById(R.id.user_group_sensor_btn);
            sensorButton.setOnClickListener(callback.createSensorButtonOnClickListener());

            MaterialButton executiveButton = constraintLayout.findViewById(R.id.user_group_executive_btn);
            executiveButton.setOnClickListener(callback.createExecutiveButtonOnClickListener());

            view.setTag("userGroupFragmentView");
        }

        return view;
    }

    void populateListView() {
        callback.createUserGroups();
        if (isAdded() && adapter == null) {
            adapter = new UserGroupAdapter(getContext(), userGroupNames);
            ListView listView = view.findViewById(R.id.user_groups_list_view);
            listView.setAdapter(adapter);
        }
    }

    void setUserGroupNames(List<String> userGroupNames) {
        this.userGroupNames.clear();
        this.userGroupNames.addAll(userGroupNames);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (callback != null) {
            callback.passUserGroupNamesToMain(userGroupNames);
        }
    }
}