package org.autoride.driver.utils.reference;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


import org.autoride.driver.R;

import java.util.ArrayList;

/**
 * Created by msc10 on 16/02/2017.
 */

public class ReferenceGalleryAdapter extends RecyclerView.Adapter<ReferenceGalleryAdapter.ImageViewHolder> {

    private final ReferenceItemClickListener referenceItemClickListener;
    private ArrayList<ReferenceItem> referenceItems;

    public ReferenceGalleryAdapter(ArrayList<ReferenceItem> referenceItems, ReferenceItemClickListener referenceItemClickListener) {
        this.referenceItems = referenceItems;
        this.referenceItemClickListener = referenceItemClickListener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reference_square, parent, false));
    }

    @Override
    public int getItemCount() {
        return referenceItems.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final ReferenceItem referenceItem = referenceItems.get(position);

        Picasso.with(holder.itemView.getContext())
                .load(referenceItem.imageUrl)
                .into(holder.referenceImageView);

        ViewCompat.setTransitionName(holder.referenceImageView, referenceItem.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceItemClickListener.onRefereneItemClick(holder.getAdapterPosition(), referenceItem, holder.referenceImageView);
            }
        });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView referenceImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            referenceImageView = (ImageView) itemView.findViewById(R.id.item_reference_square_image);
        }
    }
}
