package com.openclassrooms.realestatemanager.data.repository;

import androidx.lifecycle.LiveData;
import com.openclassrooms.realestatemanager.data.database.RealEstateDao;
import com.openclassrooms.realestatemanager.data.models.RealEstate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RealEstateRepository {

    private final RealEstateDao realEstateDao;
    private final Executor executor;

    public RealEstateRepository(RealEstateDao realEstateDao) {
        this.realEstateDao = realEstateDao;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<RealEstate>> getAllRealEstates() {
        return realEstateDao.getAllRealEstates();
    }

    public void upsert(RealEstate realEstate) {
        executor.execute(() -> realEstateDao.upsert(realEstate));
    }

    public void deleteAll() {
        executor.execute(realEstateDao::deleteAll);
    }
}
