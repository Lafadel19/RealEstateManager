package com.openclassrooms.realestatemanager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.utils.SettingsManager;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.R;
import java.util.List;
import java.util.Locale;

public class RealEstateAdapter extends RecyclerView.Adapter<RealEstateAdapter.RealEstateViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RealEstate realEstate);
    }

    private List<RealEstate> realEstates;
    private final OnItemClickListener listener;

    public RealEstateAdapter(List<RealEstate> realEstates, OnItemClickListener listener) {
        this.realEstates = realEstates;
        this.listener = listener;
    }

    public void setRealEstates(List<RealEstate> realEstates) {
        this.realEstates = realEstates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RealEstateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_real_estate, parent, false);
        return new RealEstateViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateViewHolder holder, int position) {
        RealEstate realEstate = realEstates.get(position);
        holder.bind(realEstate);
    }

    @Override
    public int getItemCount() {
        return realEstates.size();
    }

    public static class RealEstateViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnail;
        private final TextView type;
        private final TextView neighborhood;
        private final TextView price;
        private final OnItemClickListener listener;
        private RealEstate currentRealEstate;

        public RealEstateViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            thumbnail = itemView.findViewById(R.id.image);
            type = itemView.findViewById(R.id.type);
            neighborhood = itemView.findViewById(R.id.neighborhood);
            price = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentRealEstate != null) {
                    listener.onItemClick(currentRealEstate);
                }
            });
        }

        public void bind(RealEstate realEstate) {
            this.currentRealEstate = realEstate;
            type.setText(realEstate.getType());
            neighborhood.setText(realEstate.getCity());

            String currency = SettingsManager.getCurrency(price.getContext());
            if ("EUR".equals(currency)) {
                int priceInEuro = Utils.convertDollarToEuro(realEstate.getPrice());
                price.setText(String.format(Locale.getDefault(), "%,d €", priceInEuro));
            } else {
                price.setText(String.format(Locale.getDefault(), "$%,d", realEstate.getPrice()));
            }

            if (!realEstate.getPhotos().isEmpty()) {
                String photo = realEstate.getPhotos().get(0);
                try {
                    int resId = Integer.parseInt(photo);
                    Glide.with(thumbnail.getContext()).load(resId).into(thumbnail);
                } catch (NumberFormatException e) {
                    Glide.with(thumbnail.getContext()).load(photo).into(thumbnail);
                }
            } else {
                thumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }
    }
}
