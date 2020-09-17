package com.kingapawlowska.songmanager.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.setlists_part.SetlistItemAdapter;
import com.kingapawlowska.songmanager.setlists_part.SetlistItemModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenSetlistActivity extends AppCompatActivity {

    static final int REQUEST_REFRESH_AFTER_EDIT_SETLIST = 3001;

    int id_menu_open_setlist_action_edit_setlist;
    int id_menu_open_setlist_action_delete;

    Intent intentOpenSetlist;
    Bundle bundle;

    String filename;

    private List<SetlistItemModel> setlistItems = new ArrayList<SetlistItemModel>();
    private SetlistItemAdapter setlistItemAdapter;
    private ListView setlistItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_open_setlist);

        intentOpenSetlist = getIntent();
        bundle = intentOpenSetlist.getExtras();

        if (intentOpenSetlist.getData() != null) {
            filename = bundle.getString("TITLE_OF_SETLIST");
            android.support.v7.app.ActionBar actionBarOpenSetlistActivity = getSupportActionBar();
            actionBarOpenSetlistActivity.setTitle(filename); // or setTitle only without subtitle

            File setlistFile = new File(intentOpenSetlist.getData().getPath());
            String stringOfSetlistFile = readTheFile(setlistFile);

            /** Dodanie piosenek z setlisty do listy */
            setlistItems = getSetlistElements(stringOfSetlistFile);

            setlistItemList = (ListView) findViewById(R.id.setlist_item_list);
            setlistItemAdapter = new SetlistItemAdapter(this, setlistItems);
            setlistItemList.setAdapter(setlistItemAdapter);

            setlistItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                    // nic
                    System.out.println("Klik");
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_open_setlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        id_menu_open_setlist_action_edit_setlist = item.getItemId();
        id_menu_open_setlist_action_delete = item.getItemId();


        if (id_menu_open_setlist_action_edit_setlist ==
                R.id.id_menu_open_setlist_action_edit_setlist) {

            intentEditSetlist();
            return true;
        }

        if (id_menu_open_setlist_action_delete ==
                R.id.id_menu_open_setlist_action_delete) {

            intentRemoveSetlist();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_EDIT_SETLIST) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    /**
     * =========================================================================================
     */

    public void intentRemoveSetlist() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        File file = new File(intentOpenSetlist.getData().getPath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            setResult(RESULT_OK, null);
                            finish();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(OpenSetlistActivity.this);
        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void intentEditSetlist() {

        Intent intentEditSetlistActivity = new Intent
                (OpenSetlistActivity.this, EditSetlistActivity.class);

        String pathToTheSetlistFile = intentOpenSetlist.getData().getPath();
        File fileToShareViaIntent = new File(pathToTheSetlistFile);
        intentEditSetlistActivity.setData(Uri.fromFile(fileToShareViaIntent));

        startActivityForResult(intentEditSetlistActivity, REQUEST_REFRESH_AFTER_EDIT_SETLIST);
        setResult(RESULT_OK, null);
        finish();
    }


    private ArrayList<SetlistItemModel> getSetlistElements(String stringOfSetlistFile) {

        ArrayList<SetlistItemModel> values = new ArrayList<SetlistItemModel>();

        Scanner scanner = new Scanner(stringOfSetlistFile);

        if (stringOfSetlistFile.length() != 0) {

            int linesCounter = 0;
            while (scanner.hasNextLine()) {
                linesCounter++;
                String line = scanner.nextLine();

                String title = getTitleFromLine(line);
                String artist = getArtistFromLine(line);
                values.add(new SetlistItemModel(title, artist));

            }

            scanner.close();

        } else {
            // String jest pusty

        }

        return values;

    }

    public String getTitleFromLine(String line) {
        String title = "";
        String regexTitle = "\\{(.+)\\}\\,\\{(.+)\\}\\;";
        Pattern pattern = Pattern.compile(regexTitle);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            title = matcher.group(1);
        }

        return title;
    }

    public String getArtistFromLine(String line) {
        String artist = "";
        String regexArtist = "\\{(.+)\\}\\,\\{(.+)\\}\\;";
        Pattern pattern = Pattern.compile(regexArtist);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            artist = matcher.group(2);
        }

        return artist;
    }

    public String readTheFile(File file) {

        String stringOfFile = null;
        try {
            stringOfFile = getStringFromFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringOfFile;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

}
