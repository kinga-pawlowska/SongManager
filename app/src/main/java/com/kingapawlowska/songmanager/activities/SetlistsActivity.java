package com.kingapawlowska.songmanager.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.setlists_part.SetlistAdapter;
import com.kingapawlowska.songmanager.setlists_part.SetlistModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetlistsActivity extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {

    int menu_options_setlists_action_search;
    int menu_options_setlists_action_addasetlist;
    int id_menu_setlists_action_sort;
    int id_menu_setlists_action_sort_by_reverse_order;
    int id_menu_setlists_action_sort_normally;

    private ArrayList<File> fileListSetlistFile = new ArrayList<File>();
    private List<SetlistModel> fileListSetlistStringArray = new ArrayList<SetlistModel>();
    private ArrayList<File> newFileListSetlistFile = new ArrayList<File>();
    private List<SetlistModel> newFileListSetlistStringArray = new ArrayList<SetlistModel>();
    private SetlistAdapter setlistAdapter;
    private ListView setlistList;

    static final int REQUEST_REFRESH_AFTER_EDIT_SELIST = 2001;
    static final int REQUEST_REFRESH_AFTER_ADD_A_SETLIST = 2002;
    static final int REQUEST_REFRESH_AFTER_OPEN_SETLIST_AND_DELETE = 2003;
    static final int REQUEST_REFRESH_AFTER_DUPLICATE_SELIST = 2004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setlists);

        /** Dodanie setlist do listy plikow */
        Folders folders = new Folders();
        File root = new File(folders.getChildFolderSetlists().getAbsolutePath());
        getfile(root); // tutaj


        for (int i = 0; i < fileListSetlistFile.size(); i++) {
            String filename = fileListSetlistFile.get(i).getName();
            String title = getTitleFromFilename(filename);

            SetlistModel setlistModel = new SetlistModel(title);
            fileListSetlistStringArray.add(setlistModel);
            newFileListSetlistStringArray.add(setlistModel);
        }

        setlistList = (ListView) findViewById(R.id.setlist_list);
        setlistAdapter = new SetlistAdapter(this, fileListSetlistStringArray);
        setlistList.setAdapter(setlistAdapter);

        sortNormally();

        setlistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                intentOpenSetlist(position);
            }
        });

        registerForContextMenu(setlistList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_ADD_A_SETLIST) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_OPEN_SETLIST_AND_DELETE) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_EDIT_SELIST) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_DUPLICATE_SELIST) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        //menu.setHeaderTitle(fileListSetlistStringArray.get(position).getTitle());
        menu.setHeaderTitle(newFileListSetlistStringArray.get(position).getTitle());
        menu.add(0, v.getId(), 0, "Open");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Remove");
        menu.add(0, v.getId(), 0, "Duplicate");


        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int indexSelected = info.position;


        if (item.getTitle() == "Open") {
            intentOpenSetlist(indexSelected);

        } else if (item.getTitle() == "Edit") {
            intentEditSetlist(indexSelected);

        } else if (item.getTitle() == "Remove") {
            intentRemoveSetlist(indexSelected);

        } else if (item.getTitle() == "Duplicate") {
            intentDuplicateSetlist(indexSelected);

        } else {
            return false;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_setlists, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_options_setlists_action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();
        List<SetlistModel> newList = new ArrayList<SetlistModel>();

        newFileListSetlistFile.clear();
        newFileListSetlistStringArray.clear();

        for (int i = 0; i < fileListSetlistFile.size(); i++) {
            String filename = fileListSetlistFile.get(i).getName();
            String title = getTitleFromFilename(filename);

            if (title.toLowerCase().contains(userInput)) {

                SetlistModel setlistModel = new SetlistModel(title);
                newList.add(setlistModel);
                newFileListSetlistFile.add(fileListSetlistFile.get(i));
                newFileListSetlistStringArray.add(setlistModel);

            }
        }

        setlistList = (ListView) findViewById(R.id.setlist_list);
        setlistAdapter.clear();

        setlistAdapter = new SetlistAdapter(this, newList);
        setlistList.setAdapter(setlistAdapter);

        setlistAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_setlists_action_search = item.getItemId();
        menu_options_setlists_action_addasetlist = item.getItemId();

        id_menu_setlists_action_sort_by_reverse_order = item.getItemId();
        id_menu_setlists_action_sort_normally = item.getItemId();

        if (menu_options_setlists_action_search ==
                R.id.menu_options_setlists_action_search) {

            //
            return true;
        }
        if (menu_options_setlists_action_addasetlist ==
                R.id.menu_options_setlists_action_addasetlist) {

            addASetlist();
            return true;
        }

        if (id_menu_setlists_action_sort_normally ==
                R.id.id_menu_setlists_action_sort_normally) {

            sortNormally();
            return true;
        }

        if (id_menu_setlists_action_sort_by_reverse_order ==
                R.id.id_menu_setlists_action_sort_by_reverse_order) {

            sortByReverseOrder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * =========================================================================================
     */

    private void sortNormally() {
        Collections.sort(fileListSetlistFile);

        List<SetlistModel> newList = new ArrayList<SetlistModel>();

        fileListSetlistStringArray.clear();
        newFileListSetlistFile.clear();
        newFileListSetlistStringArray.clear();

        for (int i = 0; i < fileListSetlistFile.size(); i++) {
            String filename = fileListSetlistFile.get(i).getName();
            String title = getTitleFromFilename(filename);

            SetlistModel setlistModel = new SetlistModel(title);
            newList.add(setlistModel);
            newFileListSetlistFile.add(fileListSetlistFile.get(i));
            fileListSetlistStringArray.add(setlistModel);
            newFileListSetlistStringArray.add(setlistModel);

        }

        setlistList = (ListView) findViewById(R.id.setlist_list);
        setlistAdapter.clear();

        setlistAdapter = new SetlistAdapter(this, newList);
        setlistList.setAdapter(setlistAdapter);

        setlistAdapter.notifyDataSetChanged();
    }

    private void sortByReverseOrder() {
        Collections.sort(fileListSetlistFile, Collections.reverseOrder());

        List<SetlistModel> newList = new ArrayList<SetlistModel>();

        fileListSetlistStringArray.clear();
        newFileListSetlistFile.clear();
        newFileListSetlistStringArray.clear();


        for (int i = 0; i < fileListSetlistFile.size(); i++) {
            String filename = fileListSetlistFile.get(i).getName();
            String title = getTitleFromFilename(filename);

            SetlistModel setlistModel = new SetlistModel(title);
            newList.add(setlistModel);
            newFileListSetlistFile.add(fileListSetlistFile.get(i));
            fileListSetlistStringArray.add(setlistModel);
            newFileListSetlistStringArray.add(setlistModel);

        }

        setlistList = (ListView) findViewById(R.id.setlist_list);
        setlistAdapter.clear();

        setlistAdapter = new SetlistAdapter(this, newList);
        setlistList.setAdapter(setlistAdapter);

        setlistAdapter.notifyDataSetChanged();
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
                        fileListSetlistFile.add(listFile[i]);
                        newFileListSetlistFile.add(listFile[i]);
                    }
                }
            }
        }

        return fileListSetlistFile;
    }

    public String getTitleFromFilename(String filename) {
        String title = "";
        String regexTitle = "\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(regexTitle);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            title = matcher.group(1);
        }

        return title;
    }

    private void addASetlist() {
        Intent intentAddASetlistActivity = new Intent
                (SetlistsActivity.this, AddASetlistActivity.class);
        startActivityForResult(intentAddASetlistActivity, REQUEST_REFRESH_AFTER_ADD_A_SETLIST);
    }

    /**
     * Odświeżanie widoku activity
     */
    private void refreshThisActivity() {
        Intent refresh = new Intent(this, SetlistsActivity.class);
        startActivity(refresh);
        this.finish();
    }


    private void intentOpenSetlist(int position) {
        Intent intentOpenSetlistActivity = new Intent
                (SetlistsActivity.this, OpenSetlistActivity.class);

        //String filename = fileListSetlistStringArray.get(position).getTitle();
        String filename = newFileListSetlistStringArray.get(position).getTitle();
        //String pathToTheSetlistFile = fileListSetlistFile.get(position).getAbsolutePath();
        String pathToTheSetlistFile = newFileListSetlistFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheSetlistFile);

        intentOpenSetlistActivity.setData(Uri.fromFile(fileToShareViaIntent));
        intentOpenSetlistActivity.putExtra("TITLE_OF_SETLIST", filename);

        startActivityForResult(intentOpenSetlistActivity, REQUEST_REFRESH_AFTER_OPEN_SETLIST_AND_DELETE);
    }

    private void intentRemoveSetlist(int position) {

        final int pos = position;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        //File file = new File(fileListSetlistFile.get(pos).getAbsolutePath());
                        File file = new File(newFileListSetlistFile.get(pos).getAbsolutePath());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SetlistsActivity.this);
        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void intentEditSetlist(int position) {

        Intent intentEditSetlistActivity = new Intent
                (SetlistsActivity.this, EditSetlistActivity.class);

        //String titleOfSetlist = fileListSetlistStringArray.get(position).getTitle();
        String titleOfSetlist = newFileListSetlistStringArray.get(position).getTitle();
        //String pathToTheSetlistFile = fileListSetlistFile.get(position).getAbsolutePath();
        String pathToTheSetlistFile = newFileListSetlistFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheSetlistFile);

        intentEditSetlistActivity.setData(Uri.fromFile(fileToShareViaIntent));
        intentEditSetlistActivity.putExtra("TITLE_OF_SETLIST", titleOfSetlist);

        startActivityForResult(intentEditSetlistActivity, REQUEST_REFRESH_AFTER_EDIT_SELIST);
    }

    private void intentDuplicateSetlist(int position) {
        Intent intentDuplicateSetlistActivity = new Intent
                (SetlistsActivity.this, DuplicateSetlistActivity.class);

        String filename = getTitleFromFilename(newFileListSetlistFile.get(position).getName());
        String pathToTheSetlistFile = newFileListSetlistFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheSetlistFile);

        intentDuplicateSetlistActivity.setData(Uri.fromFile(fileToShareViaIntent));
        intentDuplicateSetlistActivity.putExtra("TITLE_OF_SETLIST", filename);

        startActivityForResult(intentDuplicateSetlistActivity, REQUEST_REFRESH_AFTER_DUPLICATE_SELIST);
    }

}
