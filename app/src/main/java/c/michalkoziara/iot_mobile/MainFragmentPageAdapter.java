package c.michalkoziara.iot_mobile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentPageAdapter extends FragmentStatePagerAdapter {
    private Context context;
    String isSensorOrExecutive;

    MainFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.isSensorOrExecutive = null;
    }

    @Override
    public int getCount() {
        if (isSensorOrExecutive != null) {
            return 3;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new DeviceGroupFragment();
        } else if (position == 1) {
            return new UserGroupFragment();
        } else if (position == 2 && isSensorOrExecutive.equals("executive")) {
            return new ExecutiveDeviceFragment();
        } else if (position == 2 && isSensorOrExecutive.equals("sensor")) {
            return new SensorFragment();
        }
        return new Fragment();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof ExecutiveDeviceFragment ||
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
        tabTitles.add(context.getString(R.string.user_group_tab_item));

        if (position == 2 && isSensorOrExecutive.equals("executive")) {
            tabTitles.add(context.getString(R.string.executive_device_tab_item));
        } else if (position == 2 && isSensorOrExecutive.equals("sensor")) {
            tabTitles.add(context.getString(R.string.sensor_tab_item));
        }

        return tabTitles.get(position);
    }
}