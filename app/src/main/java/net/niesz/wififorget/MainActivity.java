package net.niesz.wififorget;


import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

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

        //DBCreator();
        CheckifSDfolderExist();
        onNewIntent(getIntent()); // GETS DATE PASTED IN

        //fillData();
        //killremindernotification();

        if (!"".equals(BSSID) && !"".equals(SSID) && !"".equals(ComingFrom)
                && BSSID != null && SSID != null && ComingFrom != null) {


          //  StartAlert();
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
        setContentView(R.layout.activity_main);
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
}
