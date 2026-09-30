package com.openclassrooms.realestatemanager.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.openclassrooms.realestatemanager.data.repository.RealEstateRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RealEstateRepository repository;

    public ViewModelFactory(RealEstateRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        }
        if (modelClass.isAssignableFrom(AddRealEstateViewModel.class)) {
            return (T) new AddRealEstateViewModel(repository);
        }
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
