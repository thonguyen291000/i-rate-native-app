package com.example.rentalz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AnalyticFragment extends Fragment {
    private TextView number, lastDate;
    private int size = 0;
    @Nullable
    @Override
    //Create new fragment
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytic, container, false);

        number = view.findViewById(R.id.txtNumberOfRestaurant);
        lastDate = view.findViewById(R.id.txtDateOfLastCreate);
    // This code I refer from Firebase firestore document 
        FirebaseFirestore.getInstance().collection("restaurants").orderBy("dateCreate", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            lastDate.setText("- Date of the last creating a rating restaurant: " + documentSnapshot.getString("dateCreate"));
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("restaurants").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    size += 1;
                }
                String sizeString = String.valueOf(size);
                number.setText("- The number of created rating restaurant: " + sizeString);
            }
        });

        return view;
    }
}
