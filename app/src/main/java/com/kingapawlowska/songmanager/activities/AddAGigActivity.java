package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.gigs_part.GigsAddANewGig;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAGigActivity extends AppCompatActivity implements TextWatcher {

    int menu_options_add_a_gig_action_save;
    DatePicker add_a_gig_datePicker_date;
    EditText add_a_gig_et_venue;
    EditText add_a_gig_et_name;
    Button add_a_gig_btn_save;

    String filename = "";
    Boolean isFilledTheForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_a_gig);

        add_a_gig_datePicker_date = findViewById(R.id.add_a_gig_datePicker_date);
        add_a_gig_et_venue = findViewById(R.id.add_a_gig_et_venue);
        add_a_gig_et_name = findViewById(R.id.add_a_gig_et_name);
        add_a_gig_btn_save = findViewById(R.id.add_a_gig_btn_save);

        add_a_gig_btn_save.setEnabled(false);
        add_a_gig_et_venue.addTextChangedListener(this);
        add_a_gig_et_name.addTextChangedListener(this);

        add_a_gig_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_a_gig, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_add_a_gig_action_save = item.getItemId();

        if (menu_options_add_a_gig_action_save ==
                R.id.menu_options_add_a_gig_action_save) {

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
        if ((add_a_gig_et_venue.getText().length() > 0) && (add_a_gig_et_name.getText().length() > 0)) {
            add_a_gig_btn_save.setEnabled(true);
            isFilledTheForm = true;
        } else if (add_a_gig_et_venue.getText().length() <= 0) {
            add_a_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_gig_et_venue.setError("This field is required");
        } else if (add_a_gig_et_name.getText().length() <= 0) {
            add_a_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_gig_et_name.setError("This field is required");
        } else {
            add_a_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            add_a_gig_et_venue.setError("This field is required");
            add_a_gig_et_name.setError("This field is required");
        }
    }

    /**
     * =========================================================================================
     */

    private void saveFile() {

        if (isFilledTheForm) {

            int day = add_a_gig_datePicker_date.getDayOfMonth();
            int month = add_a_gig_datePicker_date.getMonth() + 1;
            int year = add_a_gig_datePicker_date.getYear();
            String venue = add_a_gig_et_venue.getText().toString();
            String name = add_a_gig_et_name.getText().toString();

            String strDay, strMonth, strYear;
            strYear = year + "";
            if (day < 10) {
                strDay = "0" + day;
            } else {
                strDay = day + "";
            }
            if (month < 10) {
                strMonth = "0" + month;
            } else {
                strMonth = month + "";
            }

            filename = strYear + strMonth + strDay + "_" + venue;

            Boolean isPossibleToSaveFile = !checkIfTheFileAlreadyExists();

            if (isPossibleToSaveFile) {

                GigsAddANewGig newGigFile = new GigsAddANewGig(day, month, year, venue, name, filename);
                newGigFile.createAFile();

                setResult(RESULT_OK, null);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAGigActivity.this);
                builder.setMessage("A file with this date and venue already exists. Save the file under a different date or venue.")
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
        String filenameToCheck = "[" + filename + "].txt";
        File f = new File(folders.getChildFolderGigs(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
    }
}
