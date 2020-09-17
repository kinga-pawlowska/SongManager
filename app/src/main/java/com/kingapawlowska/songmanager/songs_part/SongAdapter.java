package com.kingapawlowska.songmanager.songs_part;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kingapawlowska.songmanager.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kinga on 09.05.2018.
 */

public class SongAdapter extends ArrayAdapter<SongModel> {

    private Context mContext;
    private List<SongModel> fileListSongStringArray = new ArrayList<>();


    public SongAdapter(@NonNull Context context, @LayoutRes List<SongModel> list) {
        super(context, 0 , list);
        mContext = context;
        fileListSongStringArray = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.activity_songs_lv_file_item, parent,false);

        SongModel currentSong = fileListSongStringArray.get(position);


        TextView songTitle = (TextView) listItem.findViewById(R.id.song_title);
        songTitle.setText(currentSong.getTitle());

        TextView songArtist = (TextView) listItem.findViewById(R.id.song_artist);
        songArtist.setText(currentSong.getArtist());

        return listItem;
    }




}
