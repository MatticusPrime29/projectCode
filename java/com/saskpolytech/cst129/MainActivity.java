package com.saskpolytech.cst129;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * CST129 Matt Martens
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    //Global attributes for the MainActivity
    Button btnTakePicture;
    Button btnViewPhotos;
    ImageView ivDisplay;
    byte[] imgStorage;
    DailyPictureHelper db;
    EditText etPicsTaken;
    String sLong;
    String sLat;
    GoogleApiClient mGoogleApiClient;


    /**
     * Called when the app is started, It will assign variables and set what needs to be started as necessary
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DailyPictureHelper(this);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnViewPhotos = (Button) findViewById(R.id.btnViewPhotos);
        ivDisplay = (ImageView) findViewById(R.id.ivDisplay);
        etPicsTaken = (EditText) findViewById(R.id.etTotal1);

        //So the text cannot be edited
        etPicsTaken.setEnabled(false);

        //Called for scheduleing a notification at 12:30 everyday to take a picture
        scheduleNotification();

        // Create an instance of the client
        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    /**
     * Method used for creating an alarm intent (AlarmReceiver) which will schedule a notification every day at 12
     */
    private void scheduleNotification()
    {
        //Get intent for alarm and create a pending intent with it
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Alarm manager used to set the scheduled time every day
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();
        Calendar calendarAlarm = Calendar.getInstance(); //Calendar object used to have a fixed time
        Calendar current = Calendar.getInstance();
        current.setTime(date);
        calendarAlarm.setTime(date);
        calendarAlarm.set(Calendar.HOUR_OF_DAY,12); //Sets it to 12: 30 everyday
        calendarAlarm.set(Calendar.MINUTE,30);
        calendarAlarm.set(Calendar.SECOND,0);

        if(calendarAlarm.before(current))
        {
            calendarAlarm.add(Calendar.DATE,1);
        }

        //Set the alarm
        manager.set(AlarmManager.RTC_WAKEUP,calendarAlarm.getTimeInMillis(), pendingIntent);
    }


    /**
     * Method for the buttons on the activity_main, gets the id's and calls the given case for the id passed in
     * @param v
     */
    public void btnClickListener(View v)
    {
        switch(v.getId())
        {
            case R.id.btnTakePicture:

                //Creates an intent to go to the camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File file = getFile(); //Gets a File object from the getFile() method below
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(MEDIA_TYPE_IMAGE));

                //Starts the camera intent
                startActivityForResult(cameraIntent, 0);

                break;

            case R.id.btnViewPhotos:

                //Intent for starting the layout of the second activity
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);

                break;
        }
    }

    /**
     * Method called after the picture has been taken. Will save the DailyPicture object to the database
     */
    public void saveDailyPictureToDB()
    {
        //Instance of a new DailyPicture object, with picture longitude and latitude
        DailyPicture dailyPicture = new DailyPicture(imgStorage, sLong, sLat);

        //Opens the database and calls the appropriate method for putting it in. Also will total the pictures in there
        db.open();

        db.createDailyPicture(dailyPicture);

        int nTotal = db.getTotalPics();

        //Set the text of the et again
        etPicsTaken.setText(String.format("%d",nTotal));

        db.close();

        Toast.makeText(this, "You will be reminded everyday at 12:30 to take a photo",Toast.LENGTH_LONG).show();
    }

    /**
     * What is called after the Activity (Camera) is finished. Will call appropriate methods for getting the image
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        //Check if there was actually a picture taken
        if(data == null)
        {
            //If there was none taken and the user decided to take it again. Create the intent again
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            //Start the intent again
            startActivityForResult(cameraIntent, 0);
        }
        else
        {
            if(resultCode == 0) //If the user selects the X button on the Camera app it will take them back to the main activity
            {
                //New intent to go to the MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else //This is the option for the user selecting the check mark for choosing a picture
            {
                //If there is a picture taken stores the bitmap from the picture taken as a byte array for storage
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivDisplay.setImageBitmap(bitmap);
                imgStorage = getBitmapAsByteArray(bitmap);
                saveDailyPictureToDB(); //Calls the method above to save the DailyPicture
            }
        }


    }

    /**
     * Only made this public for j unit tests
     * method for converting an image bitmap into a byte array for storage
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        //Outputstream to compress the bitmap into a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        //Output stream is converted into a byte array and returned as such
        return outputStream.toByteArray();
    }

    /**
     * onStart method that will connected to the ApiClient when the app starts and will assign variables
     */
    @Override
    protected  void onStart()
    {

        //Checking to see if the permission has been grandted by the user and prompts for allowing storage and location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        else
        {
            //Open the database to get the total amount of pictures in the database for the count
            db.open();
            int nTotal = db.getTotalPics();

            etPicsTaken.setText(String.format("%d",nTotal));
            db.close();

        }

        // When the activity starts, try to connect to Location Services
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Method called when the app stops
     */
    @Override
    protected void onStop()
    {
        // When the activity stops, disconnect from location services
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Method for the google client when the app pauses
     */
    @Override
    protected void onPause()
    {
        // When the app pauses, usually you want to stop listening for location changes
        super.onPause();
        if(mGoogleApiClient.isConnected())// Made an if statement because when the camera is chosen and the intent is paused it calls this without being connected
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
        }

    }

    /**
     * onResume handled when the app resumes for connection to the client
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        //When the app resumes, restart listening for location changes
        //Only going to do this if the client is connected
        if(mGoogleApiClient.isConnected())
        {
            // Create a request to listen for changes, set minimum time between updates that we will accept
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Send the request to Location Services and indicate which class is listening for the updates (our activity)
            try
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
            }
            catch(SecurityException e)
            {
                Toast.makeText(this, "Need Permission for Location",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Sets the Strings for the database of where the current location is. Will change if location is moved and updated as appropriate
     * @param location
     */
    @Override
    public void onLocationChanged(Location location)
    {
        //Sets the strings to the current location
        sLong = location.getLongitude() + "";
        sLat = location.getLatitude() + "";

    }

    /**
     * Method called when the api connects
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        // Create a request to listen for changes, set minimum time between updates that we will accept
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Send the request to Location Services and indicate which class is listening for the updates (our activity)
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
        }
        catch(SecurityException e)
        {
            Toast.makeText(this, "Need Permission for Location",Toast.LENGTH_SHORT);
        }
    }

    /**
     * Method called when abrupt suspension of the location
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i)
    {
        Toast.makeText(this,"Location Services Suspended", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method called when the conenction fails
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this,"Location Services Failed", Toast.LENGTH_SHORT).show();
    }
}
