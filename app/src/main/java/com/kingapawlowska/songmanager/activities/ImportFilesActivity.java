package com.kingapawlowska.songmanager.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ImportFilesActivity extends AppCompatActivity {

    private static final String TAG = "SongManager";

    Button btnImportBackup;
    TextView tvImportBackupInformation;
    HashMap<String, String> mime_map;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private Drive mClient;
    private final HttpTransport m_transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory m_jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final int PICKFILE_REQUEST_CODE = 2001;

    private static final String BUTTON_TEXT = "IMPORT A BACKUP";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {DriveScopes.DRIVE, DriveScopes.DRIVE_FILE};


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /** Inicjalizacja obiektów widoku, przycisków itp
         ******************************************************************************************/
        setContentView(R.layout.activity_import_files);

        btnImportBackup = findViewById(R.id.btn_import_backup);
        tvImportBackupInformation = findViewById(R.id.tv_import_backup_information);
        /******************************************************************************************/

        mime_map = new HashMap<String, String>();
        mime_map.put("jpg", "image/jpeg");
        mime_map.put("txt", "text/plain");
        mime_map.put("*", "*/*");

        btnImportBackup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Export Backup");
                btnImportBackup.setEnabled(false);
                tvImportBackupInformation.setText("");
                getImportResultsFromApi();
                btnImportBackup.setEnabled(true);
            }
        });

        tvImportBackupInformation.setText("Click the \'" + BUTTON_TEXT + "\' button to import application files.");
        mProgress = new ProgressDialog(ImportFilesActivity.this);
        mProgress.setMessage("Download files from Google Drive ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        mClient = new com.google.api.services.drive.Drive.Builder(
                m_transport, m_jsonFactory, mCredential).setApplicationName("AppName/1.0")
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //==========================================================================================

    private void getImportResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            tvImportBackupInformation.setText("No network connection available.");

        } else {
            System.out.println("CREDITES:" + mCredential);
            new MakeRequestTaskImportBackup(mCredential).execute();

        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getImportResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    tvImportBackupInformation.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getImportResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getImportResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getImportResultsFromApi();
                }
                break;

            case PICKFILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    new MakeRequestTaskImportBackup(mCredential).execute();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ImportFilesActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTaskImportBackup extends AsyncTask<Void, Void, String> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        private String returnString = null;
        private Folders folders = new Folders();

        MakeRequestTaskImportBackup(GoogleAccountCredential credential) {
            System.out.println("Start download");
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("SongManager")
                    .build();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return downloadFilesFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String downloadFilesFromApi() throws IOException {

            downloadSetlistsFilesFromGoogleDrive();
            downloadSongsFilesFromGoogleDrive();
            downloadGigsFilesFromGoogleDrive();

            return returnString;
        }

        private void downloadSetlistsFilesFromGoogleDrive() throws IOException {

            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());

                File subfolderSetlists = null;
                List<File> listOfSubFolders = mService.files().list()
                        .setQ("name='Setlists' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                        .execute().getFiles();
                System.out.println("SubFolder search result:" + listOfSubFolders);
                if (listOfSubFolders.size() < 1) {
                } else {
                    subfolderSetlists = listOfSubFolders.get(0);
                    System.out.println("Subfolder founded:" + folderSongManager.getId());

                    List<File> listOffiles = mService.files().list()
                            .setQ("'" + subfolderSetlists.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name, parents)")
                            .execute()
                            .getFiles();


                    if (!folders.checkIfFoldersExists()) {
                        folders.createFolders();
                    }

                    for (File f : listOffiles) {

                        OutputStream outputStream = new ByteArrayOutputStream();
                        mService.files().get(f.getId())
                                .executeMediaAndDownloadTo(outputStream);

                        String filename = f.getName();
                        java.io.File file = new java.io.File(folders.getChildFolderSetlists().toString(), filename + "");
                        try {
                            FileWriter writerFile = new FileWriter(file);
                            writerFile.append(outputStream.toString());
                            writerFile.flush();
                            writerFile.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnString = "File ID: " + f.getId();
                    }
                }
            }
        }

        private void downloadSongsFilesFromGoogleDrive() throws IOException {

            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());

                File subfolderSongs = null;
                List<File> listOfSubFolders = mService.files().list()
                        .setQ("name='Songs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                        .execute().getFiles();
                System.out.println("SubFolder search result:" + listOfSubFolders);
                if (listOfSubFolders.size() < 1) {
                } else {
                    subfolderSongs = listOfSubFolders.get(0);
                    System.out.println("Subfolder founded:" + folderSongManager.getId());

                    List<File> listOffiles = mService.files().list()
                            .setQ("'" + subfolderSongs.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name, parents)")
                            .execute()
                            .getFiles();

                    if (!folders.checkIfFoldersExists()) {
                        folders.createFolders();
                    }

                    for (File f : listOffiles) {

                        OutputStream outputStream = new ByteArrayOutputStream();
                        mService.files().get(f.getId())
                                .executeMediaAndDownloadTo(outputStream);

                        String filename = f.getName();
                        java.io.File file = new java.io.File(folders.getChildFolderSongs().toString(), filename + "");
                        try {
                            FileWriter writerFile = new FileWriter(file);
                            writerFile.append(outputStream.toString());
                            writerFile.flush();
                            writerFile.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnString = "File ID: " + f.getId();
                    }
                }
            }
        }

        private void downloadGigsFilesFromGoogleDrive() throws IOException {

            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());

                File subfolderGigs = null;
                List<File> listOfSubFolders = mService.files().list()
                        .setQ("name='Gigs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                        .execute().getFiles();
                System.out.println("SubFolder search result:" + listOfSubFolders);
                if (listOfSubFolders.size() < 1) {
                } else {
                    subfolderGigs = listOfSubFolders.get(0);
                    System.out.println("Subfolder founded:" + folderSongManager.getId());

                    List<File> listOffiles = mService.files().list()
                            .setQ("'" + subfolderGigs.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name, parents)")
                            .execute()
                            .getFiles();

                    if (!folders.checkIfFoldersExists()) {
                        folders.createFolders();
                    }

                    for (File f : listOffiles) {

                        OutputStream outputStream = new ByteArrayOutputStream();
                        mService.files().get(f.getId())
                                .executeMediaAndDownloadTo(outputStream);

                        String filename = f.getName();
                        java.io.File file = new java.io.File(folders.getChildFolderGigs().toString(), filename + "");
                        try {
                            FileWriter writerFile = new FileWriter(file);
                            writerFile.append(outputStream.toString());
                            writerFile.flush();
                            writerFile.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        returnString = "File ID: " + f.getId();
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            tvImportBackupInformation.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();
            if (output == null || output.length() == 0) {
                tvImportBackupInformation.setText("Backup files on Google Drive not found");
            } else {
                output = ("The backup has been successfully downloaded from Google Drive.");
                tvImportBackupInformation.setText(output);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ImportFilesActivity.REQUEST_AUTHORIZATION);
                } else {
                    tvImportBackupInformation.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                tvImportBackupInformation.setText("Request cancelled.");
            }
        }
    }

}
