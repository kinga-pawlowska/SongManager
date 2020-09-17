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
import com.kingapawlowska.songmanager.setlists_part.SetlistsAddANewSetlist;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddASetlistActivity extends AppCompatActivity implements TextWatcher {

    int menu_options_add_a_setlist_action_save;
    EditText add_a_setlist_et_filename;
    Button add_a_setlist_btn_save;
    Boolean isFilledTheForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_a_setlist);

        add_a_setlist_et_filename = findViewById(R.id.add_a_setlist_et_filename);
        add_a_setlist_btn_save = findViewById(R.id.add_a_setlist_btn_save);

        add_a_setlist_btn_save.setEnabled(false);
        add_a_setlist_et_filename.addTextChangedListener(this);

        add_a_setlist_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_a_setlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_add_a_setlist_action_save = item.getItemId();

        if (menu_options_add_a_setlist_action_save ==
                R.id.menu_options_add_a_setlist_action_save) {

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

        if (add_a_setlist_et_filename.getText().length() > 0) {
            add_a_setlist_btn_save.setEnabled(true);
            isFilledTheForm = true;
        } else {
            add_a_setlist_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_setlist_et_filename.setError("This field is required");
        }
    }

    /**
     * =========================================================================================
     */

    private void saveFile() {

        if (isFilledTheForm) {
            String filename = add_a_setlist_et_filename.getText().toString();

            Boolean isPossibleToSaveFile = !checkIfTheFileAlreadyExists();

            if (isPossibleToSaveFile) {

                SetlistsAddANewSetlist newSetlistFile = new SetlistsAddANewSetlist(filename);
                newSetlistFile.createAFile();

                setResult(RESULT_OK, null);
                finish();
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddASetlistActivity.this);
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

        String filenameToCheck = "[" + add_a_setlist_et_filename.getText().toString() + "].txt";
        File f = new File(folders.getChildFolderSetlists(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }
}
