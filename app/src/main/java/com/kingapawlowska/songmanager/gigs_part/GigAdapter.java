package com.kingapawlowska.songmanager.gigs_part;

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
 * Created by Kinga on 10.05.2018.
 */

public class GigAdapter extends ArrayAdapter<GigModel> {

    private Context mContext;
    private List<GigModel> fileListGigStringArray = new ArrayList<>();

    public GigAdapter(@NonNull Context context, @LayoutRes List<GigModel> list) {
            super(context, 0 , list);
        mContext = context;
        fileListGigStringArray = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.activity_gigs_lv_file_item, parent,false);

        GigModel currentGig = fileListGigStringArray.get(position);

        TextView gigDay = (TextView) listItem.findViewById(R.id.tvDay);
        String strGigDay = currentGig.getDay()+"";
        gigDay.setText(strGigDay);

        TextView gigMonth = (TextView) listItem.findViewById(R.id.tvMonth);
        String strGigMonth = monthNumberToString(currentGig.getMonth()) + "";
        gigMonth.setText(strGigMonth);

        TextView gigYear = (TextView) listItem.findViewById(R.id.tvYear);
        String strGigYear = currentGig.getYear()+"";
        gigYear.setText(strGigYear);

        TextView gigVenue = (TextView) listItem.findViewById(R.id.tvVenue);
        String strGigVenue = currentGig.getVenue() + "";
        gigVenue.setText(strGigVenue);

        TextView gigName = (TextView) listItem.findViewById(R.id.tvName);
        String strGigName = currentGig.getName() + "";
        gigName.setText(strGigName);

        return listItem;
    }

    public String monthNumberToString (int month) {

        String monthResult = "";

        try {
            monthResult = mContext.getResources().getStringArray(R.array.month_names)[month-1];
        } catch (ArrayIndexOutOfBoundsException e) {
            monthResult = Integer.toString(month);
        }

        return monthResult;
    }
}
