package net.niesz.wififorget;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;

import net.niesz.wififorget.DB.DBCreator;
import net.niesz.wififorget.DB.DbAdapter;

import java.io.File;

public class MainActivity extends ListActivity implements

GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
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

        new DBCreator( this);
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        String[] from = new String[] { "ssid", "bssid" };

        // and an array of the fields we want to bind those fields to
        int[] to = new int[] { R.id.tvSSID, R.id.tvBSSID };
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


}}
