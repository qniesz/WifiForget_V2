package net.niesz.wififorget;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.plus.Plus;

import net.niesz.wififorget.DB.DBCreator;
import net.niesz.wififorget.DB.DbAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends ListActivity implements

GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int FIRST = Menu.FIRST;
    private static String BSSID, SSID, ComingFrom = null;
    private static int iNetId;
    // private AdView mAdView;
    private static GoogleApiClient mGoogleApiClient;
    SimpleCursorAdapter adapter;
    // protected static final int REQUEST_CODE_RESOLUTION = 1;
    // private static final String TAG = "BaseDriveActivity";
    private static final String TAG = "android-drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final String MIME_PHOTO = "image/jpeg";
    private static final String MIME_ZIP = "application/zip";
    public static final String AppName = "wififorget";
    public static final String SD_DB_SAVE_FILE_LOCATION = Environment
            .getExternalStorageDirectory() + "/WifiForget/BackupDB.zip";
    protected static final File SD_DB_SAVE_FOLDER_LOCATION = new File(
            Environment.getExternalStorageDirectory(), AppName);

    private static final int BUFFER_SIZE = 1024;
    private static final int SYNC_ID = Menu.FIRST;
    private static final int REMOVE_ID = Menu.FIRST + 1;
    private static final int CHANGE_ID = Menu.FIRST + 2;
    private String comingFrom = "";


    @Override
    protected void onResume() {
//Log.i("view", "onResume");
        super.onResume();
        ChangeLog cl = new ChangeLog(this);
        if (cl.firstRun())
            cl.getLogDialog().show();

        new DBCreator(this);
        CheckifSDfolderExist();
        onNewIntent(getIntent()); // GETS DATE PASTED IN

        fillData();
        killremindernotification();

        if (!"".equals(BSSID) && !"".equals(SSID) && !"".equals(ComingFrom)
                && BSSID != null && SSID != null && ComingFrom != null) {


            StartAlert();
        }

    }

    //
    private void CheckifSDfolderExist() {
        // TODO Auto-generated method stub
        File exportDir = SD_DB_SAVE_FOLDER_LOCATION;

        if (!exportDir.exists()) {

            exportDir.mkdirs();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_log);

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        // Log.i("view", dtNOW.toString("MM-dd-yyyy HH:mm:ss"));

        String SyncOn = sp.getString("sync", "false");

        //Log.i("view", "SyncOn " + SyncOn);
        if ("true".equals(SyncOn)) {


            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                    .addApi(Plus.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();


            //comingFrom = "Restore";
            //ConnectSync();

        } else {
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        switch (comingFrom) {

            case "Restore":
                //Log.i("view", "Restore");

                comingFrom = "";
                break;

            case "Save":
                //Log.i("view", "Save");
                saveFiletoDrive();
                comingFrom = "";
                break;
            case "Disconnect":
                //Log.i("view", "Disconnect");
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                comingFrom = "";
                break;
            case "Change":
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();

                comingFrom = "";
                break;
            default:
                //Log.i("view", "Case Default");
                comingFrom = "";


        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
//Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            //Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }


    // //////////////////////////////////////////////////

    private void killremindernotification() {
        NotificationManager nm = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.string.alert_started); // Cancel the persistent
        // notification.

    }

    private void fillData() {

        // TextView tvSSID = (TextView)findViewById(R.id.tvSSID);
        // TextView tvBSSID = (TextView)findViewById(R.id.tvBSSID);

        // Fill ListView
        DbAdapter mDbHelper = new DbAdapter(this);
        mDbHelper.open();

        // Cursor cur =
        // mDbHelper.returnMonthDateSet("2013-01-01 00:00:00:00","2013-10-01 00:00:00:00"
        // );
        Cursor cur = mDbHelper.returnAll();
        // startManagingCursor(cur);

        // Log.i("view",String.valueOf(cur.getCount()));

        // Create an array to specify the fields we want to display in the list.
        // (only TITLE)
        String[] from = new String[]{"ssid", "bssid"};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.tvSSID, R.id.tvBSSID};
        // Now create a simple cursor adapter and set it to display

        adapter = new SimpleCursorAdapter(this, R.layout.act_log_row, cur,
                from, to);

        setListAdapter(adapter);
        registerForContextMenu(getListView());
        adapter.notifyDataSetChanged();
        // mDbHelper.close();

    }

    private void StartAlert() {

        // Log.i("view", "test");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("WARNING");
        builder.setMessage("Mac address '"
                + BSSID
                + "' of access point '"
                + SSID
                + "' is not in Wifi Forget's database. This could be a hacker.\nWhat would you like to do?");

        builder.setNeutralButton("Forget AP",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WifiManager wm1 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wm1.removeNetwork(iNetId);
                        wm1.saveConfiguration();
                        Toast.makeText(getBaseContext(),
                                "Access Point? What Access Point?",
                                Toast.LENGTH_LONG).show();

                        Intent intent = getIntent();
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.removeExtra("BSSID");

                        intent.removeExtra("SSID");
                        intent.removeExtra("ComingFrom");
                        SSID = "";
                        BSSID = "";
                        ComingFrom = "";
                    }
                });


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForget:
                WifiManager wm1 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wm1 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wm1.getConnectionInfo();
                // Log.i("view",String.valueOf(wm1.getConfiguredNetworks().size()));

                //

                int iWifiCount = wm1.getConfiguredNetworks().size();
                for (int i = 0; i < iWifiCount; i++) {
                    // Log.i("view", "BSSID "+wifiInfo.getBSSID());

                    // wm1.removeNetwork(i);
                }
                wm1.saveConfiguration();

                Toast.makeText(this, "Networks? What Networks?", Toast.LENGTH_LONG)
                        .show();

                break;

        }
        // finish();
    }



    private void DBCreator() {

        net.niesz.wififorget.DB.DBCreator dbHelper = null;
        dbHelper = new net.niesz.wififorget.DB.DBCreator(this);
        dbHelper.createDatabase();

    }

    public void onNewIntent(Intent intent) {

        // Log.i("view","intent"+intent.getComponent().getClass());
        SSID = intent.getStringExtra("SSID");
        BSSID = intent.getStringExtra("BSSID");
        ComingFrom = intent.getStringExtra("ComingFrom");
        iNetId = intent.getIntExtra("netid", -1);

        // Log.i("view","SSID: "+SSID);

    }



    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, FIRST, 0, "Delete");

    }


    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {

            case FIRST:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                DbAdapter mDbHelper = new DbAdapter(getBaseContext());
                                mDbHelper.open();
                                DbAdapter.deleteRow(info.id);
                                mDbHelper.close();

                                SharedPreferences sp = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                // Log.i("view", dtNOW.toString("MM-dd-yyyy HH:mm:ss"));

                                String SyncOn = sp.getString("sync", "false");

                                if ("true".equals(SyncOn)) {
                                    fillData();
                                    if (mGoogleApiClient.isConnected()) {


                                        saveFiletoDrive();
                                    } else {
                                        comingFrom = "Save";
                                        mGoogleApiClient.connect();
                                    }
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to DELETE?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                // .setTitle(R.string.Delete_all_mood_symptoms_too);

                return true;
        }

        return false;

    }




    private void saveFiletoDrive() {
        // TODO Auto-generated method stub
        //Log.i("view", "SaveFileToDrive");
        if (mGoogleApiClient.isConnected()) {
            // Log.i("view", "isConnected");
            //deleteOldVersion();
        }

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        //Log.i("view", "sp Drive ID: "+sp.getString("driveFileID", ""));

        final File file = new File(SD_DB_SAVE_FILE_LOCATION);
        final String mime = "application/zip";
        // Log.i("view", "internal db location: "+
        //Create File locally for upload
        if (net.niesz.wififorget.Drive.S_Backup_Restore.SdIsPresent()) {// Check
            // if
            // SD_Card
            // present
            ArrayList<String> arrayList = new ArrayList<String>();

            File dir = new File(
                    net.niesz.wififorget.Drive.S_Backup_Restore.DATABASE_INTERNAL_DIRECTORY);
            if (dir.listFiles() != null) {
                for (File child : dir.listFiles()) {
                    //Log.i("view", child.toString());
                    arrayList.add(child.toString());
                }

                String saveto = SD_DB_SAVE_FILE_LOCATION;
                // Log.i("view", saveto);

                try {
                    net.niesz.wififorget.Drive.S_Backup_Restore.zip(this,
                            arrayList, saveto, "5", "Local");
                    // Toast.makeText(this, R.string.sdSuccess,
                    // Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    // Toast.makeText(this, R.string.sdFailure,
                    // Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
        //Log.i("view", sp.getString("driveFileID", ""));
        saveNew(file, mime);
        // overright(sp.getString("driveFileID", ""),file);

        // saveNew(file,mime);
        /*
         * if (!"".equals(sp.getString("driveFileID", ""))){ Log.i("view",
         * "OverRight"); overright(sp.getString("driveFileID", ""),file); }else{
         * Log.i("view", "NewSave");
         *
         * saveNew(file,mime); }
         */
    }
    private void saveNew(final File file, final String mime) {
        //Log.i("view","SaveNew");
        // Start by creating a new contents, and setting a callback.

        Query query = new Query.Builder().addFilter(
                Filters.eq(SearchableField.TITLE, "BackupDB.zip")).build();


    }



    private void deleteOldVersion() {
        //Log.i("view","deleteOldVersion");
        // TODO Auto-generated method stub

        Query query = new Query.Builder().addFilter(
                Filters.eq(SearchableField.TITLE, "BackupDB.zip")).build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback( new
                                                                                 ResultCallback<DriveApi.MetadataBufferResult>() {

                                                                                     @Override public void onResult(DriveApi.MetadataBufferResult result) {
                                                                                         //Log.i("view","Count: "+ result.getMetadataBuffer().getCount());





                                                                                         if (result.getMetadataBuffer().getCount() >= 1) {

                                                                                            // DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient,
                                                                                            //         DriveId.decodeFromString(result.getMetadataBuffer().get(0).getDriveId().toString()));
                                                                                             // Call to delete file.

                                                                                             //driveFile.trash(mGoogleApiClient);
                                                                                           //  driveFile.delete(mGoogleApiClient);
                                                                                             //result.getMetadataBuffer().get(0). DriveFile driveFile =
                                                                                             //Drive.DriveApi.getFile(mGoogleApiClient,
                                                                                             //result.getMetadataBuffer().get(0).getDriveId().); DriveFile driveFile;

                                                                                             //Drive.DriveApi.


	  /*
	   DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient,
		        DriveId.decodeFromString(result.getMetadataBuffer().get(0).getDriveId().toString()));
		// Call to delete file.
	   Log.i("view","test");
	   //driveFile.trash(mGoogleApiClient);
	   driveFile.delete(mGoogleApiClient);
	//			  ResultCallback<deleteCallback>() {


	   */


                                                                                         }

                                                                                     } });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        menu.add(0, SYNC_ID, 0, "Sync");
        String SyncOn = sp.getString("sync", "false");
        //Log.i("view", SyncOn);

        if ("true".equals(SyncOn)) {
            menu.add(0, REMOVE_ID, 0, "Turn Off Sync");
            menu.add(0,CHANGE_ID,0,"Change Accounts");
        }
        return true;



        //return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sp.edit();

        switch (item.getItemId()) {

            case SYNC_ID:
                //invalidateOptionsMenu();
                //Log.i("view", "Sync");
                editor.putString("sync", "true");
                editor.commit();
                // Intent i3 = new Intent(this,Act_Preferences.class);
                // startActivity(i3);

                // Log.i("view", dtNOW.toString("MM-dd-yyyy HH:mm:ss"));

                new A_Google_Drive().execute("",1,1);


                //ConnectSync();
                //restoreDBfromDrive();



                break;
            case REMOVE_ID:
                invalidateOptionsMenu();
                // setalarm(getBaseContext());
                comingFrom= "Disconnect";
                // deleteOldVersion();
                mGoogleApiClient.reconnect();

                // Log.i("view", String.valueOf(mGoogleApiClient.isConnected()));
			/*
			if (mGoogleApiClient.isConnected()) {
				Log.i("view", "Remove");
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				editor.putString("sync", "false");
				editor.commit();
				// mGoogleApiClient.connect();
			}
			*/


                editor.putString("sync", "false");
                editor.commit();

                // Log.i("view",Plus.AccountApi.getAccountName(mGoogleApiClient));

                // saveFiletoDrive();

                break;
            case CHANGE_ID:
                comingFrom= "Change";

                mGoogleApiClient.reconnect();

                //Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                //mGoogleApiClient.disconnect();
                //mGoogleApiClient.connect();

                break;
        }

        //Log.i("view", "end");
        return super.onOptionsItemSelected(item);
    }

    private void ConnectSync() {

        //Log.i("view", "ConnectSync");
		/*
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
		.addApi(Plus.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).build();
*/
        //Log.i("view", "is Sync Connected: "+mGoogleApiClient.isConnected());
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();

            } else {
                mGoogleApiClient.connect();
            }

        }
    }

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

        //Intent i9 = new Intent(context, Bcr_WifiReceiver.class);
        //PendingIntent pi = PendingIntent.getBroadcast(context, 0, i9,
         //       PendingIntent.FLAG_CANCEL_CURRENT);

       // AlarmManager am = (AlarmManager) context
       //         .getSystemService(context.ALARM_SERVICE);
        // am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 5000,
        // pi);
      //  am.setRepeating(AlarmManager.RTC_WAKEUP,
      //          calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

    }


    private class A_Google_Drive extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... params) {



            return null;
        }

        public void execute(String string, int i, int j) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            //Log.i("view","doInBackground");
            ConnectSync();

        }



    }














}