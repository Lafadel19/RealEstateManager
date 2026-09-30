package com.openclassrooms.realestatemanager.data.models;

import java.io.Serializable;

public class SearchFilter implements Serializable {
    private String type;
    private Integer minSurface;
    private Integer maxSurface;
    private Integer minPrice;
    private Integer maxPrice;
    private boolean filterSchool;
    private boolean filterStation;
    private boolean filterShops;

    public SearchFilter() {
        this.type = "All";
        this.minSurface = null;
        this.maxSurface = null;
        this.minPrice = null;
        this.maxPrice = null;
        this.filterSchool = false;
        this.filterStation = false;
        this.filterShops = false;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getMinSurface() { return minSurface; }
    public void setMinSurface(Integer minSurface) { this.minSurface = minSurface; }
    public Integer getMaxSurface() { return maxSurface; }
    public void setMaxSurface(Integer maxSurface) { this.maxSurface = maxSurface; }
    public Integer getMinPrice() { return minPrice; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }
    public Integer getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Integer maxPrice) { this.maxPrice = maxPrice; }
    public boolean isFilterSchool() { return filterSchool; }
    public void setFilterSchool(boolean filterSchool) { this.filterSchool = filterSchool; }
    public boolean isFilterStation() { return filterStation; }
    public void setFilterStation(boolean filterStation) { this.filterStation = filterStation; }
    public boolean isFilterShops() { return filterShops; }
    public void setFilterShops(boolean filterShops) { this.filterShops = filterShops; }
}
