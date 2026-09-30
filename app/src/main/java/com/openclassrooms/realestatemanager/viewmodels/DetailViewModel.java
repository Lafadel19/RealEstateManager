package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;

public class DetailViewModel extends ViewModel {

    private final RealEstateRepository repository;
    private final MutableLiveData<RealEstate> realEstate = new MutableLiveData<>();

    public DetailViewModel(RealEstateRepository repository) {
        this.repository = repository;
    }

    public LiveData<RealEstate> getRealEstate() {
        return realEstate;
    }

    public void init(RealEstate realEstate) {
        if (this.realEstate.getValue() == null) {
            this.realEstate.setValue(realEstate);
        }
    }
}
