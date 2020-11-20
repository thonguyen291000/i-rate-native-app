package com.example.rentalz;

public class Restaurant {
    //Properties
    private String id;
    private String type;
    private String name;
    private String visitDate;
    private double serviceRating;
    private double cleanlinessRating;
    private double foodRating;
    private double totalRating;
    private String dateCreate;
    private double price;
    private String location;
    private String notes;
    private String reporter;

    private String imgUrl;

    //Constructor
    public Restaurant() {
    }

    public Restaurant(String type, String name, String visitDate, double serviceRating, double cleanlinessRating, double foodRating, double totalRating, String dateCreate, double price, String location, String notes, String reporter, String imgUrl) {
        this.type = type;
        this.name = name;
        this.visitDate = visitDate;
        this.serviceRating = serviceRating;
        this.cleanlinessRating = cleanlinessRating;
        this.foodRating = foodRating;
        this.totalRating = totalRating;
        this.dateCreate = dateCreate;
        this.price = price;
        this.location = location;
        this.notes = notes;
        this.reporter = reporter;
        this.imgUrl = imgUrl;
    }

    //Getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public double getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(double serviceRating) {
        this.serviceRating = serviceRating;
    }

    public double getCleanlinessRating() {
        return cleanlinessRating;
    }

    public void setCleanlinessRating(double cleanlinessRating) {
        this.cleanlinessRating = cleanlinessRating;
    }

    public double getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(double foodRating) {
        this.foodRating = foodRating;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
