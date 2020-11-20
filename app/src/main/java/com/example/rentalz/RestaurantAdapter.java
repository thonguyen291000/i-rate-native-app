package com.example.rentalz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantAdapter.RestaurantHolder> {

    private Context context;
    private OnItemClickListener listener;
     /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //Get view from apartment_item layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
        return new RestaurantHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantHolder restaurantHolder, int position, @NonNull Restaurant restaurant) {
        //Set data to apartment item properties
        restaurantHolder.txtName.setText(restaurant.getName());
        restaurantHolder.txtDate.setText(restaurant.getDateCreate());
        restaurantHolder.txtPrice.setText(String.valueOf(restaurant.getPrice()) + "$");
        Picasso.with(context).load(restaurant.getImgUrl()).fit().centerCrop().into(restaurantHolder.imgAdapter);


    }
    public void deleteRestaurant(int position) {
        //Delete apartment
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public void deleteAll() {
        if(getSnapshots().size() > 0) {
            for(int i = 0; i < getSnapshots().size(); i++) {
                getSnapshots().getSnapshot(i).getReference().delete();
            }
        }
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDate, txtPrice;
        ImageView imgAdapter;

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);
            //Set each item in recycle view
            txtDate = itemView.findViewById(R.id.txtDate);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgAdapter = itemView.findViewById(R.id.img_restaurant);

            //Action when click on item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    //Send function click in adapter to activity by interface
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
