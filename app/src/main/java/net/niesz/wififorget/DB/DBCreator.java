package net.niesz.wififorget.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;



public class DBCreator extends SQLiteOpenHelper{
	
	
	//private static final String DATABASE_PATH="/data/data/net.niesz.periodjournaltracker/databases/";
	
	
	private static final String DATABASE_NAME= "wifi_forget";
	private static final int 	SCHEMA_VERSION = 1;
	
	
	public SQLiteDatabase dbSqlite;
	
	private final Context myContext;
	
	private static final String DATABASE_PATH="/data/data/net.niesz.wififorget/databases/";
	private static final String databasepath = DATABASE_PATH + DATABASE_NAME;
	
	public DBCreator(Context context){
		super(context, DATABASE_NAME,null, SCHEMA_VERSION);
		this.myContext = context;
		
		//Log.i("view","db Path: "+myContext.getDatabasePath(DATABASE_NAME));
		//Log.i("view","db Path: "+DATABASE_PATH);
	}

	
@Override
public void onCreate(SQLiteDatabase db){
	
}
	
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
	
}
		
	public void createDatabase(){
		createDB();
	}
	
	private void createDB(){
		
		
		if(!doesDatabaseExist(myContext, databasepath)){
			//Log.i("view","testDB");
			this.getReadableDatabase();
			
			
			copyDBFromResource();
		}
		
		//boolean dbExist = DBExists();
		
		//if(!dbExist){
		//	Log.i("view","exist "+ dbExist);
			
			//this.getReadableDatabase();
			
			
			//copyDBFromResource();
		//}
		
		
	}
		
	private static boolean doesDatabaseExist(Context context, String dbName) {
	    File dbFile = context.getDatabasePath(dbName);
	    return dbFile.exists();
	}
		
		private boolean DBExists(){
			
			SQLiteDatabase db = null;
			
			try{
				
				
				
				db = SQLiteDatabase.openDatabase(databasepath, null, SQLiteDatabase.OPEN_READONLY);
				
				//Log.i("view","db Path: "+databasepath);
				db.setLocale(Locale.getDefault());
				db.setLockingEnabled(true);
				db.setVersion(1);
				db.close();
				
			}catch (SQLiteException e){
			//	Log.e("SqlHelper", "database not found");
			}
			
			if (db!=null){
				db.close();
			}
					
			return db !=null ? true:false;
			
			
		}
		
		private void copyDBFromResource(){
			
			
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String dbFilePath = DATABASE_PATH+ DATABASE_NAME;
			
			
			try {
				
				
				inputStream = myContext.getAssets().open(DATABASE_NAME);
				
				outputStream = new FileOutputStream(dbFilePath);
				
				byte[] buffer =  new byte[1024];
				int length;
				while ((length = inputStream.read(buffer))>0) {
					outputStream.write(buffer,0,length);
				}
				
				
				outputStream.flush();
				outputStream.close();
				inputStream.close();
				
				
				
			}catch(IOException e){
				throw new Error("Problem coying database from resource file.");
			}
			
			
			
			
			
			
		}
		
		
		 @Override
		 public synchronized void close() {
		  
		 if(dbSqlite != null)
			 dbSqlite.close();
		  
		 super.close();
		
		
	
		 }
	
	
	
	
	
}
