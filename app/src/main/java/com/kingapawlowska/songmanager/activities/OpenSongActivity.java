package com.kingapawlowska.songmanager.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingapawlowska.songmanager.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OpenSongActivity extends AppCompatActivity {

    static final int REQUEST_REFRESH_AFTER_EDIT_SONG = 3001;

    int menu_options_open_song_action_decrease;
    int menu_options_open_song_action_increase;

    int id_menu_open_song_action_edit_song;
    int id_menu_open_song_action_show_title_and_info;
    int id_menu_open_song_action_delete;

    Intent intentOpenSong;
    Bundle bundle;

    String titleOfSong;
    String artistOfSong;
    String stringCutOutMainInformation; // title and artist sequence
    String stringContentOfFile;
    String dividedStringContentOfFile;
    float defaultTextSize;

    TextView openSong_tv_title;
    TextView openSong_tv_artist;
    TextView openSong_tv_time_content;
    TextView openSong_tv_time;
    TextView openSong_tv_capo_content;
    TextView openSong_tv_capo;
    TextView openSong_tv_tempo_content;
    TextView openSong_tv_tempo;
    LinearLayout openSong_linearLayout_contentOfFile;
    LinearLayout openSong_linearLayout_info;
    LinearLayout openSong_linearLayout_info_and_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_open_song);

        intentOpenSong = getIntent();
        bundle = intentOpenSong.getExtras();
        if (intentOpenSong.getData() != null) {
            titleOfSong = bundle.getString("TITLE_OF_SONG");
            artistOfSong = bundle.getString("ARTIST_OF_SONG");

            android.support.v7.app.ActionBar actionBarOpenSongActivity = getSupportActionBar();
            actionBarOpenSongActivity.setTitle(titleOfSong); // or setTitle only without subtitle
            actionBarOpenSongActivity.setSubtitle(artistOfSong);

            openSong_tv_title = findViewById(R.id.openSong_tv_title);
            openSong_tv_artist = findViewById(R.id.openSong_tv_artist);
            openSong_tv_time_content = findViewById(R.id.openSong_tv_time_content);
            openSong_tv_time = findViewById(R.id.openSong_tv_time);
            openSong_tv_capo_content = findViewById(R.id.openSong_tv_capo_content);
            openSong_tv_capo = findViewById(R.id.openSong_tv_capo);
            openSong_tv_tempo_content = findViewById(R.id.openSong_tv_tempo_content);
            openSong_tv_tempo = findViewById(R.id.openSong_tv_tempo);
            openSong_linearLayout_contentOfFile = findViewById(R.id.openSong_linearLayout_contentOfFile);
            openSong_linearLayout_info = findViewById(R.id.openSong_linearLayout_info);
            openSong_linearLayout_info_and_title = findViewById(R.id.openSong_linearLayout_info_and_title);

            File songFile = new File(intentOpenSong.getData().getPath());
            String stringOfSongFile = readTheFile(songFile);

            checkAndSetDefaultTextSize();
            stringContentOfFile = readMainInformationAndCut(stringOfSongFile);

            if(stringContentOfFile == null || stringContentOfFile.equals("") ) {}
            else {
                dividedStringContentOfFile = divideStringIntoLines(stringContentOfFile, defaultTextSize);
                setContentOfFile(dividedStringContentOfFile+"", defaultTextSize);
            }




        } else {
            // komunikat że plik nie zły, jest uszkodzony i powrót do songsActivity
        }


    }

    private void checkAndSetDefaultTextSize() {
        TextView tv_exapleElementToCheckSize = new TextView(this);
        tv_exapleElementToCheckSize.setText("___");
        tv_exapleElementToCheckSize.setTypeface(Typeface.MONOSPACE);
        openSong_linearLayout_contentOfFile.addView(tv_exapleElementToCheckSize);
        defaultTextSize = tv_exapleElementToCheckSize.getTextSize();


        openSong_linearLayout_contentOfFile.removeAllViews();
    }

    private String divideStringIntoLines(String stringContentOfFile, float textSize) {

        Scanner scanner = new Scanner(stringContentOfFile);

        if (stringContentOfFile.length() != 0) {

            float defaultTextSizeFloat = defaultTextSize;
            float actualTextSizeFloat = textSize;

            float defaultLengthOfLine;


            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                defaultLengthOfLine = 42.0f;
            }
            else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                defaultLengthOfLine = 75.0f;
            }
            else {
                defaultLengthOfLine = 42.0f;
            }

            float maxLengthOfLineFloat = (defaultTextSizeFloat/actualTextSizeFloat)*(defaultLengthOfLine-1.0f);
            int maxLengthOfLine = (int)maxLengthOfLineFloat;
            System.out.println("max length of line: " + maxLengthOfLine);

            List<StringBuilder> listOfStringBuilders= new ArrayList<>();

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                String[] arr = line.split("\\s+");

                listOfStringBuilders.add(new StringBuilder());
                int actualStringBuilder = listOfStringBuilders.size()-1;

                for (String word : arr) {
                    int actualLength = listOfStringBuilders.get(actualStringBuilder).length();
                    int lengthOfWord;

                    String newWord;

                    if(actualLength == 0) {
                        newWord = word;
                    }
                    else {
                        newWord = " " + word;
                    }
                    lengthOfWord = newWord.length();

                    System.out.println("len " + lengthOfWord);

                    String regex = "\\[([a-zA-Z0-9\\-\\+\\/\\#][^\\[\\]]*)\\]";
                    Pattern pChord = Pattern.compile(regex);
                    Matcher mChord = pChord.matcher(newWord);
                    int counterOfOccurrencesChordsInWord = 0;
                    while (mChord.find()) {
                        counterOfOccurrencesChordsInWord = counterOfOccurrencesChordsInWord + 1;
                        System.out.println("counterOfOccurrences " + counterOfOccurrencesChordsInWord);
                    }
                    int numberOfCharactersToRemoveFromWord = counterOfOccurrencesChordsInWord*2; // dla [ and ]
                    if(lengthOfWord>=0 && numberOfCharactersToRemoveFromWord >= 0) {
                        lengthOfWord = lengthOfWord - numberOfCharactersToRemoveFromWord;
                    }


                    String actualLine = listOfStringBuilders.get(actualStringBuilder).toString();
                    mChord = pChord.matcher(actualLine);
                    int counterOfOccurrencesChordsInLine = 0;
                    while (mChord.find()) {
                        counterOfOccurrencesChordsInLine = counterOfOccurrencesChordsInLine + 1;
                    }
                    int numberOfCharactersToRemoveFromLine = counterOfOccurrencesChordsInLine*2; // dla [ and ]
                    if(actualLength>=0 && numberOfCharactersToRemoveFromLine >= 0) {
                        actualLength = actualLength - numberOfCharactersToRemoveFromLine;
                    }



//                    if((lengthOfWord == 0) && (actualLength == 0)) {
////                        actualStringBuilder++;
////                        listOfStringBuilders.add(new StringBuilder());
//                        listOfStringBuilders.get(actualStringBuilder).append(word);
//                    }
                    if ((lengthOfWord > maxLengthOfLine) && (actualLength == 0)) {
                        listOfStringBuilders.get(actualStringBuilder).append(newWord);
                    }
                    else if(actualLength+lengthOfWord <= maxLengthOfLine) {
                        listOfStringBuilders.get(actualStringBuilder).append(newWord);
                    }
                    else {
                        actualStringBuilder++;
                        listOfStringBuilders.add(new StringBuilder());
                        listOfStringBuilders.get(actualStringBuilder).append(word);
                    }
                }
            }

            StringBuilder dividedStringContentOfFile = new StringBuilder();
            for(int i=0; i<listOfStringBuilders.size(); i++) {
                dividedStringContentOfFile.append(listOfStringBuilders.get(i).toString() + "\n");
                System.out.println(listOfStringBuilders.get(i).toString());
            }

            scanner.close();

            return dividedStringContentOfFile.toString();

        } else {
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_open_song, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        menu_options_open_song_action_decrease = item.getItemId();
        menu_options_open_song_action_increase = item.getItemId();

        id_menu_open_song_action_edit_song = item.getItemId();
        id_menu_open_song_action_show_title_and_info = item.getItemId();
        id_menu_open_song_action_delete = item.getItemId();

        if (menu_options_open_song_action_decrease ==
                R.id.menu_options_open_song_action_decrease) {

            decrease();
            return true;
        }
        if (menu_options_open_song_action_increase ==
                R.id.menu_options_open_song_action_increase) {

            increase();
            return true;
        }


        if (id_menu_open_song_action_edit_song ==
                R.id.id_menu_open_song_action_edit_song) {

            intentEditSong();
            return true;
        }

        if (id_menu_open_song_action_show_title_and_info ==
                R.id.id_menu_open_song_action_show_title_and_info) {

            showOrHideTitleAndInfo();
            return true;
        }

        if (id_menu_open_song_action_delete ==
                R.id.id_menu_open_song_action_delete) {

            intentRemoveSong();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * =========================================================================================
     */

    private void showOrHideTitleAndInfo() {

        if (openSong_linearLayout_info_and_title.getVisibility() == View.VISIBLE) {
            openSong_linearLayout_info_and_title.setVisibility(View.GONE);
        } else {
            openSong_linearLayout_info_and_title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_REFRESH_AFTER_EDIT_SONG) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public void intentRemoveSong() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        File file = new File(intentOpenSong.getData().getPath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            setResult(RESULT_OK, null);
                            finish();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(OpenSongActivity.this);
        builder.setMessage("Are you sure you want to delete this file?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void intentEditSong() {

        Intent intentEditSongActivity = new Intent
                (OpenSongActivity.this, EditSongActivity.class);

        String pathToTheSongFile = intentOpenSong.getData().getPath();
        File fileToShareViaIntent = new File(pathToTheSongFile);
        intentEditSongActivity.setData(Uri.fromFile(fileToShareViaIntent));

        startActivityForResult(intentEditSongActivity, REQUEST_REFRESH_AFTER_EDIT_SONG);
        setResult(RESULT_OK, null);
        finish();
    }

    public void decrease() {

//        for (int i = 0; i < openSong_linearLayout_contentOfFile.getChildCount(); i++) {
//            View child = openSong_linearLayout_contentOfFile.getChildAt(i);
//            TextView tvChild = (TextView) child;
//            System.out.println(tvChild.getTextSize() + " " + (tvChild.getTextSize() - 2));
//            tvChild.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvChild.getTextSize() - 2);
//        }
//
//        //openSong_tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, openSong_tv_title.getTextSize()-2);
//        //openSong_tv_artist.setTextSize(TypedValue.COMPLEX_UNIT_PX, openSong_tv_artist.getTextSize()-2);


        if(openSong_linearLayout_contentOfFile.getChildCount() == 0) {}
        else {
            View child = openSong_linearLayout_contentOfFile.getChildAt(0);
            TextView tvChild = (TextView) child;
            float textsize = tvChild.getTextSize();
            dividedStringContentOfFile = divideStringIntoLines(stringContentOfFile, textsize-2.0f);
            setContentOfFile(dividedStringContentOfFile, textsize-2.0f);
        }
    }

    public void increase() {

//        for (int i = 0; i < openSong_linearLayout_contentOfFile.getChildCount(); i++) {
//            View child = openSong_linearLayout_contentOfFile.getChildAt(i);
//            TextView tvChild = (TextView) child;
//            System.out.println(tvChild.getTextSize() + " " + (tvChild.getTextSize() + 2));
//            tvChild.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvChild.getTextSize() + 2);
//        }
//
//        //openSong_tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, openSong_tv_title.getTextSize()+2);
//        //openSong_tv_artist.setTextSize(TypedValue.COMPLEX_UNIT_PX, openSong_tv_artist.getTextSize()+2);

        if(openSong_linearLayout_contentOfFile.getChildCount() == 0) {}
        else {
            View child = openSong_linearLayout_contentOfFile.getChildAt(0);
            TextView tvChild = (TextView) child;
            float textsize = tvChild.getTextSize();
            dividedStringContentOfFile = divideStringIntoLines(stringContentOfFile, textsize+2.0f);
            setContentOfFile(dividedStringContentOfFile, textsize+2.0f);
        }

    }

    private void setContentOfFile(String stringContentOfFile, float textSize) {

        openSong_linearLayout_contentOfFile.removeAllViews();

        Scanner scanner = new Scanner(stringContentOfFile);

        if (stringContentOfFile.length() != 0) {

            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                /** Sprawdzanie chorusa **/
                Pattern pChorus = Pattern.compile("\\{CHORUS\\}");
                Matcher mChorus = pChorus.matcher(line);
                while (mChorus.find()) {

                    line = line.replace("{CHORUS}", "");
                    StringBuilder sbElements = new StringBuilder();
                    sbElements.append("CHORUS:");

                    /** Dodawanie chorusa do widoku**/
                    SpannableStringBuilder builderElements = new SpannableStringBuilder();
                    StyleSpan boldSpanElements = new StyleSpan(Typeface.BOLD);
                    ForegroundColorSpan foregroundSpanElements = new ForegroundColorSpan(Color.WHITE);
                    TextView tv_elements = new TextView(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builderElements.append(sbElements.toString(), boldSpanElements, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else {
                        //builderElements.append(sbElements.toString(), boldSpanElements, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderElements.append(sbElements.toString());
                        builderElements.setSpan(boldSpanElements, 0, builderElements.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    //builderElements.setSpan(new UnderlineSpan(), 0, builderElements.length(), 0);
                    builderElements.setSpan(new BackgroundColorSpan(Color.BLACK), 0, builderElements.length(), 0);
                    builderElements.setSpan(foregroundSpanElements, 0, builderElements.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_elements.setText(builderElements);
                    tv_elements.setTypeface(Typeface.MONOSPACE);
                    tv_elements.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                    openSong_linearLayout_contentOfFile.addView(tv_elements);

                }
                /** Koniec sprawdzania chorusa **/


                /** Dodawanie akordów i tekstu do widoku **/
                SpannableStringBuilder builderChords = new SpannableStringBuilder();
                SpannableStringBuilder builderText = new SpannableStringBuilder();
                StyleSpan boldSpanChords = new StyleSpan(Typeface.BOLD_ITALIC);
                ForegroundColorSpan foregroundSpanText = new ForegroundColorSpan(Color.BLACK);
                ForegroundColorSpan foregroundSpanChords = new ForegroundColorSpan(Color.RED);


                String regex = "\\[([a-zA-Z0-9\\-\\+\\/\\#][^\\[\\]]*)\\]";

                Pattern pChord = Pattern.compile(regex);
                Matcher mChord = pChord.matcher(line);

                StringBuilder sbText = new StringBuilder();
                sbText.append(line);
                StringBuilder sbChords = new StringBuilder();
                sbChords.append(line);
                StringBuilder sbText2 = new StringBuilder();
                StringBuilder sbChords2 = new StringBuilder();

                int counterOfOccurrences = 0;

                while (mChord.find()) {

                    counterOfOccurrences = counterOfOccurrences + 1;
                    int start = mChord.start(1);
                    //int end = mChord.end(1);
                    int len = mChord.group(1).length();

                    int licznikStart = start;
                    int licznikEnd = licznikStart + 1;
                    for (int i = 0; i < len; i++) {
                        sbText.replace(licznikStart, licznikEnd, " ");
                        licznikStart++;
                        licznikEnd++;
                    }
                }

                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == sbText.charAt(i)) {
                        sbChords.replace(i, i + 1, " ");
                    }
                }

                for (int j = 0; j < sbText.length(); j++) {
                    if ((sbText.charAt(j) == '[') || (sbText.charAt(j) == ']')) {

                    } else {
                        sbChords2.append(sbChords.charAt(j));
                        sbText2.append(sbText.charAt(j));
                    }
                }


                if (counterOfOccurrences == 0) {
                    TextView tv_text = new TextView(this);
                    builderText.append(sbText2.toString());
                    builderText.setSpan(foregroundSpanText, 0, builderText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_text.setText(builderText);
                    tv_text.setTypeface(Typeface.MONOSPACE);
                    tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                    openSong_linearLayout_contentOfFile.addView(tv_text);

                    System.out.println("Wielkosc czcionki: " + tv_text.getTextSize());


                } else {

                    boolean isEmpty = true;
                    for (int i = 0; i < sbText2.length(); i++) {
                        if (sbText2.charAt(i) != ' ') {
                            isEmpty = false;
                        }
                    }

                    if (isEmpty == false) {
                        TextView tv_chords = new TextView(this);
                        TextView tv_text = new TextView(this);
                        builderText.append(sbText2.toString());
                        builderText.setSpan(foregroundSpanText, 0, builderText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builderChords.append(sbChords2.toString(), boldSpanChords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        else {
                            builderChords.append(sbChords2.toString());
                            builderChords.setSpan(boldSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }


                        builderChords.setSpan(foregroundSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_chords.setText(builderChords);
                        tv_text.setText(builderText);
                        tv_chords.setTypeface(Typeface.MONOSPACE);
                        tv_text.setTypeface(Typeface.MONOSPACE);

                        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                        tv_chords.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                        openSong_linearLayout_contentOfFile.addView(tv_chords);
                        openSong_linearLayout_contentOfFile.addView(tv_text);
                    } else {
                        TextView tv_chords = new TextView(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builderChords.append(sbChords2.toString(), boldSpanChords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        else {
                            builderChords.append(sbChords2.toString());
                            builderChords.setSpan(boldSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }


                        builderChords.setSpan(foregroundSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_chords.setText(builderChords);
                        tv_chords.setTypeface(Typeface.MONOSPACE);
                        tv_chords.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                        openSong_linearLayout_contentOfFile.addView(tv_chords);
                    }
                }
            }

            scanner.close();

        } else {
            // String jest pusty
            //openSong_tv_contentOfFile.append("Text is empty. You must edit this file.");
        }
    }

    private String readMainInformationAndCut(String stringOfSongFile) {

        String stringOfSongFileWithoutMainInformation = stringOfSongFile;
        StringBuilder stringBuilderCutOutMainInformation = new StringBuilder();

        Pattern pTitle = Pattern.compile("\\{title\\:\\s(.*?)\\}\\n");
        Matcher mTitle = pTitle.matcher(stringOfSongFile);
        if (mTitle.find()) {

            String strTitle = mTitle.group(1);
            openSong_tv_title.setText(strTitle);

            stringBuilderCutOutMainInformation.append(mTitle.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{title\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pArtist = Pattern.compile("\\{artist\\:\\s(.*?)\\}\\n");
        Matcher mArtist = pArtist.matcher(stringOfSongFile);
        if (mArtist.find()) {

            String strArtist = mArtist.group(1);
            openSong_tv_artist.setText(strArtist);

            stringBuilderCutOutMainInformation.append(mArtist.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{artist\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Boolean ifExistAdditionalInfo = false;

        Pattern pTime = Pattern.compile("\\{time\\:\\s(.*?)\\}\\n");
        Matcher mTime = pTime.matcher(stringOfSongFile);
        if (mTime.find()) {

            ifExistAdditionalInfo = true;

            String strTime = mTime.group(1);
            openSong_tv_time_content.setText(strTime);

            stringBuilderCutOutMainInformation.append(mTime.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{time\\:\\s(.*?)\\}\\n", "");
        } else {
            openSong_tv_time_content.setVisibility(View.INVISIBLE);
            openSong_tv_time.setVisibility(View.INVISIBLE);
        }

        Pattern pCapo = Pattern.compile("\\{capo\\:\\s(.*?)\\}\\n");
        Matcher mCapo = pCapo.matcher(stringOfSongFile);
        if (mCapo.find()) {

            ifExistAdditionalInfo = true;

            String strCapo = mCapo.group(1);
            openSong_tv_capo_content.setText(strCapo);

            stringBuilderCutOutMainInformation.append(mCapo.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{capo\\:\\s(.*?)\\}\\n", "");
        } else {
            openSong_tv_capo_content.setVisibility(View.INVISIBLE);
            openSong_tv_capo.setVisibility(View.INVISIBLE);
        }

        Pattern pTempo = Pattern.compile("\\{tempo\\:\\s(.*?)\\}\\n");
        Matcher mTempo = pTempo.matcher(stringOfSongFile);
        if (mTempo.find()) {

            ifExistAdditionalInfo = true;

            String strTempo = mTempo.group(1);
            openSong_tv_tempo_content.setText(strTempo);

            stringBuilderCutOutMainInformation.append(mTempo.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{tempo\\:\\s(.*?)\\}\\n", "");
        } else {
            openSong_tv_tempo_content.setVisibility(View.INVISIBLE);
            openSong_tv_tempo.setVisibility(View.INVISIBLE);
        }

        if (ifExistAdditionalInfo) {
            openSong_linearLayout_info.setVisibility(View.VISIBLE);
        } else {
            openSong_linearLayout_info.setVisibility(View.GONE);
        }


        stringCutOutMainInformation = stringBuilderCutOutMainInformation.toString();

        return stringOfSongFileWithoutMainInformation;
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


}
