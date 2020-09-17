package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.songs_part.SongsAddANewSong;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddASongActivity extends AppCompatActivity implements TextWatcher {

    int menu_options_add_a_song_action_save;
    EditText add_a_song_et_title;
    EditText add_a_song_et_artist;
    EditText add_a_song_et_time;
    EditText add_a_song_et_capo;
    EditText add_a_song_et_tempo;
    Button add_a_song_btn_save;

    Boolean isFilledTheForm = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_a_song);

        add_a_song_et_title = findViewById(R.id.add_a_song_et_title);
        add_a_song_et_artist = findViewById(R.id.add_a_song_et_artist);
        add_a_song_et_time = findViewById(R.id.add_a_song_et_time);
        add_a_song_et_capo = findViewById(R.id.add_a_song_et_capo);
        add_a_song_et_tempo = findViewById(R.id.add_a_song_et_tempo);
        add_a_song_btn_save = findViewById(R.id.add_a_song_btn_save);

        add_a_song_btn_save.setEnabled(false);
        add_a_song_et_title.addTextChangedListener(this);
        add_a_song_et_artist.addTextChangedListener(this);

        add_a_song_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_a_song, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_add_a_song_action_save = item.getItemId();

        if (menu_options_add_a_song_action_save ==
                R.id.menu_options_add_a_song_action_save) {

            saveFile();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        if ((add_a_song_et_title.getText().length() > 0) && (add_a_song_et_artist.getText().length() > 0)) {
            add_a_song_btn_save.setEnabled(true);
            isFilledTheForm = true;
        } else if (add_a_song_et_title.getText().length() <= 0) {
            add_a_song_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_song_et_title.setError("This field is required");
        } else if (add_a_song_et_artist.getText().length() <= 0) {
            add_a_song_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_song_et_artist.setError("This field is required");
        } else {
            add_a_song_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_song_et_title.setError("This field is required");
            add_a_song_et_artist.setError("This field is required");
        }
    }

    /**
     * =========================================================================================
     */

    private void saveFile() {

        if (isFilledTheForm) {
            String title = add_a_song_et_title.getText().toString();
            String artist = add_a_song_et_artist.getText().toString();
            String time = add_a_song_et_time.getText().toString();
            String capo = add_a_song_et_capo.getText().toString();
            String tempo = add_a_song_et_tempo.getText().toString();

            System.out.println(title);

            Boolean isPossibleToSaveFile = !checkIfTheFileAlreadyExists();

            if (isPossibleToSaveFile) {

                SongsAddANewSong newSongFile = new SongsAddANewSong(title, artist, time, capo, tempo);
                newSongFile.createAFile();

                setResult(RESULT_OK, null);
                finish();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddASongActivity.this);
                builder.setMessage("A file with this name already exists. Save the file under a different title.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public Boolean checkIfTheFileAlreadyExists() {
        Folders folders = new Folders();

        String filenameToCheck = "[" + add_a_song_et_artist.getText().toString() + "]-[" +
                add_a_song_et_title.getText().toString() + "].txt";
        File f = new File(folders.getChildFolderSongs(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }
}
