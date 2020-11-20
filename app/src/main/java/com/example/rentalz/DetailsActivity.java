package com.example.rentalz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
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

import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {
    //Declare for using open file chooser
    private static final int PICK_IMAGE_REQUEST = 1;

    private RatingBar serviceRating, cleanlinessRating, foodRating, totalRating;
    private TextView service, cleanliness, food, total;
    private EditText type, name, visitDate, dateCreate, price, notes, reporter, location;
    private ImageView imgRestaurant;
    private Button btnUpdate;
    private String idRestaurant;
    private ProgressBar progressBar;
    private Uri imageUri;
    private String imgUrlDatabase;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private ImageButton btnDatePicker;

    private Spinner spinner;
    private String locationName;
    private int flag = 0;

    private AlertDialog.Builder builder;

    //Declare database
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private CollectionReference restaurantsRef = FirebaseFirestore.getInstance().collection("restaurants");

    private Dictionary<String, String> ratingList = new Hashtable<String, String>();

    // Get data object
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Get instance from database to upload image
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        //Reference id
        type = findViewById(R.id.edtTypeDetail);
        name = findViewById(R.id.edtNameDetail);
        dateCreate = findViewById(R.id.edtDateDetail);
        price = findViewById(R.id.edtPriceDetail);
        visitDate = findViewById(R.id.edtVisitDateDetail);
        serviceRating = findViewById(R.id.serviceRatingDetail);
        cleanlinessRating = findViewById(R.id.cleanlinessRatingDetail);
        foodRating = findViewById(R.id.foodRatingDetail);
        totalRating = findViewById(R.id.totalRating);
        service = findViewById(R.id.txtServiceRatingDetail);
        cleanliness = findViewById(R.id.txtCleanlinessRatingDetail);
        food = findViewById(R.id.txtFoodRatingDetail);
        total = findViewById(R.id.txtTotalRating);
        notes = findViewById(R.id.edtNotesDetail);
        reporter = findViewById(R.id.edtReporterDetail);
        imgRestaurant = findViewById(R.id.imgRestaurant);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressBar = findViewById(R.id.progressBar);
        btnDatePicker = findViewById(R.id.btnDatePickerDetail);
        location = findViewById(R.id.edtLocationDetail);

        //Get data from intent object
        intent = getIntent();
        idRestaurant = intent.getStringExtra(RestaurantActivity.EXTRA_ID);

        //Set data for a restaurant detail
        type.setText(intent.getStringExtra(RestaurantActivity.EXTRA_TYPE));
        name.setText(intent.getStringExtra(RestaurantActivity.EXTRA_NAME));
        dateCreate.setText(intent.getStringExtra(RestaurantActivity.EXTRA_DATECREATE));
        price.setText(intent.getStringExtra(RestaurantActivity.EXTRA_PRICE));
        visitDate.setText(intent.getStringExtra(RestaurantActivity.EXTRA_VISITDATE));
        serviceRating.setRating(Float.parseFloat(intent.getStringExtra(RestaurantActivity.EXTRA_SERVICE)));
        cleanlinessRating.setRating(Float.parseFloat(intent.getStringExtra(RestaurantActivity.EXTRA_CLEANLINESS)));
        foodRating.setRating(Float.parseFloat(intent.getStringExtra(RestaurantActivity.EXTRA_FOOD)));
        totalRating.setRating(Float.parseFloat(intent.getStringExtra(RestaurantActivity.EXTRA_TOTAL)));
        location.setText(intent.getStringExtra(RestaurantActivity.EXTRA_LOCATION));
        notes.setText(intent.getStringExtra(RestaurantActivity.EXTRA_NOTES));
        reporter.setText(intent.getStringExtra(RestaurantActivity.EXTRA_REPORTER));
        Picasso.with(this).load(intent.getStringExtra(RestaurantActivity.EXTRA_IMG)).fit().centerCrop().into(imgRestaurant);

        // Set list result for rating
        createRatingList();

        // Set initial rating result for detail
        service.setText("9. Service rating: " + ratingList.get(Float.toString(serviceRating.getRating())));
        cleanliness.setText("10. Cleanliness rating: " + ratingList.get(Float.toString(cleanlinessRating.getRating())));
        food.setText("11. Food rating: " + ratingList.get(Float.toString(foodRating.getRating())));
        total.setText("12. Total rating: " + ratingList.get(Float.toString(totalRating.getRating())));

        // Set on change rating bar
        serviceRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                for(double i = 1; i < 5.5; i += 0.5) {
                    if(i == v) {
                        service.setText("9. Service rating: " + ratingList.get(Double.toString(i)));
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
                        cleanliness.setText("10. Cleanliness rating: " + ratingList.get(Double.toString(i)));
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
                        food.setText("11. Food rating: " + ratingList.get(Double.toString(i)));
                        return;
                    }
                }
            }
        });

        totalRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                for(double i = 1; i < 5.5; i += 0.5) {
                    if(i == v) {
                        total.setText("12. Total rating: " + ratingList.get(Double.toString(i)));
                        return;
                    }
                }
            }
        });

        // Call date picker
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDatePicker();
            }
        });

        // Set builder for alert dialog
        builder = new AlertDialog.Builder(this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Set message") .setTitle("Alert!");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you want to update this restaurant ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                updateProcess();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Alert!");
                alert.show();
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

        datePickerDialog = new DatePickerDialog(DetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                visitDate.setText(mDay + "/" + mMonth + "/" + mYear);
            }
        }, day, month, year);

        datePickerDialog.show();
    }

    public void updateProcess() {
        //Create updated restaurant
        if (type.getText().toString().trim().isEmpty()
                || name.getText().toString().trim().isEmpty()
                || reporter.getText().toString().trim().isEmpty()
                || visitDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(DetailsActivity.this, "Please input enough field with *", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Map<String, Object> updatedRestaurant = new HashMap<>();

            updatedRestaurant.put("type", type.getText().toString());
            updatedRestaurant.put("name", name.getText().toString());
            updatedRestaurant.put("dateCreate", dateCreate.getText().toString());
            updatedRestaurant.put("price", Double.parseDouble(price.getText().toString()));
            updatedRestaurant.put("visitDate", visitDate.getText().toString());
            updatedRestaurant.put("serviceRating", (double) serviceRating.getRating());
            updatedRestaurant.put("cleanlinessRating", (double) cleanlinessRating.getRating());
            updatedRestaurant.put("foodRating", (double) foodRating.getRating());
            updatedRestaurant.put("totalRating", (double) totalRating.getRating());
            updatedRestaurant.put("notes", notes.getText().toString());
            updatedRestaurant.put("reporter", reporter.getText().toString());
            updatedRestaurant.put("location", location.getText().toString());
            updatedRestaurant.put("imgUrl", imageUri == null ? intent.getStringExtra(RestaurantActivity.EXTRA_IMG) : imgUrlDatabase);

            //Update
            restaurantsRef.document(idRestaurant).update(updatedRestaurant).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(DetailsActivity.this, RestaurantActivity.class);
                    //Come back recycle view
                    startActivity(intent);
                    Toast.makeText(DetailsActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailsActivity.this, "Update failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Action when click on the image
    public void uploadImgRestaurant(View view) {
        uploadImage();
    }

    //Function active when pick a file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get uri
            imageUri = data.getData();

            //Load image on image view
            Picasso.with(this).load(imageUri).fit().centerCrop().into(imgRestaurant);

            //Upload image on database
            uploadImage();
        }
    }

    private void uploadImage() {
            //Open file chooser
            Upload.openFileChooser(DetailsActivity.this);

        if (imageUri != null) {
            //Get file extension
            String fileExtension = Upload.getFileExtension(imageUri, getContentResolver());

            String fileName = System.currentTimeMillis() + "." + fileExtension;

            //Url to get image from storage
            imgUrlDatabase = "https://firebasestorage.googleapis.com/v0/b/rentalz-4f645.appspot.com/o/uploads%2F"+fileName+"?alt=media";
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

                    Toast.makeText(DetailsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload(taskSnapshot.getUploadSessionUri().toString());

                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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