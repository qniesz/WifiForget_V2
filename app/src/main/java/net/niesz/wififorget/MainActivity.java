package net.niesz.wififorget;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.cursoradapter.widget.SimpleCursorAdapter;

import net.niesz.wififorget.DB.DBCreator;
import net.niesz.wififorget.DB.DbAdapter;

import java.io.File;

public class MainActivity extends ListActivity {
    private static final int FIRST = Menu.FIRST;
    private static String BSSID, SSID, ComingFrom = null;
    private static int iNetId;
    // private AdView mAdView;

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

               


                //ConnectSync();
                //restoreDBfromDrive();



                break;
            case REMOVE_ID:
                invalidateOptionsMenu();
                // setalarm(getBaseContext());
                comingFrom= "Disconnect";
                // deleteOldVersion();


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



                //Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                //mGoogleApiClient.disconnect();
                //mGoogleApiClient.connect();

                break;
        }

        //Log.i("view", "end");
        return super.onOptionsItemSelected(item);
    }


















}