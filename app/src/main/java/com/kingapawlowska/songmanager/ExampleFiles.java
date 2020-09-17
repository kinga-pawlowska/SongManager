package com.kingapawlowska.songmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kinga on 18.04.2018.
 */

public class ExampleFiles extends Folders {

    private File exampleSong1;
    String song_artist1 = "Riverside";
    String song_title1 = "Escalator Shrine";

    private File exampleSong2;
    String song_artist2 = "Artist2";
    String song_title2 = "Title of song2";

    private File exampleSong3;
    String song_artist3 = "Marillion";
    String song_title3 = "Estonia";

    private File exampleSong4;
    String song_artist4 = "Sprawdzony";
    String song_title4 = "Tytul4";

    // ***

    private File exampleSetlist1;
    String setlist_title1 = "Example title of setlist 1";

    private File exampleSetlist2;
    String setlist_title2 = "Example title of setlist 2";

    private File exampleSetlist3;
    String setlist_title3 = "Example title of setlist 3";

    // ***

//    private File exampleGig1;
//    String gig_title1 = "Example title of gig 1";
//
//    private File exampleGig2;
//    String gig_title2 = "Example title of gig 2";
//
//    private File exampleGig3;
//    String gig_title3 = "Example title of gig 3";
//
//    private File exampleGig4;
//    String gig_title4 = "Example title of gig 4";


    public ExampleFiles() {
        this.exampleSong1 = new File(getChildFolderSongs().toString(),"[" + song_artist1 + "]-[" + song_title1 + "]" + ".txt");
        this.exampleSong2 = new File(getChildFolderSongs().toString(),"[" + song_artist2 + "]-[" + song_title2 + "]" + ".txt");
        this.exampleSong3 = new File(getChildFolderSongs().toString(),"[" + song_artist3 + "]-[" + song_title3 + "]" + ".txt");
        this.exampleSong4 = new File(getChildFolderSongs().toString(),"[" + song_artist4 + "]-[" + song_title4 + "]" + ".txt");

        this.exampleSetlist1 = new File(getChildFolderSetlists().toString(),"[" + setlist_title1 + "]" + ".txt");
        this.exampleSetlist2 = new File(getChildFolderSetlists().toString(),"[" + setlist_title2 + "]" + ".txt");
        this.exampleSetlist3 = new File(getChildFolderSetlists().toString(),"[" + setlist_title3 + "]" + ".txt");

//        this.exampleGig1 = new File(getChildFolderGigs().toString(),"[" + gig_title1 + "]" + ".txt");
//        this.exampleGig2 = new File(getChildFolderGigs().toString(),"[" + gig_title2 + "]" + ".txt");
//        this.exampleGig3 = new File(getChildFolderGigs().toString(),"[" + gig_title3 + "]" + ".txt");
//        this.exampleGig4 = new File(getChildFolderGigs().toString(),"[" + gig_title4 + "]" + ".txt");
    }

    public void writeFiles() {

        /** Tworzenie exampleSong1 **/
        try {
            FileWriter writerExampleSong1 = new FileWriter(exampleSong1);
            writerExampleSong1.append("" +
                    "{title: " + song_title1 + "}" + "\n" +
                    "{artist: " + song_artist1 + "}" + "\n" +
                    "{tempo: 120}" + "\n" +
                    "We are escalator walkers\n" +
                    "In the brand new temple\n" +
                    "Came to reshape identities\n" +
                    "Shed our skins\n" +
                    "Be reborn\n" +
                    "And feel the same\n" +
                    "That no one here is real\n" +
                    "\n" +
                    "We are moving standees\n" +
                    "In the shrine of choices\n" +
                    "Incarcerated between floors of\n" +
                    "Hope and disappointment\n" +
                    "We feel the same\n" +
                    "That no one here is real\n" +
                    "We feel the same\n" +
                    "That nothing here is still\n" +
                    "\n" +
                    "We are stairway drifters\n" +
                    "Made of cyber paper\n" +
                    "Google boys and wiki girls\n" +
                    "Children of the self care\n" +
                    "\n" +
                    "We come to pray every single training day\n" +
                    "Looking for a chance to survive\n" +
                    "Buying reduced price illusions\n" +
                    "Floating into another light\n" +
                    "Melting into another lonely crowd\n" +
                    "\n" +
                    "We feel the same\n" +
                    "That no one here is real\n" +
                    "We feel the same\n" +
                    "That nothing here is still\n" +
                    "\n" +
                    "***\n" +
                    "Used to have our love\n" +
                    "And now\n" +
                    "Disposable needs\n" +
                    "Used to have our souls\n" +
                    "And now\n" +
                    "Refined new skins\n" +
                    "\n" +
                    "Take\n" +
                    "Use\n" +
                    "Throw Away\n" +
                    "Forget\n" +
                    "\n" +
                    "***\n" +
                    "Dragging our feet\n" +
                    "Tired and deceived\n" +
                    "Slowly moving on\n" +
                    "Bracing shaky legs\n" +
                    "Against all those wasted years\n" +
                    "We roll the boulders of sins\n" +
                    "Up a hill of new days\n" +
                    "\n" +
                    "In the arms of the setting sun\n" +
                    "Our burdens cast shadows over fiery ground\n" +
                    "Catching final rays\n" +
                    "We try to reach the journey’s end\n" +
                    "Before the sun will die\n" +
                    "\n" +
                    "We sense we’re almost there\n" +
                    "But the night comes too soon\n" +
                    "And we crawl in the dark\n" +
                    "Not ready to face up\n" +
                    "To unknowing lies\n" +
                    "We ache to go back\n" +
                    "\n" +
                    "But we can’t stop\n" +
                    "So we walk ahead");

            writerExampleSong1.flush();
            writerExampleSong1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Tworzenie exampleSong2 **/
        try {
            FileWriter writerExampleSong2 = new FileWriter(exampleSong2);
            writerExampleSong2.append("" +
                    "{title: " + song_title2 + "}" + "\n" +
                    "{artist: " + song_artist2 + "}" + "\n" +
                    "{time: 0:03:30}" + "\n" +
                    "{capo: 1}" + "\n" +
                    "{tempo: 120}" + "\n" +
                    "[B]Cos [C]tam [Dm]z examplefile2 [b6+/Db] [B]Cos [C]tam [Dm]z examplefile2 [b6+/Db]" + " [B]Cos [C]tam [Dm]z examplefile2 [b6+/Db] [B]Cos [C]tam [Dm]z examplefile2 [b6+/Db]" + "\n" +
                    "example [Fm]chord [Am]" + "\n" +
                    "Linijka bez akordów" + "\n" +
                    "{CHORUS}" + "\n" +
                    "Polski [B]akord H " +
                    "[Bm] [Am] [A7] [Bb]" + "\n" +
                    "Linijka bez akordów" + "\n" +
                    "Linijka bez akordów2 fff" + "\n");

            writerExampleSong2.flush();
            writerExampleSong2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Tworzenie exampleSong3 **/
        try {
            FileWriter writerExampleSong3 = new FileWriter(exampleSong3);
            writerExampleSong3.append("" +
                    "{title: " + song_title3 + "}" + "\n" +
                    "{artist: " + song_artist3 + "}" + "\n" +
                    "e|-----------------0-|\n" +
                    "B|-------------------|\n" +
                    "G|--------0~-----0---| (Gtr. 1)\n" +
                    "D|----0h2----2s4-----|\n" +
                    "A|-0~----------------|\n" +
                    "E|-------------------|\n" +
                    "\n" +
                    "Feeling you shake \n" +
                    "Feel your heart break \n" +
                    "Thinking if only, if only, if only, if only \n" +
                    "And the salt water runs \n" +
                    "Through your veins and your bones\n" +
                    "Telling you no not this way, not this way, not this way \n" +
                    "And you would give anything \n" +
                    "Give up everything \n" +
                    "Offer your life blood away\n" +
                    "For yesterday \n" +
                    "\n" +
                    "{CHORUS}\n" +
                    "No one [C] leaves you\n" +
                    "When you [Bb] live in their [F] heart and [Gm] mind [Bb]\n" +
                    "And no one [C] dies \n" +
                    "They just [Bb] move to the [F] other [Gm] side [Bb]\n" +
                    "When we're [C] gone\n" +
                    "Watch the [Bb] world simply [F] carry [Gm] on [Bb]\n" +
                    "We live [C] on [Bb] laughing and in [F] no pain \n" +
                    "We'll [Gm]stay and be [F] happy \n" +
                    "With [Bb] those [F] who have loved us [Am]today\n" +
                    "\n" +
                    "Finding the answer\n" +
                    "It's a human obsession\n" +
                    "But you might as well talk to the stones and the trees and the sea\n" +
                    "'Cause nobody knows \n" +
                    "And so few can see \n" +
                    "There's only beauty and caring and truth beyond darkness\n" +
                    "\n" +
                    "{CHORUS}\n" +
                    "No one leaves you \n" +
                    "When you live in their heart and mind\n" +
                    "And no one dies \n" +
                    "They just move to the other side \n" +
                    "When we're gone\n" +
                    "Watch the world simply carry on \n" +
                    "We live on laughing and in no pain \n" +
                    "We'll stay and be happy \n" +
                    "With those who have loved us today \n" +
                    "\n" +
                    "And we won't understand your grief\n" +
                    "Because time is illusion \n" +
                    "As this watery world spins around \n" +
                    "This timeless sun \n" +
                    "Will dry your eyes \n" +
                    "And calm your mind \n" +
                    "\n" +
                    "{CHORUS}\n" +
                    "No one leaves you \n" +
                    "When you live in their heart and mind \n" +
                    "And no one dies \n" +
                    "They just move to the other side\n" +
                    "When we're gone\n" +
                    "Watch the world simply carry on \n" +
                    "It's okay, we will stay and be happy \n" +
                    "Stay and be happy \n" +
                    "With those who have loved us today ");

            writerExampleSong3.flush();
            writerExampleSong3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Tworzenie exampleSong4 **/
        try {
            FileWriter writerExampleSong4 = new FileWriter(exampleSong4);
            writerExampleSong4.append("" +
                    "{title: " + song_title4 + "}" + "\n" +
                    "{artist: " + song_artist4 + "}" + "\n" +
                    "{time: 0:03:30}" + "\n" +
                    "{capo: 1}" + "\n" +
                    "{tempo: 120}" + "\n" +
                    "12345678901234567890123456789012345678901234567890" + "\n" +
                    "---" + "\n" +
                    "12345678901234567890123456789012345678901234567890" + "\n");

            writerExampleSong4.flush();
            writerExampleSong4.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // ***

        /** Tworzenie exampleSetlist1 **/
        try {
            FileWriter writerExampleSetlist1 = new FileWriter(exampleSetlist1);
            writerExampleSetlist1.append("" +
                    "{Tytuł piosenki 1},{Artysta piosenki 1};" + "\n" +
                    "{Tytuł piosenki 2},{Artysta piosenki 2};" + "\n" +
                    "{Tytuł piosenki 3},{Artysta piosenki 3};");

            writerExampleSetlist1.flush();
            writerExampleSetlist1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Tworzenie exampleSetlist2 **/
        try {
            FileWriter writerExampleSetlist2 = new FileWriter(exampleSetlist2);
            writerExampleSetlist2.append("" +
                    "{Tytuł piosenki 1},{Artysta piosenki 1};" + "\n" +
                    "{Tytuł piosenki 2},{Artysta piosenki 2};" + "\n" +
                    "{Tytuł piosenki 3},{Artysta piosenki 3};");

            writerExampleSetlist2.flush();
            writerExampleSetlist2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Tworzenie exampleSetlist3 **/
        try {
            FileWriter writerExampleSetlist3 = new FileWriter(exampleSetlist3);
            writerExampleSetlist3.append("" +
                    "{Tytuł piosenki 1},{Artysta piosenki 1};" + "\n" +
                    "{Tytuł piosenki 2},{Artysta piosenki 2};" + "\n" +
                    "{Tytuł piosenki 3},{Artysta piosenki 3};");

            writerExampleSetlist3.flush();
            writerExampleSetlist3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        /** Tworzenie exampleGig1 **/
//        try {
//            FileWriter writerExampleGig1 = new FileWriter(exampleGig1);
//            writerExampleGig1.append("" +
//                    "{day: 11};\n" +
//                    "{month: 1};\n" +
//                    "{year: 2018};\n" +
//                    "{venue: Łódź, Magnetofon};\n" +
//                    "{name: ProgRockFest};");
//
//            writerExampleGig1.flush();
//            writerExampleGig1.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        /** Tworzenie exampleGig2 **/
//        try {
//            FileWriter writerExampleGig2 = new FileWriter(exampleGig2);
//            writerExampleGig2.append("" +
//                    "{day: 13};\n" +
//                    "{month: 6};\n" +
//                    "{year: 2018};\n" +
//                    "{venue: Warszawa, Klub Progresja};\n" +
//                    "{name: ProgRockFest};");
//
//            writerExampleGig2.flush();
//            writerExampleGig2.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        /** Tworzenie exampleGig3 **/
//        try {
//            FileWriter writerExampleGig3 = new FileWriter(exampleGig3);
//            writerExampleGig3.append("" +
//                    "{day: 20};\n" +
//                    "{month: 7};\n" +
//                    "{year: 2018};\n" +
//                    "{venue: Kraków, Klub Kwadrat};\n" +
//                    "{name: ProgRockFest};");
//
//            writerExampleGig3.flush();
//            writerExampleGig3.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        /** Tworzenie exampleGig4 **/
//        try {
//            FileWriter writerExampleGig4 = new FileWriter(exampleGig4);
//            writerExampleGig4.append("" +
//                    "{day: 31};\n" +
//                    "{month: 12};\n" +
//                    "{year: 2018};\n" +
//                    "{venue: Monachium, Cultural Center};\n" +
//                    "{name: ProgRockFest};");
//
//            writerExampleGig4.flush();
//            writerExampleGig4.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
