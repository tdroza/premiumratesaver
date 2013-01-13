package net.droza.android.premiumratesaver.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HistoryDBAdapter {

	private static final String DATABASE_NAME          = "prs.db";
	private static final String HISTORY_TABLE          = "history";
	public static final String KEY_HIST_ORIG_NUM       = "orig_number";
	public static final String KEY_HIST_ALT_NUM        = "alt_number";
	public static final String KEY_HIST_DESCRIPTION    = "description";
	public static final String KEY_HIST_DIALLED_NUM    = "dialled_num";
	public static final String KEY_HIST_SEARCH_DATE    = "search_date";
    public static final String KEY_HIST_LAST_DIAL_DATE = "last_dial_date";
    public static final String KEY_HIST_FAVE 		   = "favourite";
    public static final String KEY_HIST_ID             = "_id";
    
	private static final int DATABASE_VERSION = 1;
	private static final String LOGTAG = HistoryDBAdapter.class.getSimpleName();
	private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
	private final Context mCtx; 
	
	/**
     * Database creation sql statements
     */
    private static final String DATABASE_CREATE_HISTORY =
        "CREATE TABLE history (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "orig_number TEXT not null, " 
                + "alt_number TEXT not null, "
                + "description TEXT null,"
                + "dialled_number TEXT null,"
                + "search_date TIMESTAMP NOT NULL DEFAULT current_timestamp, " 
                + "last_dial_date TIMESTAMP null,"
                + "state text NOT NULL DEFAULT 'new'," 
                + "favourite INTEGER not null DEFAULT 0);";
	
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public HistoryDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the task database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public HistoryDBAdapter open() throws SQLException {
        mDbHelper = new DbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new task using the name provided. If the task is
     * successfully created return the new rowId for that task, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the task
     * @return rowId or -1 if failed
     */
    public long addHistory(String orig_num, String alt_num, String desc) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_HIST_ORIG_NUM, orig_num);
        initialValues.put(KEY_HIST_ALT_NUM, alt_num);
        initialValues.put(KEY_HIST_DESCRIPTION, desc);

        return mDb.insert(HISTORY_TABLE, null, initialValues);
    }
    
    /**
     * Delete the task with the given rowId
     * 
     * @param id id of task to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteHistory(long id) {
        return mDb.delete(HISTORY_TABLE, KEY_HIST_ID + "=" + id, null) > 0;
    }
    
    public boolean setFave(long id, boolean fave) {
        ContentValues args = new ContentValues();
        args.put(KEY_HIST_FAVE, fave);

        return mDb.update(HISTORY_TABLE, args, KEY_HIST_ID + "=" + id, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all tasks in the database
     * 
     * @return Cursor over all tasks
     */
    public Cursor fetchHistory(boolean showOnlyFaves) {
        return fetchHistory(KEY_HIST_SEARCH_DATE, "DESC", showOnlyFaves);
    }
    
    /**
     * Return a Cursor over the list of all tasks in the database, order by the specified parameters
     * 
     * @param orderKey The column name to sort by
     * @param orderDirection One of either ASC or DESC
     * @return Cursor over all tasks
     */
    public Cursor fetchHistory(String orderKey, String orderDirection, boolean showOnlyFaves) {
    	String orderBy = null;
    	if (orderKey != null && orderDirection != null) {
    		orderBy = orderKey + " " + orderDirection;
    	}
    	
    	String whereClause = "state != 'deleted'";
    	if (showOnlyFaves) {
    		whereClause += " AND favourite = 1";
    	}
    	
        Cursor cur = mDb.query(HISTORY_TABLE, new String[] {
        		KEY_HIST_ID, KEY_HIST_ORIG_NUM, KEY_HIST_ALT_NUM, 
        		KEY_HIST_DESCRIPTION, KEY_HIST_SEARCH_DATE, KEY_HIST_FAVE},
        		whereClause, null, null, null, orderBy);
        Log.v(LOGTAG, "" + cur.getCount());
        return cur;
    }
    /**
     * Return a Cursor positioned at the task that matches the given rowId
     * 
     * @param id id of task to retrieve
     * @return Cursor positioned to matching task, if found
     * @throws SQLException if task could not be found/retrieved
     */
    public Cursor fetchTask(long id) throws SQLException {
        Cursor mCursor =

                mDb.query(true, HISTORY_TABLE, new String[] {
                		KEY_HIST_ID, KEY_HIST_ORIG_NUM, KEY_HIST_ALT_NUM, 
                		KEY_HIST_DESCRIPTION, KEY_HIST_SEARCH_DATE, KEY_HIST_FAVE},
                		KEY_HIST_ID + "=" + id, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }       
    
    protected static class DbHelper extends SQLiteOpenHelper {

		DbHelper(Context context) {        	
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		    Log.w(LOGTAG, "Constructing DatabaseHelper");
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(LOGTAG, "Creating new database. Version " + DATABASE_VERSION);
		    db.execSQL(DATABASE_CREATE_HISTORY);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		    Log.w(LOGTAG, "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
	    	if (oldVersion < 2) {
	    		db.execSQL("ALTER TABLE task ADD COLUMN has_alarm NOT NULL DEFAULT 1");
	    	}
		    //db.execSQL("DROP TABLE IF EXISTS history");
	    	//onCreate(db);
		}
	}
}
