package c.michalkoziara.iot_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import android.transition.Explode;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.TransitionManager;

import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    TextInputEditText _nameText;
    TextInputLayout _nameTextLayout;
    TextInputEditText _emailText;
    TextInputLayout _emailTextLayout;
    TextInputEditText _passwordText;
    TextInputLayout _passwordTextLayout;
    MaterialButton _signupButton;
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setReturnTransition(new Explode());
        }

        setContentView(R.layout.activity_register);

        _nameText = findViewById(R.id.input_name);
        _nameTextLayout = findViewById(R.id.input_layout_username);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            _nameTextLayout.startAnimation(AnimationUtils.loadAnimation(
                    this.getApplicationContext(), R.anim.slide_from_right
            ));
        }

        _emailText = findViewById(R.id.input_email);
        _emailTextLayout = findViewById(R.id.input_layout_email);
        _passwordText = findViewById(R.id.input_password);
        _passwordTextLayout = findViewById(R.id.input_layout_password);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.logo).setTransitionName("logo");
            _emailText.setTransitionName("input_email");
            _emailTextLayout.setTransitionName("input_layout_email");
            _passwordText.setTransitionName("input_password");
            _passwordTextLayout.setTransitionName("input_layout_password");
            _signupButton.setTransitionName("btn_login");
            _loginLink.setTransitionName("link_sign_up");
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        _nameTextLayout.startAnimation(AnimationUtils.loadAnimation(
                                RegisterActivity.this.getApplicationContext(), R.anim.slide_to_left
                        ));
                    }

                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.registration_dialog));
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                if (output == null) {
                    onSignupFailed();
                } else {
                    onSignupSuccess();
                }
                progressDialog.dismiss();
            }

            @Override
            public String createRequest(String[] params) {
                JSONObject credentials = new JSONObject();
                try {
                    credentials.put("username", params[1]);
                    credentials.put("password", params[2]);
                    credentials.put("email", params[3]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return HttpConnectionFactory.createPostConnection(params[0], credentials);
            }
        });

        sync.execute(Constants.register_url, name, password, email);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    public void onSignupFailed() {
        View contextView = findViewById(R.id.register_scrollview);
        Snackbar.make(contextView, R.string.register_failed_message, Snackbar.LENGTH_SHORT).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 6) {
            _nameTextLayout.setError(getString(R.string.invalid_username_message));
            _nameTextLayout.setErrorEnabled(true);
            valid = false;
        } else {
            _nameText.setError(null);
            _nameTextLayout.setErrorEnabled(false);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailTextLayout.setError(getString(R.string.invalid_email_message));
            _emailTextLayout.setErrorEnabled(true);
            valid = false;
        } else {
            _emailTextLayout.setError(null);
            _emailTextLayout.setErrorEnabled(false);
        }

        if (password.isEmpty() || password.length() < 8) {
            _passwordTextLayout.setError(getString(R.string.invalid_new_password_message));
            _passwordTextLayout.setErrorEnabled(true);
            valid = false;
        } else {
            _passwordTextLayout.setError(null);
            _passwordTextLayout.setErrorEnabled(false);
        }

        return valid;
    }
}