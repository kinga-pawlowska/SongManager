package com.kingapawlowska.songmanager.activities;

import android.app.ActionBar;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kingapawlowska.songmanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreviewSongActivity extends AppCompatActivity {

    Intent intentPreviewSong;
    Bundle bundle;

    String titleOfSong;
    String artistOfSong;
    String stringCutOutMainInformation; // title and artist sequence
    String stringContentOfFile;
    String dividedStringContentOfFile;
    float defaultTextSize;

    TextView previewSong_tv_title;
    TextView previewSong_tv_artist;
    TextView previewSong_tv_time_content;
    TextView previewSong_tv_time;
    TextView previewSong_tv_capo_content;
    TextView previewSong_tv_capo;
    TextView previewSong_tv_tempo_content;
    TextView previewSong_tv_tempo;
    LinearLayout previewSong_linearLayout_contentOfFile;
    LinearLayout previewSong_linearLayout_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview_song);

        intentPreviewSong = getIntent();
        bundle = intentPreviewSong.getExtras();
        if (bundle != null) {

            String stringOfSongFile = intentPreviewSong.getStringExtra("CONTENT_OF_FILE");

            previewSong_tv_title = findViewById(R.id.previewSong_tv_title);
            previewSong_tv_artist = findViewById(R.id.previewSong_tv_artist);
            previewSong_tv_time_content = findViewById(R.id.previewSong_tv_time_content);
            previewSong_tv_time = findViewById(R.id.previewSong_tv_time);
            previewSong_tv_capo_content = findViewById(R.id.previewSong_tv_capo_content);
            previewSong_tv_capo = findViewById(R.id.previewSong_tv_capo);
            previewSong_tv_tempo_content = findViewById(R.id.previewSong_tv_tempo_content);
            previewSong_tv_tempo = findViewById(R.id.previewSong_tv_tempo);
            previewSong_linearLayout_contentOfFile = findViewById(R.id.previewSong_linearLayout_contentOfFile);
            previewSong_linearLayout_info = findViewById(R.id.previewSong_linearLayout_info);


            checkAndSetDefaultTextSize();
            stringContentOfFile = readMainInformationAndCut(stringOfSongFile);

            if(stringContentOfFile == null || stringContentOfFile.equals("") ) {}
            else {
                dividedStringContentOfFile = divideStringIntoLines(stringContentOfFile, defaultTextSize);
                setContentOfFile(dividedStringContentOfFile+"", defaultTextSize);
            }

        } else {
        }

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

    private void checkAndSetDefaultTextSize() {
        TextView tv_exapleElementToCheckSize = new TextView(this);
        tv_exapleElementToCheckSize.setText("___");
        tv_exapleElementToCheckSize.setTypeface(Typeface.MONOSPACE);
        previewSong_linearLayout_contentOfFile.addView(tv_exapleElementToCheckSize);
        defaultTextSize = tv_exapleElementToCheckSize.getTextSize();
        previewSong_linearLayout_contentOfFile.removeAllViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /** ========================================================================================= */

    private String readMainInformationAndCut(String stringOfSongFile) {

        String stringOfSongFileWithoutMainInformation = stringOfSongFile;
        StringBuilder stringBuilderCutOutMainInformation = new StringBuilder();

        Pattern pTitle = Pattern.compile("\\{title\\:\\s(.*?)\\}\\n");
        Matcher mTitle = pTitle.matcher(stringOfSongFile);
        if (mTitle.find()) {

            String strTitle = mTitle.group(1);
            previewSong_tv_title.setText(strTitle);

            stringBuilderCutOutMainInformation.append(mTitle.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{title\\:\\s(.*?)\\}\\n", "");
        } else {
        }

        Pattern pArtist = Pattern.compile("\\{artist\\:\\s(.*?)\\}\\n");
        Matcher mArtist = pArtist.matcher(stringOfSongFile);
        if (mArtist.find()) {

            String strArtist = mArtist.group(1);
            previewSong_tv_artist.setText(strArtist);

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
            previewSong_tv_time_content.setText(strTime);

            stringBuilderCutOutMainInformation.append(mTime.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{time\\:\\s(.*?)\\}\\n", "");
        } else {
            previewSong_tv_time_content.setVisibility(View.INVISIBLE);
            previewSong_tv_time.setVisibility(View.INVISIBLE);
        }

        Pattern pCapo = Pattern.compile("\\{capo\\:\\s(.*?)\\}\\n");
        Matcher mCapo = pCapo.matcher(stringOfSongFile);
        if (mCapo.find()) {

            ifExistAdditionalInfo = true;

            String strCapo = mCapo.group(1);
            previewSong_tv_capo_content.setText(strCapo);

            stringBuilderCutOutMainInformation.append(mCapo.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{capo\\:\\s(.*?)\\}\\n", "");
        } else {
            previewSong_tv_capo_content.setVisibility(View.INVISIBLE);
            previewSong_tv_capo.setVisibility(View.INVISIBLE);
        }

        Pattern pTempo = Pattern.compile("\\{tempo\\:\\s(.*?)\\}\\n");
        Matcher mTempo = pTempo.matcher(stringOfSongFile);
        if (mTempo.find()) {

            ifExistAdditionalInfo = true;

            String strTempo = mTempo.group(1);
            previewSong_tv_tempo_content.setText(strTempo);

            stringBuilderCutOutMainInformation.append(mTempo.group(0));

            stringOfSongFileWithoutMainInformation = stringOfSongFileWithoutMainInformation.
                    replaceAll("\\{tempo\\:\\s(.*?)\\}\\n", "");
        } else {
            previewSong_tv_tempo_content.setVisibility(View.INVISIBLE);
            previewSong_tv_tempo.setVisibility(View.INVISIBLE);
        }

        if(ifExistAdditionalInfo) {
            previewSong_linearLayout_info.setVisibility(View.VISIBLE);
        }
        else {
            previewSong_linearLayout_info.setVisibility(View.GONE);
        }


        stringCutOutMainInformation = stringBuilderCutOutMainInformation.toString();

        return stringOfSongFileWithoutMainInformation;
    }

    private void setContentOfFile(String stringContentOfFile, float textSize) {

        previewSong_linearLayout_contentOfFile.removeAllViews();

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
                    builderElements.append(sbElements.toString(), boldSpanElements, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //builderElements.setSpan(new UnderlineSpan(), 0, builderElements.length(), 0);
                    builderElements.setSpan(new BackgroundColorSpan(Color.BLACK), 0, builderElements.length(), 0);
                    builderElements.setSpan(foregroundSpanElements, 0, builderElements.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_elements.setText(builderElements);
                    tv_elements.setTypeface(Typeface.MONOSPACE);
                    tv_elements.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                    previewSong_linearLayout_contentOfFile.addView(tv_elements);

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
                    int licznikEnd = licznikStart+1;
                    for(int i=0; i<len; i++)
                    {
                        sbText.replace(licznikStart, licznikEnd, " " );
                        licznikStart++;
                        licznikEnd++;
                    }
                }

                for(int i=0; i<line.length(); i++) {
                    if(line.charAt(i) == sbText.charAt(i)) {
                        sbChords.replace(i, i+1, " ");
                    }
                }

                for(int j=0; j<sbText.length(); j++) {
                    if((sbText.charAt(j) == '[') || (sbText.charAt(j) == ']')) {

                    }
                    else {
                        sbChords2.append(sbChords.charAt(j));
                        sbText2.append(sbText.charAt(j));
                    }
                }



                if(counterOfOccurrences == 0) {
                    TextView tv_text = new TextView(this);
                    builderText.append(sbText2.toString());
                    builderText.setSpan(foregroundSpanText, 0, builderText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_text.setText(builderText);
                    tv_text.setTypeface(Typeface.MONOSPACE);
                    tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                    previewSong_linearLayout_contentOfFile.addView(tv_text);
                }
                else {

                    boolean isEmpty = true;
                    for(int i=0; i<sbText2.length(); i++) {
                        if(sbText2.charAt(i) != ' ') {
                            isEmpty = false;
                        }
                    }

                    if(isEmpty==false) {
                        TextView tv_chords = new TextView(this);
                        TextView tv_text = new TextView(this);
                        builderText.append(sbText2.toString());
                        builderText.setSpan(foregroundSpanText, 0, builderText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderChords.append(sbChords2.toString(), boldSpanChords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderChords.setSpan(foregroundSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_chords.setText(builderChords);
                        tv_text.setText(builderText);
                        tv_chords.setTypeface(Typeface.MONOSPACE);
                        tv_text.setTypeface(Typeface.MONOSPACE);

                        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                        tv_chords.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
                        previewSong_linearLayout_contentOfFile.addView(tv_chords);
                        previewSong_linearLayout_contentOfFile.addView(tv_text);
                    }
                    else {
                        TextView tv_chords = new TextView(this);
                        builderChords.append(sbChords2.toString(), boldSpanChords, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderChords.setSpan(foregroundSpanChords, 0, builderChords.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_chords.setText(builderChords);
                        tv_chords.setTypeface(Typeface.MONOSPACE);
                        tv_chords.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                        previewSong_linearLayout_contentOfFile.addView(tv_chords);
                    }
                }
            }

            scanner.close();

        } else {
            // String jest pusty
            //previewSong_tv_contentOfFile.append("Text is empty. You must edit this file.");
        }
    }
}
