package com.example.blockchainwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
ArrayList<Passwords> passwordsArrayList;
PasswordAdapter adapter;
String idEmail;
    ProgressBar pgbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pgbar= findViewById(R.id.pgbar);
        pgbar.setVisibility(View.VISIBLE);
        CardView cardView=findViewById(R.id.card);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        String email2,name;
        Uri profileLink;
        email2=acct.getEmail();
        name=acct.getDisplayName();
        profileLink=acct.getPhotoUrl();

        CircleImageView profilePic=findViewById(R.id.profile);
        TextView Tvname=findViewById(R.id.name);
        TextView tvEmail=findViewById(R.id.email);
        Picasso.get().load(profileLink).into(profilePic);
        tvEmail.setText(email2);
        Tvname.setText(name);

        String[] split = email2.split("@");
        idEmail=split[0];
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        passwordsArrayList=new ArrayList<>();
        adapter=new PasswordAdapter(this,passwordsArrayList);
        recyclerView.setAdapter(adapter);
        getList();



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PasswordAddingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void getList(){
        FirebaseDatabase.getInstance().getReference("Passwords").child(idEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    passwordsArrayList.add( dataSnapshot.getValue(Passwords.class));

                adapter.notifyDataSetChanged();
                pgbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pgbar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}