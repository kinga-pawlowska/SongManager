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
import android.widget.ListView;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.songs_part.SongAdapter;
import com.kingapawlowska.songmanager.songs_part.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SongsActivity extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {

    int menu_options_songs_action_search;
    int menu_options_songs_action_addasong;
    int id_menu_songs_action_sort_normally;
    int id_menu_songs_action_sort_by_reverse_order;

    private ArrayList<File> fileListSongFile = new ArrayList<File>();
    private List<SongModel> fileListSongStringArray = new ArrayList<SongModel>();
    private ArrayList<File> newFileListSongFile = new ArrayList<File>();
    private List<SongModel> newFileListSongStringArray = new ArrayList<SongModel>();;
    private SongAdapter songAdapter;
    private ListView songList;

    static final int REQUEST_REFRESH_AFTER_ADD_A_SONG = 2001;
    static final int REQUEST_REFRESH_AFTER_OPEN_SONG_AND_DELETE = 2002;
    static final int REQUEST_REFRESH_AFTER_EDIT_SONG = 2003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_songs);

        /** Dodanie piosenek do listy plikow */
        Folders folders = new Folders();
        File root = new File(folders.getChildFolderSongs().getAbsolutePath());
        getfile(root); // tutaj


        for (int i = 0; i < fileListSongFile.size(); i++) {
            String filename = fileListSongFile.get(i).getName();
            String title = getTitleFromFilename(filename);
            String artist = getArtistFromFilename(filename);

            SongModel songModel = new SongModel(title, artist);
            fileListSongStringArray.add(songModel);
            newFileListSongStringArray.add(songModel);
        }

        songList = (ListView) findViewById(R.id.song_list);
        songAdapter = new SongAdapter(this, fileListSongStringArray);
        songList.setAdapter(songAdapter);

        sortNormally();

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                intentOpenSong(position);
            }
        });

        registerForContextMenu(songList);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_ADD_A_SONG) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_OPEN_SONG_AND_DELETE) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }

        if (requestCode == REQUEST_REFRESH_AFTER_EDIT_SONG) {
            if (resultCode == RESULT_OK) {
                refreshThisActivity();
            }
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        //menu.setHeaderTitle(fileListSongStringArray.get(position).getTitle());
        menu.setHeaderTitle(newFileListSongStringArray.get(position).getTitle());
        menu.add(0, v.getId(), 0, "Open");
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Remove");

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //  info.position will give the index of selected item
        int indexSelected = info.position;


        if (item.getTitle() == "Open") {
            intentOpenSong(indexSelected);

        } else if (item.getTitle() == "Edit") {
            intentEditSong(indexSelected);

        } else if (item.getTitle() == "Remove") {
            intentRemoveSong(indexSelected);

        } else {
            return false;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_songs, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_options_songs_action_search);
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
        List<SongModel> newList = new ArrayList<SongModel>();

        newFileListSongFile.clear();
        newFileListSongStringArray.clear();

        for (int i = 0; i < fileListSongFile.size(); i++) {
            String filename = fileListSongFile.get(i).getName();
            String title = getTitleFromFilename(filename);
            String artist = getArtistFromFilename(filename);

            if((title.toLowerCase().contains(userInput)) || (artist.toLowerCase().contains(userInput))) {

                SongModel songModel = new SongModel(title, artist);
                newList.add(songModel);
                newFileListSongFile.add(fileListSongFile.get(i));
                newFileListSongStringArray.add(songModel);

            }
        }

        songList = (ListView) findViewById(R.id.song_list);
        songAdapter.clear();

        songAdapter = new SongAdapter(this, newList);
        songList.setAdapter(songAdapter);

        songAdapter.notifyDataSetChanged();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_songs_action_search = item.getItemId();
        menu_options_songs_action_addasong = item.getItemId();

        id_menu_songs_action_sort_normally = item.getItemId();
        id_menu_songs_action_sort_by_reverse_order = item.getItemId();

        if (menu_options_songs_action_search ==
                R.id.menu_options_songs_action_search) {

            return true;
        }
        if (menu_options_songs_action_addasong ==
                R.id.menu_options_songs_action_addasong) {

            intentAddASong();
            return true;
        }

        if (id_menu_songs_action_sort_normally ==
                R.id.id_menu_songs_action_sort_normally) {

            sortNormally();
            return true;
        }

        if (id_menu_songs_action_sort_by_reverse_order ==
                R.id.id_menu_songs_action_sort_by_reverse_order) {

            sortByReverseOrder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /** ========================================================================================= */

    private void sortNormally() {
        Collections.sort(fileListSongFile);

        List<SongModel> newList = new ArrayList<SongModel>();

        fileListSongStringArray.clear();
        newFileListSongFile.clear();
        newFileListSongStringArray.clear();


        for (int i = 0; i < fileListSongFile.size(); i++) {
            String filename = fileListSongFile.get(i).getName();
            String title = getTitleFromFilename(filename);
            String artist = getArtistFromFilename(filename);

            SongModel songModel = new SongModel(title, artist);
            newList.add(songModel);
            newFileListSongFile.add(fileListSongFile.get(i));
            fileListSongStringArray.add(songModel);
            newFileListSongStringArray.add(songModel);
        }

        songList = (ListView) findViewById(R.id.song_list);
        songAdapter.clear();

        songAdapter = new SongAdapter(this, newList);
        songList.setAdapter(songAdapter);

        songAdapter.notifyDataSetChanged();
    }

    private void sortByReverseOrder() {
        /** Odwrotne sortowanie Z-A jj */
        Collections.sort(fileListSongFile ,Collections.reverseOrder());

        List<SongModel> newList = new ArrayList<SongModel>();

        fileListSongStringArray.clear();
        newFileListSongFile.clear();
        newFileListSongStringArray.clear();


        for (int i = 0; i < fileListSongFile.size(); i++) {
            String filename = fileListSongFile.get(i).getName();
            String title = getTitleFromFilename(filename);
            String artist = getArtistFromFilename(filename);

            SongModel songModel = new SongModel(title, artist);
            newList.add(songModel);
            newFileListSongFile.add(fileListSongFile.get(i));
            fileListSongStringArray.add(songModel);
            newFileListSongStringArray.add(songModel);
        }

        songList = (ListView) findViewById(R.id.song_list);
        songAdapter.clear();

        songAdapter = new SongAdapter(this, newList);
        songList.setAdapter(songAdapter);

        songAdapter.notifyDataSetChanged();

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
                        fileListSongFile.add(listFile[i]);
                        newFileListSongFile.add(listFile[i]);
                    }
                }
            }
        }

        return fileListSongFile;
    }

    public String getArtistFromFilename(String filename) {
        String artist = "";
        String regexArtist = "\\[(.*?)\\]\\-";
        Pattern pattern = Pattern.compile(regexArtist);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            artist = matcher.group(1);
        }

        return artist;
    }

    public String getTitleFromFilename(String filename) {
        String title = "";
        String regexTitle = "\\-\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(regexTitle);
        Matcher matcher = pattern.matcher(filename);
        if (matcher.find()) {
            title = matcher.group(1);
        }

        return title;
    }

    /** Odświeżanie widoku activity */
    private void refreshThisActivity() {
        Intent refresh = new Intent(this, SongsActivity.class);
        startActivity(refresh);
        this.finish();
    }

    private void intentAddASong() {
        Intent intentAddASongActivity = new Intent
                (SongsActivity.this, AddASongActivity.class);
        startActivityForResult(intentAddASongActivity, REQUEST_REFRESH_AFTER_ADD_A_SONG);
    }

    public void intentOpenSong(int position) {

        Intent intentOpenSongActivity = new Intent
                (SongsActivity.this, OpenSongActivity.class);

        //String titleOfSong = fileListSongStringArray.get(position).getTitle();
        String titleOfSong = newFileListSongStringArray.get(position).getTitle();
        //String artistOfSong = fileListSongStringArray.get(position).getArtist();
        String artistOfSong = newFileListSongStringArray.get(position).getArtist();
        //String pathToTheSongFile = fileListSongFile.get(position).getAbsolutePath();
        String pathToTheSongFile = newFileListSongFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheSongFile);

        intentOpenSongActivity.setData(Uri.fromFile(fileToShareViaIntent));
        intentOpenSongActivity.putExtra("TITLE_OF_SONG", titleOfSong);
        intentOpenSongActivity.putExtra("ARTIST_OF_SONG", artistOfSong);

        //SongsActivity.this.startActivity(intentOpenSongActivity);
        startActivityForResult(intentOpenSongActivity, REQUEST_REFRESH_AFTER_OPEN_SONG_AND_DELETE);

    }

    public void intentRemoveSong(int position) {

        final int pos = position;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        //File file = new File(fileListSongFile.get(pos).getAbsolutePath());
                        File file = new File(newFileListSongFile.get(pos).getAbsolutePath());
                        boolean deleted = file.delete();

                        if(deleted)
                        {
                            refreshThisActivity();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(SongsActivity.this);
        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void intentEditSong(int position) {

        Intent intentEditSongActivity = new Intent
                (SongsActivity.this, EditSongActivity.class);

        //String pathToTheSongFile = fileListSongFile.get(position).getAbsolutePath();
        String pathToTheSongFile = newFileListSongFile.get(position).getAbsolutePath();
        File fileToShareViaIntent = new File(pathToTheSongFile);
        intentEditSongActivity.setData(Uri.fromFile(fileToShareViaIntent));

        startActivityForResult(intentEditSongActivity, REQUEST_REFRESH_AFTER_EDIT_SONG);
    }
}
