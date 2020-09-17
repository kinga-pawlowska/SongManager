package com.kingapawlowska.songmanager.setlists_part;

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

public class SetlistItemAdapter extends ArrayAdapter<SetlistItemModel> {

    private Context mContext;
    private List<SetlistItemModel> setlistItemsArray= new ArrayList<>();


    public SetlistItemAdapter(@NonNull Context context, @LayoutRes List<SetlistItemModel> list) {
        super(context, 0 , list);
        mContext = context;
        setlistItemsArray = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_open_setlist, parent,false);

        SetlistItemModel currentSetlistItem = setlistItemsArray.get(position);


        // tu zmienic number
        TextView setlistItemNumber = (TextView) listItem.findViewById(R.id.item_open_setlist_tv_number);
        String strNumber = position+1 + ".";
        setlistItemNumber.setText(strNumber);

        TextView setlistItemTitle = (TextView) listItem.findViewById(R.id.item_open_setlist_tv_title);
        setlistItemTitle.setText(currentSetlistItem.getTitle());

        TextView setlistItemArtist = (TextView) listItem.findViewById(R.id.item_open_setlist_tv_artist);
        setlistItemArtist.setText(currentSetlistItem.getArtist());


        return listItem;
    }
}