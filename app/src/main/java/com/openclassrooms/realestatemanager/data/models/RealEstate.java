package com.openclassrooms.realestatemanager.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "real_estate")
public class RealEstate implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private final String type;
    private final String city;
    private final String postcode;
    private final int price;
    private final String description;
    private final int surface;
    private final int rooms;
    private final int bathrooms;
    private final int bedrooms;
    private final String address;
    private final List<String> photos;
    private final List<String> interestPoints;
    private final boolean isSold;
    private final String saleDate;
    private final String agentName;
    private final String addDate;


    public RealEstate(String type, String city, String postcode, int price, String description, int surface, int rooms, int bathrooms, int bedrooms, String address, List<String> photos, List<String> interestPoints, boolean isSold, String saleDate, String agentName, String addDate) {
        this.type = type;
        this.city = city;
        this.postcode = postcode;
        this.price = price;
        this.description = description;
        this.surface = surface;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.bedrooms = bedrooms;
        this.address = address;
        this.photos = photos;
        this.interestPoints = interestPoints;
        this.isSold = isSold;
        this.saleDate = saleDate;
        this.agentName = agentName;
        this.addDate = addDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getType() { return type; }
    public String getCity() { return city; }
    public String getPostcode() { return postcode; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    public int getSurface() { return surface; }
    public int getRooms() { return rooms; }
    public int getBathrooms() { return bathrooms; }
    public int getBedrooms() { return bedrooms; }
    public String getAddress() { return address; }
    public List<String> getPhotos() { return photos; }
    public List<String> getInterestPoints() { return interestPoints; }
    public boolean isSold() { return isSold; }
    public String getSaleDate() { return saleDate; }
    public String getAgentName() { return agentName; }
    public String getAddDate() { return addDate; }
}
