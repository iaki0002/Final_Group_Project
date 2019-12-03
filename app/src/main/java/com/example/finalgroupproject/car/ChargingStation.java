package com.example.finalgroupproject.car;

/**
 * This class is used to hold the data for each charging station that the server returns in its response.
 * @author Alex Hamilton
 * @since November 11, 2019
 */

public class ChargingStation {

    /**
     * Instance field that contains a charging station's name.
     */
    private String title;
    /**
     * Instance field that contains a charging station's latitude coordinate.
     */
    private double latitude;
    /**
     * Instance field that contains a charging station's longitude coordinate.
     */
    private double longitude;
    /**
     * Instance field that contains a charging station's phone number.
     */
    private String phoneNum;
    /**
     * Instance fields that contains a charging station's database id number.
     */
    // private long id;

    /**
     * Initializing Constructor
     * @param title - charging station's name
     * @param latitude - charging station's latitude coordinate
     * @param longitude - charging station's longitude coordinate
     * @param phoneNum - charging station's phone number
     */
    public ChargingStation(String title, double latitude, double longitude, String phoneNum/*, long id*/) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNum = phoneNum;
        // this.id = id;
    }

    /**
     * Getter for title instance field
     * @return charging station's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for title instance field
     * @param title - charging station's name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for latitude instance field
     * @return charging station's latitude coordinate
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter for latitude instance field
     * @param latitude - charging station's latitude coordinate
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for longitude instance field
     * @return charging station's longitude coordinate
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter for longitude instance field
     * @param longitude - charging station's longitude coordinate
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for phoneNum instance field
     * @return charging station's phone number
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * Setter for phoneNum instance field
     * @param phoneNum - charging station's phone number
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /**
     * Getter for the charging station's id field (from the database)
     * @return charging station's database id
     */
    // public long getId() {
    //     return id;
    // }

}






















