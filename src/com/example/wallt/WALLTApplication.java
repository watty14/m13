package com.example.wallt;

import com.example.wallt.presenters.ServerUtility;

import android.app.Application;

public class WALLTApplication extends Application {

    private ServerUtility server;
    public static WALLTApplication Instance = null;

    public void onCreate() {
        server = ServerUtility.getInstance();
        server.initalize(this);
        Instance = this;
    }
}
