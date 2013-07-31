package net.droza.android.premiumratesaver.fragment;

import net.droza.android.premiumratesaver.R;
import net.droza.android.premiumratesaver.db.HistoryCursorAdapter;
import net.droza.android.premiumratesaver.db.HistoryDBAdapter;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.timroes.swipetodismiss.SwipeDismissList;
import de.timroes.swipetodismiss.SwipeDismissList.SwipeDirection;
import de.timroes.swipetodismiss.SwipeDismissList.Undoable;

/**
 * A fragment to display search history rows
 */
public class HistoryFragment extends SherlockListFragment {
	private HistoryDBAdapter mDbHelper;
	
	private SwipeDismissList mSwipeList;
	private boolean showFaves = false;
	private static final String LOGTAG = HistoryFragment.class.getSimpleName();

	public HistoryFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		
		ListView listView = (ListView)getView().findViewById(android.R.id.list);
		
		mSwipeList = new SwipeDismissList(
				listView,
				new SwipeDismissList.OnDismissCallback() {
					@Override
					public SwipeDismissList.Undoable onDismiss(AbsListView listView, final int position) {
						// Get the swiped item
						final SQLiteCursor cursor = (SQLiteCursor)listView.getItemAtPosition(position);
						Log.w("DELETE", "" + cursor.getString(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_DESCRIPTION)));

						mDbHelper.stageHistoryForDelete(cursor.getLong(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ID)));
						refreshHistory(getView().getContext(), showFaves);	
						
						return new SwipeDismissList.Undoable() {
							@Override
							public String getTitle() {
								return "Deleted " + cursor.getString(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_DESCRIPTION));
							}
							
							@Override
							public void undo() {
								// Reinsert the item at its previous position.
								mDbHelper.undoDeleteHistory(cursor.getLong(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ID)));
								refreshHistory(getView().getContext(), showFaves);
								Log.w("Undo", "" + cursor.getLong(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ID)));
							}
							
							// Delete from DB
							@Override
							public void discard() {
								// Just write a log message (use logcat to see the effect)
								Log.w("DISCARD", "item now finally discarded");
								mDbHelper.deleteHistory(cursor.getLong(cursor.getColumnIndex(HistoryDBAdapter.KEY_HIST_ID)));
							}
						};
						
					}
					
				},
				SwipeDismissList.UndoMode.SINGLE_UNDO);
		mSwipeList.setAutoHideDelay(3000);
	}	
	
	@Override
	public void onListItemClick(ListView list, View v, int position, long id) {
		super.onListItemClick(list, v, position, id);
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
	
	@Override
	public void onStop() {
		super.onStop();
		// Throw away all pending undos.
		mSwipeList.discardUndo();
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
		int[] to = new int[] { R.id.history_orig_num, 
				R.id.history_alt_num,
				R.id.history_description, 
				R.id.history_timestamp };

		// Now create an array adapter and set it to display using our row
		HistoryCursorAdapter history = new HistoryCursorAdapter(ctx,
				R.layout.history_row, cursor, from, to);
		setListAdapter(history);
	}
}
