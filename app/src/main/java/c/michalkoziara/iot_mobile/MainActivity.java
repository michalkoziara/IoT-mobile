package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        UserGroupFragment.UserGroupListener,
        DeviceGroupFragment.DeviceGroupListener {
    MainFragmentPageAdapter mainFragmentPageAdapter;
    ViewPager viewPager;

    Map<String, String> deviceGroupProductKeyByNames;
    String deviceGroupProductKey;

    Boolean isUserGroupSelected = false;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (deviceGroupProductKey != null) {
            outState.putString("deviceGroupProductKey", deviceGroupProductKey);
            outState.putBoolean("isUserGroupSelected", isUserGroupSelected);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            deviceGroupProductKey = savedInstanceState.getString("deviceGroupProductKey");
            isUserGroupSelected = savedInstanceState.getBoolean("isUserGroupSelected");
        }

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.tab_layout_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mainFragmentPageAdapter = new MainFragmentPageAdapter(
                fragmentManager,
                MainActivity.this
        );
        mainFragmentPageAdapter.isUserGroupSelected = isUserGroupSelected;

        viewPager.setAdapter(
                mainFragmentPageAdapter
        );

        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        if (fragment instanceof UserGroupFragment) {
            UserGroupFragment userGroupFragment = (UserGroupFragment) fragment;
            userGroupFragment.setUserGroupListener(this);
        }
        if (fragment instanceof DeviceGroupFragment) {
            DeviceGroupFragment deviceGroupFragment = (DeviceGroupFragment) fragment;
            deviceGroupFragment.setDeviceGroupListener(this);
        }
    }

    @Override
    public void onDeviceGroupClick(String deviceGroupName) {
        if (deviceGroupProductKeyByNames != null && deviceGroupProductKeyByNames.containsKey(deviceGroupName)) {
            isUserGroupSelected = true;
            deviceGroupProductKey = deviceGroupProductKeyByNames.get(deviceGroupName);

            if (mainFragmentPageAdapter != null) {
                mainFragmentPageAdapter.isUserGroupSelected = true;
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public void onDeviceGroupCreate() {
        String authToken = getToken();

        DeviceGroupFragment deviceGroupFragment = (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                viewPager,
                0
        );

//        getMainDevices(authToken);
        deviceGroupProductKeyByNames = new HashMap<>();
        for (int i = 0; i < 350; i++) {
            deviceGroupProductKeyByNames.put(String.valueOf(i), String.valueOf(i));
        }

        deviceGroupFragment.setDeviceGroupProductKeyByNames(deviceGroupProductKeyByNames);
    }

    @Override
    public View.OnClickListener createSensorButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(2);
            }
        };
    }

    @Override
    public View.OnClickListener createExecutiveButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainFragmentPageAdapter.isSensorOrExecutive = "executive";
                mainFragmentPageAdapter.notifyDataSetChanged();

                viewPager.setCurrentItem(2);
            }
        };
    }

    private void getMainDevices(String authToken) {
        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output == null) {
                    View contextView = findViewById(R.id.tab_layout_view_pager);
                    Snackbar.make(contextView, R.string.main_menu_load_failed_message, Snackbar.LENGTH_LONG).show();
                } else {
                    Map<String, String> deviceGroupProductKeyByNames = new HashMap<>();
                    try {
                        JSONArray deviceGroupListJson = new JSONArray(output);
                        for (int i = 0; i < deviceGroupListJson.length(); i++) {
                            JSONObject deviceGroupInfoJson = deviceGroupListJson.getJSONObject(i);

                            deviceGroupProductKeyByNames.put(
                                    deviceGroupInfoJson.getString("name"),
                                    deviceGroupInfoJson.getString("productKey")
                            );

                            DeviceGroupFragment deviceGroupFragment =
                                    (DeviceGroupFragment) mainFragmentPageAdapter.instantiateItem(
                                            viewPager,
                                            0
                                    );

                            deviceGroupFragment.setDeviceGroupProductKeyByNames(deviceGroupProductKeyByNames);
                        }
                    } catch (JSONException e) {
                        View contextView = findViewById(R.id.tab_layout_view_pager);
                        Snackbar.make(contextView, R.string.main_menu_load_failed_message, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public String createRequest(String[] params) {
                return HttpConnectionFactory.createGetConnection(params[0], params[1]);
            }
        });

        sync.execute(Constants.hubs_url, authToken);
    }

    private String getToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("authorization", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("authToken", "");

        if (authToken == null || authToken.equals("")) {
            View contextView = findViewById(R.id.tab_layout_view_pager);
            Snackbar.make(contextView, R.string.main_menu_login_failed_message, Snackbar.LENGTH_LONG).show();
        }
        return authToken;
    }
}
