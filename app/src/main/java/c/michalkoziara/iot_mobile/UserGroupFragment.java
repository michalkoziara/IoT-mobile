package c.michalkoziara.iot_mobile;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class UserGroupFragment extends Fragment {

    UserGroupListener callback;

    public void setUserGroupListener(UserGroupListener callback) {
        this.callback = callback;
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface UserGroupListener {
        public View.OnClickListener createSensorButtonOnClickListener();
        public View.OnClickListener createExecutiveButtonOnClickListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_groups, container, false);

        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        MaterialButton sensorButton = constraintLayout.findViewById(R.id.user_group_sensor_btn);
        sensorButton.setOnClickListener(callback.createSensorButtonOnClickListener());

        MaterialButton executiveButton = constraintLayout.findViewById(R.id.user_group_executive_btn);
        executiveButton.setOnClickListener(callback.createExecutiveButtonOnClickListener());
        return view;
    }
}