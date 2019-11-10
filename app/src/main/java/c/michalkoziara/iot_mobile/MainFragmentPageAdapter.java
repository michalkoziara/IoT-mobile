package c.michalkoziara.iot_mobile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentPageAdapter extends FragmentStatePagerAdapter {

    String isSensorOrExecutive;
    Boolean isUserGroupSelected = false;

    private Context context;

    MainFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.isSensorOrExecutive = null;
    }

    @Override
    public int getCount() {
        if (isSensorOrExecutive != null) {
            return 3;
        } else if (isUserGroupSelected) {
            return 2;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();

        if (position == 0) {
            fragment = new DeviceGroupFragment();
        } else if (position == 1 && isUserGroupSelected) {
            fragment = new UserGroupFragment();
        } else if (position == 2 && isSensorOrExecutive.equals("executive")) {
            fragment = new ExecutiveDeviceFragment();
        } else if (position == 2 && isSensorOrExecutive.equals("sensor")) {
            fragment = new SensorFragment();
        }

        return fragment;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof UserGroupFragment ||
                object instanceof ExecutiveDeviceFragment ||
                object instanceof SensorFragment) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        List<String> tabTitles = new ArrayList<>();
        tabTitles.add(context.getString(R.string.device_group_tab_item));

        if (isUserGroupSelected) {
            tabTitles.add(context.getString(R.string.user_group_tab_item));
        }

        if (isSensorOrExecutive != null) {
            if (isSensorOrExecutive.equals("executive")) {
                tabTitles.add(context.getString(R.string.executive_device_tab_item));
            } else if (isSensorOrExecutive.equals("sensor")) {
                tabTitles.add(context.getString(R.string.sensor_tab_item));
            }
        }

        return tabTitles.get(position);
    }
}