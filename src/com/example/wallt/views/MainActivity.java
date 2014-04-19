package com.example.wallt.views;

import java.util.HashMap;

import com.example.wallt.R;
import com.example.wallt.R.anim;
import com.example.wallt.R.id;
import com.example.wallt.R.layout;
import com.example.wallt.presenters.ServerUtility;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {

	private ServerUtility server;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		server = ServerUtility.getInstance();
		Fragment fragment = new AccountActivity();
		fragmentManager = getFragmentManager();
	    FragmentTransaction fragmentTransaction =
	    		fragmentManager.beginTransaction();
	    fragmentTransaction.replace(R.id.container, fragment, null);
	    fragmentTransaction.addToBackStack(null);
	    fragmentTransaction.commit();
	    ParseCloud.callFunctionInBackground("hello",
	    		new HashMap<String, Object>(),
	    		new FunctionCallback<String>() {
	    	  	public void done(final String result,
	    			  final ParseException e) {
	    	    if (e == null) {
	    	    	System.out.println(result);
	    	    }
	    	  }
	    	});
	    PushService.subscribe(this, ParseUser.getCurrentUser().
	    			getObjectId(), MainActivity.class);
	}
	
	public void addFragment(final Fragment fragment, final String tag) {
	    FragmentManager fragmentManager = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.
	    		beginTransaction();
	    fragmentTransaction.setCustomAnimations(R.anim.no_anim_show,
	    		R.anim.no_anim,
	    		R.anim.no_anim_show, R.anim.slide_right_away);
	    fragmentTransaction.replace(R.id.container, fragment, tag);
	    fragmentTransaction.addToBackStack(tag);
	    fragmentTransaction.commit();
	}

	public void finishFragment() {
		if (getFragmentManager().getBackStackEntryCount() > 1) {
	    	getFragmentManager().popBackStack();
	    }
	}

	public void onBackPressed() {
	    if (getFragmentManager().getBackStackEntryCount() > 1) {
	    	finishFragment();
	    } else {
	    	finish();
	    }
	}

	public void returnToLogin() {
		server.logOutUser();
		Intent i = new Intent(MainActivity.this, LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.
				FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}
}
