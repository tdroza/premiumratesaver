package net.droza.android.premiumratesaver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("NewApi")
public class StartPreferenceActivity extends Activity {

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		  
		 getFragmentManager().beginTransaction().replace(android.R.id.content,
				 new PrefsFragment()).commit();
	 }

	}