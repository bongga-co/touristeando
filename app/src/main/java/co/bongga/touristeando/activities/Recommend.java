package co.bongga.touristeando.activities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.PlaceCategory;

public class Recommend extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<String> categoryList = new ArrayList<>();

    private Spinner spCategory;
    private EditText etAddress, etCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        spCategory = (Spinner) findViewById(R.id.rc_place_category);
        etAddress = (EditText) findViewById(R.id.rc_place_address);
        etCity = (EditText) findViewById(R.id.rc_place_city);
        etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String city = etCity.getText().toString();
                    String address = etAddress.getText().toString();

                    if(!address.isEmpty() &&
                            !city.isEmpty()){
                        LatLng location = getLocationFromAddress(getApplicationContext(),
                                String.format(Locale.getDefault(), "%s, %s", address, city));

                        System.out.println(location.toString());
                    }
                }
            }
        });

        getCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recommend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.ic_action_save_place:
                savePlace();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCategories(){
        DatabaseReference categoriesRef = databaseReference.child("place_categories");
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    PlaceCategory category = item.getValue(PlaceCategory.class);
                    categoryList.add(category.getName());
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, categoryList);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(dataAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private void savePlace() {

    }
}
