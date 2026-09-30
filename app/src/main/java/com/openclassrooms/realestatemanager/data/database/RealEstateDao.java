package com.openclassrooms.realestatemanager.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import com.openclassrooms.realestatemanager.data.models.RealEstate;
import java.util.List;

@Dao
public interface RealEstateDao {
    @Query("SELECT * FROM real_estate")
    LiveData<List<RealEstate>> getAllRealEstates();

    @Upsert
    long upsert(RealEstate realEstate);

    @Query("DELETE FROM real_estate")
    void deleteAll();
}
