package com.example.rentalz;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewRestaurantActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private RatingBar serviceRating, cleanlinessRating, foodRating;
    private TextView service, cleanliness, food;
    private EditText type, name, visitDate,  notes, reporter, price, location;
    private ProgressBar progressBar;
    private Uri imageUri;
    private ImageView imgRestaurant;
    private String imgUrlDatabase;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private ImageButton btnDatePicker;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private Spinner spinner;

    private Dictionary<String, String> ratingList = new Hashtable<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        setTitle("i-Rate");

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        type = findViewById(R.id.edtNewType);
        price = findViewById(R.id.edtNewPrice);
        name = findViewById(R.id.edtNewName);
        visitDate = findViewById(R.id.edtVisitDate);
        serviceRating = findViewById(R.id.serviceRating);
        cleanlinessRating = findViewById(R.id.cleanlinessRating);
        foodRating = findViewById(R.id.foodRating);
        service = findViewById(R.id.txtServiceRatingCreate);
        cleanliness = findViewById(R.id.txtCleanlinessRatingCreate);
        food = findViewById(R.id.txtFoodRatingCreate);

        notes = findViewById(R.id.edtNewNotes);
        reporter = findViewById(R.id.edtNewReporter);
        imgRestaurant = findViewById(R.id.imgRestaurant_create);
        progressBar = findViewById(R.id.progressBar);

        btnDatePicker = findViewById(R.id.btnDatePicker);

        location = findViewById(R.id.edtLocation);

        // Get email and gain it to reporter
        reporter.setText(getIntent().getStringExtra("email"));

        // Set list result for rating
        createRatingList();

        // Set on change rating bar
        serviceRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                for(double i = 1; i < 5.5; i += 0.5) {
                    if(i == v) {
                        service.setText("Service rating: " + ratingList.get(Double.toString(i)));
                        return;
                    }
                }
            }
        });

        cleanlinessRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                for(double i = 1; i < 5.5; i += 0.5) {
                    if(i == v) {
                        cleanliness.setText("Service rating: " + ratingList.get(Double.toString(i)));
                        return;
                    }
                }
            }
        });

        foodRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                for(double i = 1; i < 5.5; i += 0.5) {
                    if(i == v) {
                        food.setText("Service rating: " + ratingList.get(Double.toString(i)));
                        return;
                    }
                }
            }
        });

        // Set visited date
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDatePicker();
            }
        });

    }

    // List result of rating
    private void createRatingList() {
        ratingList.put("1.0", "Terrible");
        ratingList.put("1.5", "Very bad");
        ratingList.put("2.0", "Bad");
        ratingList.put("2.5", "Need to improve");
        ratingList.put("3.0", "Normal");
        ratingList.put("3.5", "Quite");
        ratingList.put("4.0", "Good");
        ratingList.put("4.5", "Very good");
        ratingList.put("5.0", "Perfect");
    }

    public void onCreateDatePicker() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(NewRestaurantActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                visitDate.setText(mDay + "/" + mMonth + "/" + mYear);
            }
        }, day, month, year);

        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_restaurant_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_restaurant:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNote() {
        String type = this.type.getText().toString();
        String name = this.name.getText().toString();
        String visitDate = this.visitDate.getText().toString();
        double serviceRating = (double) this.serviceRating.getRating();
        double cleanlinessRating = (double) this.cleanlinessRating.getRating();
        double foodRating = (double) this.foodRating.getRating();
        double totalRating = (serviceRating + cleanlinessRating + foodRating)/3;
        String notes = this.notes.getText().toString();
        String reporter = this.reporter.getText().toString(); //Get auto
        double price = Double.parseDouble(this.price.getText().toString());
        String imgUrl = imgUrlDatabase;
        String location = "No information";

        if(this.location.getText().toString() != "") {
            location = this.location.getText().toString();
        }

        if (type.trim().isEmpty()
                || name.trim().isEmpty()
                || reporter.trim().isEmpty()
                || visitDate.trim().isEmpty()) {
                    Toast.makeText(this, "Please input enough field with *", Toast.LENGTH_SHORT).show();
                    return;
        } else if (price == 0) {
            Toast.makeText(this, "Price does not equal to 0", Toast.LENGTH_SHORT).show();
        } else {
            if (notes.trim().isEmpty()) notes = "";

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Calendar calendar = Calendar.getInstance();
            String dateCreate = DateFormat.getDateInstance().format(calendar.getTime());

            CollectionReference restaurantsRef = db.collection("restaurants");
            restaurantsRef.add(new Restaurant(type, name, visitDate, serviceRating, cleanlinessRating, foodRating, totalRating, dateCreate, price, location, notes, reporter, imgUrl));
            Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void uploadImgRestaurant(View view) {
        uploadImage();
    }


    //Function active when pick a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != PICK_IMAGE_REQUEST || resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        // Get uri
        imageUri = data.getData();

        //Load image on image view
        Picasso.with(this).load(imageUri).fit().centerCrop().into(imgRestaurant);

        //Upload image on database
        uploadImage();
    }

    private void uploadImage() {
        //Open file chooser
        Upload.openFileChooser(NewRestaurantActivity.this);

        if (imageUri != null) {
            //Get file extension
            String fileExtension = Upload.getFileExtension(imageUri, getContentResolver());

            String fileName = System.currentTimeMillis() + "." + fileExtension;

            //Url to get image from storage
            imgUrlDatabase = "https://firebasestorage.googleapis.com/v0/b/rentalz-4f645.appspot.com/o/uploads%2F" + fileName + "?alt=media";

            //Create file name with extension ex: 14821743829.jpg
            StorageReference fileReference = storageReference.child(fileName);

            //Load process
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 5000);

                    Toast.makeText(NewRestaurantActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload(taskSnapshot.getUploadSessionUri().toString());

                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewRestaurantActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}