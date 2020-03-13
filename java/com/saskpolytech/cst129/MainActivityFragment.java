package com.saskpolytech.cst129;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by cst129 Matthew Martens on 5/24/2018. This class will be a fragment for adapting a list view
 */
public class MainActivityFragment extends Fragment{

    //Global attributes for the fragment
    ListView lvPictures;
    ArrayList<DailyPicture> pictures;
    //ArrayAdapter<Person> adapter;
    PictureListAdapter customAdapter;
    DailyPictureHelper db; //Database reference

    /**
     * Method that will create and return the view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //We need to use the view before we return it
        View fragView = inflater.inflate(R.layout.fragment_main, container, false);
        lvPictures = (ListView) fragView.findViewById(R.id.lvPictures);

        //Method will get the arrayList in the database of DailyPicture objects
        db = new DailyPictureHelper(getActivity());

        db.open();

        pictures = new ArrayList<>();

        pictures = db.getAllDailyPictures();

        db.close();

        //Sets the adapter with the arrayList from the DB
        customAdapter = new PictureListAdapter(getActivity(), pictures);

        //connect the list to the adapter
        lvPictures.setAdapter(customAdapter);

        return fragView;
    }




}
