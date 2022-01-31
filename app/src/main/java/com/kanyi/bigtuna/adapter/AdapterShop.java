package com.kanyi.bigtuna.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kanyi.bigtuna.R;
import com.kanyi.bigtuna.activities.ShopDetailsActivity;
import com.kanyi.bigtuna.models.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{

    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.row_shop, parent, false);

        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {

        ModelShop modelShop = shopsList.get(position);
        String accountType = modelShop.getAccountType();
        String address = modelShop.getAddress();
        String city = modelShop.getCity();
        String country = modelShop.getCountry();
        String deliveryFee = modelShop.getDeliveryFee();
        String email = modelShop.getEmail();
        String latitude = modelShop.getLatitude();
        String longitude = modelShop.getLongitude();
        String online = modelShop.getOnline();
        String name = modelShop.getName();
        String phone = modelShop.getPhone();
        final String uid = modelShop.getUid();
        String timestamp = modelShop.getTimestamp();
        String companyOpen = modelShop.getCompanyOpen();
        String state = modelShop.getState();
        String profileImage = modelShop.getProfileImage();
        String companyName = modelShop.getCompanyName();

        loadReviews(modelShop,holder);

        holder.companyNameTv.setText(companyName);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
        if (online.equals("true")){

            holder.onlineIv.setVisibility(View.VISIBLE);
        }
        else{
            holder.onlineIv.setVisibility(View.GONE);
        }
        if(companyOpen.equals("true")){
            holder.shopClosedTv.setVisibility(View.GONE);

        }
        else {
            holder.shopClosedTv.setVisibility(View.VISIBLE);
        }

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(holder.shopIv);

        }
        catch (Exception e){
            holder.shopIv.setImageResource(R.drawable.ic_store_gray);

        }

        // handle click listener for shop details

        holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( context, ShopDetailsActivity.class );
                intent.putExtra ( "companyUid",uid );
                context.startActivity ( intent );
            }
        } );

    }

    private float ratingSum =0;
    private void loadReviews(ModelShop modelShop , HolderShop holder) {

        String companyUid = modelShop.getUid ( );

        DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ("Users");
        ref.child ( companyUid ).child ( "Ratings" )
                .addValueEventListener ( new ValueEventListener ( ) {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // clear list before adding data into it
                        ratingSum=0;
                        for(DataSnapshot ds: dataSnapshot.getChildren ()){
                            float rating = Float.parseFloat ( ""+ds.child ( "ratings" ).getValue () );
                            ratingSum = ratingSum+rating;


                        }
                        long numberOfReviews = dataSnapshot.getChildrenCount ();
                        float avgRating = ratingSum/numberOfReviews;

                        holder.ratingBar.setRating ( avgRating );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder{

        private ImageView shopIv,onlineIv;
        private TextView shopClosedTv, companyNameTv, phoneTv,addressTv;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);

            shopIv = itemView.findViewById(R.id.shopIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            shopClosedTv = itemView.findViewById(R.id.shopClosedTv);
            companyNameTv = itemView.findViewById(R.id.companyNameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}
