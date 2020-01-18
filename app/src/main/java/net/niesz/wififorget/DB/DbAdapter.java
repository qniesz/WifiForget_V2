package net.niesz.wififorget.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DbAdapter {
	/**
	 * Stings for P_Calendar
	 * 
	 * Strings
	 */
	// ROW_ID for all tables
	//	public static final String KEY_EVENT_DATE = "event_date";


	public static final String DATABASE_NAME = "wifi_forget";
	private static final String DATABASE_TABLE1 = "wifidb";

	private static final int DATABASE_VERSION = 1;
	
	// ///////////////////////////////////////////////////////////////////////
	
	private static DatabaseHelper mDbHelper;
	private static  SQLiteDatabase mDb;
		private static Context mCtx;


	 
	/**
	  * Constructor
	  * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	  *
	  */
	/////////////////////////////////////////////////////////////////////////////////
	private static class DatabaseHelper extends SQLiteOpenHelper {
		 //private final Context myContext;
		 //private SQLiteDatabase myDataBase; 
		

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mCtx=context;
		}

		@Override
		public void onCreate(SQLiteDatabase mDb) {
		
			
			
		}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// //////////////////////////////////////////////////////////////////////
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}// DatabaseHelper

	
	public static String[]ALL_COLUMN_KEYS =new String[]{
		
		
		
		
		
};

	public DbAdapter(Context ctx) {
		mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {

		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  ////////////////////////////////////////Funtions////////////////////////////////////////////////////////////////////////
	  
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public static long insertSSID(String SSID,String BSSID){
ContentValues initialValues = new ContentValues();

// event_date = "2013-02-26 00:00:00";

// notes = "notes";
initialValues.put("ssid", SSID);
initialValues.put("bssid", BSSID);
return mDb.insert(DATABASE_TABLE1, null, initialValues);
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Cursor check_if_bssid_in_db(String bssid) throws SQLException {

		try {
			Cursor mCursor = mDb.rawQuery(
					"SELECT *" +

					"FROM "+DATABASE_TABLE1+" " + 
					"WHERE bssid ="+
					" '" + bssid + "'",

					null);

			if (mCursor != null) {

				mCursor.moveToFirst();
				return mCursor;
			}
			return mCursor;
		} catch (SQLException e) {
			return null;
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Cursor returnAll() throws SQLException {

		try {
			Cursor mCursor = mDb.rawQuery("SELECT *" +

			"FROM " + DATABASE_TABLE1 ,

			null);

			if (mCursor != null) {

				mCursor.moveToFirst();
				return mCursor;
			}
			return mCursor;
		} catch (SQLException e) {
			return null;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean  deleteRow(Long id) {
		 
		return mDb.delete(DATABASE_TABLE1, "_id" + "=" + id,null) >0;

		
	}
	  
	} 