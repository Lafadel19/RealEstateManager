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

    public void onRealEstatesEmpty() {
        prePopulateDatabase();
    }

    private void prePopulateDatabase() {
        List<RealEstate> dummyData = generateDummyData();
        for (RealEstate re : dummyData) {
            repository.upsert(re);
        }
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

    private List<RealEstate> generateDummyData() {
        List<RealEstate> realEstates = new ArrayList<>();
        List<String> dummyPhotos = new ArrayList<>();
        dummyPhotos.add(String.valueOf(android.R.drawable.ic_menu_gallery));
        dummyPhotos.add(String.valueOf(android.R.drawable.ic_menu_camera));

        List<String> poi1 = new ArrayList<>(); poi1.add("School"); poi1.add("Shops");
        List<String> poi2 = new ArrayList<>(); poi2.add("Station");
        List<String> poi3 = new ArrayList<>(); poi3.add("School"); poi3.add("Station"); poi3.add("Shops");

        realEstates.add(new RealEstate("Apartment", "Paris", "75001", 500000, "Beautiful apartment in the center of Paris.", 75, 4, 1, 2, "123 Rue de Rivoli", dummyPhotos, poi1, true, "10/09/2026", "Alexa", "01/09/2026"));
        realEstates.add(new RealEstate("House", "Lyon", "69000", 350000, "Spacious house with a large garden.", 150, 6, 2, 3, "45 Avenue de la République", dummyPhotos, poi2, false, null, "Walter", "05/09/2026"));
        realEstates.add(new RealEstate("Loft", "Marseille", "13000", 250000, "Modern loft with sea view.", 90, 3, 1, 1, "10 Quai du Port", dummyPhotos, poi3, false, null, "Harry", "08/09/2026"));
        realEstates.add(new RealEstate("Villa", "Nice", "06000", 1200000, "Luxury villa with swimming pool.", 250, 8, 3, 5, "5 Prom. des Anglais", dummyPhotos, poi1, false, null, "Emma", "12/09/2026"));
        realEstates.add(new RealEstate("Studio", "Bordeaux", "33000", 150000, "Cozy studio perfect for students.", 25, 1, 1, 0, "20 Cours de l'Intendance", dummyPhotos, poi2, false, null, "Alexa", "14/09/2026"));
        return realEstates;
    }
}
