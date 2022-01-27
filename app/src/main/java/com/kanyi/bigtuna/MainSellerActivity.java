package com.kanyi.bigtuna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTv, shopNameTv, emailTv;
    private ImageButton logoutBtn,editProfileBtn, addProductBtn;
    private ImageView profileIv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main_seller );

        nameTv = findViewById ( R.id.nameTv );
        shopNameTv = findViewById ( R.id.shopNameTv);
        emailTv = findViewById ( R.id.emailTv );
        logoutBtn = findViewById ( R.id.logoutBtn);
        editProfileBtn = findViewById ( R.id.editProfileBtn);
        addProductBtn = findViewById ( R.id.addProductBtn);
        profileIv = findViewById(R.id.profileIv);

        firebaseAuth = FirebaseAuth.getInstance ();
        progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( "Please Wait" );
        progressDialog.setCanceledOnTouchOutside ( false );
        checkUser();

        logoutBtn.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        } );

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open edit add product activity
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));

            }
        });
    }

    private void makeMeOffline() {
        // after logging out, make user offline
        progressDialog.setMessage ( "Logging out user..." );

        HashMap <String, Object> hashMap = new HashMap <> (  );
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener ( new OnSuccessListener < Void > ( ) {
                    @Override
                    public void onSuccess(Void unused) {
                        // update successfully
                        firebaseAuth.signOut ();
                        checkUser ();
                    }
                } )
                .addOnFailureListener ( new OnFailureListener ( ) {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss ();
                        Toast.makeText ( MainSellerActivity.this , ""+e.getMessage () , Toast.LENGTH_SHORT ).show ( );
                    }
                } );
    }

    private void checkUser() {
        FirebaseUser user= firebaseAuth.getCurrentUser ();
        if (user==null){
            startActivity ( new Intent ( MainSellerActivity.this, LoginActivity.class ) );
            finish ();
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ("Users");
        ref.orderByChild ( "uid" ).equalTo ( firebaseAuth.getUid () )
                .addValueEventListener ( new ValueEventListener ( ) {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren ()){
                            //get data from db
                            String name = ""+ds.child ( "name" ).getValue ();
                            String accountType = ""+ds.child ( "accountType" ).getValue ();
                            String email = ""+ds.child ( "email" ).getValue ();
                            String shopName = ""+ds.child ( "shopName" ).getValue ();
                            String profileImage = ""+ds.child ( "profileImage" ).getValue ();

                            //set data to UI
                            nameTv.setText (name);
                            shopNameTv.setText(shopName);
                            emailTv.setText(email);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            }
                            catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_store_gray);

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
    }
}