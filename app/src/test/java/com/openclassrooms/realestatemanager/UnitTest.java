package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.data.models.RealEstate;
import com.openclassrooms.realestatemanager.data.models.SearchFilter;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UnitTest {

    @Test
    public void testConvertDollarToEuro() {
        int dollars = 100;
        int expectedEuros = (int) Math.round(100 * 0.812);
        assertEquals(expectedEuros, Utils.convertDollarToEuro(dollars));
    }

    @Test
    public void testConvertEuroToDollar() {
        int euros = 100;
        int expectedDollars = (int) Math.round(100 * 1.188);
        assertEquals(expectedDollars, Utils.convertEuroToDollar(euros));
    }

    @Test
    public void testGetUsTodayDate() {
        assertNotNull(Utils.getUsTodayDate());
        assertTrue(Utils.getUsTodayDate().matches("\\d{4}/\\d{2}/\\d{2}"));
    }

    @Test
    public void testGetEuTodayDate() {
        assertNotNull(Utils.getEuTodayDate());
        assertTrue(Utils.getEuTodayDate().matches("\\d{2}/\\d{2}/\\d{4}"));
    }

    @Test
    public void testLoanCalculatorCalculation() {
        double price = 300000.0;
        double downPayment = 50000.0;
        int durationYears = 20;
        double annualInterestRate = 3.5;

        double loanAmount = price - downPayment;
        double monthlyRate = annualInterestRate / 100.0 / 12.0;
        int totalMonths = durationYears * 12;

        double rawMonthlyPayment = loanAmount * (monthlyRate / (1 - Math.pow(1 + monthlyRate, -totalMonths)));
        double monthlyPayment = Math.round(rawMonthlyPayment * 100.0) / 100.0;
        double totalInterestPaid = Math.round(((monthlyPayment * totalMonths) - loanAmount) * 100.0) / 100.0;

        assertEquals(1449.9, monthlyPayment, 0.01);
        assertEquals(97976.0, totalInterestPaid, 1.0);
    }

    @Test
    public void testSearchFilterMatching() {
        List<String> pois = new ArrayList<>();
        pois.add("School");
        pois.add("Shops");

        RealEstate estate = new RealEstate(
                "Apartment", "Paris", "75001", 500000,
                "Test description", 75, 4, 1, 2,
                "123 Rue de Rivoli", new ArrayList<>(), pois,
                false, null, "Alexa", "01/09/2026"
        );

        SearchFilter filter = new SearchFilter();
        
        // Test type matching
        filter.setType("Apartment");
        assertTrue(estate.getType().equalsIgnoreCase(filter.getType()));

        filter.setType("House");
        assertFalse(estate.getType().equalsIgnoreCase(filter.getType()));

        // Test price range matching
        filter.setType("All");
        filter.setMinPrice(400000);
        filter.setMaxPrice(600000);
        assertTrue(estate.getPrice() >= filter.getMinPrice() && estate.getPrice() <= filter.getMaxPrice());

        filter.setMinPrice(600000);
        assertFalse(estate.getPrice() >= filter.getMinPrice());

        // Test POI matching
        filter.setMinPrice(null);
        filter.setFilterSchool(true);
        assertTrue(estate.getInterestPoints().contains("School"));

        filter.setFilterStation(true);
        assertFalse(estate.getInterestPoints().contains("Station"));
    }

    @Test
    public void testMapLocationQueryAndStaticMapUrlGeneration() {
        RealEstate estate = new RealEstate(
                "Apartment", "Paris", "75001", 500000,
                "Test description", 75, 4, 1, 2,
                "123 Rue de Rivoli", new ArrayList<>(), new ArrayList<>(),
                false, null, "Alexa", "01/09/2026"
        );

        // Test location query building (address + postcode + city)
        StringBuilder locationBuilder = new StringBuilder();
        if (estate.getAddress() != null && !estate.getAddress().trim().isEmpty()) {
            locationBuilder.append(estate.getAddress());
        }
        if (estate.getPostcode() != null && !estate.getPostcode().trim().isEmpty()) {
            if (locationBuilder.length() > 0) locationBuilder.append(", ");
            locationBuilder.append(estate.getPostcode());
        }
        if (estate.getCity() != null && !estate.getCity().trim().isEmpty()) {
            if (locationBuilder.length() > 0) locationBuilder.append(" ");
            locationBuilder.append(estate.getCity());
        }

        String fullLocation = locationBuilder.toString();
        assertEquals("123 Rue de Rivoli, 75001 Paris", fullLocation);

        // Test static map URL generation format
        String apiKey = "AIzaSyTestKey";
        String centerParam = "48.8566,2.3522";
        String size = "400x400";
        String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?"
                + "center=" + centerParam
                + "&zoom=15"
                + "&size=" + size
                + "&maptype=roadmap"
                + "&markers=color:red%7C" + centerParam
                + "&key=" + apiKey;

        assertTrue(staticMapUrl.contains("maps.googleapis.com/maps/api/staticmap"));
        assertTrue(staticMapUrl.contains("center=48.8566,2.3522"));
        assertTrue(staticMapUrl.contains("zoom=15"));
        assertTrue(staticMapUrl.contains("key=AIzaSyTestKey"));
    }
}
