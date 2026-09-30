package com.openclassrooms.realestatemanager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import java.util.List;

public class PropertyPhotoAdapter extends RecyclerView.Adapter<PropertyPhotoAdapter.PhotoViewHolder> {

    private final List<String> photos;

    public PropertyPhotoAdapter(List<String> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String photo = photos.get(position);
        try {
            int resId = Integer.parseInt(photo);
            Glide.with(holder.imageView.getContext()).load(resId).into(holder.imageView);
        } catch (NumberFormatException e) {
            Glide.with(holder.imageView.getContext()).load(photo).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.property_photo);
        }
    }
}
