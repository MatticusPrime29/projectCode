package com.saskpolytech.cst129;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.ArrayList;

public class DailyPictureHelper extends SQLiteOpenHelper
{
    //File name for the Database
    private static final String DB_NAME = "daily_picture.db";
    //Database version
    private static final int DB_VERSION = 1;

    // Table Constants
    public static final String TABLE_NAME = "DailyPicture";
    public static final String ID = "_id";
    public static final String PICTURE = "picture";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    // Reference to the database
    public SQLiteDatabase sqlDB;

    // Constructor method
    public DailyPictureHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Opens the instance of the database
     * @throws SQLException
     */
    public void open() throws SQLException
    {
        // Set the instance of the sqlDB to a connection to the database
        sqlDB = this.getWritableDatabase();
    }

    /**
     * Closes the instance of the database
     */
    public void close()
    {
        // Close the connection
        sqlDB.close();
    }

    /**
     * Creates the database with appropriate fields
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create the table
        String sql = "CREATE TABLE " +
                TABLE_NAME + "(" + ID + " integer primary key autoincrement, " +
                PICTURE + " BLOB not null, " + //BLOB for the byte[]
                LONGITUDE + " text not null, " +
                LATITUDE + " text not null);";

        //Run the statement against the DB
        db.execSQL(sql);
    }

    /**
     * If upgrade happens
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Delete the old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Re-create everything
        onCreate(db);
    }

    /**
     * Method called in the main activity when a new picture is taken
     * puts in attributes passed from the parameter DailyPicture
     * and sets the values in the database as a row
     * @param dailyPicture
     * @return
     */
    public long createDailyPicture(DailyPicture dailyPicture)
    {
        ContentValues cvs = new ContentValues();

        // Add values for each column from the object passed in
        cvs.put(PICTURE, dailyPicture.picture);
        cvs.put(LONGITUDE, dailyPicture.longitude);
        cvs.put(LATITUDE, dailyPicture.latitude);

        //Save the record and generate an id
        long id = sqlDB.insert(TABLE_NAME, null, cvs);
        dailyPicture.id = id;

        return id;
    }

    /**
     * Method used to get the total amount of rows in the database for the top of the layouts
     * @return
     */
    public int getTotalPics()
    {
        // Select the fields to return
        String[] sFields = new String[] {ID, PICTURE, LONGITUDE, LATITUDE};
        int nTotal = 0;
        //Cursor for getting all the fields in the db
        Cursor cursor = sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null, null);

        //Loop through and count each instance
        if(cursor.moveToFirst())
        {
            do
            {
                //For each record, add to list
                nTotal += 1;

            } while(cursor.moveToNext());
        }

        //Return the totaled value
        return nTotal;
    }

    /**
     * Method used to return a list of daily pictures to populate the listviews
     * @return
     */
    public ArrayList<DailyPicture> getAllDailyPictures()
    {
        // Select the fields to return
        String[] sFields = new String[] {ID, PICTURE, LONGITUDE, LATITUDE};
        //List that will be populated with db data
        ArrayList<DailyPicture> arrPics = new ArrayList<>();
        //Cursor used to grab everything
        Cursor cursor = sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            do
            {
                //For each record, add to list
                long id = cursor.getLong(0);
                byte[] image = cursor.getBlob(1);
                String longitude = cursor.getString(2);
                String latitude = cursor.getString(3);

                //New DailyPicture to be added each row
                DailyPicture dailyPicture = new DailyPicture(image, longitude, latitude);

                arrPics.add(dailyPicture);

            } while(cursor.moveToNext());
        }

        return arrPics;
    }
}
