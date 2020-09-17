package com.kingapawlowska.songmanager.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.kingapawlowska.songmanager.ExampleFiles;
import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.PermissionsForApp;
import com.kingapawlowska.songmanager.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PermissionsForApp permissionsForApp = new PermissionsForApp(this);
    private static final String TAG = "SongManager";

    Button btnOptionSongs;
    Button btnOptionSetlists;
    Button btnOptionGigs;
    int id_menu_main_action_about;
    int id_menu_main_action_checkInternetConnection;
    int id_menu_main_action_exportApplicationFiles;
    int id_menu_main_action_importApplicationFiles;
    int id_menu_main_action_clearApplicationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /** Pełny ekran w aplikacji - wyłączenie status bar */
        fullScreen();

        /** Inicjalizacja obiektów widoku, przycisków itp
         ******************************************************************************************/
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.ic_icon);
        actionBar.setDisplayShowHomeEnabled(true);

        btnOptionSongs = findViewById(R.id.main_btn_option_songs);
        btnOptionSetlists = findViewById(R.id.main_btn_option_setlists);
        btnOptionGigs = findViewById(R.id.main_btn_option_gigs);
        /******************************************************************************************/

        /** Nadawanie uprawnień */
        if (!permissionsForApp.checkMultiplePermissions()) {
            permissionsForApp.requestMultiplePermissions();
            alertDialogRestart();
        }

        /** Tworzenie folderów w pamięci wewnętrznej */
        Folders folders = new Folders();
        if (!folders.checkIfFoldersExists()) {
            folders.createFolders();

            /** Tworzenie przykładowych plików w klasie */
            ExampleFiles exampleFiles = new ExampleFiles();
            exampleFiles.writeFiles();
        }

        btnOptionSongs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentSongsActivity = new Intent
                        (MainActivity.this, SongsActivity.class);
                //intentSongsActivity.putExtra("key", 100);
                MainActivity.this.startActivity(intentSongsActivity);
            }
        });

        btnOptionSetlists.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentSetlistsActivity = new Intent
                        (MainActivity.this, SetlistsActivity.class);
                MainActivity.this.startActivity(intentSetlistsActivity);
            }
        });

        btnOptionGigs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentGigsActivity = new Intent
                        (MainActivity.this, GigsActivity.class);
                MainActivity.this.startActivity(intentGigsActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        id_menu_main_action_about = item.getItemId();
        id_menu_main_action_clearApplicationData = item.getItemId();
        id_menu_main_action_exportApplicationFiles = item.getItemId();
        id_menu_main_action_importApplicationFiles = item.getItemId();

        if (id_menu_main_action_about ==
                R.id.menu_main_action_about) {

            aboutApplication();
            return true;
        }

        if (id_menu_main_action_clearApplicationData ==
                R.id.menu_main_action_clearApplicationData) {

            clearApplicationData();
            return true;
        }

        if (id_menu_main_action_exportApplicationFiles ==
                R.id.menu_main_action_exportApplicationFiles) {

            exportApplicationFiles();
            return true;
        }

        if (id_menu_main_action_importApplicationFiles ==
                R.id.menu_main_action_importApplicationFiles) {

            importApplicationFiles();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * =========================================================================================
     */

    private void aboutApplication() {
        alertDialogAboutApplication();
    }

    private void clearApplicationData() {

        AlertDialog dialogClearAppFiles;
        final CharSequence[] items = {"All", "Songs", "Setlists", "Gigs"};
        final ArrayList seletedItems = new ArrayList();
        final Folders folders = new Folders();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the items to be removed:");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            // write your code when user checked the checkbox
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println(seletedItems);

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Do you really want to delete these items?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (seletedItems.contains(0)) {
                                            System.out.println("usuwamy all");
                                            deleteRecursive(new File(folders.getChildFolderSongs().getAbsolutePath()));
                                            deleteRecursive(new File(folders.getChildFolderSetlists().getAbsolutePath()));
                                            deleteRecursive(new File(folders.getChildFolderGigs().getAbsolutePath()));
                                        }
                                        if (seletedItems.contains(1)) {
                                            System.out.println("usuwamy songs");
                                            deleteRecursive(new File(folders.getChildFolderSongs().getAbsolutePath()));
                                        }
                                        if (seletedItems.contains(2)) {
                                            System.out.println("usuwamy setlists");
                                            deleteRecursive(new File(folders.getChildFolderSetlists().getAbsolutePath()));
                                        }
                                        if (seletedItems.contains(3)) {
                                            System.out.println("usuwamy gigs");
                                            deleteRecursive(new File(folders.getChildFolderGigs().getAbsolutePath()));
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        android.support.v7.app.AlertDialog alert = builder.create();
                        alert.show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel

                    }
                });

        dialogClearAppFiles = builder.create();//AlertDialog dialog; create like this outside onClick
        dialogClearAppFiles.show();
    }

    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.exists()) {
            File listFile[] = fileOrDirectory.listFiles();
            for (File x : listFile) {
                x.delete();
            }
        }
    }

    /**
     * Kopia zapasowa
     */
    private void exportApplicationFiles() {
        System.out.println("Kopia zapasowa - export");

        Intent intentExportFilesActivity = new Intent
                (MainActivity.this, ExportFilesActivity.class);
        //intentSongsActivity.putExtra("key", 100);
        MainActivity.this.startActivity(intentExportFilesActivity);
    }

    private void importApplicationFiles() {
        System.out.println("Kopia zapasowa - import");

        Intent intentImportFilesActivity = new Intent
                (MainActivity.this, ImportFilesActivity.class);
        //intentSongsActivity.putExtra("key", 100);
        MainActivity.this.startActivity(intentImportFilesActivity);
    }

    /**
     * Pełny ekran w aplikacji - wyłączenie status bar
     */
    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Dialog restart app
     */
    private void alertDialogRestart() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Restart Required");
        alertDialog.setMessage("Press OK to save the new permission settings and restart the application.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    /**
     * Check internet connection
     */
    private void checkInternetConnection() {
        boolean isOnlineBool = isOnline();
        alertDialogCheckInternetConnection(isOnlineBool);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void alertDialogCheckInternetConnection(boolean isOnlineBool) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Check Internet Connection");

        if (isOnlineBool) {
            alertDialog.setMessage("I am online. Press OK to continue.");
        } else {
            alertDialog.setMessage("I am offline. Press OK to continue.");
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void alertDialogAboutApplication() {
        AlertDialog alertDialogAboutApp = new AlertDialog.Builder(MainActivity.this).create();
        alertDialogAboutApp.setTitle("SongManager");

        alertDialogAboutApp.setMessage(getString(R.string.string_main_about_application));

        alertDialogAboutApp.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialogAboutApp.setIcon(R.drawable.ic_icon);
        alertDialogAboutApp.show();
    }

}
