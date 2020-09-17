package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditGigActivity extends AppCompatActivity implements TextWatcher {

    int menu_options_edit_gig_action_save_file;
    DatePicker edit_gig_datePicker_date;
    EditText edit_gig_et_venue;
    EditText edit_gig_et_name;
    Button edit_gig_btn_save;

    Intent intentEditGig;

    String filename = "";
    StringBuilder stringBuilderContent;
    Boolean isFilledTheForm = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_gig);

        edit_gig_datePicker_date = findViewById(R.id.edit_gig_datePicker_date);
        edit_gig_et_venue = findViewById(R.id.edit_gig_et_venue);
        edit_gig_et_name = findViewById(R.id.edit_gig_et_name);
        edit_gig_btn_save = findViewById(R.id.edit_gig_btn_save);

        stringBuilderContent = new StringBuilder();

        edit_gig_btn_save.setEnabled(false);
        edit_gig_et_venue.addTextChangedListener(this);
        edit_gig_et_name.addTextChangedListener(this);


        intentEditGig = getIntent();

        File gigFile = new File(intentEditGig.getData().getPath());
        String stringOfGigFile = readTheFile(gigFile);
        System.out.println(stringOfGigFile);

        readMainInformationAndSet(stringOfGigFile);

        edit_gig_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_gig, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_edit_gig_action_save_file = item.getItemId();

        if (menu_options_edit_gig_action_save_file ==
                R.id.menu_options_edit_gig_action_save_file) {

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
        if ((edit_gig_et_venue.getText().length() > 0) && (edit_gig_et_name.getText().length() > 0)) {
            edit_gig_btn_save.setEnabled(true);
            isFilledTheForm = true;
        } else if (edit_gig_et_venue.getText().length() <= 0) {
            edit_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            edit_gig_et_venue.setError("This field is required");
        } else if (edit_gig_et_name.getText().length() <= 0) {
            edit_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            edit_gig_et_name.setError("This field is required");
        } else {
            edit_gig_btn_save.setEnabled(false);
            isFilledTheForm = false;
            edit_gig_et_venue.setError("This field is required");
            edit_gig_et_name.setError("This field is required");
        }
    }

    /** ========================================================================================= */

    private void readMainInformationAndSet(String stringOfGigFile) {

        Pattern pDay = Pattern.compile("\\{day\\:\\s(\\d+)\\}\\;\\n");
        Matcher mDay = pDay.matcher(stringOfGigFile);
        Pattern pMonth = Pattern.compile("\\{month\\:\\s(\\d+)\\}\\;\\n");
        Matcher mMonth = pMonth.matcher(stringOfGigFile);
        Pattern pYear = Pattern.compile("\\{year\\:\\s(\\d+)\\}\\;\\n");
        Matcher mYear = pYear.matcher(stringOfGigFile);
        if (mDay.find() && mMonth.find() && mYear.find()) {
            int year = Integer.parseInt(mYear.group(1));
            int month = Integer.parseInt(mMonth.group(1));
            month = month - 1;
            int day = Integer.parseInt(mDay.group(1));
            edit_gig_datePicker_date.init(year, month, day, null);
        } else {
        }

        Pattern pVenue = Pattern.compile("\\{venue\\:\\s(.+)\\}\\;\\n");
        Matcher mVenue = pVenue.matcher(stringOfGigFile);
        if (mVenue.find()) {
            String venue = mVenue.group(1);
            edit_gig_et_venue.setText(venue);
            edit_gig_et_venue.setSelection(edit_gig_et_venue.getText().length());
        } else {
        }

        Pattern pName = Pattern.compile("\\{name\\:\\s(.+)\\}\\;\\n");
        Matcher mName = pName.matcher(stringOfGigFile);
        if (mName.find()) {
            String name = mName.group(1);
            edit_gig_et_name.setText(name);
            edit_gig_et_name.setSelection(edit_gig_et_name.getText().length());
        } else {
        }

    }

    private void saveFile() {

        if (isFilledTheForm) {

            int day = edit_gig_datePicker_date.getDayOfMonth();
            int month = edit_gig_datePicker_date.getMonth() + 1;
            int year = edit_gig_datePicker_date.getYear();
            String venue = edit_gig_et_venue.getText().toString();
            String name = edit_gig_et_name.getText().toString();

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
            createContentOfFile(day, month, year, venue, name);

            final Boolean isFilenameIsDifferent = checkIfFilenameIsDifferent(filename);
            final Boolean isTheFileAlreadyExistst = checkIfTheFileAlreadyExists();

            if (isFilenameIsDifferent) {
                System.out.println("Different Filename");

                if (isTheFileAlreadyExistst) {
                    System.out.println("Already exists");

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditGigActivity.this);
                    builder.setMessage("A gig with this date and venue already exists. Save the file under a different date or venue.")
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
    }

    private void overwriteFile() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        try {
                            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(intentEditGig.getData().getPath()));
                            bufwriter.write(stringBuilderContent.toString());
                            bufwriter.close();

                            renameFile(filename);

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

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditGigActivity.this);
        builder.setMessage("Are you sure you want to overwrite this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void createContentOfFile(int day, int month, int year, String venue, String name) {
        stringBuilderContent.delete(0, stringBuilderContent.length());
        String strDay = "{day: " + day + "};" + "\n";
        stringBuilderContent.append(strDay);
        String strMonth = "{month: " + month + "};" + "\n";
        stringBuilderContent.append(strMonth);
        String strYear = "{year: " + year + "};" + "\n";
        stringBuilderContent.append(strYear);
        String strVenue = "{venue: " + venue + "};" + "\n";
        stringBuilderContent.append(strVenue);
        String strName = "{name: " + name + "};" + "\n";
        stringBuilderContent.append(strName);
    }

    private Boolean checkIfFilenameIsDifferent(String filename) {

        String newFilename = "[" + filename + "]" + ".txt";
        File file = new File(intentEditGig.getData().getPath());

        String oldFilename = file.getName();

        if (newFilename.equals(oldFilename)) {
            return false;
        } else {
            return true;
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

    private void renameFile(String newFilename) {
        Folders folders = new Folders();
        File dir = folders.getChildFolderGigs();
        File file = new File(intentEditGig.getData().getPath());

        if (dir.exists()) {
            File from = new File(dir, file.getName());
            String newFilenameWithExtension = "[" + newFilename + "]" + ".txt";
            File to = new File(dir, newFilenameWithExtension);
            if (from.exists())
                from.renameTo(to);
        }
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
        fin.close();
        return ret;
    }


}
