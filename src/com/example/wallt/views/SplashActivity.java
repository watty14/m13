package com.example.wallt.views;

import com.example.wallt.R;
import com.example.wallt.R.layout;
import com.example.wallt.presenters.ServerUtility;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

    private ServerUtility server;

    @Override
	public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        server = ServerUtility.getInstance();
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 1000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent i = null;
                    if (server.isAlreadyLoggedIn()) {
                        i = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        i = new Intent(SplashActivity.this,
                                LoginActivity.class);
                    }
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        };
        splashThread.start();
    }
}
