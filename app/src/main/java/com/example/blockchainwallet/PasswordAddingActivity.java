package com.example.blockchainwallet;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class PasswordAddingActivity extends AppCompatActivity {
    ImageView imageView;
    Uri selectedImage;
    ProgressBar pgbar;
    TextInputEditText password,email,description;
    String idEmail;
    String input="";
    String isQr="false";

    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_adding);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(PasswordAddingActivity.this);
        String email2;
        email2=acct.getEmail();
        String[] split = email2.split("@");
        idEmail=split[0];


        Button upload,save;
        pgbar=findViewById(R.id.pgbar);
        pgbar.setVisibility(View.INVISIBLE);



        upload=findViewById(R.id.upload);
        description=findViewById(R.id.etNote);
        password=findViewById(R.id.etPassword);
        email=findViewById(R.id.etEmail);
        description=findViewById(R.id.etNote);
        save=findViewById(R.id.save);
        imageView=findViewById(R.id.image);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImage==null&&password.getText().toString().equals("")&&email.getText().toString().equals(""))
                    Toast.makeText(PasswordAddingActivity.this, "Please enter the password or upload image", Toast.LENGTH_SHORT).show();
            else
                {
                    if(selectedImage==null)
                    {
                        if(password.getText().toString().equals("")||email.getText().toString().equals(""))
                            Toast.makeText(PasswordAddingActivity.this, "Please enter the password and email or upload image", Toast.LENGTH_SHORT).show();
                        else if(!password.getText().toString().equals("")&&!email.getText().toString().equals(""))
                        {

                            // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
                             input ="email:"+email.getText().toString()+":password:"+password.getText().toString();

                            QRGEncoder qrgEncoder = new QRGEncoder(input, null, QRGContents.Type.TEXT, 400);
                            qrgEncoder.setColorBlack(Color.BLACK);
                            qrgEncoder.setColorWhite(Color.WHITE);
                            // Getting QR-Code as Bitmap
                             bitmap = qrgEncoder.getBitmap();
                            imageView.setImageBitmap(qrgEncoder.getBitmap());
                            isWriteStoragePermissionGranted();


                            // Setting Bitmap to ImageView
                        }
                        else
                            Toast.makeText(PasswordAddingActivity.this, "Please enter the password and email or upload image", Toast.LENGTH_SHORT).show();

                    }

                    else
                    {
                        isReadStoragePermissionGranted();
                    }
                }
            }
        });


    }




    public void uploadImage(){
        pgbar.setVisibility(View.VISIBLE);

        StorageReference storageRef=FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storageRef.child("images/"+System.currentTimeMillis());
       UploadTask uploadTask = ref.putFile(selectedImage);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    pgbar.setVisibility(View.INVISIBLE);

                    Log.i("maryToritik",""+downloadUri);
                    Toast.makeText(PasswordAddingActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();
                    String timestamp=""+System.currentTimeMillis();

                    Passwords passwords =new Passwords(downloadUri.toString(),timestamp,isQr,description.getText().toString(),input);
                    FirebaseDatabase.getInstance().getReference("Passwords").child(idEmail).child(timestamp).setValue(passwords);
                } else {
                    pgbar.setVisibility(View.INVISIBLE);
                    Log.i("maryToritik","Failed bitch");

                }
            }
        });
    }


    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.v(TAG,"Permission is granted1");
                saveImage();

                uploadImage();

                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            saveImage();

            uploadImage();

            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                isReadStoragePermissionGranted();
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            isReadStoragePermissionGranted();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    isReadStoragePermissionGranted();
                     }else{
                    Toast.makeText(PasswordAddingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    saveImage();
                    uploadImage();
                }else{
                    Toast.makeText(PasswordAddingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void saveImage(){
        if(bitmap!=null){
            isQr="true";
        QRGSaver qrgSaver = new QRGSaver();
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"BlockchainWallet");
        String timestamp=""+System.currentTimeMillis();
        qrgSaver.save(file.getPath(), timestamp, bitmap, QRGContents.ImageType.IMAGE_JPEG);
        Log.i("ritikToMary",file.getPath());
            Log.i("ritikToMary",file.getPath()+timestamp+".jpg");

            selectedImage=Uri.fromFile(new File(file.getPath()+timestamp+".jpg"));}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            Picasso.get().load(selectedImage).into(imageView);
        }
    }
}