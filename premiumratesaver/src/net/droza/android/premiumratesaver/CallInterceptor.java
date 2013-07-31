package net.droza.android.premiumratesaver;

import net.droza.android.premiumratesaver.util.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class CallInterceptor extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

		final String origNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER); 
		if (prefs.getBoolean(Constants.ENABLE_KEY, false) && (
				(prefs.getBoolean(Constants.KEY_FOUR, false)  && origNumber.startsWith("084")) || 
				(prefs.getBoolean(Constants.KEY_SEVEN, false) && origNumber.startsWith("087")) || 
				(prefs.getBoolean(Constants.KEY_ZERO, false) && origNumber.startsWith("080"))  ||
				(prefs.getBoolean(Constants.KEY_NINE, false) && origNumber.startsWith("09")) )) {
			Toast.makeText(ctx, "Premium Rate number detected", Toast.LENGTH_LONG).show();
			Intent i = new Intent();
	        i.setClassName("net.droza.android.premiumratesaver", "net.droza.android.premiumratesaver.MainActivity");
	        i.putExtra(Constants.QUERY_TEXT, origNumber);
	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        ctx.startActivity(i);
			setResultData(null);
		} else {
			setResultData(origNumber);
		}
		
	}

}
