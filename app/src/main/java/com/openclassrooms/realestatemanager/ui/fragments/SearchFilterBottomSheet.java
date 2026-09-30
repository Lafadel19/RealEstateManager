package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.models.SearchFilter;

public class SearchFilterBottomSheet extends BottomSheetDialogFragment {

    private AutoCompleteTextView typeDropdown;
    private EditText minPriceEdit, maxPriceEdit, minSurfaceEdit, maxSurfaceEdit;
    private CheckBox schoolCheck, stationCheck, shopsCheck;
    private OnFilterAppliedListener listener;
    private SearchFilter currentFilter;

    public interface OnFilterAppliedListener {
        void onFilterApplied(SearchFilter filter);
    }

    public static SearchFilterBottomSheet newInstance(SearchFilter filter, OnFilterAppliedListener listener) {
        SearchFilterBottomSheet fragment = new SearchFilterBottomSheet();
        fragment.listener = listener;
        fragment.currentFilter = filter;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filter, container, false);
        initViews(view);
        setupDropdown();
        populateFields();
        return view;
    }

    private void initViews(View view) {
        typeDropdown = view.findViewById(R.id.filter_type);
        minPriceEdit = view.findViewById(R.id.filter_min_price);
        maxPriceEdit = view.findViewById(R.id.filter_max_price);
        minSurfaceEdit = view.findViewById(R.id.filter_min_surface);
        maxSurfaceEdit = view.findViewById(R.id.filter_max_surface);
        schoolCheck = view.findViewById(R.id.filter_poi_school);
        stationCheck = view.findViewById(R.id.filter_poi_station);
        shopsCheck = view.findViewById(R.id.filter_poi_shops);

        view.findViewById(R.id.btn_apply).setOnClickListener(v -> applyFilters());
        view.findViewById(R.id.btn_reset).setOnClickListener(v -> resetFilters());
    }

    private void setupDropdown() {
        String[] types = {"All", "House", "Apartment", "Penthouse"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        typeDropdown.setAdapter(adapter);
    }

    private void populateFields() {
        if (currentFilter == null) return;
        if (currentFilter.getType() != null) {
            typeDropdown.setText(currentFilter.getType(), false);
        }
        if (currentFilter.getMinPrice() != null) {
            minPriceEdit.setText(String.valueOf(currentFilter.getMinPrice()));
        }
        if (currentFilter.getMaxPrice() != null) {
            maxPriceEdit.setText(String.valueOf(currentFilter.getMaxPrice()));
        }
        if (currentFilter.getMinSurface() != null) {
            minSurfaceEdit.setText(String.valueOf(currentFilter.getMinSurface()));
        }
        if (currentFilter.getMaxSurface() != null) {
            maxSurfaceEdit.setText(String.valueOf(currentFilter.getMaxSurface()));
        }
        schoolCheck.setChecked(currentFilter.isFilterSchool());
        stationCheck.setChecked(currentFilter.isFilterStation());
        shopsCheck.setChecked(currentFilter.isFilterShops());
    }

    private void applyFilters() {
        SearchFilter filter = new SearchFilter();
        String type = typeDropdown.getText().toString();
        filter.setType(type.isEmpty() ? "All" : type);

        filter.setMinPrice(parseInteger(minPriceEdit.getText().toString()));
        filter.setMaxPrice(parseInteger(maxPriceEdit.getText().toString()));
        filter.setMinSurface(parseInteger(minSurfaceEdit.getText().toString()));
        filter.setMaxSurface(parseInteger(maxSurfaceEdit.getText().toString()));

        filter.setFilterSchool(schoolCheck.isChecked());
        filter.setFilterStation(stationCheck.isChecked());
        filter.setFilterShops(shopsCheck.isChecked());

        if (listener != null) {
            listener.onFilterApplied(filter);
        }
        dismiss();
    }

    private void resetFilters() {
        typeDropdown.setText("All", false);
        minPriceEdit.setText("");
        maxPriceEdit.setText("");
        minSurfaceEdit.setText("");
        maxSurfaceEdit.setText("");
        schoolCheck.setChecked(false);
        stationCheck.setChecked(false);
        shopsCheck.setChecked(false);

        SearchFilter filter = new SearchFilter();
        if (listener != null) {
            listener.onFilterApplied(filter);
        }
        dismiss();
    }

    private Integer parseInteger(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
