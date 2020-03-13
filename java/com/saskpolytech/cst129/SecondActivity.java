package com.saskpolytech.cst129;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Seoond activity for the activity_second.xml file that is used to view all the DailyPicture objects created in
 * the database
 */
public class SecondActivity extends AppCompatActivity{

    EditText etTotal2;
    ListView lvDailyPics;
    Button btnTakeMore;
    DailyPictureHelper db;

    /**
     * Method called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //Keyboard kept popping up and I found this way to keep it hidden in this activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Variables in the layout instansiated and assigned
        etTotal2 = (EditText) findViewById(R.id.etTotal2);
        btnTakeMore = (Button) findViewById(R.id.btnTakeMore);
        db = new DailyPictureHelper(this);
        etTotal2.setEnabled(false);

        LinearLayout ll = (LinearLayout) findViewById(R.id.llFragContainerH);

        if(savedInstanceState == null)
        {
            //Make a new transaction that will switch between fragments
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

            //The frag does not require data so just call the constructor
            trans.add(ll.getId(), new MainActivityFragment());

            //Make the swap
            trans.commit();
        }

        //Open the database again to set the total pictures taken at the top of the layout
        db.open();

        int nTotal = db.getTotalPics();

        etTotal2.setText(String.format("%d",nTotal));


        db.close();

    }

    /**
     * Listener for the take more button which will take the user back to the home screen
     * @param v
     */
    public void btnTakeMoreListener(View v)
    {

        //New intent to go to the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

}
