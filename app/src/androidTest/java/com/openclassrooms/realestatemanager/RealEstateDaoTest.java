package com.openclassrooms.realestatemanager;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.data.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.database.RealEstateDao;
import com.openclassrooms.realestatemanager.data.models.RealEstate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RealEstateDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private RealEstateDao dao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.realEstateDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertAndGetRealEstate() throws InterruptedException {
        List<String> photos = new ArrayList<>();
        photos.add("android.resource://com.openclassrooms.realestatemanager/drawable/ic_launcher");
        List<String> pois = new ArrayList<>();
        pois.add("School");

        RealEstate realEstate = new RealEstate(
                "Apartment", "Paris", "75001", 300000,
                "Test description", 60, 3, 1, 1,
                "10 Rue de Paris", photos, pois,
                false, null, "Alice", "01/01/2026"
        );

        dao.upsert(realEstate);

        LiveData<List<RealEstate>> liveData = dao.getAllRealEstates();
        List<RealEstate> result = LiveDataTestUtil.getValue(liveData);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Apartment", result.get(0).getType());
        assertEquals("Paris", result.get(0).getCity());
        assertEquals("75001", result.get(0).getPostcode());
        assertEquals(300000, result.get(0).getPrice());
    }

    @Test
    public void updateRealEstateViaUpsert() throws InterruptedException {
        List<String> photos = new ArrayList<>();
        List<String> pois = new ArrayList<>();

        RealEstate realEstate = new RealEstate(
                "House", "Lyon", "69000", 400000,
                "Initial description", 120, 5, 2, 3,
                "20 Avenue de Lyon", photos, pois,
                false, null, "Bob", "01/01/2026"
        );

        long id = dao.upsert(realEstate);
        realEstate.setId(id);

        RealEstate updatedEstate = new RealEstate(
                "House", "Lyon", "69000", 450000,
                "Updated description", 120, 5, 2, 3,
                "20 Avenue de Lyon", photos, pois,
                true, "10/01/2026", "Bob", "01/01/2026"
        );
        updatedEstate.setId(id);

        dao.upsert(updatedEstate);

        List<RealEstate> result = LiveDataTestUtil.getValue(dao.getAllRealEstates());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(450000, result.get(0).getPrice());
        assertEquals("Updated description", result.get(0).getDescription());
        assertTrue(result.get(0).isSold());
    }
}
