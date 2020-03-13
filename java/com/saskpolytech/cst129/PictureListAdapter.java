package com.saskpolytech.cst129;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cst129 on 5/24/2018.
 */

/**
 * Adapter that will set the views for each DailyPicture according to the picture_item layout
 */
public class PictureListAdapter extends ArrayAdapter {


    /**
     * Constructor method
     * @param context
     * @param dailyPictures
     */
    public PictureListAdapter(Context context, List<DailyPicture> dailyPictures)
    {
        //Sets the context to the picture_item layout with the array list passed in
        super(context, R.layout.picture_item, dailyPictures);
    }

    /**
     * Method for setting the view for the list
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        //The passed in convert view is a view that has been scrolled out of the screen
        //To make our code more efficient we can re-use that view instead of inflating a new view
        if(convertView == null) //if null then we have to use a new inflated view
        {
            //Get our own custom view but we do not have an inflater
            LayoutInflater inflater = LayoutInflater.from(getContext());

            //Get the view by inflating the xml layout
            convertView = inflater.inflate(R.layout.picture_item,parent, false);
        }


        View pictureItemView = convertView; //Re-use an existing view

        //Get the current person object that contains the date for this view
        DailyPicture dailyPicture = (DailyPicture) this.getItem(position);

        //Use the view to set values in the layout before returning the view
        //Set the values of the textView that correspond to the picture attributes
        TextView tvLong = (TextView) pictureItemView.findViewById(R.id.tvLong);
        TextView tvLat= (TextView) pictureItemView.findViewById(R.id.tvLat);
        ImageView ivDisplay = (ImageView)  pictureItemView.findViewById(R.id.ivPicture);

        //Set the text and bitmap for each one to be displayed in the view
        tvLong.setText(dailyPicture.longitude);
        tvLat.setText(dailyPicture.latitude);
        Bitmap bitmap = BitmapFactory.decodeByteArray(dailyPicture.picture, 0, dailyPicture.picture.length);
        ivDisplay.setImageBitmap(bitmap);

        return pictureItemView;
    }
}
