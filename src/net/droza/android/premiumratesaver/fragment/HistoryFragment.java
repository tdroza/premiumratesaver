package net.droza.android.premiumratesaver.fragment;

import net.droza.android.premiumratesaver.R;
import net.droza.android.premiumratesaver.db.HistoryCursorAdapter;
import net.droza.android.premiumratesaver.db.HistoryDBAdapter;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A fragment to display search history rows
 */
public class HistoryFragment extends ListFragment {
	private HistoryDBAdapter mDbHelper;
	private boolean showFaves = false;
	private static final String LOGTAG = HistoryFragment.class.toString();

	public HistoryFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
	}	
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		super.onListItemClick(list, v, position, id);

		Toast.makeText(getActivity(), "Click. Pos:" + position + "; id:" + id,
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		mDbHelper = new HistoryDBAdapter(inflater.getContext());
		mDbHelper.open();

		View view = inflater.inflate(R.layout.history_list, container, false);
		refreshHistory(inflater.getContext());

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.history, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.showFaves:
			showFaves = !showFaves;
			if (showFaves) {
				item.setIcon(R.drawable.ic_menu_star_selected);
			} else {
				item.setIcon(R.drawable.ic_menu_star);
			}
			Log.v(LOGTAG, "Refreshing with faves: " + showFaves);

			refreshHistory(getView().getContext(), showFaves);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshHistory(Context ctx) {
		refreshHistory(ctx, false);
	}

	private void refreshHistory(Context ctx, boolean showOnlyFaves) {
		// Get all of the tasks from the database and create the item list
		Cursor cursor = mDbHelper.fetchHistory(showOnlyFaves);
		getActivity().startManagingCursor(cursor);

		String[] from = new String[] { HistoryDBAdapter.KEY_HIST_ORIG_NUM,
				HistoryDBAdapter.KEY_HIST_ALT_NUM,
				HistoryDBAdapter.KEY_HIST_DESCRIPTION,
				HistoryDBAdapter.KEY_HIST_SEARCH_DATE };
		int[] to = new int[] { R.id.history_orig_num, R.id.history_alt_num,
				R.id.history_description, R.id.history_timestamp };

		// Now create an array adapter and set it to display using our row
		HistoryCursorAdapter history = new HistoryCursorAdapter(ctx,
				R.layout.history_row, cursor, from, to);
		setListAdapter(history);
	}
	
		
}
