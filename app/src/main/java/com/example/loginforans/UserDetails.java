package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetails extends AppCompatActivity {

    String TAG="MyTag";

    EditText et1,et2,et3;
    Button btn1;
    CircleImageView dp;
    private ProgressBar pb;

    FirebaseAuth auth;

    private FirebaseDatabase MyDatabase;
    private DatabaseReference RefToDatabase;
    private FirebaseStorage MyFirebaseStorage;
    private StorageReference RefToStorage;

     String profileimageurl;






    String Current_User;

    Integer Pick_Image=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        auth=FirebaseAuth.getInstance();
        Current_User=auth.getCurrentUser().getUid();
        //same id as it is in Auth
        MyDatabase=FirebaseDatabase.getInstance();
        RefToDatabase=MyDatabase.getReference().child("Users").child(Current_User); //id that was left uncompelete that id will be taken and will be made in real database and data will store in it

        initviews();


        MyFirebaseStorage=FirebaseStorage.getInstance();
        RefToStorage=MyFirebaseStorage.getReference().child("ProfileImages");





        HideProgressBar();


    }

    private void initviews() {

        et1=findViewById(R.id.ET_Name_UD);
        et2=findViewById(R.id.ET_Email_UD);
        et3=findViewById(R.id.ET_Course_UD);
        btn1=findViewById(R.id.Btn_Save_Information_UD);
        pb=findViewById(R.id.progressBar_UD);
        dp=findViewById(R.id.CI_Dp_UD);
    }


    public void SaveInformation(View view)
    {


        ShowProgressBar();

        if(!filedValidation())
        {
            HideProgressBar();
            return;
        }
        else
        {
            String name=et1.getText().toString();
            String email=et2.getText().toString();
            String Semester=et3.getText().toString();

            Map<String,Object> Insertdata = new HashMap<>();//coz we insert multiple  data in something that dont have values e.g user1->name,email,semester.user1 has no value it is root will use map for it

            Insertdata.put("Name",name);
            Insertdata.put("Email",email);
            Insertdata.put("Semester",Semester);
            RefToDatabase.updateChildren(Insertdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {
                        HideProgressBar();
                        Toast.makeText(getApplicationContext(),"Account Created Successfully",Toast.LENGTH_SHORT).show();
                        SendToHomeActivity();
                    }
                    else
                    {
                        HideProgressBar();
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });






        }

    }



    public void SelectImage(View view)
    {



            Intent gallery = new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult(gallery, Pick_Image);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);




        Log.d(TAG, "onActivityResult: ");
        if(requestCode == Pick_Image && resultCode == RESULT_OK && data!=null)

        {
            Log.d(TAG, "onActivityResult: 2"+requestCode);

            

            Uri imagesuri = data.getData();
            try {  //this will show image on userdetail xml
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagesuri);
                // Log.d(TAG, String.valueOf(bitmap));


                dp.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();

            }

//this will put file in filestorage





                final StorageReference filepath = RefToStorage.child(Current_User + ".jpg");
                filepath.putFile(imagesuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       //     Toast.makeText(getApplicationContext(), "Image uploaded Successfully", Toast.LENGTH_SHORT).show();


                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                 profileimageurl = uri.toString();
                                Log.d(TAG, "onSuccess: uriiiiiiiiiiii= "+ uri.toString());
                                
                                SaveUrlToDatabase();
                                Log.d(TAG, "onSuccess: file saved to databse");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });



                            
                        }






                });

            }
        }

        
        
        
        
        public void SaveUrlToDatabase()
        {
            RefToDatabase.child("profileurl").setValue(profileimageurl)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: pic is uploaded to real time database");


                              //  Toast.makeText(getApplicationContext(), "Profile pic uploaded", Toast.LENGTH_SHORT).show();

//                                            Intent selfintent = new Intent(getApplicationContext(),UserDetails.class);
//                                            startActivity(selfintent);
////                                            //self calling because other details also need to be filled.


                            } else {
                            //    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                          //  Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        
        

    public void SendToHomeActivity(){

        Intent i = new Intent(getApplicationContext(),home.class);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //new task if activity is already present it wont create its new instance but pooped other activities and use called activity
        //Clear task finishes all other activites before starting the targeted activity

        startActivity(i);
        finish(); //can't go to previous activity with back button

    }
    private void ShowProgressBar() {
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);
    }

    private void HideProgressBar() {

        pb.setVisibility(View.GONE);
    }



    public boolean filedValidation()
    {

        String name=et1.getText().toString();
        String email=et2.getText().toString();
        String Semester=et3.getText().toString();




        if(TextUtils.isEmpty(name))
        {
            et1.setError("Fill the field first");
            return false;
        }
        else if(TextUtils.isEmpty(email))
        {
            et2.setError("Fill the field first");
            return false;
        }
        else if(TextUtils.isEmpty(Semester))
        {
            et3.setError("Fill the field first");
            return false;
        }
        else
            return true;

    }

//    public boolean imagevalidation()
//    {
//        if(dp.equals(null))
//        {
//
//
//            Toast.makeText(getApplicationContext(),"select image please",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else
//            return true;
//    }




}



