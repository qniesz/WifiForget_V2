package net.niesz.wififorget.WifiBroadCastReceivers;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.plus.Plus;

import net.niesz.wififorget.DB.DbAdapter;
import net.niesz.wififorget.R;

import org.apache.http.util.ByteArrayBuffer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;





public class Bcr_WifiReceiver extends BroadcastReceiver implements 
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	Context Context ;
    private static boolean firstConnect = true;
	private static final int BUFFER_SIZE = 1024;
	static GoogleApiClient mGoogleApiClient;
	private static final String AppName = "wififorget";
	public static final String SD_DB_SAVE_FILE_LOCATION = Environment
			.getExternalStorageDirectory() + "/WifiForget/BackupDB.zip";
	protected static final File SD_DB_SAVE_FOLDER_LOCATION = new File(
			Environment.getExternalStorageDirectory(), AppName);
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent arg1) {
		Context = context;

		// //////////////////////////////////////////////////////////////////////////////////////////

		// check if it is coming from boot
		if (arg1.getAction() != null) {

			if (arg1.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

				// Set Alarm
				setalarm(context);

			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////

		if (arg1.getAction() == null) {
			// Log.i("view", arg1.getAction().toString());
			// Check for latest DB versions
			CheckDB(context);
			// Set Alarm
			// setalarm(context);

		}

		// Special code to force the onRecieve to only run once

		Context = context;
		final ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			if (firstConnect) {
				Log.i("view", "onRecieve");
				mainSub(Context);
				firstConnect = false;
			}
		} else {
			firstConnect = true;
		}

	}
	private void mainSub(Context context) {
		Log.i("view", "mainSub");
		WifiManager wm1 = (WifiManager) Context
				.getSystemService(android.content.Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wm1.getConnectionInfo();
		
	if(!"00:00:00:00:00:00".equals(wifiInfo.getBSSID())){
	
	

			DbAdapter mDbHelper = new DbAdapter(Context);
			mDbHelper.open();
		  if(wifiInfo.getBSSID()!=null){
			  
			  
			  //Check if the current SSID and BSSID match on in the DB
			  Cursor cur = mDbHelper.check_if_bssid_in_db(wifiInfo.getBSSID());

			  
			if(wm1.isWifiEnabled()){

				
			if (cur.getCount()<=0 && cur!=null){//SSID not in DB. 
				
				//Log.i("view",wifiInfo.getBSSID());
				try {
				    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				    Ringtone r = RingtoneManager.getRingtone(Context.getApplicationContext(), notification);
				    r.play();
				} catch (Exception e) {
				    e.printStackTrace();
				}
				
				Vibrator v = (Vibrator) context.getSystemService(android.content.Context.VIBRATOR_SERVICE);
				// Vibrate for 500 milliseconds
				
				
					long[] pattern = {0,500,500,500,500,500};
					v.vibrate(pattern,-1);
					   
				
				
				Log.i("view", "Start Notification");
				
				
				 NotificationManager nm = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			        // In this sample, we'll use the same text for the ticker and the expanded notification
			        CharSequence text = "WARNING: Unknown Wireless";
			        // Set the icon, scrolling text and timestamp
			        Intent intent = new Intent(context,net.niesz.wififorget.MainActivity.class );
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 intent.putExtra("BSSID", wifiInfo.getBSSID());
					 intent.putExtra("netid",wifiInfo.getNetworkId());
					 intent.putExtra("SSID",wifiInfo.getSSID());
					 intent.putExtra("ComingFrom", "Bbr_WifiReceiver");
			        
			        Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
			        // The PendingIntent to launch our activity if the user selects this notification
			        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			        // Set the info for the views that show in the notification panel.

			        
			        // TODO Removed for android 6
			        //notification.setLatestEventInfo(context, "Wifi Forget", text, contentIntent);

			        
			        // Send the notification.
			        // We use a layout id because it is a unique number.  We use it later to cancel.
			        //notification.flags=Notification.;
			        nm.notify(R.string.alert_started, notification);
				
				//Open App
			        /*
				 Intent i = new Intent(context.getApplicationContext(), net.niesz.wififorget.Main.MainActivity.class);
			       // i.setClassName("net.niesz", "net.niesz.wififorget.Main.MainActivity");
				 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 i.putExtra("BSSID", wifiInfo.getBSSID());
				 i.putExtra("SSID",wifiInfo.getSSID());
				 i.putExtra("ComingFrom", "Bbr_WifiReceiver");
			        context.startActivity(i);
			        */
			     ////////////////////////////////////////
			        
			        
			//	Log.i("view", "BSSID "+wifiInfo.getBSSID());
			//	  Log.i("view", "SSID "+ wifiInfo.getSSID());
			  
			  
			}else{
			//	Log.i("view",String.valueOf(cur.getCount()));
				mDbHelper.close();
				
				
			}
			mDbHelper.close();
			}
		  }
	}
		
	}
	private void CheckDB(Context context) {
		// TODO Auto-generated method stub
		
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		// Log.i("view", dtNOW.toString("MM-dd-yyyy HH:mm:ss"));

		String SyncOn = sp.getString("sync", "false");

		//Log.i("view", "SyncOn " + SyncOn);
		if ("true".equals(SyncOn)) {
			
			
			mGoogleApiClient = new GoogleApiClient.Builder(context)
			
			.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
			.addApi(Plus.API)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this).build();
			mGoogleApiClient.connect();
			
			
		}
		
	}
	//end onReceive
////////////////////////////Local Functions////////////////////////////
	
	
	

	
	public static void setalarm(Context context) {
		// Log.i("view", "setalarm callaed");
	
			// Log.i("view","alarm ON");
			Calendar calendar = Calendar.getInstance();

			// 12:59:59 PM
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);

			// long settime = calendar.getTimeInMillis();
			//Log.i("view", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
			// Log.i("view",String.valueOf(arg1));

			Intent i9 = new Intent(context, Bcr_WifiReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, i9,
					PendingIntent.FLAG_CANCEL_CURRENT);

			AlarmManager am = (AlarmManager) context
					.getSystemService(android.content.Context.ALARM_SERVICE);
			// am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 5000,
			// pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

		}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		restoreDBfromDrive();
		
	}
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}



private void restoreDBfromDrive() {
		
		

		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("MM-dd-yyyy HH:mm:ss");

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(Context);

		String lastchange = sp.getString("lastchange", "01-01-1980 00:00:00");

		final DateTime DeviceFileDateTime = formatter.parseDateTime(lastchange);
		//Log.i("view", "Device " + DeviceFileDateTime);

		
		  Query query = new Query.Builder().addFilter(
				  Filters.eq(SearchableField.TITLE, "BackupDB.zip")).build();
				  
				  Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback( new
				  ResultCallback<MetadataBufferResult>() {
					  
					@Override
					public void onResult(MetadataBufferResult result) {
						
						 // Log.i("view","Count: "+ result.getMetadataBuffer().getCount());

						if (result.getMetadataBuffer().getCount() == 1) {
							//Log.i("view", "Drive Has DB File");
							DateTimeFormatter formatter = DateTimeFormat
									.forPattern("EEE MMM dd HH:mm:ss zzz yyyy");
							DateTime DriveFileDateTime = formatter
									.parseDateTime(result.getMetadataBuffer()
											.get(0).getCreatedDate().toString());

							// Log.i("view","Drive "+DriveFileDateTime.toString());

							if (DriveFileDateTime.isAfter(DeviceFileDateTime
									.getMillis())) {
								//Log.i("view", "Drive has latest file");
								
								Toast.makeText(
										Context,
										"Restoring Google Drive File",
										Toast.LENGTH_SHORT).show();
								
								
								DriveId dID = result.getMetadataBuffer().get(0)
										.getDriveId();
								//DriveFile file = Drive.DriveApi.getFile(
								//		mGoogleApiClient, dID);



								// Log.i("view",file.getDriveId().toString());
								//RestoreData(file, DriveFileDateTime
								//		.toString("MM-dd-yyyy HH:mm:ss"));

							} else {

								//Log.i("view", "Device has latest file");

							}
						} else {
							//Log.i("view",
							//		"No File on Drive, or Error with File");
							Toast.makeText(
									Context,
									"No File on Drive, or Error with File, trying to fix issue now",
									Toast.LENGTH_SHORT).show();
//							saveFiletoDrive();

						}

						// .await();

						// file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY,
						// null).await();
						// if (!driveContentsResult.getStatus().isSuccess()) {
						// return null;
						// }

						// net.niesz.wififorget.Drive.S_Backup_Restore.unzip(file.,
						// net.niesz.wififorget.Drive.S_Backup_Restore.DATABASE_INTERNAL_DIRECTORY);

					}

					private void RestoreData(DriveFile file,
							final String restoreDate) {
						
						//Log.i("view", "Starting Restore Process");

						// DriveFile file =
						// Drive.DriveApi.getFile(mGoogleApiClient, dID);




						file.open(mGoogleApiClient,
								DriveFile.MODE_READ_ONLY, null)
								.setResultCallback(
										new ResultCallback<DriveContentsResult>() {

											@Override
											public void onResult(
													DriveContentsResult result) {

												if (result.getStatus()
														.isSuccess()) {

													DriveContents driveContents = result.getDriveContents();
															
													// driveContents.g
													BufferedInputStream bIS = new BufferedInputStream(
															driveContents
																	.getInputStream());

													ByteArrayBuffer baf = new ByteArrayBuffer(
															BUFFER_SIZE);
													File file = new File(
															SD_DB_SAVE_FILE_LOCATION);

													int current = 0;
													try {
														while ((current = bIS
																.read()) != -1) {
															baf.append((byte) current);
														}

														FileOutputStream fos = new FileOutputStream(
																file);
														fos.write(baf
																.toByteArray());
														fos.close();
														bIS.close();
													//	Log.i("view", result
													//			.getStatus()
													//			.toString());

														net.niesz.wififorget.Drive.S_Backup_Restore
																.unzip(SD_DB_SAVE_FILE_LOCATION,
																		net.niesz.wififorget.Drive.S_Backup_Restore.DATABASE_INTERNAL_DIRECTORY);

														SharedPreferences sp = PreferenceManager
																.getDefaultSharedPreferences(Context);

														Editor editor = sp
																.edit();

														editor.putString(
																"lastchange",
																restoreDate);

														editor.commit();

														//fillData();
														// BufferedReader reader
														// = new BufferedReader(
														// new
														// InputStreamReader(driveContents.getInputStream()));

													} catch (IOException e) {

														e.printStackTrace();
													} finally {
														// isImage = null;
														// urlImageCon = null;
														// urlImage = null;
													}

												} else {
													//Log.i("view", "failure");
												}

											}
										});
					}
				});

	
	}

}
