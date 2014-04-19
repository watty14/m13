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

public class LoginActivity extends Activity {

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mSignupButton;
    private ProgressBar mProgressBar;
    private ServerUtility server;
    private String username;
    private String password;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        server = ServerUtility.getInstance();
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignupButton = (Button) findViewById(R.id.signup_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
               username = mUsernameField.getText().toString();
                password = mPasswordField.getText().toString();
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Invalid Input",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new AsyncTaskLogInUser().execute(username, password);
                    mSignupButton.setVisibility(View.INVISIBLE);
                    mLoginButton.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private class AsyncTaskLogInUser extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(final String... params) {
            String username = params[0];
            String password = params[1];
            return server.logInUser(username, password);
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            if (result) {
                SharedPreferences settings = getSharedPreferences(
            			getString(R.string.preferences_table),
        				MODE_PRIVATE);
        		Editor edit = settings.edit();
        		edit.putString(getString(R.string.
        				preferences_username),
        				server.getCurrentUsername());
        		edit.putString(getString(R.string.
        				preferences_email),
        				server.getCurrentEmail());
        		edit.apply();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoginButton.setVisibility(View.VISIBLE);
                mSignupButton.setVisibility(View.VISIBLE);
                mPasswordField.setText("");
                Toast.makeText(LoginActivity.this, "Log in failed! Try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}

