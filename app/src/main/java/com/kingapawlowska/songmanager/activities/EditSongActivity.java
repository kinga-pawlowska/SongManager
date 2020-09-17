package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditSongActivity extends AppCompatActivity implements TextWatcher {

    static final int REQUEST_REFRESH_AFTER_PREVIEW_A_SONG = 4001;

    int menu_options_edit_song_action_insert_element;
    int menu_options_edit_song_action_instert_chord;
    int menu_options_edit_song_action_save_file;
    int menu_options_edit_song_action_preview;
    EditText editSong_et_contentOfFile;
    EditText editSong_et_changeArtist;
    EditText editSong_et_changeTitle;
    EditText editSong_et_changeTime;
    EditText editSong_et_changeCapo;
    EditText editSong_et_changeTempo;

    Intent intentEditSong;

    StringBuilder stringBuilderCutOutMainInformation;
    StringBuilder stringBuilderNewCutOutMainInformation;
    StringBuilder stringBuilderNewFilenameWithExtension;
    String filenameWithExtension = "";
    Boolean isFilledTheForm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_song);

        editSong_et_contentOfFile = findViewById(R.id.editSong_et_contentOfFile);
        editSong_et_contentOfFile.setTypeface(Typeface.MONOSPACE);
        editSong_et_changeArtist = findViewById(R.id.editSong_et_changeArtist);
        editSong_et_changeTitle = findViewById(R.id.editSong_et_changeTitle);
        editSong_et_changeTime = findViewById(R.id.editSong_et_changeTime);
        editSong_et_changeCapo = findViewById(R.id.editSong_et_changeCapo);
        editSong_et_changeTempo = findViewById(R.id.editSong_et_changeTempo);

        editSong_et_changeArtist.addTextChangedListener(this);
        editSong_et_changeTitle.addTextChangedListener(this);
        editSong_et_changeTime.addTextChangedListener(this);
        editSong_et_changeCapo.addTextChangedListener(this);
        editSong_et_changeTempo.addTextChangedListener(this);

        stringBuilderCutOutMainInformation = new StringBuilder();
        stringBuilderNewCutOutMainInformation = new StringBuilder();
        stringBuilderNewFilenameWithExtension = new StringBuilder();

        intentEditSong = getIntent();

        File songFile = new File(intentEditSong.getData().getPath());
        String stringOfSongFile = readTheFile(songFile);
        String stringContentOfFile = readMainInformationAndCut(stringOfSongFile);

        editSong_et_contentOfFile.setText(stringContentOfFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_PREVIEW_A_SONG) {
            if (resultCode == RESULT_OK) {
                //refreshThisActivity();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_song, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_edit_song_action_insert_element = item.getItemId();
        menu_options_edit_song_action_instert_chord = item.getItemId();
        menu_options_edit_song_action_preview = item.getItemId();
        menu_options_edit_song_action_save_file = item.getItemId();

        if (menu_options_edit_song_action_insert_element ==
                R.id.menu_options_edit_song_action_insert_element) {

            final CharSequence[] itemsElements = {
                    //"Edit song information",
                    "Add CHORUS"
            };

            AlertDialog.Builder builderElement = new AlertDialog.Builder(this);
            builderElement.setTitle("Add elements");
            builderElement.setItems(itemsElements, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (itemsElements[item].toString().equals("Add CHORUS")) {
                        String chorusElement = "{CHORUS}";
                        editSong_et_contentOfFile.getText().insert(editSong_et_contentOfFile.getSelectionStart(), chorusElement);
                    }

                }
            });
            AlertDialog alert = builderElement.create();
            alert.show();


            return true;
        }

        if (menu_options_edit_song_action_instert_chord ==
                R.id.menu_options_edit_song_action_instert_chord) {

            final CharSequence[] itemsChords = {
                    "[]",
                    "A", "Bb", "B", "C", "D", "E", "F", "G",
                    "A7", "Bb7", "B7", "C7", "D7", "E7", "F7", "G7",
                    "am", "Bbm", "bm", "cm", "dm", "em", "fm", "gm",
                    "am7", "Bbm7", "bm7", "cm7", "dm7", "em7", "fm7", "gm7",
                    "A7+", "Bb7+", "B7+", "C7+", "D7+", "E7+", "F7+", "G7+",
                    "A4", "Bb4", "B4", "C4", "D4", "E4", "F4", "G4",
                    "A7/4", "Bb7/4", "B7/4", "C7/4", "D7/4", "E7/4", "F7/4", "G7/4",
                    "A6", "Bb6", "B6", "C6", "D6", "E6", "F6", "G6",
                    "am6", "Bbm6", "bm6", "cm6", "dm6", "em6", "fm6", "gm6",
                    "A9", "Bb9", "B9", "C9", "D9", "E9", "F9", "G9",
                    "Cis", "Dis", "Fis", "Gis",
                    "Cis7", "Dis7", "Fis7", "Gis7",
                    "cism", "dism", "fism", "gism",
                    "cism7", "dism7", "fism7", "gism7",
                    "Cis7+", "Dis7+", "Fis7+", "Gis7+",
                    "Cis4", "Dis4", "Fis4", "Gis4",
                    "Cis7/4", "Dis7/4", "Fis7/4", "Gis7/4",
                    "Cis6", "Dis6", "Fis6", "Gis6",
                    "cism6", "dism6", "fism6", "gism6",
            };

            AlertDialog.Builder builderChord = new AlertDialog.Builder(this);
            builderChord.setTitle("Insert a chord");
            builderChord.setItems(itemsChords, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

                    if (itemsChords[item].toString().equals("[]")) {
                        String chordElement = itemsChords[item].toString();
                        editSong_et_contentOfFile.getText().insert(editSong_et_contentOfFile.getSelectionStart(), chordElement);
                        editSong_et_contentOfFile.setSelection(editSong_et_contentOfFile.getSelectionStart() - 1);
                    } else {
                        String chordElement = "[" + itemsChords[item].toString() + "] ";
                        editSong_et_contentOfFile.getText().insert(editSong_et_contentOfFile.getSelectionStart(), chordElement);
                    }

                }
            });
            AlertDialog alert = builderChord.create();
            alert.show();

            return true;
        }

        if (menu_options_edit_song_action_preview ==
                R.id.menu_options_edit_song_action_preview) {

            checkChangesInTheTitleAndInfo();
            intentPreviewSong();
            return true;
        }

        if (menu_options_edit_song_action_save_file ==
                R.id.menu_options_edit_song_action_save_file) {

            if (isFilledTheForm) {
                checkChangesInTheTitleAndInfo();
                saveFile();
            }

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

        if ((editSong_et_changeTitle.getText().length() > 0) && (editSong_et_changeArtist.getText().length() > 0)) {
            isFilledTheForm = true;
        } else if (editSong_et_changeTitle.getText().length() <= 0) {
            isFilledTheForm = false;
            editSong_et_changeTitle.setError("This field is required");
        } else if (editSong_et_changeArtist.getText().length() <= 0) {
            isFilledTheForm = false;
            editSong_et_changeArtist.setError("This field is required");
        } else {
            isFilledTheForm = false;
            editSong_et_changeTitle.setError("This field is required");
            editSong_et_changeArtist.setError("This field is required");
        }
    }

    /**
     * =========================================================================================
     */

    private void checkChangesInTheTitleAndInfo() {

        stringBuilderNewCutOutMainInformation.delete(0, stringBuilderNewCutOutMainInformation.length());

        String strCutOutMainInformation = stringBuilderCutOutMainInformation.toString();
        String strTitle = "";
        String strArtist = "";
        String strTime = "";
        String strCapo = "";
        String strTempo = "";


        Pattern pTitle = Pattern.compile("\\{title\\:\\s(.*?)\\}\\n");
        Matcher mTitle = pTitle.matcher(strCutOutMainInformation);
        if (mTitle.find()) {
            strTitle = mTitle.group(1);
        }

        Pattern pArtist = Pattern.compile("\\{artist\\:\\s(.*?)\\}\\n");
        Matcher mArtist = pArtist.matcher(strCutOutMainInformation);
        if (mArtist.find()) {
            strArtist = mArtist.group(1);
        }

        Pattern pTime = Pattern.compile("\\{time\\:\\s(.*?)\\}\\n");
        Matcher mTime = pTime.matcher(strCutOutMainInformation);
        if (mTime.find()) {
            strTime = mTime.group(1);
        }

        Pattern pCapo = Pattern.compile("\\{capo\\:\\s(.*?)\\}\\n");
        Matcher mCapo = pCapo.matcher(strCutOutMainInformation);
        if (mCapo.find()) {
            strCapo = mCapo.group(1);
        }

        Pattern pTempo = Pattern.compile("\\{tempo\\:\\s(.*?)\\}\\n");
        Matcher mTempo = pTempo.matcher(strCutOutMainInformation);
        if (mTempo.find()) {
            strTempo = mTempo.group(1);
        }

        String strEtTitle = editSong_et_changeTitle.getText().toString();
        String strEtArtist = editSong_et_changeArtist.getText().toString();
        String strEtTime = editSong_et_changeTime.getText().toString();
        String strEtCapo = editSong_et_changeCapo.getText().toString();
        String strEtTempo = editSong_et_changeTempo.getText().toString();

        if ((strTitle.equals(strEtTitle)) && (strArtist.equals(strEtArtist))
                && (strTime.equals(strEtTime)) && (strCapo.equals(strEtCapo))
                && (strTempo.equals(strEtTempo))) {
            stringBuilderNewCutOutMainInformation.append(stringBuilderCutOutMainInformation.toString());
        } else {

            String strNewTitleSequence = "{title: " + strEtTitle + "}\n";
            String strNewArtistSequence = "{artist: " + strEtArtist + "}\n";
            stringBuilderNewCutOutMainInformation.append(strNewTitleSequence);
            stringBuilderNewCutOutMainInformation.append(strNewArtistSequence);


            if (editSong_et_changeTime.getText().length() > 0) {
                String strNewTimeSequence = "{time: " + strEtTime + "}\n";
                stringBuilderNewCutOutMainInformation.append(strNewTimeSequence);
            }
            if (editSong_et_changeCapo.getText().length() > 0) {
                String strNewCapoSequence = "{capo: " + strEtCapo + "}\n";
                stringBuilderNewCutOutMainInformation.append(strNewCapoSequence);
            }
            if (editSong_et_changeTempo.getText().length() > 0) {
                String strNewTempoSequence = "{tempo: " + strEtTempo + "}\n";
                stringBuilderNewCutOutMainInformation.append(strNewTempoSequence);
            }

        }

        String strNewFilenameWithExtension = "[" + strEtArtist + "]-[" + strEtTitle + "]" + ".txt";
        stringBuilderNewFilenameWithExtension.delete(0, stringBuilderNewFilenameWithExtension.length());
        stringBuilderNewFilenameWithExtension.append(strNewFilenameWithExtension);
    }

    private void saveFile() {

        filenameWithExtension = stringBuilderNewFilenameWithExtension.toString();
        final Boolean isFilenameIsDifferent = checkIfFilenameIsDifferent(filenameWithExtension);
        final Boolean isTheFileAlreadyExistst = checkIfTheFileAlreadyExists();

        if (isFilenameIsDifferent) {
            System.out.println("Different Filename");

            if (isTheFileAlreadyExistst) {
                System.out.println("Already exists");

                AlertDialog.Builder builder = new AlertDialog.Builder(EditSongActivity.this);
                builder.setMessage("A song with this artist and title already exists. Save the file under a different artist or title.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                System.out.println("No exists");
                overwriteFile();
            }
        } else {
            System.out.println("No different");
            overwriteFile();
        }
    }

    private void overwriteFile() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        try {
                            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(intentEditSong.getData().getPath()));
                            bufwriter.write(stringBuilderNewCutOutMainInformation.toString());
                            bufwriter.write(editSong_et_contentOfFile.getText().toString());
                            bufwriter.close();
                            renameFile(stringBuilderNewFilenameWithExtension.toString());
                        } catch (Exception e) {
                            System.out.println("Error occured while attempting to write to file: " + e.getMessage());
                        }

                        setResult(RESULT_OK, null);
                        finish();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditSongActivity.this);
        builder.setMessage("Are you sure you want to overwrite this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private Boolean checkIfFilenameIsDifferent(String filename) {

        String newFilename = filename;
        File file = new File(intentEditSong.getData().getPath());

        String oldFilename = file.getName();

        if (newFilename.equals(oldFilename)) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkIfTheFileAlreadyExists() {

        Folders folders = new Folders();
        String filenameToCheck = "[" + editSong_et_changeArtist.getText().toString() + "]-[" +
                editSong_et_changeTitle.getText().toString() + "].txt";
        File f = new File(folders.getChildFolderSongs(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }

    private void renameFile(String newName) {
        Folders folders = new Folders();
        File dir = folders.getChildFolderSongs();
        File file = new File(intentEditSong.getData().getPath());
        System.out.println("dir: " + dir);
        System.out.println("file: " + file);
        System.out.println("new name: " + newName);

        if (dir.exists()) {
            File from = new File(dir, file.getName());
            File to = new File(dir, newName);
            if (from.exists())
                from.renameTo(to);
        }
    }


    private String readMainInformationAndCut(String stringOfSongFile) {

        String stringOfSongFileWithoutMainInformation = stringOfSongFile;

        Pattern pTitle = Pattern.compile("\\{title\\:\\s(.*?)\\}\\n");
        Matcher mTitle = pTitle.matcher(stringOfSongFile);
        if (mTitle.find()) {

            stringBuilderCutOutMainInformation.append(mTitle.group(0));
            editSong_et_changeTitle.setText(mTitle.group(1));
            editSong_et_changeTitle.setSelection(editSong_et_changeTitle.getText().length());


            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{title\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pArtist = Pattern.compile("\\{artist\\:\\s(.*?)\\}\\n");
        Matcher mArtist = pArtist.matcher(stringOfSongFile);
        if (mArtist.find()) {

            stringBuilderCutOutMainInformation.append(mArtist.group(0));
            editSong_et_changeArtist.setText(mArtist.group(1));
            editSong_et_changeArtist.setSelection(editSong_et_changeArtist.getText().length());

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{artist\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pTime = Pattern.compile("\\{time\\:\\s(.*?)\\}\\n");
        Matcher mTime = pTime.matcher(stringOfSongFile);
        if (mTime.find()) {

            stringBuilderCutOutMainInformation.append(mTime.group(0));
            editSong_et_changeTime.setText(mTime.group(1));
            editSong_et_changeTime.setSelection(editSong_et_changeTime.getText().length());

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{time\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pCapo = Pattern.compile("\\{capo\\:\\s(.*?)\\}\\n");
        Matcher mCapo = pCapo.matcher(stringOfSongFile);
        if (mCapo.find()) {

            stringBuilderCutOutMainInformation.append(mCapo.group(0));
            editSong_et_changeCapo.setText(mCapo.group(1));
            editSong_et_changeCapo.setSelection(editSong_et_changeCapo.getText().length());

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{capo\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pTempo = Pattern.compile("\\{tempo\\:\\s(.*?)\\}\\n");
        Matcher mTempo = pTempo.matcher(stringOfSongFile);
        if (mTempo.find()) {

            stringBuilderCutOutMainInformation.append(mTempo.group(0));
            editSong_et_changeTempo.setText(mTempo.group(1));
            editSong_et_changeTempo.setSelection(editSong_et_changeTempo.getText().length());

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{tempo\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        return stringOfSongFileWithoutMainInformation;
    }

    private void intentPreviewSong() {

        Intent intentPreviewSongActivity = new Intent
                (EditSongActivity.this, PreviewSongActivity.class);

        String contentToShareViaIntent = stringBuilderNewCutOutMainInformation.toString() + "\n" +
                editSong_et_contentOfFile.getText().toString();
        intentPreviewSongActivity.putExtra("CONTENT_OF_FILE", contentToShareViaIntent);

        startActivityForResult(intentPreviewSongActivity, REQUEST_REFRESH_AFTER_PREVIEW_A_SONG);

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
