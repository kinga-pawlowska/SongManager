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

public class SetlistAdapter extends ArrayAdapter<SetlistModel> {

    private Context mContext;
    private List<SetlistModel> fileListSetlistStringArray = new ArrayList<>();

    public SetlistAdapter(@NonNull Context context, @LayoutRes List<SetlistModel> list) {
        super(context, 0 , list);
        mContext = context;
        fileListSetlistStringArray = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.activity_setlists_lv_file_item, parent,false);

        SetlistModel currentSetlist = fileListSetlistStringArray.get(position);


        TextView setlistTitle = (TextView) listItem.findViewById(R.id.setlist_title);
        setlistTitle.setText(currentSetlist.getTitle());

        return listItem;
    }
}