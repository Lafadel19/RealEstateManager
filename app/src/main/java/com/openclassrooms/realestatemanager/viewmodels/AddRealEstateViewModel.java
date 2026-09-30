package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;
import java.util.ArrayList;
import java.util.List;

public class AddRealEstateViewModel extends ViewModel {

    private final RealEstateRepository repository;
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public AddRealEstateViewModel(RealEstateRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public void validateAndSave(long id, String type, String city, String postcode, String priceStr, String description,
                                String surfaceStr, String roomsStr, String bathroomsStr, String bedroomsStr,
                                String address, List<String> photoPaths, List<String> interestPoints,
                                boolean isSold, String agentName, String addDate) {
        
        if (isAnyFieldEmpty(type, city, postcode, priceStr, description, surfaceStr, roomsStr, bathroomsStr, bedroomsStr, address, addDate, agentName) || photoPaths.isEmpty()) {
            saveSuccess.setValue(false);
            return;
        }

        try {
            int price = Integer.parseInt(priceStr);
            int surface = Integer.parseInt(surfaceStr);
            int rooms = Integer.parseInt(roomsStr);
            int bathrooms = Integer.parseInt(bathroomsStr);
            int bedrooms = Integer.parseInt(bedroomsStr);

            String saleDate = isSold ? new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date()) : null;

            RealEstate realEstate = new RealEstate(
                    type, city, postcode, price, description, surface, rooms, bathrooms, bedrooms, address,
                    new ArrayList<>(photoPaths), new ArrayList<>(interestPoints), isSold, saleDate, agentName, addDate
            );
            if (id != -1) {
                realEstate.setId(id);
            }

            repository.upsert(realEstate);
            saveSuccess.setValue(true);
        } catch (NumberFormatException e) {
            saveSuccess.setValue(false);
        }
    }

    private boolean isAnyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty()) return true;
        }
        return false;
    }
}
