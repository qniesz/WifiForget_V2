package net.niesz.wififorget.Drive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



public class S_Backup_Restore {
 
	//public static final String TAG = DbExportImport.class.getName();
 
	/** Directory that files are to be read from and written to on SDCard**/
	
	public static final String AppName= "wififorget";
	
	
	
	protected static final File DATABASE_DIRECTORY =
		new File(Environment.getExternalStorageDirectory(),AppName);
	

	/** File path of Db to be imported **/
	//protected static final File IMPORT_FILE =
	//	new File(DATABASE_DIRECTORY,DbAdapter.DATABASE_NAME);
 
	public static final String PACKAGE_NAME = "net.niesz.wififorget";
	//public static final String DATABASE_NAME = DbAdapter.DATABASE_NAME;
	//public static final String DATABASE_TABLE = "Reports";
	private static final File DB_DIRECTORY =
		new File(Environment.getDataDirectory() +
		"/data/" + PACKAGE_NAME +
		"/databases/");
	
	
	public static final String DATABASE_INTERNAL_DIRECTORY=
				Environment.getDataDirectory()+
				"/data/" + PACKAGE_NAME +
				"/databases/";	
	
	public static final String DATABASE_EXTERNAL_DIRECTORY=
			Environment.getExternalStorageDirectory() + "/WifiForget/";
	
	
	//static final DateTime dtToday = DateTime.now();
	
	
	public static final String DATABASE_EXTERNAL_FOLDER=
			Environment.getExternalStorageDirectory() + "/WifiForget/";
	private static final int BUFFER_SIZE = 1024;
	
	
 
	/** Saves the application database to the
	 * export directory under MyDb.db **/
	protected  static boolean exportDb(Context context){
		//Log.i("view","BackupDB");
		
		

		if( ! SdIsPresent() ) return false;
		
		File importDir = DB_DIRECTORY;
		File exportDir = DATABASE_DIRECTORY;
		//Log.i("view","import folder "+ String.valueOf(importDir));
		//Log.i("view","export folder "+ String.valueOf(exportDir));
 
		DeleteRecursive(exportDir);
		if (!exportDir.exists()) {
			
			exportDir.mkdirs();
		}
 
		try {
			DeleteRecursive(exportDir);
			copyDirectory(importDir,exportDir); 
			//Toast.makeText(context, "Data has been backed up", Toast.LENGTH_LONG).show();		

			return true;
		} catch (IOException e) {
		
			e.printStackTrace();
			return false;
		}
	}
 
	/** Replaces current database with the IMPORT_FILE if
	 * import database is valid and of the correct type **/
	protected static boolean restoreDb(Context context){
		//Log.i("view","RestoreDB");
		
		if( ! SdIsPresent() ) return false;

		File importDir = DATABASE_DIRECTORY;
		File exportDir = DB_DIRECTORY;
		//Log.i("view","import folder "+ String.valueOf(importDir));
		//Log.i("view","export folder "+ String.valueOf(exportDir));
		
		if (!importDir.exists()) {
		//	Toast.makeText(context, "No data files found to restore", Toast.LENGTH_LONG).show();
			return false;
		}
 
		if (!exportDir.exists()) {
			DeleteRecursive(exportDir);
			exportDir.mkdirs();
		}
		try {
			 
			
			copyDirectory(importDir,exportDir);
			
			//Toast.makeText(context, "Data has been restored", Toast.LENGTH_LONG).show();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
			
		}
		
	}
 /*
	 //Imports the file at IMPORT_FILE 
	protected static boolean importIntoDb(Context ctx){
		if( ! SdIsPresent() ) return false;
 
		File importFile = IMPORT_FILE;
 
		if( ! checkDbIsValid(importFile) ) return false;
 
		try{
			SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase
					(importFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
 
			Cursor cursor = sqlDb.query(true, DATABASE_TABLE,
		    		null, null, null, null, null, null, null
		    );
 
			DbAdapter dbAdapter = new DbAdapter(ctx);
			dbAdapter.open();
 
			final int titleColumn = cursor.getColumnIndexOrThrow("title");
			final int timestampColumn = cursor.getColumnIndexOrThrow("timestamp");
 
			// Adds all items in cursor to current database
			cursor.moveToPosition(-1);
			while(cursor.moveToNext()){
				dbAdapter.createQuote(
						cursor.getString(titleColumn),
						cursor.getString(timestampColumn)
				);
			}
 
			sqlDb.close();
			cursor.close();
			dbAdapter.close();
		} catch( Exception e ){
			e.printStackTrace();
			return false;
		}
 
		return true;
	}
 */
	
 

 
	/** Returns whether an SD card is present and writable **/
	public static boolean SdIsPresent() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	public static void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	// If targetLocation does not exist, it will be created.
	public static  void copyDirectory(File sourceLocation , File targetLocation)
	throws IOException {

	    if (sourceLocation.isDirectory()) {
	        if (!targetLocation.exists() && !targetLocation.mkdirs()) {
	            throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
	        }

	        String[] children = sourceLocation.list();
	        for (int i=0; i<children.length; i++) {
	            copyDirectory(new File(sourceLocation, children[i]),
	                    new File(targetLocation, children[i]));
	        }
	    } else {

	        // make sure the directory we plan to store the recording in exists
	        File directory = targetLocation.getParentFile();
	        if (directory != null && !directory.exists() && !directory.mkdirs()) {
	            throw new IOException("Cannot create dir " + directory.getAbsolutePath());
	        }

	        InputStream in = new FileInputStream(sourceLocation);
	        OutputStream out = new FileOutputStream(targetLocation);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	        	
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }
	    
	}
	
	 
	public static void zip(Context context,ArrayList<String>  files, String zipFileSaveLocation, String sNumberOfBackups,String sBackupLocation) throws IOException {
		
		

		File temp = new File(DATABASE_DIRECTORY.toString());

		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sp.edit();
		DateTime dtToday = DateTime.now();
		
		editor.putString("lastsavedate", dtToday.toString("YYYY-MM-dd"));
		editor.commit();
		
		
		
		
		
		
	if (!temp.exists()) {// Check if Direcotry Exists
		//Log.i("view", "external Location: "+ DATABASE_DIRECTORY.toString());
			temp.mkdirs();
			//Log.i("view", "external Location: "+ DATABASE_DIRECTORY.toString()); 
		}
		
		
		
	
	    BufferedInputStream origin = null;

	    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileSaveLocation)));
	    try {
			byte[] data = new byte[BUFFER_SIZE];

	        for (int i = 0; i < files.size(); i++) {
	            FileInputStream fi = new FileInputStream(files.get(i));    
	            origin = new BufferedInputStream(fi, BUFFER_SIZE);
	            try {
	                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
	                out.putNextEntry(entry);
	                int count;
	                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
	                    out.write(data, 0, count);
	                }
	            }
	            finally {
	                origin.close();
	                
	            }
	        }
	    }
	    finally {
	        out.close();
	    }
	    

	    DeleteOldFiles(sNumberOfBackups,zipFileSaveLocation,sBackupLocation);
	   
	}

	@SuppressWarnings("unchecked")
	private static void DeleteOldFiles(String sNumberOfBackups, String sTarget,
			String sBackupLocation) {

		int iNumOfBack = Integer.parseInt(sNumberOfBackups);
//////////////////////////////////////Local//////////////////////////////////////////////////////
		if ("Local".equals(sBackupLocation)) {
			File target = new File(sTarget);
			File[] fTarget = target.getParentFile().listFiles();
			// Log.i("view", "count "+ fTarget.listFiles().toString());

			// Sort Files by Date
			Arrays.sort(fTarget, new Comparator() {
				public int compare(Object o1, Object o2) {

					if (((File) o1).lastModified() > ((File) o2).lastModified()) {
						return -1;
					} else if (((File) o1).lastModified() < ((File) o2)
							.lastModified()) {
						return +1;
					} else {
						return 0;
					}
				}

			});

			// Delete Anything older then the Number of Backups Shared Pref
			int count = 0;
			for (File file2 : fTarget) {
				if (count >= iNumOfBack) {
					file2.delete();

				}

				count++;

			}
		}// End If
		///////////////////////////////Google Drive////////////////////////////////
		
		
		
		
		
		
	}// End Delete Files

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Unzip a zip file.  Will overwrite existing files.
	 * 
	 * @param zipFile Full path of the zip file you'd like to unzip.
	 * @param location Full path of the directory you'd like to unzip to (will be created if it doesn't exist).
	 * @throws IOException
	 */
	public static void unzip(String zipFile, String location) throws IOException {
	    int size;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    	File dir = new File(location);
	    	
	    	//Log.i("view", "zip Location "+ zipFile);
	    	//Log.i("view", "InternalDB "+location);
	    	DeleteRecursive(dir);
	   
	    	
	    	

	    	
	    	
	    try {

	    	
	    	
	        if ( !location.endsWith("/") ) {
	            location += "/";
	        } 	
	       // Log.i("view", location.toString());
	   // File exportDir = location;
	        
	        File f = new File(location);
	        if(!f.isDirectory()) {
	            f.mkdirs();
	        }
	        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
	        try {
	            ZipEntry ze = null;
	            while ((ze = zin.getNextEntry()) != null) {
	                String path = location + ze.getName();
	                File unzipFile = new File(path);

	                if (ze.isDirectory()) {
	                    if(!unzipFile.isDirectory()) {
	                        unzipFile.mkdirs();
	                    }
	                } else {
	                    // check for and create parent directories if they don't exist
	                    File parentDir = unzipFile.getParentFile();
	                    if ( null != parentDir ) {
	                        if ( !parentDir.isDirectory() ) {
	                            parentDir.mkdirs();
	                        }
	                    }

	                    // unzip the file
	                    FileOutputStream out = new FileOutputStream(unzipFile, false);
	                    BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
	                    try {
	                        while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
	                            fout.write(buffer, 0, size);
	                        }

	                        zin.closeEntry();
	                    }
	                    finally {
	                        fout.flush();
	                        fout.close();
	                    }
	                }
	            }
	        }
	        finally {
	            zin.close();
	        }
	    }
	    catch (Exception e) {
	        //Log.e(TAG, "Unzip exception", e);
	    }
	    
	  
	}
	


}