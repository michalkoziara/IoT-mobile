package c.michalkoziara.iot_mobile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements UserGroupFragment.UserGroupListener {
    private static final String TAG = "MainActivity";
    MainFragmentPageAdapter mainFragmentPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String authToken = getToken();
        Log.d(TAG, authToken);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.tab_layout_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mainFragmentPageAdapter = new MainFragmentPageAdapter(
                fragmentManager,
                MainActivity.this
        );
        viewPager.setAdapter(
                mainFragmentPageAdapter
        );

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof UserGroupFragment) {
            UserGroupFragment userGroupFragment = (UserGroupFragment) fragment;
            userGroupFragment.setUserGroupListener(this);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void getMainDevices(final String authToken) {

        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output == null) {
                    View contextView = findViewById(R.id.tab_layout_view_pager);
                    Snackbar.make(contextView, R.string.main_menu_load_failed_message, Snackbar.LENGTH_LONG).show();
                } else {
                    try {
                        JSONArray deviceGroupListJson = new JSONArray(output);

                        Map<String, String> deviceGroupProductKeyByNames = new HashMap<>();
                        for (int i = 0; i < deviceGroupListJson.length(); i++) {
                            JSONObject deviceGroupInfoJson = deviceGroupListJson.getJSONObject(i);

                            deviceGroupProductKeyByNames.put(
                                    deviceGroupInfoJson.getString("productKey"),
                                    deviceGroupInfoJson.getString("name")
                            );
                        }

                        Log.d(TAG, deviceGroupProductKeyByNames.toString());

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

    @Override
    public View.OnClickListener createSensorButtonOnClickListener() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mainFragmentPageAdapter.isSensorOrExecutive = "sensor";
                mainFragmentPageAdapter.notifyDataSetChanged();
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
            }
        };
    }
}
