package com.example.wallt.views;

import com.example.wallt.R;
import com.example.wallt.R.id;
import com.example.wallt.R.layout;
import com.example.wallt.R.string;
import com.example.wallt.presenters.ServerUtility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignupActivity extends Activity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mVerifyField;
    private EditText mEmailField;
    private Button mSignupButton;
    private ProgressBar mProgressBar;
    private ServerUtility server;

    @Override
	protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        server = ServerUtility.getInstance();
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mSignupButton = (Button) findViewById(R.id.signup_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mVerifyField = (EditText) findViewById(R.id.verifypassword_field);
        mEmailField = (EditText) findViewById(R.id.email_field);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String username = mUsernameField.getText().toString();
                String password = mPasswordField.getText().toString();
                String verify = mVerifyField.getText().toString();
                String email = mEmailField.getText().toString();
                if (username.equals("") || password.equals("")
                        || verify.equals("") || email.equals("")) {
                    Toast.makeText(SignupActivity.this, "Invalid Input",
                            Toast.LENGTH_SHORT).show();
                } else if (!password.equals(verify)) {
                    mPasswordField.setText("");
                    mVerifyField.setText("");
                    Toast.makeText(SignupActivity.this,
                        "Passwords Do Not Match",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncTaskSignupUser().execute(username,
                        password, email);
                    mSignupButton.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class AsyncTaskSignupUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(final String... params) {
            String username = params[0];
            String email = params[2];
            String password = params[1];
            return server.signUpUser(username, email, password);
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            if (result) {
            	SharedPreferences settings = getSharedPreferences(
            			getString(R.string.preferences_table), 
        				MODE_PRIVATE);
        		Editor edit = settings.edit();
        		edit.putString(getString(R.string.preferences_username),
        				server.getCurrentUsername());
        		edit.putString(getString(R.string.preferences_email),
        				server.getCurrentEmail());
        		edit.apply();
                Intent i = new Intent(SignupActivity.this,
                        MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mSignupButton.setVisibility(View.VISIBLE);
                mPasswordField.setText("");
                mVerifyField.setText("");
                Toast.makeText(SignupActivity.this,
                        "Sign up failed! Try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
