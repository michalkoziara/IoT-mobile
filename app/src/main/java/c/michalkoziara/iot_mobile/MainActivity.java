package c.michalkoziara.iot_mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String authToken = getToken();
        Log.d(TAG, authToken);
    }

    @Override
    public void onBackPressed() {
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
