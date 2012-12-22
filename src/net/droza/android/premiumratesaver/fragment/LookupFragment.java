package net.droza.android.premiumratesaver.fragment;

import net.droza.android.premiumratesaver.CallUtils;
import net.droza.android.premiumratesaver.R;
import net.droza.android.premiumratesaver.db.HistoryDBAdapter;
import net.droza.android.premiumratesaver.io.LongRunningGetIO;
import net.droza.android.premiumratesaver.io.LongRunningGetIO.JSONListener;
import net.droza.android.premiumratesaver.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LookupFragment extends Fragment implements OnClickListener, JSONListener {
	
	EditText query;
	TextView result;
	ViewGroup resultFrame;
	TextView description;
	Button search;
	Button dial;
	private ProgressDialog dialog;
	private HistoryDBAdapter mDbHelper;
	
	private static final String LOGTAG = LookupFragment.class.toString();

    public LookupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	setMenuVisibility(false);
    	mDbHelper = new HistoryDBAdapter(inflater.getContext());
    	mDbHelper.open();

    	View view =  inflater.inflate(R.layout.fragment_lookup, container, false);
    	
    	search = (Button) view.findViewById(R.id.search_btn);
    	search.setOnClickListener(this);

    	dial = (Button) view.findViewById(R.id.dial_btn);
    	dial.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CallUtils.dial(view.getContext(), result.getText().toString());
			}
		});
    	    	
    	query = (EditText)view.findViewById(R.id.queryText);
    	
    	query.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
    	        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
    	          // Perform action on key press
    	          search.performClick();
    	          return true;
    	        }
    	        return false;
			}
		});
    	
    	Intent i = getActivity().getIntent();
    	String queryParam = i.getStringExtra(Constants.QUERY_TEXT);
    	if (queryParam != null && queryParam.length() > 0) {
    		query.setText(queryParam);
    		search.performClick();
    	}
    	result = (TextView) view.findViewById(R.id.resultText);
    	resultFrame = (LinearLayout) view.findViewById(R.id.resultFrame);
    	resultFrame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Context ctx = v.getContext();
				ClipboardManager clipboard = (ClipboardManager)
				        ctx.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Alternative telephone number", result.getText());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(ctx, "Copied to clipboard", Toast.LENGTH_SHORT).show();
			}
		});
    	description = (TextView)view.findViewById(R.id.descriptionText);
    	return view;
    }
    
    @Override
    public void onClick(View view) {    	
	    search.setEnabled(false);
	    dial.setEnabled(false);

	    hideKeypad(view.getContext());
	    
	    dialog = ProgressDialog.show(getActivity(), "Searching...", "Finding an alternative number");
	    new LongRunningGetIO(query.getText().toString(), this).execute();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
    	menu.clear();
    } 

	@Override
	public void onRemoteCallComplete(JSONObject json) {		
		Log.v(LOGTAG, "received response: " + json);
		if (json != null) {
			try {
				JSONObject number = json.getJSONObject(Constants.ROOT_NODE);
				String resultText = number.getString(Constants.ALT_NUMBER);
				String desc = number.getString(Constants.DESCRIPTION);
				result.setText(resultText);
				description.setText(desc);
				if (!query.getText().toString().equals(result.getText().toString())) {
					mDbHelper.addHistory(number.getString(Constants.ORIG_NUMBER), resultText, desc);
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
	
					dial.setEnabled(true);
					if (prefs.getBoolean(Constants.KEY_AUTODIAL, false)) {
						CallUtils.dial(getView().getContext(), resultText);
					}
				}
			} catch (JSONException e) {
				Log.e(LOGTAG, e.getMessage());
				description.setText("Could not find alternative number");
			}
		}
		search.setEnabled(true);	
		dialog.cancel();
	}
	
	private void hideKeypad(Context ctx) {
	    InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
	}
}
