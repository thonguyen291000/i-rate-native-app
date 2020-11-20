package com.example.rentalz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ResultSearchActivity extends AppCompatActivity {
    //Declare keys
    public static final String EXTRA_ID = "com.example.application.rentalz.EXTRA_ID";
    public static final String EXTRA_TYPE = "com.example.application.rentalz.EXTRA_TYPE";
    public static final String EXTRA_NAME = "com.example.application.rentalz.EXTRA_NAME";
    public static final String EXTRA_VISITDATE = "com.example.application.rentalz.EXTRA_VISITDATE";
    public static final String EXTRA_SERVICE = "com.example.application.rentalz.EXTRA_SERVICE";
    public static final String EXTRA_CLEANLINESS = "com.example.application.rentalz.EXTRA_CLEANLINESS";
    public static final String EXTRA_FOOD = "com.example.application.rentalz.EXTRA_FOOD";
    public static final String EXTRA_TOTAL = "com.example.application.rentalz.EXTRA_TOTAL";
    public static final String EXTRA_PRICE = "com.example.application.rentalz.EXTRA_PRICE";
    public static final String EXTRA_LOCATION = "com.example.application.rentalz.EXTRA_LOCATION";
    public static final String EXTRA_NOTES = "com.example.application.rentalz.EXTRA_NOTES";
    public static final String EXTRA_REPORTER = "com.example.application.rentalz.EXTRA_REPORTER";
    public static final String EXTRA_DATECREATE = "com.example.application.rentalz.EXTRA_DATECREATE";
    public static final String EXTRA_IMG = "com.example.application.rentalz.EXTRA_IMG";

    //Firestore database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference restaurantsRef = db.collection("restaurants");

    private RestaurantAdapter adapter;

    private FirestoreRecyclerOptions<Restaurant> options;

    private AlertDialog.Builder builder;

    private String constraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_search_restaurant);

        // Get constraint
        constraint = getIntent().getStringExtra("constraint");
        showList(constraint);

        // Set builder for alert dialog
        builder = new AlertDialog.Builder(this);

        setUpRecycleView();
    }

    private void setUpRecycleView() {

        //Using setonItemClickListener from interface
        adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String type = documentSnapshot.getString("type");
                String name = documentSnapshot.getString("name");
                String dateCreate = documentSnapshot.getString("dateCreate");
                String visitDate = documentSnapshot.getString("visitDate");
                double serviceRating = documentSnapshot.getDouble("serviceRating");
                double cleanlinessRating = documentSnapshot.getDouble("cleanlinessRating");
                double foodRating = documentSnapshot.getDouble("foodRating");
                double totalRating = documentSnapshot.getDouble("totalRating");
                double price = documentSnapshot.getDouble("price");
                String location = documentSnapshot.getString("location");
                String notes = documentSnapshot.getString("notes");
                String reporter = documentSnapshot.getString("reporter");
                String imgUrl = documentSnapshot.getString("imgUrl");
                String id = documentSnapshot.getId();

                openDetails(id, type, name, visitDate, serviceRating, cleanlinessRating, foodRating, totalRating, dateCreate, price, location, notes, reporter, imgUrl);
            }
        });
    }

    private void openDetails(String id, String type, String name, String visitDate, double serviceRating, double cleanlinessRating, double foodRating, double totalRating, String dateCreate, double price, String location, String notes, String reporter, String imgUrl) {
        //Transfer restaurant detail to detail activity
        Intent intent = new Intent(this, DetailsActivity.class);

        //Put information into intent object
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_VISITDATE, visitDate);
        intent.putExtra(EXTRA_SERVICE, String.valueOf(serviceRating));
        intent.putExtra(EXTRA_CLEANLINESS, String.valueOf(cleanlinessRating));
        intent.putExtra(EXTRA_FOOD, String.valueOf(foodRating));
        intent.putExtra(EXTRA_TOTAL, String.valueOf(totalRating));
        intent.putExtra(EXTRA_DATECREATE, dateCreate);
        intent.putExtra(EXTRA_PRICE, String.valueOf(price));
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_NOTES, notes);
        intent.putExtra(EXTRA_REPORTER, reporter);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_IMG, imgUrl);

        //Start change activity to detail activity
        startActivity(intent);
    }

    private void showList(String constraint) {
        Query query = restaurantsRef.whereEqualTo("name", constraint);
        options = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class)
                .build();

        //Transfer data in recycle view
        adapter = new RestaurantAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycle_view_result);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Touch swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                // Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Set message") .setTitle("Alert!");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to delete this restaurant ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Delete when swiping
                                adapter.deleteRestaurant(viewHolder.getAdapterPosition());

                                Toast.makeText(ResultSearchActivity.this, "Delete successful!", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                startActivity(new Intent(ResultSearchActivity.this, ResultSearchActivity.class));
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert!");
                alert.show();
            }
        }).attachToRecyclerView(recyclerView); //Update to recycle view
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}