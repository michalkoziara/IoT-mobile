package c.michalkoziara.iot_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class UserGroupFragment extends Fragment {

    private UserGroupListener callback;

    void setUserGroupListener(UserGroupListener callback) {
        this.callback = callback;
    }

    public interface UserGroupListener {
        View.OnClickListener createSensorButtonOnClickListener();
        View.OnClickListener createExecutiveButtonOnClickListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_groups, container, false);

        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        MaterialButton sensorButton = constraintLayout.findViewById(R.id.user_group_sensor_btn);
        sensorButton.setOnClickListener(callback.createSensorButtonOnClickListener());

        MaterialButton executiveButton = constraintLayout.findViewById(R.id.user_group_executive_btn);
        executiveButton.setOnClickListener(callback.createExecutiveButtonOnClickListener());

        view.setTag("userGroupFragmentView");
        return view;
    }
}