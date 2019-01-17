package com.projects.andreafranco.snapchatclone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.andreafranco.snapchatclone.R;
import com.projects.andreafranco.snapchatclone.models.Snap;

import java.util.ArrayList;

public class SnapListAdapter extends RecyclerView.Adapter<SnapListAdapter.SnapViewHolder> {

    private Context mContext;
    private ArrayList<Snap> mSnaps;

    public SnapListAdapter(Context context, ArrayList<Snap> snaps) {
        mContext = context;
        mSnaps = snaps;
    }

    public class SnapViewHolder extends RecyclerView.ViewHolder {

        ImageView mSnapImageView;
        TextView mUsernameTextView;
        TextView mMessageTextView;

        public SnapViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mSnapImageView = itemView.findViewById(R.id.snap_imageview);
            mUsernameTextView = itemView.findViewById(R.id.receivedFrom_textview);
            mMessageTextView = itemView.findViewById(R.id.message_textview);
        }

        public void bindSnaps(Snap snap) {
            mSnapImageView.setImageBitmap(snap.getImage());
            mUsernameTextView.setText(snap.getEmail());
            mMessageTextView.setText(snap.getMessage());
        }
    }

    @NonNull
    @Override
    public SnapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.snaps_list_item, viewGroup, false);

        SnapViewHolder snapViewHolder = new SnapViewHolder(view);
        return snapViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SnapViewHolder snapViewHolder, int i) {
        snapViewHolder.bindSnaps(mSnaps.get(i));
    }

    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    public void removeItem(int position) {
        mSnaps.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSnaps.size());
    }

    public void restoreItem(Snap model, int position) {
        mSnaps.add(position, model);
        // notify item added by position
        notifyItemInserted(position);
    }
}
