package com.example.rentalz;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProfileActivity extends AppCompatActivity {
    //Declare
    private ImageView logoUser;
    private TextView email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Reference
        email = findViewById(R.id.txtProfileEmail);
        logoUser = findViewById(R.id.imgLogoUser);

        if (getIntent().getStringExtra("uid") != null) {
            email.setText(getIntent().getStringExtra("email"));
            FirebaseFirestore.getInstance().collection("users").document(getIntent().getStringExtra("uid")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Picasso.with(ProfileActivity.this).load(documentSnapshot.getString("imgUrl")).fit().centerCrop().into(logoUser);
                }
            });
        }
    }
}
