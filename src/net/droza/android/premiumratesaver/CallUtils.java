package net.droza.android.premiumratesaver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class CallUtils {
	private static final String LOGTAG = CallUtils.class.toString();
    
    public static void dial(Context ctx, String number) {
    	Log.v(LOGTAG, "Dialling: " + number);
     	ctx.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
    }
}
