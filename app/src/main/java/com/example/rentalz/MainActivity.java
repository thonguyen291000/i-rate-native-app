package com.example.rentalz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ImageView imgProfile;

    private Uri imageUri;
    private String imgUrlDatabase;

    private String uid;
    private String email;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user from login
        uid = getIntent().getStringExtra("userId");
        email = getIntent().getStringExtra("userEmail");

        //Home fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        toolbar = findViewById(R.id.toolbar);
        //Set toolbar as action bar
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        //Object for handling selected item on nav
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Create hamburger icon for open drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Sure that active when the first time app open or after back press
        if (savedInstanceState == null) {
            //Open message fragment and choose item for initial open
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        //Create bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.nav_bottom);
        //Bottom selected item listener declare
        bottomNav.setOnNavigationItemSelectedListener(navBottomListener);
    }

    //Bottom navigation selected item
    private BottomNavigationView.OnNavigationItemSelectedListener navBottomListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_bottom_dashboard:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_bottom_analyze:
                    selectedFragment = new AnalyticFragment();
                    break;
            }

            //Statement change fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };

    //Action when choose item on action bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //Restaurants page
            case R.id.nav_restaurants:
                //Statement for change fragment
                Intent intentRes = new Intent(MainActivity.this, RestaurantActivity.class);
                intentRes.putExtra("email", email);
                startActivity(intentRes);
                break;
            //Profile page
            case R.id.nav_profile:
                //Statement for change fragment
                Intent intentProfie = new Intent(MainActivity.this, ProfileActivity.class);
                intentProfie.putExtra("uid", uid);
                intentProfie.putExtra("email", email);
                startActivity(intentProfie);
                break;
            //Log out
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                //Statement for change fragment
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }

        //CLose drawer from right to left
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUpImgProfile(View view) {
        imgProfile = view.findViewById(R.id.imgImageUser);

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
            Picasso.with(this).load(imageUri).fit().centerCrop().into(imgProfile);

            //Upload image on database
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            //Open file chooser
            Upload.openFileChooser(MainActivity.this);

            //Get file extension
            String fileExtension = Upload.getFileExtension(imageUri, getContentResolver());

            String fileName = System.currentTimeMillis() + "." + fileExtension;

            //Url to get image from storage
            imgUrlDatabase = "https://firebasestorage.googleapis.com/v0/b/rentalz-4f645.appspot.com/o/uploads%2F"+fileName+"?alt=media";

            //Create file name with extension ex: 14821743829.jpg
            StorageReference fileReference = storageReference.child(fileName);

            //Save in database
            Map<String, Object> user = new HashMap<>();
            user.put("imgUrl", imgUrlDatabase);
            FirebaseFirestore.getInstance().collection("users").document(uid).set(user);

            //Load process
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload(taskSnapshot.getUploadSessionUri().toString());

                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(upload);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}