package com.kingapawlowska.songmanager.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.gigs_part.GigAdapter;
import com.kingapawlowska.songmanager.gigs_part.GigModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GigsActivity extends AppCompatActivity {

    static final int REQUEST_REFRESH_AFTER_ADD_A_GIG = 2001;
    static final int REQUEST_REFRESH_AFTER_DELETE_GIG = 2002;
    static final int REQUEST_REFRESH_AFTER_EDIT_GIG = 2003;

    int menu_options_gigs_action_addagig;
    int id_menu_options_gigs_action_sort;
    int id_menu_options_gigs_action_invert_sort;

    private ArrayList<File> fileListGigFile = new ArrayList<File>();
    private List<GigModel> fileListGigStringArray = new ArrayList<GigModel>();
    private ArrayList<File> newFileListGigFile = new ArrayList<File>();
    private List<GigModel> newFileListGigStringArray = new ArrayList<GigModel>();
    private GigAdapter gigAdapter;
    private ListView gigList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gigs);

        /** Dodanie piosenek do listy plikow */
        Folders folders = new Folders();
        File root = new File(folders.getChildFolderGigs().getAbsolutePath());
        getfile(root); // tutaj

        for (int i = 0; i < fileListGigFile.size(); i++) {
            File currentFile = new File(fileListGigFile.get(i).getAbsolutePath());
            String stringOfGigFile = readTheFile(currentFile);

            int day = getDayFromStringOfFile(stringOfGigFile);
            int month = getMonthFromStringOfFile(stringOfGigFile);
            int year = getYearFromStringOfFile(stringOfGigFile);
            String venue = getVenueFromStringOfFile(stringOfGigFile);
            String name = getNameFromStringOfFile(stringOfGigFile);


            if ((day == 0) || (month == 0) || (year == 0) || (venue == null) || (name == null)) {
            } else {
                GigModel gigModel = new GigModel(day, month, year, venue, name);
                fileListGigStringArray.add(gigModel);
                newFileListGigStringArray.add(gigModel);
            }
        }

        gigList = (ListView) findViewById(R.id.gig_list);
        gigAdapter = new GigAdapter(this, fileListGigStringArray);
        gigList.setAdapter(gigAdapter);

        sortNormally();

        gigList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                //intentOpenGig(position);

            }
        });

        registerForContextMenu(gigList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_ADD_A_GIG) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_DELETE_GIG) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_EDIT_GIG) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        //menu.setHeaderTitle(fileListGigStringArray.get(position).getName());
        menu.setHeaderTitle(newFileListGigStringArray.get(position).getName());
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Remove");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //  info.position will give the index of selected item
        int indexSelected = info.position;


        if (item.getTitle() == "Edit") {
            intentEditGig(indexSelected);

        } else if (item.getTitle() == "Remove") {
            intentRemoveGig(indexSelected);
        } else {
            return false;
        }

        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_gigs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_gigs_action_addagig = item.getItemId();
        id_menu_options_gigs_action_sort = item.getItemId();
        id_menu_options_gigs_action_invert_sort = item.getItemId();

        if (menu_options_gigs_action_addagig ==
                R.id.menu_options_gigs_action_addagig) {

            intentAddAGig();
            return true;
        }

        if (id_menu_options_gigs_action_sort ==
                R.id.id_menu_options_gigs_action_sort) {

            sortNormally();
            return true;
        }

        if (id_menu_options_gigs_action_invert_sort ==
                R.id.id_menu_options_gigs_action_invert_sort) {

            sortByReverseOrder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * =========================================================================================
     */

    private void sortNormally() {
        Collections.sort(fileListGigFile);

        List<GigModel> newList = new ArrayList<GigModel>();

        fileListGigStringArray.clear();
        newFileListGigFile.clear();
        newFileListGigStringArray.clear();

        for (int i = 0; i < fileListGigFile.size(); i++) {

            File currentFile = new File(fileListGigFile.get(i).getAbsolutePath());
            String stringOfGigFile = readTheFile(currentFile);

            int day = getDayFromStringOfFile(stringOfGigFile);
            int month = getMonthFromStringOfFile(stringOfGigFile);
            int year = getYearFromStringOfFile(stringOfGigFile);
            String venue = getVenueFromStringOfFile(stringOfGigFile);
            String name = getNameFromStringOfFile(stringOfGigFile);

            if ((day == 0) || (month == 0) || (year == 0) || (venue == null) || (name == null)) {
            } else {
                GigModel gigModel = new GigModel(day, month, year, venue, name);
                newList.add(gigModel);
                newFileListGigFile.add(fileListGigFile.get(i));
                fileListGigStringArray.add(gigModel);
                newFileListGigStringArray.add(gigModel);
            }
        }

        gigList = (ListView) findViewById(R.id.gig_list);
        gigAdapter.clear();

        gigAdapter = new GigAdapter(this, newList);
        gigList.setAdapter(gigAdapter);

        gigAdapter.notifyDataSetChanged();
    }

    private void sortByReverseOrder() {

        Collections.sort(fileListGigFile, Collections.reverseOrder());

        List<GigModel> newList = new ArrayList<GigModel>();

        fileListGigStringArray.clear();
        newFileListGigFile.clear();
        newFileListGigStringArray.clear();

        for (int i = 0; i < fileListGigFile.size(); i++) {

            File currentFile = new File(fileListGigFile.get(i).getAbsolutePath());
            String stringOfGigFile = readTheFile(currentFile);

            int day = getDayFromStringOfFile(stringOfGigFile);
            int month = getMonthFromStringOfFile(stringOfGigFile);
            int year = getYearFromStringOfFile(stringOfGigFile);
            String venue = getVenueFromStringOfFile(stringOfGigFile);
            String name = getNameFromStringOfFile(stringOfGigFile);

            if ((day == 0) || (month == 0) || (year == 0) || (venue == null) || (name == null)) {
            } else {
                GigModel gigModel = new GigModel(day, month, year, venue, name);
                newList.add(gigModel);
                newFileListGigFile.add(fileListGigFile.get(i));
                fileListGigStringArray.add(gigModel);
                newFileListGigStringArray.add(gigModel);
            }
        }

        gigList = (ListView) findViewById(R.id.gig_list);
        gigAdapter.clear();

        gigAdapter = new GigAdapter(this, newList);
        gigList.setAdapter(gigAdapter);

        gigAdapter.notifyDataSetChanged();
    }


    /**
     * Odświeżanie widoku activity
     */
    private void refreshThisActivity() {
        Intent refresh = new Intent(this, GigsActivity.class);
        startActivity(refresh);
        this.finish();
    }


    private int getDayFromStringOfFile(String stringOfGigFile) {

        int day;
        String regexDay = "\\{day\\:\\s(\\d+)\\}\\;\\n";
        Pattern pattern = Pattern.compile(regexDay);
        Matcher matcher = pattern.matcher(stringOfGigFile);
        if (matcher.find()) {
            day = Integer.parseInt(matcher.group(1));
        } else {
            day = 0;
        }

        return day;
    }

    private int getMonthFromStringOfFile(String stringOfGigFile) {

        int month;
        String regexMonth = "\\{month\\:\\s(\\d+)\\}\\;\\n";
        Pattern pattern = Pattern.compile(regexMonth);
        Matcher matcher = pattern.matcher(stringOfGigFile);
        if (matcher.find()) {
            month = Integer.parseInt(matcher.group(1));
        } else {
            month = 0;
        }

        return month;
    }

    private int getYearFromStringOfFile(String stringOfGigFile) {

        int year;
        String regexYear = "\\{year\\:\\s(\\d+)\\}\\;\\n";
        Pattern pattern = Pattern.compile(regexYear);
        Matcher matcher = pattern.matcher(stringOfGigFile);
        if (matcher.find()) {
            year = Integer.parseInt(matcher.group(1));
        } else {
            year = 0;
        }

        return year;
    }

    private String getVenueFromStringOfFile(String stringOfGigFile) {

        String venue;
        String regexVenue = "\\{venue\\:\\s(.+)\\}\\;\\n";
        Pattern pattern = Pattern.compile(regexVenue);
        Matcher matcher = pattern.matcher(stringOfGigFile);
        if (matcher.find()) {
            venue = matcher.group(1);
        } else {
            venue = null;
        }

        return venue;
    }

    private String getNameFromStringOfFile(String stringOfGigFile) {

        String name;
        String regexName = "\\{name\\:\\s(.+)\\}\\;\\n";
        Pattern pattern = Pattern.compile(regexName);
        Matcher matcher = pattern.matcher(stringOfGigFile);
        if (matcher.find()) {
            name = matcher.group(1);
        } else {
            name = null;
        }

        return name;
    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    //fileList.add(listFile[i]);
                    //getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                            || listFile[i].getName().endsWith(".txt")
                            || listFile[i].getName().endsWith(".gif"))

                    {
                        fileListGigFile.add(listFile[i]);
                        newFileListGigFile.add(listFile[i]);
                    }
                }
            }
        }

        return fileListGigFile;
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

    // from http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm
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

    public void intentRemoveGig(int position) {

        final int pos = position;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        //File file = new File(fileListGigFile.get(pos).getAbsolutePath());
                        File file = new File(newFileListGigFile.get(pos).getAbsolutePath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            refreshThisActivity();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(GigsActivity.this);
        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void intentAddAGig() {
        Intent intentAddAGigActivity = new Intent
                (GigsActivity.this, AddAGigActivity.class);
        startActivityForResult(intentAddAGigActivity, REQUEST_REFRESH_AFTER_ADD_A_GIG);
    }

    public void intentEditGig(int position) {

        Intent intentEditGigActivity = new Intent
                (GigsActivity.this, EditGigActivity.class);

        //String pathToTheGigFile = fileListGigFile.get(position).getAbsolutePath();
        String pathToTheGigFile = newFileListGigFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheGigFile);
        intentEditGigActivity.setData(Uri.fromFile(fileToShareViaIntent));

        startActivityForResult(intentEditGigActivity, REQUEST_REFRESH_AFTER_EDIT_GIG);
    }

}
