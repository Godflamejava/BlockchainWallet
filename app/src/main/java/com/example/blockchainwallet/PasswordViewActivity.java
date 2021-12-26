package com.example.blockchainwallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Positions;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;

public class PasswordViewActivity extends AppCompatActivity {
String idEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_view);
        final Boolean[] scanOrHide = {false};


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(PasswordViewActivity.this);
        String email2;
        email2=acct.getEmail();
        String[] split = email2.split("@");
        idEmail=split[0];
        ProgressBar progressBar=findViewById(R.id.progressbar);
        Button scan = findViewById(R.id.scan);



        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String link = sh.getString("link", "");
        String password  = sh.getString("password", "");
        String time = sh.getString("time", "");

        ImageView imageView= findViewById(R.id.image);
        ImageView delete=findViewById(R.id.delete);
        TextView email,Tvpassword,date;
        email=findViewById(R.id.email);
        Tvpassword=findViewById(R.id.password);
        date=findViewById(R.id.date);
        email.setVisibility(View.INVISIBLE);
        Tvpassword.setVisibility(View.INVISIBLE);
        scan.setVisibility(View.INVISIBLE);

        ImageView share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, link);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TITLE, "Share the link of the password Image");
                startActivity(Intent.createChooser(sendIntent, null));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FastDialog dialog= new FastDialogBuilder(PasswordViewActivity.this, Type.DIALOG)
                        .setTitleText("Do You want to Delete this password")
                        .setText("Enter this 'Delete' to delete")
                        .positiveText("Delete")
                        .negativeText("Cancel")
                        .changeColor(ContextCompat.getColor(getApplicationContext(),R.color.different),
                                ContextCompat.getColor(getApplicationContext(),R.color.text2),
                                ContextCompat.getColor(getApplicationContext(),R.color.text))
                        .setHint("Delete")
                        .setAnimation(Animations.SLIDE_TOP)
                        .setPosition(Positions.BOTTOM)
                        .create();
                dialog.positiveClickListener(new PositiveClick() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    if(dialog.getInputText().equals("Delete")) {
                        FirebaseDatabase.getInstance().getReference("Passwords").child(idEmail).child(time).removeValue();
                        Log.i("mary",time);
                        Toast.makeText(PasswordViewActivity.this, "Password Successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(PasswordViewActivity.this, "Try again !", Toast.LENGTH_SHORT).show();

                    }
                    }
                });
                dialog.show();
            }
        });

//        Picasso.get().load(link).into(imageView);


        progressBar.setVisibility(View.VISIBLE);
// Hide progress bar on successful load
        Picasso.get().load(link)
                .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                            progressBar.setVisibility(View.GONE);

                        scan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                             if(scanOrHide[0] ==false) {
                                 email.setVisibility(View.VISIBLE);
                                 Tvpassword.setVisibility(View.VISIBLE);
                                 scan.setText("HIDE");
                                 scanOrHide[0] =true;
                             }
                             else{
                                 email.setVisibility(View.INVISIBLE);
                                 Tvpassword.setVisibility(View.INVISIBLE);
                                 scan.setText("SCAN");
                                 scanOrHide[0] =false;
                             }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(PasswordViewActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                    }

                });

        Timestamp stamp = new Timestamp(Long.parseLong(time));
        Date date1 = new Date(stamp.getTime());
        date.setText("Last Updated : "+date1.toString());
        if(password.equals(""))
        {
            email.setVisibility(View.INVISIBLE);
            Tvpassword.setVisibility(View.INVISIBLE);
        }
        else
        {
            scan.setVisibility(View.VISIBLE);
            String[] words=password.split(":");
            email.setText("Username : "+words[1]);
            Tvpassword.setText("Password : "+words[3]);
        }








    }
}