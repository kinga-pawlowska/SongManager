package com.kingapawlowska.songmanager.setlists_part;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingapawlowska.songmanager.R;
import com.kingapawlowska.songmanager.touch_helper.ItemTouchHelperAdapter;
import com.kingapawlowska.songmanager.touch_helper.ItemTouchHelperViewHolder;
import com.kingapawlowska.songmanager.touch_helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<SetlistItemModel> mItems = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, OnStartDragListener dragStartListener, List<SetlistItemModel> values) {
        mDragStartListener = dragStartListener;
        //mItems.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dummy_items)));

        for (SetlistItemModel x : values) {

            SetlistItemModel setlistItem = new SetlistItemModel(x.getTitle(), x.getArtist());
            mItems.add(setlistItem);
        }

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_setlist, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        holder.tv_item_title.setText(mItems.get(position).getTitle());
        holder.tv_item_artist.setText(mItems.get(position).getArtist());

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.delete_setlist_elementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    public void addItem(String title, String artist) {
        SetlistItemModel setlistItem = new SetlistItemModel(title, artist);
        mItems.add(setlistItem);
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public List<SetlistItemModel> getmItems() {
        return mItems;
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView tv_item_title;
        public final TextView tv_item_artist;
        public final ImageView handleView;
        public final ImageView delete_setlist_elementView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_item_title = (TextView) itemView.findViewById(R.id.item_edit_setlist_tv_title);
            tv_item_artist = (TextView) itemView.findViewById(R.id.item_edit_setlist_tv_artist);
            handleView = (ImageView) itemView.findViewById(R.id.item_edit_setlist_handle);
            delete_setlist_elementView = (ImageView) itemView.findViewById(R.id.item_edit_setlist_btn_delete);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

}
