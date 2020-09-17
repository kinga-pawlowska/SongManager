package com.kingapawlowska.songmanager.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;

public class ExportFilesActivity extends AppCompatActivity {

    Button btnExportBackup;
    TextView tvExportBackupInformation;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    Drive mClient;
    final HttpTransport m_transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory m_jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int PICKFILE_REQUEST_CODE = 2001;

    static final String TAG = "SongManager";
    static final String BUTTON_TEXT = "EXPORT A BACKUP";
    static final String PREF_ACCOUNT_NAME = "accountName";
    static final String[] SCOPES = {DriveScopes.DRIVE, DriveScopes.DRIVE_FILE};
    HashMap<String, String> mime_map;

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
        setContentView(R.layout.activity_export_files);

        btnExportBackup = findViewById(R.id.btn_export_backup);
        tvExportBackupInformation = findViewById(R.id.tv_export_backup_information);
        /******************************************************************************************/

        mime_map = new HashMap<String, String>();
        mime_map.put("jpg", "image/jpeg");
        mime_map.put("txt", "text/plain");
        mime_map.put("*", "*/*");

        btnExportBackup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Export Backup");
                btnExportBackup.setEnabled(false);
                tvExportBackupInformation.setText("");
                getExportResultsFromApi();
                btnExportBackup.setEnabled(true);
            }
        });

        tvExportBackupInformation.setText("Click the \'" + BUTTON_TEXT + "\' button to export application files.");
        mProgress = new ProgressDialog(ExportFilesActivity.this);
        mProgress.setMessage("Sending files to Google Drive ...");

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

    private void getExportResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            tvExportBackupInformation.setText("No network connection available.");

        } else {
            System.out.println("CREDITES:" + mCredential);
            new MakeRequestTaskExportBackup(mCredential).execute();

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
                getExportResultsFromApi();
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
                    tvExportBackupInformation.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getExportResultsFromApi();
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
                        getExportResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getExportResultsFromApi();
                }
                break;

            case PICKFILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    new MakeRequestTaskExportBackup(mCredential).execute();
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
                ExportFilesActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTaskExportBackup extends AsyncTask<Void, Void, String> {

        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        private String returnString = null;
        private Folders folders = new Folders();

        MakeRequestTaskExportBackup(GoogleAccountCredential credential) {
            System.out.println("Start upload");
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
                return uploadFilesToApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String uploadFilesToApi() throws IOException {

            deleteSetlistsFilesFromGoogleDrive();
            uploadSetlistsFilesToGoogleDrive();

            deleteSongsFilesFromGoogleDrive();
            uploadSongsFilesToGoogleDrive();

            deleteGigsFilesFromGoogleDrive();
            uploadGigsFilesToGoogleDrive();

            return returnString;
        }

        private void uploadGigsFilesToGoogleDrive() throws IOException {

            for (final java.io.File f : folders.getChildFolderGigs().listFiles()) {
                if (f.isFile()) {
                    System.out.println("Plik: " + Uri.fromFile(f));
                    final File fileMetadata = new File();
                    fileMetadata.setName(f.getName());

                    String mime = getFileMime(f);
                    AbstractInputStreamContent aisc = new AbstractInputStreamContent(mime) {
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return getContentResolver().openInputStream(Uri.fromFile(f));
                        }

                        @Override
                        public long getLength() throws IOException {
                            return getContentResolver().openFileDescriptor(Uri.fromFile(f), "r").getStatSize();
                        }

                        @Override
                        public boolean retrySupported() {
                            return false;
                        }
                    };

                    List<File> listOfFolders = mService.files().list()
                            .setQ("(name='SongManager') and (trashed=false)")
                            .execute().getFiles();
                    File folderSongManager = null;
                    File subfolderGigs = null;
                    if (listOfFolders.size() < 1) {
                        final File folderMetadata = new File();
                        folderMetadata.setName("SongManager");
                        folderMetadata.setMimeType("application/vnd.google-apps.folder");
                        folderSongManager = mService.files().create(folderMetadata)
                                .setFields("id")
                                .execute();
                    } else {
                        folderSongManager = listOfFolders.get(0);
                        System.out.println("Folder founded:" + folderSongManager.getId());
                    }


                    //Search for GigsFolder
                    List<File> listOfSubFolders = mService.files().list()
                            .setQ("name='Gigs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                            .execute().getFiles();
                    System.out.println("SubFolder search result:" + listOfSubFolders);
                    if (listOfSubFolders.size() < 1) {
                        final File subFolderMetadata = new File();
                        subFolderMetadata.setName("Gigs");
                        subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                        subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                        subfolderGigs = mService.files().create(subFolderMetadata)
                                .setFields("id, parents")
                                .execute();
                        System.out.println("Subfolder created:" + subfolderGigs.getId());
                    } else {
                        subfolderGigs = listOfSubFolders.get(0);
                        System.out.println("Subfolder founded:" + folderSongManager.getId());
                    }

                    System.out.println("Upload Files:");
                    //Add parent folder
                    fileMetadata.setParents(Arrays.asList(new String[]{subfolderGigs.getId()}));

                    File file = mService.files().create(fileMetadata, aisc)
                            .setFields("id, parents") //parents for folder
                            .execute();
                    System.out.println("File ID: " + file.getId());


                    returnString = "File ID: " + file.getId();

                } else {
                }
            }
        }

        private void deleteGigsFilesFromGoogleDrive() throws IOException {
            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
                final File folderMetadata = new File();
                folderMetadata.setName("SongManager");
                folderMetadata.setMimeType("application/vnd.google-apps.folder");
                folderSongManager = mService.files().create(folderMetadata)
                        .setFields("id")
                        .execute();
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());
            }

            File subfolderGigs = null;
            List<File> listOfSubFolders = mService.files().list()
                    .setQ("name='Gigs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                    .execute().getFiles();
            System.out.println("SubFolder search result:" + listOfSubFolders);
            if (listOfSubFolders.size() < 1) {
                final File subFolderMetadata = new File();
                subFolderMetadata.setName("Gigs");
                subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                subfolderGigs = mService.files().create(subFolderMetadata)
                        .setFields("id, parents")
                        .execute();
                System.out.println("Subfolder created:" + subfolderGigs.getId());
            } else {
                subfolderGigs = listOfSubFolders.get(0);
                System.out.println("Subfolder founded:" + folderSongManager.getId());
            }

            List<File> listOffiles = mService.files().list()
                    .setQ("'" + subfolderGigs.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .execute()
                    .getFiles();

            for (File f : listOffiles) {
                System.out.println("Plik " + f.getName());
                mService.files().delete(f.getId()).execute();
                System.out.println("deleted");

            }
        }

        private void uploadSongsFilesToGoogleDrive() throws IOException {

            for (final java.io.File f : folders.getChildFolderSongs().listFiles()) {
                if (f.isFile()) {
                    System.out.println("Plik: " + Uri.fromFile(f));
                    final File fileMetadata = new File();
                    fileMetadata.setName(f.getName());

                    String mime = getFileMime(f);
                    AbstractInputStreamContent aisc = new AbstractInputStreamContent(mime) {
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return getContentResolver().openInputStream(Uri.fromFile(f));
                        }

                        @Override
                        public long getLength() throws IOException {
                            return getContentResolver().openFileDescriptor(Uri.fromFile(f), "r").getStatSize();
                        }

                        @Override
                        public boolean retrySupported() {
                            return false;
                        }
                    };

                    List<File> listOfFolders = mService.files().list()
                            .setQ("(name='SongManager') and (trashed=false)")
                            .execute().getFiles();
                    File folderSongManager = null;
                    File subfolderSongs = null;
                    if (listOfFolders.size() < 1) {
                        final File folderMetadata = new File();
                        folderMetadata.setName("SongManager");
                        folderMetadata.setMimeType("application/vnd.google-apps.folder");
                        folderSongManager = mService.files().create(folderMetadata)
                                .setFields("id")
                                .execute();
                    } else {
                        folderSongManager = listOfFolders.get(0);
                        System.out.println("Folder founded:" + folderSongManager.getId());
                    }


                    //Search for SongsFolder
                    List<File> listOfSubFolders = mService.files().list()
                            .setQ("name='Songs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                            .execute().getFiles();
                    System.out.println("SubFolder search result:" + listOfSubFolders);
                    if (listOfSubFolders.size() < 1) {
                        final File subFolderMetadata = new File();
                        subFolderMetadata.setName("Songs");
                        subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                        subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                        subfolderSongs = mService.files().create(subFolderMetadata)
                                .setFields("id, parents")
                                .execute();
                        System.out.println("Subfolder created:" + subfolderSongs.getId());
                    } else {
                        subfolderSongs = listOfSubFolders.get(0);
                        System.out.println("Subfolder founded:" + folderSongManager.getId());
                    }

                    System.out.println("Upload Files:");
                    //Add parent folder
                    fileMetadata.setParents(Arrays.asList(new String[]{subfolderSongs.getId()}));
                    File file = mService.files().create(fileMetadata, aisc)
                            .setFields("id, parents") //parents for folder
                            .execute();
                    System.out.println("File ID: " + file.getId());


                    returnString = "File ID: " + file.getId();

                } else {
                }
            }
        }

        private void deleteSongsFilesFromGoogleDrive() throws IOException {
            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
                final File folderMetadata = new File();
                folderMetadata.setName("SongManager");
                folderMetadata.setMimeType("application/vnd.google-apps.folder");
                folderSongManager = mService.files().create(folderMetadata)
                        .setFields("id")
                        .execute();
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());
            }

            File subfolderSongs = null;
            List<File> listOfSubFolders = mService.files().list()
                    .setQ("name='Songs' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                    .execute().getFiles();
            System.out.println("SubFolder search result:" + listOfSubFolders);
            if (listOfSubFolders.size() < 1) {
                final File subFolderMetadata = new File();
                subFolderMetadata.setName("Songs");
                subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                subfolderSongs = mService.files().create(subFolderMetadata)
                        .setFields("id, parents")
                        .execute();
                System.out.println("Subfolder created:" + subfolderSongs.getId());
            } else {
                subfolderSongs = listOfSubFolders.get(0);
                System.out.println("Subfolder founded:" + folderSongManager.getId());
            }

            List<File> listOffiles = mService.files().list()
                    .setQ("'" + subfolderSongs.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .execute()
                    .getFiles();

            for (File f : listOffiles) {
                System.out.println("Plik " + f.getName());
                mService.files().delete(f.getId()).execute();
                System.out.println("deleted");

            }

        }

        private void uploadSetlistsFilesToGoogleDrive() throws IOException {

            for (final java.io.File f : folders.getChildFolderSetlists().listFiles()) {
                if (f.isFile()) {
                    System.out.println("Plik: " + Uri.fromFile(f));
                    final File fileMetadata = new File();
                    fileMetadata.setName(f.getName());

                    String mime = getFileMime(f);
                    AbstractInputStreamContent aisc = new AbstractInputStreamContent(mime) {
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return getContentResolver().openInputStream(Uri.fromFile(f));
                        }

                        @Override
                        public long getLength() throws IOException {
                            return getContentResolver().openFileDescriptor(Uri.fromFile(f), "r").getStatSize();
                        }

                        @Override
                        public boolean retrySupported() {
                            return false;
                        }
                    };

                    List<File> listOfFolders = mService.files().list()
                            .setQ("(name='SongManager') and (trashed=false)")
                            .execute().getFiles();
                    File folderSongManager = null;
                    File subfolderSetlists = null;
                    if (listOfFolders.size() < 1) {
                        final File folderMetadata = new File();
                        folderMetadata.setName("SongManager");
                        folderMetadata.setMimeType("application/vnd.google-apps.folder");
                        folderSongManager = mService.files().create(folderMetadata)
                                .setFields("id")
                                .execute();
                    } else {
                        folderSongManager = listOfFolders.get(0);
                        System.out.println("Folder founded:" + folderSongManager.getId());
                    }


                    //Search for SetlistsFolder
                    List<File> listOfSubFolders = mService.files().list()
                            .setQ("name='Setlists' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                            .execute().getFiles();
                    System.out.println("SubFolder search result:" + listOfSubFolders);
                    if (listOfSubFolders.size() < 1) {
                        final File subFolderMetadata = new File();
                        subFolderMetadata.setName("Setlists");
                        subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                        subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                        subfolderSetlists = mService.files().create(subFolderMetadata)
                                .setFields("id, parents")
                                .execute();
                        System.out.println("Subfolder created:" + subfolderSetlists.getId());
                    } else {
                        subfolderSetlists = listOfSubFolders.get(0);
                        System.out.println("Subfolder founded:" + folderSongManager.getId());
                    }

                    System.out.println("Upload Files:");
                    //Add parent folder
                    fileMetadata.setParents(Arrays.asList(new String[]{subfolderSetlists.getId()}));
                    File file = mService.files().create(fileMetadata, aisc)
                            .setFields("id, parents") //parents for folder
                            .execute();
                    System.out.println("File ID: " + file.getId());


                    returnString = "File ID: " + file.getId();

                } else {
                }
            }
        }

        private void deleteSetlistsFilesFromGoogleDrive() throws IOException {
            List<File> listOfFolders = mService.files().list()
                    .setQ("(name='SongManager') and (trashed=false)")
                    .execute().getFiles();
            File folderSongManager = null;
            if (listOfFolders.size() < 1) {
                final File folderMetadata = new File();
                folderMetadata.setName("SongManager");
                folderMetadata.setMimeType("application/vnd.google-apps.folder");
                folderSongManager = mService.files().create(folderMetadata)
                        .setFields("id")
                        .execute();
            } else {
                folderSongManager = listOfFolders.get(0);
                System.out.println("Folder founded:" + folderSongManager.getId());
            }

            File subfolderSetlists = null;
            List<File> listOfSubFolders = mService.files().list()
                    .setQ("name='Setlists' and '" + folderSongManager.getId() + "' in parents and (trashed=false)")
                    .execute().getFiles();
            System.out.println("SubFolder search result:" + listOfSubFolders);
            if (listOfSubFolders.size() < 1) {
                final File subFolderMetadata = new File();
                subFolderMetadata.setName("Setlists");
                subFolderMetadata.setMimeType("application/vnd.google-apps.folder");
                subFolderMetadata.setParents(Collections.singletonList(folderSongManager.getId()));
                subfolderSetlists = mService.files().create(subFolderMetadata)
                        .setFields("id, parents")
                        .execute();
                System.out.println("Subfolder created:" + subfolderSetlists.getId());
            } else {
                subfolderSetlists = listOfSubFolders.get(0);
                System.out.println("Subfolder founded:" + folderSongManager.getId());
            }

            List<File> listOffiles = mService.files().list()
                    .setQ("'" + subfolderSetlists.getId() + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .execute()
                    .getFiles();

            for (File f : listOffiles) {
                System.out.println("Plik " + f.getName());
                mService.files().delete(f.getId()).execute();
                System.out.println("deleted");

            }

        }


        String getFileMime(java.io.File f) {
            String fileName = f.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                return mime_map.get(fileName.substring(fileName.lastIndexOf(".") + 1));
            else return "";
        }

        @Override
        protected void onPreExecute() {
            tvExportBackupInformation.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();
            if (output == null || output.length() == 0) {
                tvExportBackupInformation.setText("No files found.");
            } else {
                output = ("The backup has been successfully created on Google Drive.");
                tvExportBackupInformation.setText(output);
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
                            ExportFilesActivity.REQUEST_AUTHORIZATION);
                } else {
                    tvExportBackupInformation.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                tvExportBackupInformation.setText("Request cancelled.");
            }
        }
    }

}
