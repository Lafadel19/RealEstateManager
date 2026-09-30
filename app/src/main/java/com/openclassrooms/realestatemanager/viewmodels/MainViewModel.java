package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.data.models.SearchFilter;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final RealEstateRepository repository;
    private final LiveData<List<RealEstate>> allRealEstates;
    private final MediatorLiveData<List<RealEstate>> filteredRealEstates = new MediatorLiveData<>();
    private SearchFilter currentFilter = new SearchFilter();

    public MainViewModel(RealEstateRepository repository) {
        this.repository = repository;
        this.allRealEstates = repository.getAllRealEstates();
        
        filteredRealEstates.addSource(allRealEstates, realEstates -> applyFilter());
    }

    public LiveData<List<RealEstate>> getRealEstates() {
        return filteredRealEstates;
    }

    public SearchFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setFilter(SearchFilter filter) {
        this.currentFilter = filter;
        applyFilter();
    }

    private void applyFilter() {
        List<RealEstate> source = allRealEstates.getValue();
        if (source == null) {
            filteredRealEstates.setValue(new ArrayList<>());
            return;
        }

        List<RealEstate> result = new ArrayList<>();
        for (RealEstate re : source) {
            if (matchesFilter(re, currentFilter)) {
                result.add(re);
            }
        }
        filteredRealEstates.setValue(result);
    }

    private boolean matchesFilter(RealEstate re, SearchFilter filter) {
        if (filter.getType() != null && !filter.getType().equals("All") && !filter.getType().trim().isEmpty()) {
            if (!re.getType().equalsIgnoreCase(filter.getType())) {
                return false;
            }
        }

        if (filter.getMinPrice() != null && re.getPrice() < filter.getMinPrice()) {
            return false;
        }
        if (filter.getMaxPrice() != null && re.getPrice() > filter.getMaxPrice()) {
            return false;
        }

        if (filter.getMinSurface() != null && re.getSurface() < filter.getMinSurface()) {
            return false;
        }
        if (filter.getMaxSurface() != null && re.getSurface() > filter.getMaxSurface()) {
            return false;
        }

        List<String> pois = re.getInterestPoints();
        if (filter.isFilterSchool() && (pois == null || !pois.contains("School"))) {
            return false;
        }
        if (filter.isFilterStation() && (pois == null || !pois.contains("Station"))) {
            return false;
        }
        if (filter.isFilterShops() && (pois == null || !pois.contains("Shops"))) {
            return false;
        }

        return true;
    }
}
