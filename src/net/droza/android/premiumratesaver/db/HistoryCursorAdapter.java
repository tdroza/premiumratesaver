package net.droza.android.premiumratesaver.db;

import net.droza.android.premiumratesaver.CallUtils;
import net.droza.android.premiumratesaver.R;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryCursorAdapter extends SimpleCursorAdapter {
	private HistoryDBAdapter mDbHelper;
	private Context ctx;
	private int layout;

	public HistoryCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
		this.ctx = context;
		mDbHelper = new HistoryDBAdapter(context);
    	mDbHelper.open();
	}

	@Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Cursor c = getCursor();
        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, parent, false);
        setTextFromDB(c, v, HistoryDBAdapter.KEY_HIST_DESCRIPTION, R.id.history_description);
        setTextFromDB(c, v, HistoryDBAdapter.KEY_HIST_ORIG_NUM, R.id.history_orig_num);
        setTextFromDB(c, v, HistoryDBAdapter.KEY_HIST_ALT_NUM, R.id.history_alt_num);
        setTextFromDB(c, v, HistoryDBAdapter.KEY_HIST_SEARCH_DATE, R.id.history_timestamp);
        
        int fave = c.getInt(c.getColumnIndex(HistoryDBAdapter.KEY_HIST_FAVE));
        CheckBox cbFave = (CheckBox) v.findViewById(R.id.history_fave);
        cbFave.setChecked(fave > 0);
        return v;
    }
	
	private void setTextFromDB(Cursor c, View v, String dbField, int id) {
        String orig = c.getString(c.getColumnIndex(dbField));
        TextView orig_text = (TextView) v.findViewById(id);
        if (orig_text != null) {
            orig_text.setText(orig);
        }
	}

	@Override
    public void bindView(View view, Context context, Cursor cursor){
        final int id = cursor.getInt(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ID));
        final String altNum = cursor.getString(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ALT_NUM));
        final Context ctx = context;
        
        Button btn = (Button)view.findViewById(R.id.history_call);
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.v("NUMBER", altNum);
				CallUtils.dial(ctx,  altNum);
			}
		});

        CheckBox cb = (CheckBox)view.findViewById(R.id.history_fave);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	Log.v("CHECKED", ""+ id + ":" + isChecked);
            	mDbHelper.setFave(id, isChecked);
            }

        });
    }
	
}
