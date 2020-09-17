package com.kingapawlowska.songmanager.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kingapawlowska.songmanager.Folders;
import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.setlists_part.RecyclerListAdapter;
import com.kingapawlowska.songmanager.setlists_part.SetlistItemModel;
import com.kingapawlowska.songmanager.touch_helper.OnStartDragListener;
import com.kingapawlowska.songmanager.touch_helper.SimpleItemTouchHelperCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditSetlistActivity extends AppCompatActivity implements OnStartDragListener, TextWatcher {

    int menu_options_edit_setlist_action_instert_element;
    int menu_options_edit_setlist_action_change_filename;
    int menu_options_edit_setlist_action_save_file;
    LinearLayout editSetlist_linearLayout_newSong;
    LinearLayout editSetlist_linearLayout_change_filename;
    EditText editSetlist_et_artist;
    EditText editSetlist_et_title;
    EditText editSetlist_et_change_filename;
    Button editSetlist_btn_add;

    ItemTouchHelper mItemTouchHelper;
    List<SetlistItemModel> setlistItems = new ArrayList<SetlistItemModel>();
    RecyclerListAdapter adapter;
    RecyclerView recyclerView;

    Intent intentEditSetlist;
    Bundle bundle;

    String filename = "";
    StringBuilder stringBuilderNewSchemeOfTheFileContent;
    Boolean isFilledTheForm = false;
    Boolean isFilenameIsValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_setlist);

        editSetlist_linearLayout_newSong = findViewById(R.id.editSetlist_linearLayout_newSong);
        editSetlist_linearLayout_newSong.setVisibility(View.GONE);
        editSetlist_et_artist = findViewById(R.id.editSetlist_et_artist);
        editSetlist_et_title = findViewById(R.id.editSetlist_et_title);
        editSetlist_btn_add = findViewById(R.id.editSetlist_btn_add);
        editSetlist_linearLayout_change_filename = findViewById(R.id.editSetlist_linearLayout_change_filename);
        editSetlist_linearLayout_change_filename.setVisibility(View.GONE);
        editSetlist_et_change_filename = findViewById(R.id.editSetlist_et_change_filename);

        editSetlist_et_artist.addTextChangedListener(this);
        editSetlist_et_title.addTextChangedListener(this);
        editSetlist_et_change_filename.addTextChangedListener(this);

        stringBuilderNewSchemeOfTheFileContent = new StringBuilder();

        intentEditSetlist = getIntent();
        bundle = intentEditSetlist.getExtras();

        if (intentEditSetlist.getData() != null) {

            File setlistFile = new File(intentEditSetlist.getData().getPath());
            String stringOfSetlistFile = readTheFile(setlistFile);

            getFilenameAndSet();

            /** Dodanie piosenek z setlisty do listy */
            setlistItems = getSetlistElements(stringOfSetlistFile);
            adapter = new RecyclerListAdapter(this, this, setlistItems);

            recyclerView = (RecyclerView) findViewById(R.id.content);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);

        }

        editSetlist_btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilledTheForm) {
                    adapter.addItem(
                            editSetlist_et_title.getText().toString(),
                            editSetlist_et_artist.getText().toString()
                    );
                    adapter.notifyDataSetChanged();
                    editSetlist_et_title.setText(null);
                    editSetlist_et_artist.setText(null);
                    editSetlist_linearLayout_newSong.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_setlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_edit_setlist_action_instert_element = item.getItemId();
        menu_options_edit_setlist_action_change_filename = item.getItemId();
        menu_options_edit_setlist_action_save_file = item.getItemId();

        if (menu_options_edit_setlist_action_instert_element ==
                R.id.menu_options_edit_setlist_action_instert_element) {


            if (editSetlist_linearLayout_newSong.getVisibility() == View.VISIBLE) {
                editSetlist_linearLayout_newSong.setVisibility(View.GONE);
            } else if (editSetlist_linearLayout_newSong.getVisibility() == View.GONE) {
                editSetlist_linearLayout_newSong.setVisibility(View.VISIBLE);
            }

            return true;
        }

        if (menu_options_edit_setlist_action_change_filename ==
                R.id.menu_options_edit_setlist_action_change_filename) {


            changeFilename();

            return true;
        }

        if (menu_options_edit_setlist_action_save_file ==
                R.id.menu_options_edit_setlist_action_save_file) {

            if (isFilenameIsValid) {
                saveFile();
            } else {
                // komunikat ze zla nazwa
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

        if ((editSetlist_et_artist.getText().length() > 0) && (editSetlist_et_title.getText().length() > 0)) {
            isFilledTheForm = true;
        } else if (editSetlist_et_artist.getText().length() <= 0) {
            isFilledTheForm = false;
            editSetlist_et_artist.setError("This field is required");
        } else if (editSetlist_et_title.getText().length() <= 0) {
            isFilledTheForm = false;
            editSetlist_et_title.setError("This field is required");
        } else {
            isFilledTheForm = false;
            editSetlist_et_artist.setError("This field is required");
            editSetlist_et_title.setError("This field is required");
        }


        if (editSetlist_et_change_filename.getText().length() > 0) {
            isFilenameIsValid = true;
        } else {
            isFilenameIsValid = false;
            editSetlist_et_change_filename.setError("This field is required");
        }

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /** ========================================================================================= */

    private void changeFilename() {

        if (editSetlist_linearLayout_change_filename.getVisibility() == View.VISIBLE) {
            editSetlist_linearLayout_change_filename.setVisibility(View.GONE);
        } else if (editSetlist_linearLayout_change_filename.getVisibility() == View.GONE) {
            editSetlist_linearLayout_change_filename.setVisibility(View.VISIBLE);
        }
    }

    private void getFilenameAndSet() {

        File setlistFile = new File(intentEditSetlist.getData().getPath());
        String filename = setlistFile.getName();

        Pattern pFilename = Pattern.compile("\\[(.*?)\\]\\.txt");
        Matcher mFilename = pFilename.matcher(filename);
        if (mFilename.find()) {

            editSetlist_et_change_filename.setText(mFilename.group(1));
            editSetlist_et_change_filename.setSelection(editSetlist_et_change_filename.getText().length());
        } else {
        }
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


    private void renameFile() {

        Folders folders = new Folders();
        File dir = folders.getChildFolderSetlists();
        File file = new File(intentEditSetlist.getData().getPath());

        String newName;
        if (isFilenameIsValid) {
            newName = "[" + editSetlist_et_change_filename.getText().toString() + "].txt";
        } else {
            newName = file.getName();
        }

        if (dir.exists()) {
            File from = new File(dir, file.getName());
            File to = new File(dir, newName);
            if (from.exists())
                from.renameTo(to);
        }
    }

    private void saveFile() {

        List<SetlistItemModel> setlistItemsList = adapter.getmItems();

        stringBuilderNewSchemeOfTheFileContent.delete(0, stringBuilderNewSchemeOfTheFileContent.length());
        for (int i = 0; i < setlistItemsList.size(); i++) {
            System.out.println("Element: " + setlistItemsList.get(i).getTitle() + " - " + setlistItemsList.get(i).getArtist());
            String row = "{" + setlistItemsList.get(i).getTitle() + "},{" + setlistItemsList.get(i).getArtist() + "};" + "\n";
            stringBuilderNewSchemeOfTheFileContent.append(row);
        }

        //overwriteFile();

        filename = editSetlist_et_change_filename.getText().toString();
        final Boolean isFilenameIsDifferent = checkIfFilenameIsDifferent(filename);
        final Boolean isTheFileAlreadyExistst = checkIfTheFileAlreadyExists();

        if (isFilenameIsDifferent) {
            System.out.println("Different Filename");

            if (isTheFileAlreadyExistst) {
                System.out.println("Already exists");

                AlertDialog.Builder builder = new AlertDialog.Builder(EditSetlistActivity.this);
                builder.setMessage("A setlist with this filename already exists. Save the file under a different name.")
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
                            BufferedWriter bufwriter = new BufferedWriter(new FileWriter(intentEditSetlist.getData().getPath()));
                            bufwriter.write(stringBuilderNewSchemeOfTheFileContent.toString());
                            bufwriter.close();
                            renameFile();
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

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditSetlistActivity.this);
        builder.setMessage("Are you sure you want to overwrite this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private Boolean checkIfFilenameIsDifferent(String filename) {

        String newFilename = "[" + filename + "]" + ".txt";
        File file = new File(intentEditSetlist.getData().getPath());

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
        File f = new File(folders.getChildFolderSetlists(), filenameToCheck + "");

        if (f.exists() && !f.isDirectory()) {
            return true;
        }

        return false;
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
        fin.close();
        return ret;
    }
}
