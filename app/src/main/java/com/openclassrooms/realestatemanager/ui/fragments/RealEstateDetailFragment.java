package com.openclassrooms.realestatemanager.ui.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.openclassrooms.realestatemanager.BuildConfig;
import com.openclassrooms.realestatemanager.ui.activities.AddFormActivity;
import com.openclassrooms.realestatemanager.ui.adapters.PropertyPhotoAdapter;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;
import com.openclassrooms.realestatemanager.viewmodels.DetailViewModel;
import com.openclassrooms.realestatemanager.viewmodels.ViewModelFactory;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class RealEstateDetailFragment extends Fragment {

    private RecyclerView photosRecyclerView;
    private TextView description;
    private TextView surface;
    private TextView rooms;
    private TextView bathrooms;
    private TextView bedrooms;
    private TextView address;
    private ImageView mapSnapshot;
    private Chip statusChip;
    private ChipGroup poiChipGroup;
    private TextView agentName;
    private TextView addDate;
    private TextView saleDate;
    private DetailViewModel viewModel;

    public static RealEstateDetailFragment newInstance(RealEstate realEstate) {
        RealEstateDetailFragment fragment = new RealEstateDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("realEstate", realEstate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        configureViewModel();
    }

    private void configureViewModel() {
        RealEstateRepository repository = new RealEstateRepository(AppDatabase.getDatabase(requireContext().getApplicationContext()).realEstateDao());
        ViewModelFactory factory = new ViewModelFactory(repository);
        this.viewModel = new ViewModelProvider(this, factory).get(DetailViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            RealEstate currentRealEstate = viewModel.getRealEstate().getValue();
            if (currentRealEstate != null) {
                Intent intent = new Intent(requireContext(), AddFormActivity.class);
                intent.putExtra("realEstate", currentRealEstate);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_second, container, false);
        this.initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
        if (getArguments() != null) {
            RealEstate realEstate = (RealEstate) getArguments().getSerializable("realEstate");
            viewModel.init(realEstate);
        }
    }

    private void observeViewModel() {
        viewModel.getRealEstate().observe(getViewLifecycleOwner(), this::bindData);
    }

    private void initViews(View view) {
        this.photosRecyclerView = view.findViewById(R.id.property_photos_recycler_view);
        this.description = view.findViewById(R.id.property_description);
        this.surface = view.findViewById(R.id.property_surface);
        this.rooms = view.findViewById(R.id.property_rooms);
        this.bathrooms = view.findViewById(R.id.property_bathrooms);
        this.bedrooms = view.findViewById(R.id.property_bedrooms);
        this.address = view.findViewById(R.id.property_address);
        this.mapSnapshot = view.findViewById(R.id.property_map);
        this.statusChip = view.findViewById(R.id.property_status_chip);
        this.poiChipGroup = view.findViewById(R.id.property_poi_chip_group);
        this.agentName = view.findViewById(R.id.property_agent);
        this.addDate = view.findViewById(R.id.property_add_date);
        this.saleDate = view.findViewById(R.id.property_sale_date);
    }

    private void bindData(RealEstate realEstate) {
        if (realEstate == null) return;
        PropertyPhotoAdapter adapter = new PropertyPhotoAdapter(realEstate.getPhotos());
        this.photosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        this.photosRecyclerView.setAdapter(adapter);

        this.description.setText(realEstate.getDescription());
        this.surface.setText(String.format(Locale.getDefault(), "Surface: %d sqm", realEstate.getSurface()));
        this.rooms.setText(String.format(Locale.getDefault(), "Rooms: %d", realEstate.getRooms()));
        this.bathrooms.setText(String.format(Locale.getDefault(), "Bathrooms: %d", realEstate.getBathrooms()));
        this.bedrooms.setText(String.format(Locale.getDefault(), "Bedrooms: %d", realEstate.getBedrooms()));
        this.address.setText(realEstate.getAddress());

        StringBuilder locationBuilder = new StringBuilder();
        if (realEstate.getAddress() != null && !realEstate.getAddress().trim().isEmpty()) {
            locationBuilder.append(realEstate.getAddress());
        }
        if (realEstate.getPostcode() != null && !realEstate.getPostcode().trim().isEmpty()) {
            if (locationBuilder.length() > 0) locationBuilder.append(", ");
            locationBuilder.append(realEstate.getPostcode());
        }
        if (realEstate.getCity() != null && !realEstate.getCity().trim().isEmpty()) {
            if (locationBuilder.length() > 0) locationBuilder.append(" ");
            locationBuilder.append(realEstate.getCity());
        }

        String fullLocation = locationBuilder.length() > 0 ? locationBuilder.toString() : realEstate.getCity();

        loadStaticMap(fullLocation);

        // New fields
        this.statusChip.setText(realEstate.isSold() ? "Sold" : "Available");
        this.statusChip.setChipBackgroundColorResource(realEstate.isSold() ? android.R.color.holo_red_dark : R.color.price_blue);

        this.agentName.setText(String.format("Agent: %s", realEstate.getAgentName()));
        this.addDate.setText(String.format("Added on: %s", realEstate.getAddDate()));

        if (realEstate.isSold() && realEstate.getSaleDate() != null) {
            this.saleDate.setVisibility(View.VISIBLE);
            this.saleDate.setText(String.format("Sold on: %s", realEstate.getSaleDate()));
        } else {
            this.saleDate.setVisibility(View.GONE);
        }

        this.poiChipGroup.removeAllViews();
        if (realEstate.getInterestPoints() != null) {
            for (String poi : realEstate.getInterestPoints()) {
                Chip chip = new Chip(requireContext());
                chip.setText(poi);
                chip.setClickable(false);
                chip.setCheckable(false);
                this.poiChipGroup.addView(chip);
            }
        }
    }

    private void loadStaticMap(String locationQuery) {
        if (locationQuery == null || locationQuery.trim().isEmpty()) {
            this.mapSnapshot.setVisibility(View.GONE);
            return;
        }

        this.mapSnapshot.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            double latitude;
            double longitude;
            boolean hasCoordinates = false;

            if (Geocoder.isPresent()) {
                try {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(locationQuery, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address addressResult = addresses.get(0);
                        latitude = addressResult.getLatitude();
                        longitude = addressResult.getLongitude();
                        hasCoordinates = true;
                        Log.d("RealEstateDetail", "Geocoder resolved coordinates: " + latitude + ", " + longitude);
                    } else {
                        latitude = 0.0;
                        longitude = 0.0;
                    }
                } catch (IOException e) {
                    Log.e("RealEstateDetail", "Geocoder lookup error", e);
                    latitude = 0.0;
                    longitude = 0.0;
                }
            } else {
                latitude = 0.0;
                longitude = 0.0;
            }

            boolean finalHasCoordinates = hasCoordinates;
            double finalLat = latitude;
            double finalLng = longitude;

            if (!isAdded()) return;

            requireActivity().runOnUiThread(() -> {
                try {
                    String apiKey = BuildConfig.MAPS_API_KEY;
                    int width = this.mapSnapshot.getWidth() > 0 ? this.mapSnapshot.getWidth() : 400;
                    int height = this.mapSnapshot.getHeight() > 0 ? this.mapSnapshot.getHeight() : 400;
                    String size = width + "x" + height;

                    String centerParam;
                    String markersParam;

                    if (finalHasCoordinates) {
                        centerParam = finalLat + "," + finalLng;
                        markersParam = "color:red%7C" + finalLat + "," + finalLng;
                    } else {
                        centerParam = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8.toString());
                        markersParam = "color:red%7C" + centerParam;
                    }

                    String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?"
                            + "center=" + centerParam
                            + "&zoom=15"
                            + "&size=" + size
                            + "&maptype=roadmap"
                            + "&markers=" + markersParam
                            + "&key=" + apiKey;

                    Log.d("RealEstateDetail", "Static Map URL: " + staticMapUrl);

                    Glide.with(this)
                            .load(staticMapUrl)
                            .placeholder(android.R.drawable.ic_dialog_map)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(this.mapSnapshot);
                } catch (Exception e) {
                    Log.e("RealEstateDetail", "Error loading static map", e);
                    this.mapSnapshot.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            });
        });
    }
}
