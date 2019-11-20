package c.michalkoziara.iot_mobile;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.ListFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserGroupFragment extends ListFragment {
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

        void onUserGroupClick(String userGroupName);

        View.OnClickListener createSensorButtonOnClickListener();

        View.OnClickListener createExecutiveButtonOnClickListener();
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
            selectedPosition = (Integer) savedInstanceState.get("selectedPosition");
        }

        if (selectedPosition != null) {
            MaterialButton executiveDeviceButton = view.findViewById(R.id.user_group_executive_btn);
            MaterialButton sensorButton = view.findViewById(R.id.user_group_sensor_btn);
            executiveDeviceButton.setVisibility(View.VISIBLE);
            sensorButton.setVisibility(View.VISIBLE);
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
            MaterialTextView previouslySelectedMaterialTextView =
                    (MaterialTextView) getRowAtRealPosition(selectedPosition, l);

            if (previouslySelectedMaterialTextView != null) {
                previouslySelectedMaterialTextView.setTextColor(Color.parseColor("#757575"));
                previouslySelectedMaterialTextView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }
        adapter.setSelectedPosition(position);
        this.selectedPosition = position;

        callback.onUserGroupClick(materialTextView.getText().toString());

        MaterialButton executiveDeviceButton = view.findViewById(R.id.user_group_executive_btn);
        MaterialButton sensorButton = view.findViewById(R.id.user_group_sensor_btn);

        if (executiveDeviceButton.getVisibility() == View.GONE
                || sensorButton.getVisibility() == View.GONE) {
            executiveDeviceButton.setVisibility(View.VISIBLE);
            sensorButton.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                executiveDeviceButton.startAnimation(AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.slide_from_buttom
                ));
                sensorButton.startAnimation(
                        AnimationUtils.loadAnimation(
                                getContext(),
                                R.anim.slide_from_buttom
                        )
                );
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_groups, container, false);

        ConstraintLayout constraintLayout = (ConstraintLayout) view;
        MaterialButton sensorButton = constraintLayout.findViewById(R.id.user_group_sensor_btn);
        sensorButton.setOnClickListener(callback.createSensorButtonOnClickListener());

        MaterialButton executiveButton = constraintLayout.findViewById(R.id.user_group_executive_btn);
        executiveButton.setOnClickListener(callback.createExecutiveButtonOnClickListener());

        view.setTag("userGroupFragmentView");

        return view;
    }

    void setUserGroupNames(List<String> userGroupNames) {
        this.userGroupNames.clear();
        this.userGroupNames.addAll(userGroupNames);
        Collections.sort(userGroupNames, String.CASE_INSENSITIVE_ORDER);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if (callback != null) {
            callback.passUserGroupNamesToMain(userGroupNames);
        }
    }

    void resetSelectedPosition() {
        if (this.selectedPosition != null) {
            this.adapter.setSelectedPosition(null);
            this.selectedPosition = null;
        }
    }

    void setSelectedPosition(Integer selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    private View getRowAtRealPosition(int pos, @NonNull ListView listView) {
        int firstListItemPosition = listView.getFirstVisiblePosition();
        int childIndex = pos - firstListItemPosition;
        return listView.getChildAt(childIndex);
    }

    private void populateListView() {
        callback.createUserGroups();
        if (isAdded() && adapter == null) {
            Collections.sort(userGroupNames, String.CASE_INSENSITIVE_ORDER);
            adapter = new UserGroupAdapter(getContext(), userGroupNames);
            if (selectedPosition != null) {
                adapter.setSelectedPosition(this.selectedPosition);
            }
            setListAdapter(adapter);
        }
    }
}