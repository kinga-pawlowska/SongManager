package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DuplicateSetlistActivity extends AppCompatActivity implements TextWatcher {

    int menu_options_duplicate_setlist_action_save;
    EditText duplicate_setlist_et_filename;
    Button duplicate_setlist_btn_save;

    Intent intentDuplicateSetlist;
    Bundle bundle;

    Boolean isFilledTheForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_duplicate_setlist);

        duplicate_setlist_et_filename = findViewById(R.id.duplicate_setlist_et_filename);
        duplicate_setlist_btn_save = findViewById(R.id.duplicate_setlist_btn_save);

        duplicate_setlist_btn_save.setEnabled(false);

        intentDuplicateSetlist = getIntent();
        bundle = intentDuplicateSetlist.getExtras();
        if (intentDuplicateSetlist.getData() != null) {

            duplicate_setlist_btn_save.setEnabled(true);
            isFilledTheForm = true;
            String filename = bundle.getString("TITLE_OF_SETLIST");
            duplicate_setlist_et_filename.setText(filename);
            duplicate_setlist_et_filename.setSelection(duplicate_setlist_et_filename.length());
        }

        duplicate_setlist_et_filename.addTextChangedListener(this);

        duplicate_setlist_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_duplicate_setlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_duplicate_setlist_action_save = item.getItemId();

        if (menu_options_duplicate_setlist_action_save ==
                R.id.menu_options_duplicate_setlist_action_save) {

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

        if (duplicate_setlist_et_filename.getText().length() > 0) {
            duplicate_setlist_btn_save.setEnabled(true);
            isFilledTheForm = true;
        } else {
            duplicate_setlist_btn_save.setEnabled(false);
            isFilledTheForm = false;
            duplicate_setlist_et_filename.setError("This field is required");
        }

    }
    /** ========================================================================================= */

    private void saveFile() {
        if (isFilledTheForm) {
            Boolean isPossibleToSaveFile = !checkIfTheFileAlreadyExists();
            if (isPossibleToSaveFile) {

                Folders folders = new Folders();
                File oldFile = new File(intentDuplicateSetlist.getData().getPath());
                String newFilename = "[" + duplicate_setlist_et_filename.getText().toString() + "]" + ".txt";
                File newFile = new File(folders.getChildFolderSetlists(), newFilename + "");

                try {
                    copyFile(oldFile, newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setResult(RESULT_OK, null);
                finish();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(DuplicateSetlistActivity.this);
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

        String filenameToCheck = "[" + duplicate_setlist_et_filename.getText().toString() + "].txt";
        File f = new File(folders.getChildFolderSetlists(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
