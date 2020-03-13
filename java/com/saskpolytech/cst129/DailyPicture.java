package com.saskpolytech.cst129;

import android.graphics.Picture;
import android.location.Location;

/**
 * Created by cst129 on 5/22/2018.
 */

/**
 * DailyPicture object to be stored in the database
 */
public class DailyPicture {

    //Attributes for the DailyPicture
    public long id; //Auto Incrementing ID
    public byte[] picture; //picture in the form of a byte array from a bitmap
    public String longitude; //long location
    public String latitude; //lat location

    /**
     * Constructor method for assigning the variables to the object
     * @param picture
     * @param longitude
     * @param latitude
     */
    public DailyPicture(byte[] picture, String longitude, String latitude)
    {
        this.picture = picture;
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = -1;
    }
}
